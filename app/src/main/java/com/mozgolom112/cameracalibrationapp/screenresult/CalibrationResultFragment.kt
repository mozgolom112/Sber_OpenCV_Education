package com.mozgolom112.cameracalibrationapp.screenresult

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.mozgolom112.cameracalibrationapp.R
import com.mozgolom112.cameracalibrationapp.models.CalibrationData
import kotlinx.android.synthetic.main.fragment_calibration_result.*

class CalibrationResultFragment : Fragment(R.layout.fragment_calibration_result) {
    private lateinit var calibrationData: CalibrationData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calibrationData = CalibrationResultFragmentArgs.fromBundle(requireArguments()).calibrationData

        setTextView()
        setOnClickListeners()
    }

    private fun setTextView() {
        txtvCameraMatrix.text = calibrationData.CameraMatDump
        txtvDistMatrix.text = calibrationData.DistMatDump
    }

    private fun setOnClickListeners() {
        btnShowUndistortImage.setOnClickListener {
            findNavController().navigate(CalibrationResultFragmentDirections.actionCalibrationResultToUndistortResultFragment(calibrationData))
        }
    }
}