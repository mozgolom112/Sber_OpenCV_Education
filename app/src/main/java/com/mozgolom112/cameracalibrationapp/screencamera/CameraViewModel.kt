package com.mozgolom112.cameracalibrationapp.screencamera

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mozgolom112.cameracalibrationapp.models.CalibrationData
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

class CameraViewModel : CameraBridgeViewBase.CvCameraViewListener2 {

    companion object {
        private const val chessboardVerticalAmount = 7
        private const val chessboardHorizontalAmount = 11
        private const val squareSize = 50
    }

    private var isSizesSet = false
    var takeSnapshotClick = false

    private val amountOfSnapshots by lazy { MutableLiveData<Int>(0) }

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        // get current camera frame as OpenCV Mat object
        val frame = inputFrame.rgba()

        Log.i("onCameraFrame", "Inside")
        Log.i("onCameraFrame", "${frame.size().height} - высота ${frame.size().width} - ширина")
        if (!isSizesSet) {
            //Set necessary sizes. We need in frame, so we do it in this method
            Log.i("onCameraFrame", "SetSizes")

            //TODO('Как лучше передать сразу Size и потом reinterpret_cast делать или сделать это в c++?')
            setSizes(
                chessboardHorizontalAmount,
                chessboardVerticalAmount,
                frame.nativeObjAddr,
                squareSize
            )
            Log.i("onCameraFrame", "Were set")
            isSizesSet = true
        }
        Log.i("onCameraFrame", "identifyChessboard")
        amountOfSnapshots.postValue(identifyChessboard(frame.nativeObjAddr, takeSnapshotClick))
        Log.i("onCameraFrame", "identifiedChessboard")
        takeSnapshotClick = false

        return frame
    }

    fun calibrateCameraClick(): CalibrationData {
        val matrixMat = Mat()
        val distMat = Mat()

        calibrate(matrixMat.nativeObjAddr, distMat.nativeObjAddr)

        Log.i("Calibration", "${matrixMat.dump()}  || ${distMat.dump()}")

        return CalibrationData(matrixMat.nativeObjAddr, distMat.nativeObjAddr, matrixMat.dump(), distMat.dump())
    }

    private external fun calibrate(matrixMatAddr: Long, distMatAddr: Long)

    private external fun identifyChessboard(frameAddr: Long, takeSnapshotClick: Boolean): Int

    private external fun setSizes(
        chessboardHorizontalAmount: Int,
        chessboardVerticalAmount: Int,
        imageAddr: Long,
        squareSize: Int
    )

}