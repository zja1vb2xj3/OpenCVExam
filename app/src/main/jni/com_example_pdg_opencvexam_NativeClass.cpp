//
// Created by pdg on 2017-12-08.
//
#include <com_example_pdg_opencvexam_NativeClass.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <jni.h>
#include <vector>
#include <android/log.h>

using namespace cv;
using namespace std;

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

int thresh = 100;
int max_thresh = 255;
RNG rng(12345);

Mat thresh_callback(Mat mat) {
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    Mat thresholdOutput;

    threshold(mat, thresholdOutput, thresh, max_thresh, CV_THRESH_BINARY);

    findContours(thresholdOutput, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE,
                 Point(0, 0));

    vector<vector<Point> > contour_Poly(contours.size());
    vector<Rect> boundRect(contours.size());
    vector<Point2f> center(contours.size());
    vector<float> radius(contours.size());

    for (int i = 0; i < contours.size(); i++) {
        approxPolyDP(Mat(contours[i]), contour_Poly[i], 3, true);
        boundRect[i] = boundingRect(Mat(contour_Poly[i]));
        minEnclosingCircle(contour_Poly[i], center[i], radius[i]);
    }

    Mat drawing = Mat::zeros(thresholdOutput.size(), CV_8UC3);

    for (int i = 0; i < contours.size(); i++) {
        Scalar color = Scalar(rng.uniform(0, 255), rng.uniform(0, 255),
                              rng.uniform(0, 255));

//        drawContours(drawing, contour_Poly, i, color, 1, 8, vector<Vec4i>(), 0, Point());
        rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 2, 10, 0);
//        circle(drawing, center[i], (int) radius[i], color, 2, 8, 0);
    }

    return drawing;
}

JNIEXPORT void JNICALL
Java_com_example_pdg_opencvexam_NativeClass_getArea(JNIEnv *env, jobject instance,
                                                    jlong matAddrInput, jlong matAddrResult) {
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    Mat grayMat;

    cvtColor(matInput, grayMat, CV_BGR2GRAY);

    Mat blur = matInput;
    Mat binary4c = matInput;

    GaussianBlur(grayMat, blur, Size(5, 5), 0);

    float th = 128;
    float thMax = 255;
    threshold(blur, binary4c, th, thMax, THRESH_BINARY);

    matResult = thresh_callback(binary4c);
}

double angle(Point pt1, Point pt2, Point pt0) {
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1*dx2 + dy1*dy2) / sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

void find_squares(Mat image, vector<vector<Point> > squares)
{
    // blur will enhance edge detection
    Mat blurred(image);
    Mat dst;
    medianBlur(image, dst, 9);

    Mat gray0(dst.size(), CV_8U), gray;
    vector<vector<Point> > contours;

    // find squares in every color plane of the image
    for (int c = 0; c < 3; c++)
    {
        int ch[] = { c, 0 };
        mixChannels(&dst, 1, &gray0, 1, ch, 1);

        // try several threshold levels
        const int threshold_level = 2;
        for (int l = 0; l < threshold_level; l++)
        {
            // Use Canny instead of zero threshold level!
            // Canny helps to catch squares with gradient shading
            if (l == 0)
            {
                Canny(gray0, gray, 10, 20, 3); //

                // Dilate helps to remove potential holes between edge segments
                dilate(gray, gray, Mat(), Point(-1, -1));
            }
            else
            {
                gray = gray0 >= (l + 1) * 255 / threshold_level;
            }

            // Find contours and store them in a list
            findContours(gray, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

            // Test contours
            vector<Point> approx;
            for (size_t i = 0; i < contours.size(); i++)
            {
                // approximate contour with accuracy proportional
                // to the contour perimeter
                approxPolyDP(Mat(contours[i]), approx, arcLength(Mat(contours[i]), true)*0.02, true);

                // Note: absolute value of an area is used because
                // area may be positive or negative - in accordance with the
                // contour orientation
                if (approx.size() == 4 &&
                    fabs(contourArea(Mat(approx))) > 1000 &&
                    isContourConvex(Mat(approx)))
                {
                    double maxCosine = 0;

                    for (int j = 2; j < 5; j++)
                    {
                        double cosine = fabs(angle(approx[j % 4], approx[j - 2], approx[j - 1]));
                        maxCosine = MAX(maxCosine, cosine);
                    }

                    if (maxCosine < 0.3)
                        squares.push_back(approx);
                }
            }
        }
    }
}

vector<Point> extremePoints(vector<Point>pts)
{
    int  xmin = 0, ymin = 0, xmax = -1, ymax = -1, i;
    Point ptxmin, ptymin, ptxmax, ptymax;

    Point pt = pts[0];

    ptxmin = ptymin = ptxmax = ptymax = pt;
    xmin = xmax = pt.x;
    ymin = ymax = pt.y;

    for (size_t i = 1; i < pts.size(); i++)
    {
        pt = pts[i];

        if (xmin > pt.x)
        {
            xmin = pt.x;
            ptxmin = pt;
        }


        if (xmax < pt.x)
        {
            xmax = pt.x;
            ptxmax = pt;
        }

        if (ymin > pt.y)
        {
            ymin = pt.y;
            ptymin = pt;
        }

        if (ymax < pt.y)
        {
            ymax = pt.y;
            ptymax = pt;
        }
    }
    vector<Point> res;
    res.push_back(ptxmin);
    res.push_back(ptxmax);
    res.push_back(ptymin);
    res.push_back(ptymax);
    return res;
}

void sortCorners(vector<Point2f> corners)
{
    vector<Point2f> top, bot;
    Point2f center;
    // Get mass center
    for (int i = 0; i < corners.size(); i++)
        center += corners[i];
    center *= (1. / corners.size());

    for (int i = 0; i < corners.size(); i++)
    {
        if (corners[i].y < center.y)
            top.push_back(corners[i]);
        else
            bot.push_back(corners[i]);
    }
    corners.clear();

    if (top.size() == 2 && bot.size() == 2) {
        Point2f tl = top[0].x > top[1].x ? top[1] : top[0];
        Point2f tr = top[0].x > top[1].x ? top[0] : top[1];
        Point2f bl = bot[0].x > bot[1].x ? bot[1] : bot[0];
        Point2f br = bot[0].x > bot[1].x ? bot[0] : bot[1];


        corners.push_back(tl);
        corners.push_back(tr);
        corners.push_back(br);
        corners.push_back(bl);
    }
}

JNIEXPORT void JNICALL Java_com_example_pdg_opencvexam_NativeClass_exampleMain(JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {

    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    int largest_area = 0;
    int largest_contour_index = 0;
    Rect bounding_rect;
    Mat src, edges;

    src = matInput;

    vector<vector<Point> > contours;

    find_squares(src, contours);

    for(int i=0; i<contours.size(); i++){
        double a = contourArea(contours[i], false);

        if (a>largest_area) {
            largest_area = a;
            largest_contour_index = i;                //Store the index of largest contour
            bounding_rect = boundingRect(contours[i]); // Find the bounding rectangle for biggest contour
            RotatedRect minRect = minAreaRect(Mat(contours[i]));
        }
    };

    vector<Point> corner_points = extremePoints(contours[largest_contour_index]);
    vector<Point2f> corners;

    corners.push_back(corner_points[0]);
    corners.push_back(corner_points[1]);
    corners.push_back(corner_points[2]);
    corners.push_back(corner_points[3]);

    sortCorners(corners);

    Mat quad = Mat::zeros(norm(corners[1]-corners[2]), norm(corners[2]-corners[3]), CV_8UC3);

    vector<Point2f> quad_pts;
    quad_pts.push_back(Point2f(0, 0));
    quad_pts.push_back(Point2f(quad.cols, 0));
    quad_pts.push_back(Point2f(quad.cols, quad.rows));
    quad_pts.push_back(Point2f(0, quad.rows));

    Mat transmtx = getPerspectiveTransform(corners, quad_pts);
    warpPerspective(src, quad, transmtx, quad.size());
    resize(quad, quad, Size(), 0.25, 0.25);

    polylines(src, contours[largest_contour_index], true, Scalar(0, 0, 255), 2);

    resize(src, src, Size(), 0.5, 0.5);

    matResult = src;

}







