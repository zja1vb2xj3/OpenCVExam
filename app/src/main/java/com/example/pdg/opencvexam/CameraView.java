package com.example.pdg.opencvexam;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pdg on 2017-12-06.
 */

public class CameraView extends JavaCameraView implements JavaCameraView.CvCameraViewListener2 {
    private final String CLASSNAME = getClass().getSimpleName();
    private Context context;
    private Resources resources;
    private Paint paint;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.resources = context.getResources();
        this.setCvCameraViewListener(this);

    }

    //region print frameSize
//    int frameWidth = rgbaMat.rows();
//    int frameHeight = rgbaMat.cols()
//    System.out.println("frameWidth : " + frameWidth);
//     System.out.println("frameHeight : " + frameHeight);
//endregion


    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
//        Log.i(CLASSNAME, "onCameraFrame");

        final Mat rgbaMat = inputFrame.rgba();
        final Mat grayMat = inputFrame.gray();

        int threshold = 100;



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

        return rgbaMat;
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

    interface OnFocus {
        void on(Mat rgbaMat);
    }

    private OnFocus onFocus;

    public void setOnFocus(OnFocus onFocus) {
        this.onFocus = onFocus;
    }


    @Override
    public void onCameraViewStopped() {

    }

}
