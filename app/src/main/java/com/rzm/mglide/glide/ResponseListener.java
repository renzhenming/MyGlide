package com.rzm.mglide.glide;

public interface ResponseListener {
    void responseSuccess(Value value);

    void responseException(Exception e);
}
