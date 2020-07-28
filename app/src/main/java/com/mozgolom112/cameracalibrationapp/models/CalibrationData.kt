package com.mozgolom112.cameracalibrationapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.opencv.core.Mat
@Parcelize
data class CalibrationData(
    val CameraMat: Long,
    val DistMat: Long,
    val CameraMatDump: String,
    val DistMatDump: String
) : Parcelable