package com.example.pdg.opencvexam;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OcrActivity extends Activity {
    private final String CLASSNAME = getClass().getSimpleName();

    @BindView(R.id.image)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ButterKnife.bind(this);

        ThisApplication thisApplication = (ThisApplication) getApplicationContext();

        ArrayList<Bitmap> bitmapArrayList = thisApplication.getBitmapArrayList();


        TessCore tessCore = new TessCore(this);

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

    }//end onCreate

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
