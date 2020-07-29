#include <jni.h>
#include <android/log.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "camera_calibration.h"

#define TAG "NativeLib"

using namespace std;
using namespace cv;

//Создаем глобальную переменную
CameraCalibration cameraCalibration = CameraCalibration();

extern "C" JNIEXPORT void JNICALL
Java_com_mozgolom112_cameracalibrationapp_CvCameraViewListener2_adaptiveThresholdFromJNI(
        JNIEnv *env, jobject instance, jlong matAddr) {
   //Example
    // get Mat from raw address
    Mat &mat = *(Mat *) matAddr;

    clock_t begin = clock();

    cv::adaptiveThreshold(mat, mat, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 21, 5);

    // log computation time to Android Logcat
    double totalTime = double(clock() - begin) / CLOCKS_PER_SEC;
    __android_log_print(ANDROID_LOG_INFO, TAG, "adaptiveThreshold computation time = %f seconds\n",
                        totalTime);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mozgolom112_cameracalibrationapp_screencamera_CameraViewModel_setSizes(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jint chessboard_horizontal_amount,
                                                                                jint chessboard_vertical_amount,
                                                                                jlong imageAddr,
                                                                                jint square_size
) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "Insize setSizes\n");
    Mat &image = *(Mat *) imageAddr;

    cameraCalibration.setSizes(

            Size(chessboard_horizontal_amount, chessboard_vertical_amount),
            image.size(), //TODO('Насколько более затратно, чем передать ссылку на изображение?')
            square_size);

}
extern "C" JNIEXPORT jint JNICALL
Java_com_mozgolom112_cameracalibrationapp_screencamera_CameraViewModel_identifyChessboard(
        JNIEnv *env, jobject thiz, jlong frameAddr, jboolean take_snapshot_click) {
    Mat &frame = *(Mat *) frameAddr;
    __android_log_print(ANDROID_LOG_INFO, TAG, "Insize identifyChessboard\n");

    return cameraCalibration.identifyChessboard(frame,
                                               reinterpret_cast<bool &>(take_snapshot_click));
}extern "C"
JNIEXPORT void JNICALL
Java_com_mozgolom112_cameracalibrationapp_screencamera_CameraViewModel_calibrate(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jlong matrix_mat_addr,
                                                                                 jlong dist_mat_addr) {

    Mat &matrix = *(Mat *) matrix_mat_addr;
    Mat &dist = *(Mat *) dist_mat_addr;
    vector<Mat> result = cameraCalibration.calibrate();
    matrix = result[0];
    dist = result[1];
}