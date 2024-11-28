#ifndef CAMERA_CALIBRATION_APP_CAMERA_CALIBRATION_H
#define CAMERA_CALIBRATION_APP_CAMERA_CALIBRATION_H

#include <jni.h>
#include <opencv2/core/types.hpp>
#include <opencv2/core.hpp>
#include <opencv2/core/utility.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/calib3d.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/videoio.hpp>
#include <opencv2/highgui.hpp>

using namespace cv;
using namespace std;

class CameraCalibration {
public:
    CameraCalibration() {
        board_size = Size();
        image_size = Size();
        square_size = 0;

        image_points = vector<vector<Point2f>>();
    }

    void setSizes(const Size &board, const Size &image, int square);

    int identifyChessboard(Mat &frame, bool mode_take_snapshot);

    void calcBoardCornerPositions(vector<Point3f> &obj);

    vector<Mat> calibrate();

    void undistortImage(Mat &frame, const Mat &matrix, const Mat &dist);

private:
    int square_size;

    Size board_size;

    Size image_size;

    vector<vector<Point2f>> image_points;

};

#endif //CAMERA_CALIBRATION_APP_CAMERA_CALIBRATION_H

