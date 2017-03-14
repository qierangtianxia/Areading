package com.qrtx.areading.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by user on 17-3-9.
 */

public class ProgressDialogUtil {

    public static ProgressDialog showProgressDialog(Activity activity, String msg) {
        ProgressDialog loginDialog = creatProgressDialog(activity, msg);
        loginDialog.show();
        return loginDialog;
    }

    public static ProgressDialog creatProgressDialog(Activity activity, String msg) {
        ProgressDialog loginDialog = new ProgressDialog(activity);
        loginDialog.setTitle(msg);
        return loginDialog;
    }
}
