package com.example.pdg.opencvexam;

import org.opencv.core.Mat;

/**
 * Created by pdg on 2017-12-08.
 */

public class NativeClass {

    static{
        System.loadLibrary("MyLib");
    }

    public native static String getStringFromNative();
    public native static void colorToGray(long matAddrInput, long matAddrResult);

    public native static void getArea(long matAddrInput, long matAddrResult);

}
