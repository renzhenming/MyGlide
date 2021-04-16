package com.rzm.mglide.glide;

import android.content.Context;

public class RequestManager {
    private final Lifecycle lifecycle;
    private final Glide glide;
    private final Context context;

    public RequestManager(Glide glide, Lifecycle lifecycle, Context context) {
        this.glide = glide;
        this.lifecycle = lifecycle;
        this.context = context;
    }
}
