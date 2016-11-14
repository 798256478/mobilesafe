package com.zahowenbin.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    /**
     * Œ¢IOÁ÷ÞD»¯³É×Ö·û´®
     * @param is
     * @return String
     */
    public static String streamToString(InputStream is){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] arr = new byte[1024];
        int temp = -1;
        try {
            while ((temp = is.read(arr)) != -1){
                bos.write(arr, 0, temp);
            }
           return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
