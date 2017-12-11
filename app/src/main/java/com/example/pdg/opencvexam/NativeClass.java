package com.example.pdg.opencvexam;

import org.opencv.core.Mat;

import java.io.IOException;

/**
 * Created by pdg on 2017-12-08.
 */

public class NativeClass {

    static{
        try {
            System.loadLibrary("MyLib");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public native static String getStringFromNative();
    public native static void colorToGray(long matAddrInput, long matAddrResult);

    public native static void getArea(long matAddrInput, long matAddrResult);

    public native static void exampleMain(long matAddrInput, long matAddrResult);
}
