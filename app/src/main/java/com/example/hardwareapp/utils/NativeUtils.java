package com.example.hardwareapp.utils;

public class NativeUtils {
    static {
        System.loadLibrary("native-lib");
    }

    public static native double calculateTotalPrice(double[] prices, int length);
}
