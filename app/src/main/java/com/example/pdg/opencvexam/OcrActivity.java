package com.example.pdg.opencvexam;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OcrActivity extends Activity {

    @BindView(R.id.image)
    ImageView imageView;

    private final String CLASSNAME = getClass().getSimpleName();
    private ThisApplication thisApplication;
    private ArrayList<Bitmap> bitmapArrayList;
    private TessCore tessCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ButterKnife.bind(this);

        thisApplication = (ThisApplication) getApplicationContext();
        bitmapArrayList = thisApplication.getBitmapArrayList();

        Bitmap stateBitmap = thisApplication.getBitmap();
        imageView.setImageBitmap(stateBitmap);

        tessCore = new TessCore(this);

    }//end onCreate

    private Bitmap combineImageIntoOne(ArrayList<Bitmap> bitmap) {
        int w = 0, h = 0;
        for (int i = 0; i < bitmap.size(); i++) {
            if (i < bitmap.size() - 1) {
                w = bitmap.get(i).getWidth() > bitmap.get(i + 1).getWidth() ? bitmap.get(i).getWidth() : bitmap.get(i + 1).getWidth();
            }
            h += bitmap.get(i).getHeight();
        }

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        int top = 0;
        for (int i = 0; i < bitmap.size(); i++) {
            Log.d(CLASSNAME, "Combine: " + i + "/" + bitmap.size() + 1);

            top = (i == 0 ? 0 : top + bitmap.get(i).getHeight());
            canvas.drawBitmap(bitmap.get(i), 10f, top + i * 20f, null);
        }
        return temp;
    }



    @Override
    protected void onResume() {
        super.onResume();
//
        for (Bitmap bitmap : bitmapArrayList) {
            List<String> ocrResults = tessCore.detectText(bitmap);

            for (String result : ocrResults) {
                if (result.equalsIgnoreCase("")) {
                    System.out.println("ocr result : null");
                } else {
                    System.out.println("ocr result : " + result);
                }
            }
        }
    }


    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return rotateBitmap;
    }

    ImageView.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                Toast.makeText(OcrActivity.this, String.valueOf(motionEvent.getX() + "\n" + motionEvent.getY()), Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    };


}
