package com.qrtx.areading.net;

import org.xutils.common.Callback;

/**
 * Created by user on 17-3-15.
 */

public class SimpleCommonCallback implements Callback.CommonCallback<String> {
    @Override
    public void onSuccess(String result) {

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
