package com.qrtx.areading;

import android.app.Application;

import com.qrtx.areading.utils.AppUtil;

import org.xutils.x;

import java.util.concurrent.TimeUnit;


/**
 * Created by user on 17-3-5.
 */

public class AreadingApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        AppUtil.init(this);
    }
}
