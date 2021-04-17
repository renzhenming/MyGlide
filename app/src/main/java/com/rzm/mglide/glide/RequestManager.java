package com.rzm.mglide.glide;

import android.content.Context;
import android.util.Log;

public class RequestManager implements LifecycleListener {
    private static final String TAG = "RequestManager";
    private final Lifecycle lifecycle;
    private final Glide glide;
    private final Context context;
    private final RequestEngine requestEngine;

    public RequestManager(Glide glide, Lifecycle lifecycle, Context context) {
        this.glide = glide;
        this.lifecycle = lifecycle;
        this.context = context;
        this.requestEngine = new RequestEngine();
        this.lifecycle.addListener(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart ....");
        requestEngine.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop ....");
        requestEngine.onStop();
    }

    @Override
    public void onDestroy() {
        requestEngine.onDestroy();
        Log.d(TAG, "onDestroy ....");
        this.lifecycle.removeListener(this); // 已经给自己销毁了 【自己给自己移除】
    }

    public RequestEngine load(String url) {
        requestEngine.init(context,url);
        return requestEngine;
    }
}
