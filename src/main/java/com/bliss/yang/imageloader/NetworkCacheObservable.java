package com.bliss.yang.imageloader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/2/28  18:07
 */
public class NetworkCacheObservable extends CacheObservable {

    @Override
    public Image getDataFromCache(String url) {
        Bitmap bitmap = downloadImage(url);
        if (bitmap != null){
            return new Image(url,bitmap);
        }
        return null;
    }

    @Override
    public void putDataToCache(Image image) {

    }

    private Bitmap downloadImage(String url){
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            final URLConnection connection = new URL(url).openConnection();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

}
