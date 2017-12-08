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

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    private final String CLASSNAME = getClass().getSimpleName();

    @BindView(R.id.cameraView)
    CameraView cameraView;

    @BindView(R.id.result)
    TextView result;

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private static final String TESS_DATA = "/tessdata";
    private TessBaseAPI tessBaseAPI;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    //region mLoaderCallback
    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    cameraView.enableView();//카메라 활성
                }
                break;
                default: {
                    super.onManagerConnected(status);
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
        loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    CameraView.OnFocus onFocus = new CameraView.OnFocus() {
        @Override
        public void on(Mat mat) {

            Bitmap bitmap = matToBitmap(mat);

            Bitmap rotatedBitmap = rotateBitmap(bitmap, 90);

            Log.i(CLASSNAME, String.valueOf(bitmap.getWidth()));
            Log.i(CLASSNAME, String.valueOf(bitmap.getHeight()));

            ThisApplication thisApplication = (ThisApplication) getApplicationContext();
            thisApplication.setBitmap(rotatedBitmap);

            Intent intent = new Intent(MainActivity.this, OcrActivity.class);
            startActivity(intent);
        }
    };

    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return rotateBitmap;
    }


    private Bitmap matToBitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        return bitmap;
    }

    private Mat bitmapToMat(Bitmap bitmap){
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loaderCallback);
        } else {
            Log.i(CLASSNAME, "onResume :: OpenCV library found inside package. Using it!");
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (cameraView != null)
            cameraView.disableView();
    }


//region
//    private void prepareTessData() {
//        try {
//            File dir = new File(DATA_PATH + TESS_DATA);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            String fileList[] = getAssets().list("");
//
//            for (String fileName : fileList) {
//                String pathToDataFile = DATA_PATH + TESS_DATA + "/" + fileName;
//
//                if (!(new File(pathToDataFile)).exists()) {
//                    InputStream inputStream = getAssets().open(fileName);
//                    OutputStream outputStream = new FileOutputStream(pathToDataFile);
//                    byte[] buff = new byte[1024];
//                    int len;
//
//                    while ((len = inputStream.read(buff)) > 0){
//                        outputStream.write(buff, 0 ,len);
//                    }
//                    inputStream.close();
//                    outputStream.close();
//                }
//            }
//        } catch (IOException e) {
//            Log.i(CLASSNAME, e.getMessage());
//        }
//    }
    //endregion
}
