package com.example.pdg.opencvexam;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
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
    private Mat rgbaMat;
    private Mat resultMat;
    private Handler handler;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.resources = context.getResources();
        handler = new Handler();
        this.setCvCameraViewListener(this);
        this.setOnClickListener(onClickListener);
    }

    JavaCameraView.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCamera.getParameters() != null) {
                Log.i(CLASSNAME, "onClick");

                turnOnFlash();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        resultMat = new Mat(rgbaMat.size(), CvType.CV_8UC1);

                        Mat[] mats = NativeClass.exampleMain(rgbaMat.getNativeObjAddr(), resultMat.getNativeObjAddr());

                        turnOffFlash();

                        if (onFocus != null && resultMat.rows() != 0) {
                            onFocus.onInputMat(mats, resultMat);
                        }
                    }
                }, 1000);
            }
        }
    };

    //region print frameSize
//    int frameWidth = rgbaMat.rows();
//    int frameHeight = rgbaMat.cols()
//    System.out.println("frameWidth : " + frameWidth);
//     System.out.println("frameHeight : " + frameHeight);
//endregion


    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) throws IOException {
//        Log.i(CLASSNAME, "onCameraFrame");

        rgbaMat = inputFrame.rgba();

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
//                System.out.println("onAutoFocus success" + success);

                if (success == true) {


                } else {
                    return;
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
        parameters.setPreviewFpsRange(1500, 1500);
        parameters.setSceneMode(Camera.Parameters.WHITE_BALANCE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    interface OnFocus {
        void onInputMat(Mat[] resultMat, Mat resultState);
    }

    private OnFocus onFocus;

    public void setOnFocus(OnFocus onFocus) {
        this.onFocus = onFocus;
    }

    @Override
    public void onCameraViewStopped() {

    }

    private void turnOffFlash() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(parameters.FLASH_MODE_OFF);

        if (mCamera != null)
            mCamera.setParameters(parameters);
    }

    private void turnOnFlash() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(parameters.FLASH_MODE_ON);

        if (mCamera != null)
            mCamera.setParameters(parameters);
    }

}
