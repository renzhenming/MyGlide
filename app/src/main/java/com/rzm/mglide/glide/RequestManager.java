package com.rzm.mglide.glide;

import android.content.Context;
import android.util.Log;

public class RequestManager implements LifecycleListener {
    private static final String TAG = "RequestManager";
    private final Lifecycle lifecycle;
    private final Glide glide;
    private final Context context;

    public RequestManager(Glide glide, Lifecycle lifecycle, Context context) {
        this.glide = glide;
        this.lifecycle = lifecycle;
        this.context = context;

        this.lifecycle.addListener(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "开始执行生命周期业务 onStart: 运行队列 全部执行，等待队列 全部清空 ....");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "开始执行生命周期业务 onStop: 运行队列 全部停止，把任务都加入到等待队列 ....");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "开始执行生命周期业务 onDestroy: 自己负责移除自己绑定的生命周期监听，释放操作 ....");
        this.lifecycle.removeListener(this); // 已经给自己销毁了 【自己给自己移除】
    }
}
