package com.example.pdg.opencvexam;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

import java.io.Serializable;

/**
 * Created by pdg on 2017-12-08.
 */

public class OcrModel implements Serializable {
    private Bitmap bitmap;

    public OcrModel() {

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
