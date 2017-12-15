//
// Created by pdg on 2017-12-08.
//
#include <com_example_pdg_opencvexam_NativeClass.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <jni.h>
#include <vector>
#include <android/log.h>
#include <list>

using namespace cv;
using namespace std;


#define printStr(...)__android_log_print(ANDROID_LOG_INFO, "LOG_STR", __VA_ARGS__)
int rectC;
int rectW;
int rectH;

#define printRectState(...)__android_log_print(ANDROID_LOG_INFO, "LOG_RECT", "C = %d / W = %d / H = %d", rectC, rectW, rectH)

int X1, X2;
int Y1, Y2;
#define printRectXY(...)__android_log_print(ANDROID_LOG_INFO, "LOG_RECT", "/x1 : %d/x2 : %d/y1 : %d/y2 : %d/", X1, X2, Y1, Y2)

int matCount;
int matCols;
int matRows;
#define printMATSTATE(...)__android_log_print(ANDROID_LOG_INFO, "LOG_MAT", "/c : %d/rows : %d/cols : %d/", matCount, matRows ,matCols)

int mi;
int mj;
int mx;
int my;
#define printSIZE(...)__android_log_print(ANDROID_LOG_INFO, "LOG_SIZE", "i : %d/ j : %d / x : %d / y : %d", mi, mj, mx, my)

//region printStr
JNIEXPORT jstring JNICALL Java_com_example_pdg_opencvexam_NativeClass_getStringFromNative
        (JNIEnv *env, jobject obj) {
    return env->NewStringUTF("Hello Native Class");
}
//endregion

JNIEXPORT void JNICALL
Java_com_example_pdg_opencvexam_NativeClass_colorToGray(JNIEnv *, jobject, jlong matAddrInput,
                                                        jlong matAddrResult) {
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_RGBA2GRAY);
}

Mat thresh_callback(Mat mat) {
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;

    int thresh = 100;
    int max_thresh = 255;
    RNG rng(12345);

    threshold(mat, mat, thresh, max_thresh, THRESH_BINARY);

    findContours(mat, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));

    vector<RotatedRect> minRect(contours.size());
    vector<RotatedRect> minEllipse(contours.size());

    for (int i = 0; i < contours.size(); i++) {
        minRect[i] = minAreaRect(Mat(contours[i]));

        if (contours[i].size() > 5) {
            minEllipse[i] = fitEllipse(Mat(contours[i]));
        }
    }

    Mat drawing = Mat::zeros(mat.size(), CV_8UC3);

    for (int i = 0; i < contours.size(); i++) {
        Scalar color = Scalar(rng.uniform(0, 255), rng.uniform(0, 255), rng.uniform(0, 255));

        drawContours(drawing, contours, i, color, 1, 8, vector<Vec4i>(), 0, Point());

        ellipse(drawing, minEllipse[i], color, 2, 8);

        Point2f rect_point[4];
        minRect[i].points(rect_point);

        for (int j = 0; j < 4; j++) {
            line(drawing, rect_point[j], rect_point[(j + 1) % 4], color, 1, 8);
        }
    }

    return drawing;
}

Mat findArea(Mat inputMat, Rect rect) {
    Mat findMat = inputMat(rect);

    return findMat;
}

JNIEXPORT jobjectArray
Java_com_example_pdg_opencvexam_NativeClass_exampleMain(JNIEnv *env, jobject instance,
                                                        jlong matAddrInput, jlong matAddrResult) {

    Mat &inputMat = *(Mat *) matAddrInput;
    Mat &resultMat = *(Mat *) matAddrResult;

    Mat grayMat;

    cvtColor(inputMat, grayMat, CV_RGB2GRAY);

    grayMat = grayMat > 127;

    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;

    Mat kernel = Mat::ones(10, 5, CV_8U);
    erode(grayMat, grayMat, kernel, Point(-1, -1), 2);

    vector<Mat> mats;
    vector<Mat>::iterator matVectoriter;

    vector<Rect> rects;

    int findCount;

    findContours(grayMat, contours, hierarchy, RETR_LIST, CHAIN_APPROX_NONE, Point(0, 0));

    for (int i = 0; i < contours.size(); i++) {

        Scalar color = Scalar(255, 0, 0);
        Rect rect = boundingRect(Mat(contours[i]));

        if (rect.width > 0 &&rect.height > 49 && rect.height < 1000) {

            findCount++;
            rectangle(inputMat, rect, color);

            Mat roi = inputMat(rect);

            mats.push_back(roi);

        }
    }

    jclass matClass = env -> FindClass("org/opencv/core/Mat");
    jmethodID jMatCons = env->GetMethodID(matClass,"<init>","()V");
    jmethodID getPtrMethod = env->GetMethodID(matClass, "getNativeObjAddr", "()J");

    if(env -> ExceptionOccurred())
        return NULL;

    jobjectArray matArray = env -> NewObjectArray((jsize)mats.size(), matClass, 0);

    for(int i=0; i<(int)mats.size(); i++){
        jobject jMat = env -> NewObject(matClass, jMatCons);
        Mat & native_image= *(Mat*)env->CallLongMethod(jMat, getPtrMethod);
        native_image=mats[i];

        env ->SetObjectArrayElement(matArray, i, jMat);
//        matCount = i;
//        matCols = mats[i].cols;
//        matRows = mats[i].rows;
//
//        printMATSTATE(matCount, matRows, matCols);


    }

    resultMat = mats[1];

    return matArray;
}



//region 선택한 rect영역 추출
//    Rect rect(100, 100, 200, 200);
//
//    rectangle(inputMat, rect, Scalar(255), 1, 8, 0);
//
//    Mat roi = inputMat(rect);
//endregion
//region 사각형 그리기
//    Rect rect(200, 200, 50, 50);
//
//    Point point0 = Point(rect.x, rect.y);
//    Point point1 = Point(rect.x + rect.width, rect.y);
//    Point point2 = Point(rect.x + rect.width, rect.y + rect.height);
//    Point point3 = Point(rect.x , rect.y + rect.height);
//
//    rectangle(matInput, rect, Scalar::all(255));
//
//    circle( matInput, point0, 10, Scalar( 0, 0, 255) );
//    circle( matInput, point1, 10, Scalar( 0, 0, 255) );
//    circle( matInput, point2, 10, Scalar( 0, 0, 255) );
//    circle( matInput, point3, 10, Scalar( 0, 0, 255) );
//endregion












