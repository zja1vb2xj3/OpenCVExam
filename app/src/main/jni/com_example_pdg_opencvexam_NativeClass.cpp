//
// Created by pdg on 2017-12-08.
//
#include <com_example_pdg_opencvexam_NativeClass.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <jni.h>
#include <vector>

using namespace cv;
using namespace std;

//region printStr
JNIEXPORT jstring JNICALL Java_com_example_pdg_opencvexam_NativeClass_getStringFromNative
        (JNIEnv *env, jobject obj) {
    return env->NewStringUTF("Hello Native Class");
}
//endregion

JNIEXPORT void JNICALL Java_com_example_pdg_opencvexam_NativeClass_colorToGray(JNIEnv *, jobject, jlong matAddrInput, jlong matAddrResult) {
    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, CV_RGBA2GRAY);
}

int thresh = 100;
int max_thresh = 255;
RNG rng(12345);

JNIEXPORT void JNICALL
Java_com_example_pdg_opencvexam_NativeClass_getArea(JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_BGR2GRAY);
//    blur(matResult, matResult, Size(3, 3));

//    thresh_callback(matResult);
}

//void thresh_callback(Mat mat) {
//    vector<vector<Point> > contours;
//    vector<Vec4i> hierarchy;
//    Mat thresholdOutput;
//
//    threshold(mat, thresholdOutput, thresh, 255, CV_THRESH_BINARY);
//
//    findContours(thresholdOutput, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE,
//                 Point(0, 0));
//
//    vector<vector<Point> > contour_Poly(contours.size());
//    vector<Rect> boundRect(contours.size());
//    vector<Point2f> center(contours.size());
//    vector<float> radius(contours.size());
//
//    for (int i = 0; i < contours.size(); i++) {
//        approxPolyDP(Mat(contours[i]), contour_Poly[i], 3, true);
//        boundRect[i] = boundingRect(Mat(contour_Poly[i]));
//        minEnclosingCircle(contour_Poly[i], center[i], radius[i]);
//    }
//
//    Mat drawing = Mat::zeros(thresholdOutput.size(), CV_8UC3);
//
//    for (int i = 0; i < contours.size(); i++) {
//        Scalar color = Scalar(rng.uniform(0, 255), rng.uniform(0, 255),
//                              rng.uniform(0, 255));
//        drawContours(drawing, contour_Poly, i, color, 1, 8, vector<Vec4i>(), 0, Point());
//        rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 2, 8, 0);
//        circle(drawing, center[i], (int) radius[i], color, 2, 8, 0);
//    }
//}




