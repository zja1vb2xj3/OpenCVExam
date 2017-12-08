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
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
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

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private static final String TESS_DATA = "/tessdata";
    private TessBaseAPI tessBaseAPI;

    static {
        System.loadLibrary("opencv_java3");
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
        public void on(Mat rgbaMat) {

            Bitmap bitmap = matToBitmap(rgbaMat);

//            Log.i(CLASSNAME, String.valueOf(bitmap.getWidth()));
//            Log.i(CLASSNAME, String.valueOf(bitmap.getHeight()));

            ThisApplication thisApplication = (ThisApplication) getApplicationContext();
            thisApplication.setBitmap(bitmap);

            Intent intent = new Intent(MainActivity.this, OcrActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    };

//    private Mat detectText(Mat rgbaMat){
//
//        Mat originMat = rgbaMat;
//        Mat grayMat = new Mat();
//
//        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGB2GRAY);
//
//        Scalar rectColor = new Scalar(255, 0, 0, 255);
//        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
//        List<KeyPoint> listPoint = new ArrayList<>();
//        KeyPoint kPoint = new KeyPoint();
//        Mat mask = Mat.zeros(grayMat.size(), CvType.CV_8UC1);
//        int rectanx1;
//        int rectany1;
//        int rectanx2;
//        int rectany2;
//
//        Scalar zeros = new Scalar(0,0,0);
//        List<MatOfPoint> contour2 = new ArrayList<>();
//        Mat kernel = new Mat(1, 50, CvType.CV_8UC1, Scalar.all(255));
//        Mat morByte = new Mat();
//        Mat hierarchy = new Mat();
//
//        Rect rectan3 = new Rect();
//        int imgSize = originMat.height() * originMat.width();
//
//        if(true){
//            FeatureDetector detector = FeatureDetector.create(FeatureDetector.MSER);
//            detector.detect(grayMat, keyPoint);
//            listPoint = keyPoint.toList();
//            for(int ind = 0; ind < listPoint.size(); ++ind){
//                kPoint = listPoint.get(ind);
//                rectanx1 = (int ) (kPoint.pt.x - 0.5 * kPoint.size);
//                rectany1 = (int ) (kPoint.pt.y - 0.5 * kPoint.size);
//
//                rectanx2 = (int) (kPoint.size);
//                rectany2 = (int) (kPoint.size);
//                if(rectanx1 <= 0){
//                    rectanx1 = 1;
//                }
//                if(rectany1 <= 0){
//                    rectany1 = 1;
//                }
//                if((rectanx1 + rectanx2) > grayMat.width()){
//                    rectanx2 = grayMat.width() - rectanx1;
//                }
//                if((rectany1 + rectany2) > grayMat.height()){
//                    rectany2 = grayMat.height() - rectany1;
//                }
//                Rect rectant = new Rect(rectanx1, rectany1, rectanx2, rectany2);
//                Mat roi = new Mat(mask, rectant);
//                roi.setTo(rectColor);
//            }
//
//            Imgproc.morphologyEx(mask, morByte, Imgproc.MORPH_DILATE, kernel);
//            Imgproc.findContours(morByte, contour2, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//            for(int i = 0; i<contour2.size(); ++i){
//                rectan3 = Imgproc.boundingRect(contour2.get(i));
//                if(rectan3.area() > 0.5 * imgSize || rectan3.area()<100 || rectan3.width / rectan3.height < 2){
//                    Mat roi = new Mat(morByte, rectan3);
//                    roi.setTo(zeros);
//                }else{
//                    Imgproc.rectangle(originMat, rectan3.br(), rectan3.tl(), rectColor);
//
//                }
//            }
//        }
//
//        return originMat;
//    }

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
