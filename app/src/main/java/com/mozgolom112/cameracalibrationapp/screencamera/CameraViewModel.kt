package com.mozgolom112.cameracalibrationapp.screencamera

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mozgolom112.cameracalibrationapp.models.CalibrationData
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

class CameraViewModel : CameraBridgeViewBase.CvCameraViewListener2 {

    companion object {
        private const val chessboardVerticalAmount = 5
        private const val chessboardHorizontalAmount = 9
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

        if (!isSizesSet) {
            //Set necessary sizes. We need in frame, so we do it in this method
            setSizes(
                chessboardHorizontalAmount,
                chessboardVerticalAmount,
                frame.nativeObjAddr,
                squareSize
            )
            isSizesSet = true
        }
        amountOfSnapshots.postValue(identifyChessboard(frame.nativeObjAddr, takeSnapshotClick))

        takeSnapshotClick = false

        return frame
    }

    fun calibrateCameraClick(): CalibrationData {
        val matrixMat = Mat()
        val distMat = Mat()

        calibrate(matrixMat.nativeObjAddr, distMat.nativeObjAddr)

        return CalibrationData(
            matrixMat.nativeObjAddr,
            distMat.nativeObjAddr,
            matrixMat.dump(),
            distMat.dump()
        )
    }

    private external fun calibrate(matrixMatAddr: Long, distMatAddr: Long)

    private external fun identifyChessboard(frameAddr: Long, takeSnapshotClick: Boolean): Int

    private external fun setSizes(
        chessboardHorizontalAmount: Int, chessboardVerticalAmount: Int,
        imageAddr: Long, squareSize: Int
    )

}