package com.bliss.yang.imageloader;

import android.content.Context;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/2/28  18:01
 */
public class RequestCreator {
    private MemoryCacheObservable mMemoryCacheObservable;
    private DiskCacheObservable mDiskCacheObservable;
    private NetworkCacheObservable mNetworkCacheObservable;

    public RequestCreator(Context context){
        mMemoryCacheObservable = new MemoryCacheObservable();
        mDiskCacheObservable = new DiskCacheObservable(context);
        mNetworkCacheObservable = new NetworkCacheObservable();
    }
    public Observable<Image> getImageFromMemory(String url){

        return mDiskCacheObservable.getImage(url).filter(new Predicate<Image>() {
            @Override
            public boolean test(Image image) throws Exception {
                return image!=null;
            }
        }).doOnNext(new Consumer<Image>() {
            @Override
            public void accept(Image image) throws Exception {
                Log.e("RequestCreator","get data from memory");
            }
        });
    }

    public  Observable<Image> getImageFromDisk(String url){
        return mDiskCacheObservable.getImage(url).filter(new Predicate<Image>() {
            @Override
            public boolean test(Image image) throws Exception {
                return image != null;
            }
        }).doOnNext(new Consumer<Image>() {
            @Override
            public void accept(Image image) throws Exception {
                Log.e("RequestCreator","get data from disk");
                mMemoryCacheObservable.putDataToCache(image);
            }
        });
    }

    public Observable<Image> getImageFromNetwork(String url){
        return mNetworkCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image != null;
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        Log.e("RequestCreator","get data from network");
//                        if (image != null){
                            mDiskCacheObservable.putDataToCache(image);
                            mMemoryCacheObservable.putDataToCache(image);
//                        }
                    }
                });
    }
}
