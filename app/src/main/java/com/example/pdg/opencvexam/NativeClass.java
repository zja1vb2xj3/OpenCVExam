package com.example.pdg.opencvexam;

import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by pdg on 2017-12-08.
 */

public class NativeClass {

    static {
        System.loadLibrary("MyLib");

    }

    public native static String getStringFromNative();

    public native static void colorToGray(long matAddrInput, long matAddrResult);

    public native static Mat[] exampleMain(long matAddrInput, long matAddrResult);
}
