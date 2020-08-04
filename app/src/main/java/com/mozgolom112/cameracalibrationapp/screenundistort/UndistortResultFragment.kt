package com.mozgolom112.cameracalibrationapp.screenundistort

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mozgolom112.cameracalibrationapp.R
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_undistort_result.*


private const val CAMERA_PERMISSION_REQUEST = 1

class UndistortResultFragment : Fragment(R.layout.fragment_undistort_result){
    private val camera: UndistortCameraViewModel by lazy { UndistortCameraViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = UndistortResultFragmentArgs.fromBundle(requireArguments()).calibrationData
        camera.calibrationData = arguments

        javacamvUndistort.apply {
            visibility = SurfaceView.VISIBLE
            setCvCameraViewListener(camera)
        }

        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
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
                    javacamvUndistort.setCameraPermissionGranted()
                } else {
                    val message = "Camera permission was not granted"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Log.e("CameraFragment", "Unexpected permission request")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        javacamvUndistort.disableView()
    }

    override fun onResume() {
        super.onResume()
        javacamvUndistort.enableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        javacamvUndistort.disableView()
    }

}