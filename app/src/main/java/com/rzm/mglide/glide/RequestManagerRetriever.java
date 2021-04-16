package com.rzm.mglide.glide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.rzm.mglide.glide.util.Util;

import java.util.HashMap;
import java.util.Map;

public class RequestManagerRetriever implements Handler.Callback {
    final Map<FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
    private final Handler handler;
    private final String SUPPORT_FRAGMENT_TAG = "com.my.glide.manager";
    private final int REMOVE_SUPPORT_FRAGMENT_MSG = 2;
    private RequestManager applicationManager;

    public RequestManagerRetriever(){
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    @NonNull
    public RequestManager get(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("...");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context); // 进入FragmentActivity的get函数
            } else if (context instanceof Activity) {
                return get((Activity) context); // 进入Activity的get函数
            } else if (context instanceof ContextWrapper && ((ContextWrapper) context).getBaseContext().getApplicationContext() != null) {
                return get(((ContextWrapper) context).getBaseContext()); // 继续递归寻找 匹配合适的
            }
        }

        // 若上面的判断都不满足，就会执行下面这句代码，同学们想知道Application作用域 就需要关心这句代码（红色区域）
        return getApplicationManager(context);
    }

    private RequestManager getApplicationManager(@NonNull Context context) {
        if (this.applicationManager == null) {
            synchronized(this) {
                if (this.applicationManager == null) {
                    Glide glide = Glide.get(context.getApplicationContext());
                    this.applicationManager = new RequestManager(glide, new ApplicationLifecycle(), context.getApplicationContext());
                }
            }
        }

        return this.applicationManager;
    }

    @NonNull
    public RequestManager get(@NonNull FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        } else {
            Util.assertNotDestroyed(activity);
            FragmentManager fm = activity.getSupportFragmentManager();
            return supportFragmentGet(activity, fm);
        }
    }

    @NonNull
    private RequestManager supportFragmentGet(@NonNull Context context, @NonNull androidx.fragment.app.FragmentManager fm) {
        SupportRequestManagerFragment current = this.getSupportRequestManagerFragment(fm);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            Glide glide = Glide.get(context);
            requestManager = new RequestManager(glide,current.getGlideLifeCycle(),context);
            current.setRequestManager(requestManager);
        }

        return requestManager;
    }

    SupportRequestManagerFragment getSupportRequestManagerFragment(@NonNull androidx.fragment.app.FragmentManager fm) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment)fm.findFragmentByTag(SUPPORT_FRAGMENT_TAG);
        if (current == null) {
            current = this.pendingSupportRequestManagerFragments.get(fm);
            if (current == null) {
                current = new SupportRequestManagerFragment();
                this.pendingSupportRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, SUPPORT_FRAGMENT_TAG).commitAllowingStateLoss();
                this.handler.obtainMessage(REMOVE_SUPPORT_FRAGMENT_MSG, fm).sendToTarget();
            }
        }

        return current;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        boolean handled = true;
        switch(msg.what) {
            case REMOVE_SUPPORT_FRAGMENT_MSG:
                androidx.fragment.app.FragmentManager supportFm = (androidx.fragment.app.FragmentManager)msg.obj;
                this.pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
        }
        return handled;
    }
}
