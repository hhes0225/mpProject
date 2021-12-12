//
// Created by ASUS on 2021-12-04.
//

#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <jni.h>
#include <jni.h>
#include <jni.h>

int fd = 0;

JNIEXPORT jint JNICALL
Java_com_example_BeatProject2_ResultActivity_openDriverSeg(JNIEnv *env, jclass clazz,
                                                              jstring path) {
    // TODO: implement openDriver()
    jboolean iscopy;
    const char *path_utf=(*env)->GetStringUTFChars(env, path, &iscopy);
    fd = open(path_utf, O_WRONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(fd<0) return -1;
    else return 1;

}

JNIEXPORT void JNICALL
Java_com_example_BeatProject2_ResultActivity_closeDriverSeg(JNIEnv *env, jclass clazz) {
    // TODO: implement closeDriver()
    if(fd>0) close(fd);
}

JNIEXPORT void JNICALL
Java_com_example_BeatProject2_ResultActivity_writeDriverSeg(JNIEnv *env, jclass clazz,
        jbyteArray arr, jint count) {
// TODO: implement writeDriver()
    jbyte* chars =(*env)->GetByteArrayElements(env, arr, 0);
    if(fd>0) write(fd, (unsigned char*)chars, count);
    (*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

