#include <jni.h>

JNIEXPORT jdouble JNICALL
Java_com_example_hardwareapp_utils_NativeUtils_calculateTotalPrice(JNIEnv *env, jobject thiz, jdoubleArray prices, jint length) {
    jdouble *pricesArray = (*env)->GetDoubleArrayElements(env, prices, NULL);
    jdouble total = 0.0;

    for (int i = 0; i < length; i++) {
        total += pricesArray[i];
    }

    (*env)->ReleaseDoubleArrayElements(env, prices, pricesArray, 0);
    return total;
}
