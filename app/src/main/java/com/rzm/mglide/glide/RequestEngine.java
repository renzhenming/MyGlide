package com.rzm.mglide.glide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.rzm.mglide.glide.disk.my.DiskLruCacheImpl;
import com.rzm.mglide.glide.util.Tool;

public class RequestEngine implements ValueCallback, ResponseListener,LifecycleListener {

    private static final String TAG = "RequestEngine";
    private String url;
    private String key;
    private Context context;
    private ImageView imageView;
    // 活动缓存
    private ActiveCache activeCache;
    // 内存缓存
    private MemoryCache memoryCache;
    // 磁盘缓存
    private DiskLruCacheImpl diskLruCache;
    // Glide 获取 内存的 八分之一
    private final int MEMORY_MAX_SIXE = 1024 * 1024 * 60; // 内存缓存 的 maxSize

    public RequestEngine() {
        if (activeCache == null) {
            // 回调给外界，Value资源不再使用了 设置监听
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIXE);
        }

        diskLruCache = new DiskLruCacheImpl();
    }

    public void init(Context context, String url) {
        this.url = url;
        this.context = context;
        this.key = new Key(url).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        Value value = cacheAction();
    }

    private Value cacheAction() {
        //查找活动缓存
        Value value = activeCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在（活动缓存）中获取的资源>>>");
            return value;
        }
        //判断内存缓存
        value = memoryCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在（内存缓存）中获取的资源>>>");

            // 移动操作 剪切（内存--->活动）
            // 把内存缓存中的元素，加入到活动缓存中...
            activeCache.put(key, value);
            // 移除内存缓存
            memoryCache.remove(key);

            return value;
        }
        //从磁盘缓存中找
        value = diskLruCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在（磁盘缓存）中获取的资源>>>");

            // 把磁盘缓存中的元素 ---- 加入 ---》 活动缓存中....   不是剪切 是复制
            activeCache.put(key, value);
            return value;
        }
        //加载网络资源
        value = new LoadDataManager().loadResource(url, this, context);
        if (value != null) {
            return value;
        }
        return null;
    }

    /**
     * 从活动缓存移除的回调，加入内存缓存
     *
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        // 加入到 内存缓存
        if (key != null && value != null) {
            memoryCache.put(key, value);
        }
    }

    @Override
    public void responseSuccess(Value value) {
        if (null != value) {
            saveCache(key, value); // 调用 保存到 缓存中， 【加载成功情况下】

            imageView.setImageBitmap(value.getBitmap());  // 外置资源 成功  回调  显示给目标  显示图片
        }
    }

    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外部资源失败 请检测 e:" + e.getMessage());
    }

    private void saveCache(String key, Value value) {
        Log.d(TAG, "saveCache: >>>>>>>>>>>>>>>>>>>>>>>>>> 加载外置资源成功后 ，保存到缓存中， key:" + key + " value:" + value);
        value.setKey(key);
        if (diskLruCache != null) {
            diskLruCache.put(key, value);
            // activeCache.put(key, value);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        // 活动缓存.释放操作();
        if (activeCache != null) {
            activeCache.recycleActive();  // 活动缓存 给释放掉
        }
    }
}
