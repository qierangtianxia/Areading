package com.qrtx.areading.net.responses;

import org.xutils.common.Callback;

/**
 * Created by user on 17-3-15.
 */

public class CommonCallback<T> implements Callback.CommonCallback<T> {
    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
