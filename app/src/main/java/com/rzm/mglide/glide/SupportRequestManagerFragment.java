package com.rzm.mglide.glide;

import androidx.fragment.app.Fragment;

public class SupportRequestManagerFragment extends Fragment {
    private final ActivityFragmentLifecycle lifeCycle;
    private RequestManager requestManager;

    public SupportRequestManagerFragment(){
        this(new ActivityFragmentLifecycle());
    }

    public SupportRequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
        this.lifeCycle = lifecycle;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public ActivityFragmentLifecycle getGlideLifeCycle() {
        return lifeCycle;
    }

    @Override
    public void onStart() {
        super.onStart();
        lifeCycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifeCycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifeCycle.onDestroy();
    }
}
