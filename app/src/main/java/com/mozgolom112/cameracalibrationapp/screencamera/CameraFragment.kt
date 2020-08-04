package com.mozgolom112.cameracalibrationapp.screencamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mozgolom112.cameracalibrationapp.R
import com.mozgolom112.cameracalibrationapp.models.CalibrationData
import kotlinx.android.synthetic.main.fragment_camera.*

private val CAMERA_PERMISSION_REQUEST = 1

class CameraFragment() : Fragment(R.layout.fragment_camera){

    private val camera: CameraViewModel by lazy { CameraViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Permissions for Android 6+
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )

        setObservers()
        setOnClickListeners()

        javacamvCamera.apply {
            visibility = SurfaceView.VISIBLE
            //What do with each frame
            setCvCameraViewListener(camera)
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
                    Log.e("CameraFragment", message)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Log.e("CameraFragment", "Unexpected permission request")
            }
        }
    }


    private fun setObservers() {
        //TODO("Not yet implemented")
    }

    private fun setOnClickListeners() {
        btnMakeSnapshot.setOnClickListener {
            camera.takeSnapshotClick = true
        }
        btnCalibrate.setOnClickListener {
           val result = camera.calibrateCameraClick()
            navigateToCalibrationFrag(result)
        }
    }

    private fun navigateToCalibrationFrag(result: CalibrationData) {
        findNavController().navigate(CameraFragmentDirections.actionCameraToCalibrationResult(result))
    }

}