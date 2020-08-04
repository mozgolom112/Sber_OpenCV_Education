# Android app for camera calibration
This application is designed to calibrate the main camera of the phone using the chessboard. It allows:
-	Calibrate the camera (only Intrinsic parameters and distortion coefficients);
-	Show the result of the calibration application in the form of processing the input video stream;
-	Show calibration matrixes.

# Overview:
- Getting started
- Camera calibration theory.
- Using OpenCV
- Using JNI
- Changing OpenCV class to fix camera orientation issue
- Results

# Getting started:
This application uses [OpenCV library for Android](https://sourceforge.net/projects/opencvlibrary/files/4.4.0/opencv-4.4.0-android-sdk.zip/download). You can download it along with the project, or connect it yourself. If you will install the OpenCV library yourself, then make sure that you:
1. Installed version 4.4.0
2. Replaced the file “opencv / java / src / org / opencv / android / CameraBridgeViewBase.java” with the changed [file from the repository](https://github.com/mozgolom112/Sber_OpenCV_Education/blob/exercise-1-cam-calibration/opencv/java/src/org/opencv/android/CameraBridgeViewBase.java)
3. In the file [build.gradle](https://github.com/mozgolom112/Sber_OpenCV_Education/blob/exercise-1-cam-calibration/app/build.gradle) at the app level, the correct line was written:
    a. For Windows systems:
    `arguments "-DOpenCV_DIR =" + rootProject.projectDir.path + "/ opencv / native"`
    b. For Linux systems:
    `arguments "-DOpenCV_DIR = \" + rootProject.projectDir.path + "/ opencv / native"`

JNI will be used to run C ++ code. Make sure your IDE has [NDK, CMake and LLDB](https://developer.android.com/studio/projects/install-ndk)

# Camera calibration theory:
Camera calibration is necessary in order to obtain complete information (specifications or calculation factors) about the camera, which is necessary to determine the exact relationship between a 3D point in the real world and the corresponding 2D image, projection (each pixel) in the image captured by the calibrated camera.
Usually, for this it is necessary to obtain two types of parameters: **extrinsic**, which refers to the orientation (rotation and translation) of the camera with respect to some world coordinate system, and **intrinsic**, due to which the point is projected onto the image plane.

![extrinsic_param_math.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/extrinsic_param_math.png)
where R – rotation matrix and t – translation matrix. They form a group of extrinsic parameters.

![intristic_param_math.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/intristic_param_math.png)
![intristic_matrix_math.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/intristic_matrix_math.png)
where K - matrix of intrinsic parameters of the camera,
-	Fx и Fy - are the x and y focal lengths (they are usually the same)
-	Cx и Сy - are the x and y coordinates of the optical center in the image plane.
- Gamma - is the skew between the axes (it is usually 0)

![illustration_of_coordinate_pict.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/illustration_of_coordinate_pict.png)

Real lenses usually have some distortion, mostly radial distortion, and slight tangential distortion. So, the above model is extended as:

![dist_param_math.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/dist_param_math.png)

k_1, k_2, k_3, k_4, k_5, and k_6 are radial distortion coefficients. p_1 and p_2 are tangential distortion coefficients. Higher-order coefficients are not considered in OpenCV.

# Using OpenCV

Thanks to the OpenCV library, we make the code simpler. The functions of searching for a chessboard pattern, calibration camera, making undistorted image and others are already in.
The main idea is to get several frames from different angles, as well as at different distances, at which **the chessboard pattern** is recognized and, based on the recognition data and the frame, determine the internal camera parameters and distortion coefficients. And then apply them to get a frame without distortion.

This project uses the following OpenCV functions:

> [calibrateCamera](https://docs.opencv.org/2.4/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera)(object_points, image_points, image_size, camera_matrix, dist_coeffs, r_vecs, t_vecs)

Finds the camera intrinsic and extrinsic parameters from several views of a calibration pattern, in our case a chessboard.

> [findChessboardCorners](https://docs.opencv.org/2.4/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findchessboardcorners)(gray_frame, board_size, detected_corners, CALIB_CB_ADAPTIVE_THRESH + CALIB_CB_NORMALIZE_IMAGE + CALIB_CB_FAST_CHECK)

Finds the positions of internal corners of the chessboar. The function returns a non-zero value if all of the corners are found. You have to pass the size of your chessboard to the entrance.

> [cornerSubPix](https://docs.opencv.org/2.4/modules/imgproc/doc/feature_detection.html#cornersubpix)(gray_frame, detected_corners, Size(11, 11), Size(-1,-1), TermCriteria( TermCriteria::EPS+TermCriteria::COUNT, 30, 0.0001))

Refines the corner locations.

> [drawChessboardCorners](https://docs.opencv.org/2.4/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#drawchessboardcorners)(frame, board_size, Mat(detected_corners), pattern_found);

Renders the detected chessboard corners.

> [undistort](https://docs.opencv.org/2.4/modules/imgproc/doc/geometric_transformations.html#undistort)(temp, frame, matrix, dist)

Transforms an image to compensate for lens distortion.

# Using JNI
Since the functions are written in C ++, and the UI logic of the application is in Kotlin, we need a tool to run such code from Kotlin. For solving this problem, we will use the **Java Native Interface (JNI)**.
 ##### CMakeLists

The [file](https://github.com/mozgolom112/Sber_OpenCV_Education/blob/exercise-1-cam-calibration/app/src/main/cpp/CMakeLists.txt) contains the information needed to build CMake. If you want to use your .cpp files in native-lib, add them to the add_library line for native-lib:
`add_library(native-lib SHARED native-lib.cpp YOUR_LIBRARY.cpp)`

  ##### Native-lib
We need to create a special native-lib library – which contain C ++ functions. It is mandatory to include jni.h and define the tag:
`
#include <jni.h>`
`#define TAG "NativeLib"
`
If you need to use your own libraries, do not forget to include them in [CMakeLists](https://github.com/mozgolom112/Sber_OpenCV_Education/blob/exercise-1-cam-calibration/app/src/main/cpp/CMakeLists.txt) and include them in native-lib using the directive [#include](https://docs.microsoft.com/ru-ru/cpp/preprocessor/hash-include-directive-c-cpp?view=vs-2019)
Now you need to describe the functions following the following pattern:
```
extern "C"
JNIEXPORT void JNICALL
Java_package_name_*Class_name*_*fun_name(JNIEnv *env, //your params …
){
    //Useful code
}
```
Remember that you cannot transfer whatever you want. [All data types are here](https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html). To avoid this problem, pass references to values. In Kotlin files it looks like this:
```
val frame = inputFrame.rgba() // Тип Mat
val frameAddr = frame.nativeObjAddr //take address of frame
```
In native-lib, we get an object through a pointer:
```
Mat &frame = *(Mat *) frame_addr;
```
Example from the project:
```
extern "C"
JNIEXPORT void JNICALL
Java_com_mozgolom112_cameracalibrationapp_screenundistort_UndistortCameraViewModel_undistort(
        JNIEnv *env, jobject thiz, jlong frame_addr, jlong matrix_addr, jlong dist_addr) {
    Mat &frame = *(Mat *) frame_addr;
    Mat &matrix = *(Mat *) matrix_addr;
    Mat &dist = *(Mat *) dist_addr;

    cameraCalibration.undistortImage(frame, matrix, dist);
}
```

 #### Kotlin

If we want to call a function from a .kt file, we need to specify the same function with the same signature as defined in native-lib with the **external** modifier:
`external fun undistort(frameAddr: Long, matrixAddr: Long, distAddr: Long)`
Remember that we cannot pass any type into native-lib except this [list](https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html)

We also need to explicitly tell the system to load native-lib. Do this before using these features.
```
System.loadLibrary("native-lib")
```

 # Changing OpenCV class to fix camera orientation issue

Unfortunately, OpenCV camera does not set the camera to portrait mode by default. To fix the problem with orientation, you need to change the deliverAndDrawFrame function in the [CameraBridgeViewBase.java](https://github.com/mozgolom112/Sber_OpenCV_Education/blob/exercise-1-cam-calibration/opencv/java/src/org/opencv/android/CameraBridgeViewBase.java) file.

The tutorial is written [here](https://heartbeat.fritz.ai/working-with-the-opencv-camera-for-android-rotating-orienting-and-scaling-c7006c3e1916)

# Result
###  Virtual Device Pixel 3:
Detection of chessboard’s angles:

![detection_chessboard_pict1.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/detection_chessboard_pict1.png)

![detection_chessboard_pict2.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/detection_chessboard_pict2.png)

Intrinsic parameters and distortion coefficients:

![screen_param_pict.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/screen_param_pict.png)

Undistort images:
![undistort_pict1.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/undistort_pict1.png)

![undistort_pict2.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/undistort_pict2.png)

### Real Phone HMD Global Nokia 6.1 (API 29):
Detection of chessboard’s angles:
![detection_chessboard_pict1_real_phone.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/detection_chessboard_pict1_real_phone.png)

![detection_chessboard_pict2_real_phone.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/detection_chessboard_pict2_real_phone.png)
Intrinsic parameters and distortion coefficients:

![screen_param_pict_real_phone.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/screen_param_pict_real_phone.png)

If few screenshots are taken, then there will be defects:

![undistort_pict1_real_bad.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/undistort_pict1_real_bad.png)

An example when many screenshots are taken:
![undistort_pict1_real_good.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/undistort_pict1_real_good.png)

![undistort_pict2_real_good.png](https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/undistort_pict2_real_good.png)
<img src="https://raw.githubusercontent.com/mozgolom112/Sber_OpenCV_Education/exercise-1-cam-calibration/readme_media/detection_chessboard_pict1.png" width="400" height="790">
