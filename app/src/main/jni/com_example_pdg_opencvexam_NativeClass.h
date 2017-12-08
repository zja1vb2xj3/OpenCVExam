//
// Created by pdg on 2017-12-08.
//
#include <jni.h>
#ifndef OPENCVEXAM_COM_EXAMPLE_PDG_OPENCVEXAM_NATIVECLASS_H
#define OPENCVEXAM_COM_EXAMPLE_PDG_OPENCVEXAM_NATIVECLASS_H

#ifdef __cplusplus
extern "C" {
#endif //OPENCVEXAM_COM_EXAMPLE_PDG_OPENCVEXAM_NATIVECLASS_H

JNIEXPORT jstring JNICALL Java_com_example_pdg_opencvexam_NativeClass_getStringFromNative
        (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif