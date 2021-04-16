package com.rzm.mglide.glide;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ActivityFragmentLifecycle implements Lifecycle {

    private boolean isStarted;
    private boolean isDestroyed;
    private final Set<LifecycleListener> lifecycleListeners =
            Collections.newSetFromMap(new WeakHashMap<LifecycleListener, Boolean>());

    @Override
    public void addListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
        if (isDestroyed) {
            listener.onDestroy();
        } else if (isStarted) {
            listener.onStart();
        } else {
            listener.onStop();
        }
    }

    @Override
    public void removeListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    void onStart() {
        isStarted = true;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStart();
        }
    }

    void onStop() {
        isStarted = false;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStop();
        }
    }

    void onDestroy() {
        isDestroyed = true;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onDestroy();
        }
    }
}
