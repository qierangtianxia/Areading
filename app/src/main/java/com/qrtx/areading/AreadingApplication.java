package com.qrtx.areading;

import android.app.Application;

import com.qrtx.areading.utils.AppUtil;
import com.qrtx.areading.utils.LogUtil;

import org.xutils.x;

import cn.sharesdk.framework.ShareSDK;


/**
 * Created by user on 17-3-5.
 */

public class AreadingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.i("ALIVE", "AreadingApplication  <---------onCreate  start---------->");
        ShareSDK.initSDK(this);
        x.Ext.init(this);
        AppUtil.init(this);

        LogUtil.i("ALIVE", "AreadingApplication  <---------onCreate  end---------->");
    }
}
