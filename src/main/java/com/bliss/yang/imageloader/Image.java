package com.bliss.yang.imageloader;

import android.graphics.Bitmap;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/3/1  10:02
 */
public class Image {
    public Image(String url, Bitmap bitmap) {
        this.url = url;
        this.bitmap = bitmap;
    }

    private String url;

    private Bitmap bitmap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
