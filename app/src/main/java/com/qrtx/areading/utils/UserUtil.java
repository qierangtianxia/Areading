package com.qrtx.areading.utils;

import com.qrtx.areading.Constants;

/**
 * Created by user on 17-3-9.
 */

public class UserUtil {
    /**
     * 检测是否已经登陆成功
     *
     * @return
     */
    public static boolean checkIsLogin() {
        String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
        return token == null ? false : true;
    }
}
