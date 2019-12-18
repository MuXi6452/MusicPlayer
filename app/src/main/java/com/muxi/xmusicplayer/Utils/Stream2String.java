package com.muxi.xmusicplayer.Utils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Stream2String {
    public static String parseStream(InputStream inputStream){
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
                while ((len = inputStream.read(buffer))!=-1) {
                    arrayOutputStream.write(buffer, 0, len);
                }
                return arrayOutputStream.toString("utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
}
