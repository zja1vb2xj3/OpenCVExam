package com.example.pdg.opencvexam;

import android.app.Application;
import android.graphics.Bitmap;

import org.opencv.core.Mat;

/**
 * Created by pdg on 2017-12-07.
 */

public class ThisApplication extends Application {

    private static Bitmap bitmap;
    private static Mat mat;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        ThisApplication.bitmap = bitmap;
    }

    public void setMat(Mat mat) {
        ThisApplication.mat = mat;
    }

    public Mat getMat() {
        return mat;
    }
}
