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
    Size board_size;

    Size image_size;

    int square_size;

    vector<vector<Point2f>> image_points;

};
//TODO('Remove into cpp file')
void CameraCalibration::setSizes(const Size &board, const Size &image, int square) {
    board_size = board;
    //TODO('If we use repre_cast, we got not right values')
    image_size = image;
    square_size = square;
}

void CameraCalibration::calcBoardCornerPositions(vector<Point3f> &obj) {
    obj.clear();
    for (int i = 0; i < board_size.height; ++i)
        for (int j = 0; j < board_size.width; ++j)
            obj.emplace_back(j * square_size, i * square_size, 0);
}

vector<Mat> CameraCalibration::calibrate() {

    float grid_width = (float)square_size * (board_size.width - 1.f);

    vector<vector<Point3f>> object_points(1);
    calcBoardCornerPositions(object_points[0]);
    object_points[0][board_size.width - 1].x = object_points[0][0].x + grid_width;
    object_points.resize(image_points.size(), object_points[0]);

    Mat camera_matrix = Mat::eye(3, 3, CV_64F);
    Mat dist_coeffs = Mat::zeros(8, 1, CV_64F);

    vector<Mat> r_vecs, t_vecs;
    calibrateCamera(object_points, image_points, image_size,
                    camera_matrix, dist_coeffs, r_vecs, t_vecs);

    vector<Mat> results {camera_matrix, dist_coeffs};
    return results;
}

void CameraCalibration::undistortImage(Mat &frame, const Mat &matrix, const Mat &dist) {
    Mat temp = frame.clone();
    undistort(temp, frame, matrix, dist);
}

int CameraCalibration::identifyChessboard(Mat &frame, bool mode_take_snapshot) {
    Mat gray_frame;
    cvtColor(frame, gray_frame, COLOR_BGR2GRAY);

    vector<Point2f> detected_corners; //this will be filled by the detected corners

    bool pattern_found = findChessboardCorners(gray_frame, board_size, detected_corners,
                                               CALIB_CB_ADAPTIVE_THRESH
                                               + CALIB_CB_NORMALIZE_IMAGE
                                               + CALIB_CB_FAST_CHECK);

    if (pattern_found) {
        cornerSubPix(gray_frame, detected_corners,
                     Size(11, 11),
                     Size(-1,-1),
                     TermCriteria( TermCriteria::EPS+TermCriteria::COUNT, 30, 0.0001 ));
        if(mode_take_snapshot){
            image_points.push_back(detected_corners);
        }

        drawChessboardCorners(frame, board_size, Mat(detected_corners), pattern_found);
    }

    return image_points.size();
}

#endif //CAMERA_CALIBRATION_APP_CAMERA_CALIBRATION_H

