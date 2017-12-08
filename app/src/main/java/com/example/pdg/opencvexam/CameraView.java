package com.example.pdg.opencvexam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.Feature2D;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.BreakIterator;

/**
 * Created by pdg on 2017-12-06.
 */

public class CameraView extends JavaCameraView implements JavaCameraView.CvCameraViewListener2 {
    private final String CLASSNAME = getClass().getSimpleName();
    private Context context;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        cameraInit();
    }

    private void cameraInit() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
//        Log.i(CLASSNAME, "onCameraFrame");

        final Mat rgbaMat = inputFrame.rgba();
        final Mat grayMat = inputFrame.gray();

        int frameWidth = rgbaMat.rows();
        int frameHeight = rgbaMat.cols();

        System.out.println("frameWidth : " + frameWidth);
        System.out.println("frameHeight : " + frameHeight);

        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);// 선명한 gray

        Imgproc.GaussianBlur(grayMat, grayMat, new Size(5,5), 0, 0);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                System.out.println("onAutoFocus");
                if (success == true) {
//                    onFocus.on(rgbaMat);
                } else {

                }
            }
        });

        return grayMat;
    }


    interface OnFocus{
        void on(Mat mat);
    }

    private OnFocus onFocus;

    public void setOnFocus(OnFocus onFocus) {
        this.onFocus = onFocus;
    }


    private Mat detectText(Mat rgbaMat, Mat grayMat) {

        return rgbaMat;
    }


    @Override
    public void onCameraViewStopped() {

    }
}
