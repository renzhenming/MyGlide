package com.rzm.mglide.glide;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class Glide {

    private static volatile Glide glide;
    private final RequestManagerRetriever requestManagerRetriever;

    private Glide(Context context) {
        this.requestManagerRetriever = new RequestManagerRetriever();
    }

    @NonNull
    public static Glide get(@NonNull Context context) {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    glide = new Glide(context);
                }
            }
        }
        return glide;
    }

    @NonNull
    public static RequestManager with(@NonNull Context context) {
        return getRetriever(context).get(context);
    }

    @NonNull
    public static RequestManager with(@NonNull Activity activity) {
        return getRetriever(activity).get(activity);
    }


    @NonNull
    public static RequestManager with(@NonNull FragmentActivity activity) {
        return getRetriever(activity).get(activity);
    }


    @NonNull
    public static RequestManager with(@NonNull Fragment fragment) { // androidx Fragment
        return getRetriever(fragment.getActivity()).get(fragment);
    }


    @SuppressWarnings("deprecation")
    @Deprecated
    @NonNull
    public static RequestManager with(@NonNull android.app.Fragment fragment) { // app.Fragment
        return getRetriever(fragment.getActivity()).get(fragment);
    }

    @NonNull
    private static RequestManagerRetriever getRetriever(@Nullable Context context) {
        return Glide.get(context).getRequestManagerRetriever();
    }

    @NonNull
    public RequestManagerRetriever getRequestManagerRetriever() {
        return requestManagerRetriever;
    }
}
