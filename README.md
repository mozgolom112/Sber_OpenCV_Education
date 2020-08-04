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
This application uses [OpenCV library for Android]. You can download it along with the project, or connect it yourself. If you will install the OpenCV library yourself, then make sure that you:
1. Installed version 4.4.0
2. Replaced the file “opencv / java / src / org / opencv / android / CameraBridgeViewBase.java” with the changed [file from the repository]
3. In the file [build.gradle] at the app level, the correct line was written:
    a. For Windows systems:
    `arguments "-DOpenCV_DIR =" + rootProject.projectDir.path + "/ opencv / native"`
    b. For Linux systems:
    `arguments "-DOpenCV_DIR = \" + rootProject.projectDir.path + "/ opencv / native"`

JNI will be used to run C ++ code. Make sure your IDE has [NDK, CMake and LLDB]

# Camera calibration theory:
Camera calibration is necessary in order to obtain complete information (specifications or calculation factors) about the camera, which is necessary to determine the exact relationship between a 3D point in the real world and the corresponding 2D image, projection (each pixel) in the image captured by the calibrated camera.
Usually, for this it is necessary to obtain two types of parameters: **extrinsic**, which refers to the orientation (rotation and translation) of the camera with respect to some world coordinate system, and **intrinsic**, due to which the point is projected onto the image plane.