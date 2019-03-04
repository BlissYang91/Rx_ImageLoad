package com.bliss.yang.imageloader;


import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/2/28  18:06
 */
public class MemoryCacheObservable extends CacheObservable {
    int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);//单位kb
    int cacheSize = maxMemory / 8;
    LruCache<String,Bitmap> bitmapLruCache = new LruCache<String,Bitmap>(cacheSize){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };
    @Override
    public Image getDataFromCache(String url) {
        Bitmap bitmap = bitmapLruCache.get(url);
        if (bitmap != null){
            return  new Image(url,bitmap);
        }
        return null;
    }

    @Override
    public void putDataToCache(Image image) {
        bitmapLruCache.put(image.getUrl(),image.getBitmap());
    }
}
