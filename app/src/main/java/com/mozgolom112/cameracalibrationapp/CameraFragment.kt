package com.mozgolom112.cameracalibrationapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.graphics.scaleMatrix
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_camera.*
import org.opencv.android.OpenCVLoader

private val TAG = "MainActivity"
private val CAMERA_PERMISSION_REQUEST = 1

class CameraFragment() : Fragment(R.layout.fragment_camera){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "called onCreate")

        // Permissions for Android 6+
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST)



        javacamvCamera.apply {
            visibility = SurfaceView.VISIBLE
            //What do with each frame
            setCvCameraViewListener(CvCameraViewListener2)
        }
    }

    override fun onPause() {
        super.onPause()
        javacamvCamera.disableView()
    }

    override fun onResume() {
        super.onResume()
        javacamvCamera.enableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        javacamvCamera.disableView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                val isPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (isPermissionGranted) {
                    javacamvCamera.setCameraPermissionGranted()
                } else {
                    val message = "Camera permission was not granted"
                    Log.e(TAG, message)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Log.e(TAG, "Unexpected permission request")
            }
        }
    }

    init {
        initOpenCv()
    }

    private fun initOpenCv(){
        val isLoadOpenCVSuccess = OpenCVLoader.initDebug()
        if(isLoadOpenCVSuccess) initMyNativeLib()
        Log.d(TAG, "isLoadOpenCVSuccess: $isLoadOpenCVSuccess")
    }

    private fun initMyNativeLib(){
        System.loadLibrary("native-lib")
    }
}