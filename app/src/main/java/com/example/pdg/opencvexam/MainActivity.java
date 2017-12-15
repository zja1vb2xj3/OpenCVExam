package com.example.pdg.opencvexam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    private final String CLASSNAME = getClass().getSimpleName();

    @BindView(R.id.cameraView)
    CameraView cameraView;

    @BindView(R.id.result)
    TextView result;

    private TessCore tessCore;

    static {
        System.loadLibrary("opencv_java3");
    }

    //region mLoaderCallback
    private BaseLoaderCallback openCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    cameraView.enableView();
                }
                break;

            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        cameraView.setOnFocus(onFocus);
        openCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        tessCore = new TessCore(this);
    }

    CameraView.OnFocus onFocus = new CameraView.OnFocus() {
        @Override
        public void onInputMat(Mat[] resultMat, Mat resultState) {

            ThisApplication thisApplication = (ThisApplication) getApplicationContext();
            ArrayList<Bitmap> bitmapArrayList = thisApplication.getBitmapArrayList();

            Bitmap stateBitmap = matToBitmap(resultState);
            thisApplication.setBitmap(stateBitmap);

            for (int i = 0; i < resultMat.length; i++) {
                if (resultMat[i] != null) {
                    Bitmap resultBitmap = matToBitmap(resultMat[i]);
                    bitmapArrayList.add(resultBitmap);
                }
            }

            thisApplication.setBitmapArrayList(bitmapArrayList);

            Intent intent = new Intent(MainActivity.this, OcrActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };


    private Bitmap matToBitmap(Mat mat) {

        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        return bitmap;

    }

    private Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        return mat;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.i(CLASSNAME, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, openCVCallBack);
        } else {
            Log.i(CLASSNAME, "onResume :: OpenCV library found inside package. Using it!");
            openCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (cameraView != null)
            cameraView.disableView();
    }


}
