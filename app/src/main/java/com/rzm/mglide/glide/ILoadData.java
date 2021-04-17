package com.rzm.mglide.glide;

import android.content.Context;

public interface ILoadData {
    Value loadResource(String path, ResponseListener responseListener, Context context);
}
