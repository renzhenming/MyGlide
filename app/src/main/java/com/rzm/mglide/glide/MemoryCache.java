package com.rzm.mglide.glide;

import android.os.Build;
import android.util.LruCache;

public class MemoryCache extends LruCache<String, Value> {

    /**
     * 传入元素最大值，给LruCache
     * @param maxSize
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    // 重写父类函数 就是为了 计算每一个元素的大小   Bitmap的大小
    // 下面三种方式，获取Bitmap大小 完全一样，但是：在Bitmap内存申请环节 有区别 不一样
    @Override
    protected int sizeOf(String key, Value value) {
        // Bitmap大小获取的 发展史
        /*// 最开始的时候
        int result = value.getBitmap().getRowBytes(); // native

        // API 12  3.0
        int result = value.getBitmap().getByteCount(); // java

        // API  19 4.4
        int result = value.getBitmap().getAllocationByteCount();*/

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) { // 4.4
            return value.getBitmap().getAllocationByteCount();
        }

        return value.getBitmap().getByteCount();
    }
}
