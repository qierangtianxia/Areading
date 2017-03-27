package com.qrtx.areading.net;

/**
 * Created by user on 17-3-9.
 */

public class API {
    /**
     * 登陆
     */
    public static final String URL_BASE_LOGIN = "http://182.150.59.252:8866/user/login.do";
    /**
     * 注册
     */
    public static final String URL_BASE_REGISTER = "http://182.150.59.252:8866/user/register.do";
    /**
     * 获取书籍类型
     */
    public static final String URL_BASE_BOOKTYPE = "http://182.150.59.252:8866/read/bookclassify.do";
    /**
     * 获取小说
     */
    public static final String URL_BASE_GETBOOKLIST = "http://182.150.59.252:8866/read/getbooklist.do";
    /**
     * 下载小说
     */
    public static final String URL_BASE_GETBOOK = "http://182.150.59.252:8866/read/getbook.do";
    /**
     * 获取评论
     */
    public static final String URL_BASE_GETCOMMENT = "http://182.150.59.252:8866/read/getbookcomment.do";
    /**
     * 提交评论
     */
    public static final String URL_BASE_POSTCOMMENT = "http://182.150.59.252:8866/read/releasebookcomment.do";
    /**
     * 用户头像上传
     */
    public static final String URL_BASE_POSTICON = "http://182.150.59.252:8866/user/upfile.do?";
    /**
     * 绑定用户头像
     */
    public static final String URL_BASE_BINDICON = "http://182.150.59.252:8866/user/setUserIcon.do";
    /**
     * 获取服务器文件
     */
    public static final String URL_BASE_GETICON = "http://182.150.59.252:8866/file/";

}
