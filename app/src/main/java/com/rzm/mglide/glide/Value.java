package com.rzm.mglide.glide;

import android.graphics.Bitmap;

// Bitmap的封装
public class Value {

    private Bitmap mBitmap;
    private ValueCallback callback;
    private String key;

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 回收  回调 释放 Value本身   【通过 活动缓存 把当前Value 移动到  LRU内存缓存】
     */
    public void recycle() {
        if(callback != null) {
            // 证明我们的Value没有使用（管理回收）
            // 告诉外界，回调接口
            callback.valueNonUseListener(key, this); // 活动缓存管理监听【Value不在使用了】
        }
    }

}
