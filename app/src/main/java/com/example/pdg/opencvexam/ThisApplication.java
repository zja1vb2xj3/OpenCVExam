package com.example.pdg.opencvexam;

import android.app.Application;
import android.graphics.Bitmap;

import org.opencv.core.Mat;

import java.text.BreakIterator;
import java.util.ArrayList;

/**
 * Created by pdg on 2017-12-07.
 */

public class ThisApplication extends Application {

    private static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    private static Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        ThisApplication.bitmap = bitmap;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        ThisApplication.bitmapArrayList = bitmapArrayList;
    }
}
