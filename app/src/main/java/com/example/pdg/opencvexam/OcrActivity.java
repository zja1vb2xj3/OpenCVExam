package com.example.pdg.opencvexam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OcrActivity extends Activity {
    private final String CLASSNAME = getClass().getSimpleName();

    @BindView(R.id.image)
    ImageView imageView;

    static {
        System.loadLibrary("MyLib");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);

        System.out.println(NativeClass.getStringFromNative());

        ThisApplication thisApplication = (ThisApplication) getApplicationContext();

        Bitmap bitmap = thisApplication.getBitmap();

        imageView.setImageBitmap(bitmap);
    }



}
