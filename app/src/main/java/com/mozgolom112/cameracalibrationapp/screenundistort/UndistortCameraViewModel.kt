package com.mozgolom112.cameracalibrationapp.screenundistort

import com.mozgolom112.cameracalibrationapp.models.CalibrationData
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

class UndistortCameraViewModel : CameraBridgeViewBase.CvCameraViewListener2 {

    var calibrationData: CalibrationData? = null

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {

        val frame = inputFrame.rgba()

        calibrationData?.apply {
            undistort(frame.nativeObjAddr, CameraMat , DistMat)
        }

        return frame
    }

    private external fun undistort(frameAddr: Long, matrixAddr: Long, distAddr: Long)

}