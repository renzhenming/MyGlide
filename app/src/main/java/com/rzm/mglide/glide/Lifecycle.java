package com.rzm.mglide.glide;

public interface Lifecycle {
  void addListener(LifecycleListener listener);

  void removeListener(LifecycleListener listener);
}