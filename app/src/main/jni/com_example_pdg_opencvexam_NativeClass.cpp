//
// Created by pdg on 2017-12-08.
//
#include <com_example_pdg_opencvexam_NativeClass.h>

JNIEXPORT jstring JNICALL Java_com_example_pdg_opencvexam_NativeClass_getStringFromNative
        (JNIEnv * env, jobject obj){
    return env->NewStringUTF("Hello Native Class");
}

