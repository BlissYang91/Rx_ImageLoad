package com.bliss.yang.imageloader;

import android.content.Context;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @autor YangTianFu
 * @Email ytfunny@126.com
 * @CSDN https://blog.csdn.net/ytfunnysite
 * @Date 2019/2/28  17:54
 */
public class RxImageLoader {
    static RxImageLoader singleton;
    private String mUrl;
    private RequestCreator mRequestCreator;

    private RxImageLoader(Builder builder) {
        mRequestCreator  = new RequestCreator(builder.mContext);
    }

    public static RxImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (RxImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public RxImageLoader load(String url) {
        this.mUrl = url;
        return singleton;
    }

    public void into(final ImageView imageView) {
        Observable.concat(mRequestCreator.getImageFromMemory(mUrl),
                mRequestCreator.getImageFromDisk(mUrl),
                mRequestCreator.getImageFromNetwork(mUrl))
                .firstElement()
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image.getBitmap()!=null;
                    }
                })
                .subscribe(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        if (image.getBitmap() != null){
                            imageView.setImageBitmap(image.getBitmap());
                        }

                    }
                });
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public RxImageLoader build() {
            return new RxImageLoader(this);
        }

    }

}
