package com.mozgolom112.cameracalibrationapp

import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

object CvCameraViewListener2 : CameraBridgeViewBase.CvCameraViewListener2{
    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        // get current camera frame as OpenCV Mat object
        val frame = inputFrame.gray()

        // native call to process current camera frame
        adaptiveThresholdFromJNI(frame.nativeObjAddr)

        // return processed frame for live preview
        return frame
    }

    private external fun adaptiveThresholdFromJNI(matAddr: Long)
}