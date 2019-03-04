package com.bliss.yang.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/2/28  18:06
 */
public class DiskCacheObservable extends CacheObservable {
    private DiskLruCache mDiskLruCache;
    private Context mContext;
    //DiskLruCache中对于图片的最大缓存值.20M
    private int maxSize = 20 * 1024 * 1024;

    public DiskCacheObservable(Context context) {
        this.mContext = context;
        initDiskLruCache();
    }

    /**
     * 初始化文件缓存DiskLrucache
     */
    private void initDiskLruCache() {
        try {
            File cacheDir = DiskCacheUtil.getDiskCacheDir(this.mContext, "imge_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            int versionCode = DiskCacheUtil.getAppVersionCode(mContext);
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Image getDataFromCache(String url) {
        Bitmap bitmap = getDataFromDiskLruCache(url);
        if (bitmap != null){
            return new Image(url,bitmap);
        }
        return null;
    }

    @Override
    public void putDataToCache(final Image image) {
        Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> emitter) throws Exception {
                putDataToDiskLruCache(image);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private Bitmap getDataFromDiskLruCache(String url) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            // 生成图片URL对应的key
            final String key = DiskCacheUtil.getMd5String(url);
            // 查找key对应的缓存
            snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // 将缓存数据解析成Bitmap对象
            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void putDataToDiskLruCache(Image img) {

        try {
            //第一步:获取将要缓存的图片的对应唯一key值.
            String key = DiskCacheUtil.getMd5String(img.getUrl());
            //第二步:获取DiskLruCache的Editor
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                //第三步:从Editor中获取OutputStream
                OutputStream outputStream = editor.newOutputStream(0);
                //第四步:下载网络图片且保存至DiskLruCache图片缓存中
                boolean isSuccessful = download(img.getUrl(),outputStream);
                if (isSuccessful){
                    editor.commit();
                }else {
                    editor.abort();
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean download(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream outputStream1 = null;
        BufferedInputStream inputStream = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            outputStream = new BufferedOutputStream(outputStream, 8 * 1024);
            int i;
            while ((i = inputStream.read()) != -1) {
                outputStream.write(i);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
