package com.rzm.mglide.glide;

import com.rzm.mglide.glide.util.Tool;

import java.util.HashMap;
import java.util.Map;

// 活动缓存 ---> 正在使用的图片 访问 活动缓存
public class ActiveCache {

    private Map<String, Value> mapList = new HashMap<>();  // 容器

    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        // 每次put的时候 put进来的Value 绑定到 valueCallback
        value.setCallback(this.valueCallback);
        mapList.put(key, value);
    }

    public Value get(String key) {
        Value value = mapList.get(key);
        if (null != value) {
            return value;
        }
        return null;
    }

    public void recycleActive() {
        for (Map.Entry<String, Value> valueEntry : mapList.entrySet()) {
            valueEntry.getValue().recycle();
            mapList.remove(valueEntry.getKey());
        }
    }
}
