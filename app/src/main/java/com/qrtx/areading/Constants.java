package com.qrtx.areading;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 17-3-4.
 */

public class Constants {
    //访问网络　和　数据库存储键
    public static final String KEY_FILE = "file";
    public static final String KEY_USER_PHONE = "phone";
    public static final String KEY_USER_SEX = "sex";
    public static final String KEY_USER_NICK = "nick";
    public static final String KEY_USER_ICON = "icon";
    public static final String KEY_USER_ICON_ID = "resId";
    public static final String KEY_USER_PWD = "password";
    public static final String KEY_USER_TOKEN = "token";
    public static final String KEY_USER_BOOK_COUNT = "count";
    public static final String KEY_USER_BOOK_PAGE = "page";
    public static final String KEY_USER_BOOK_ID = "bookID";
    public static final String KEY_USER_CHAPTER_NO = "catalogueNo";
    public static final String KEY_USER_COMMENT = "comment";
    public static final String KEY_READ_CHAPTER = "chapterPro";
    public static final String KEY_READ_POS_END = "endPro";
    public static final String KEY_READ_POS_START = "startPro";
    public static final String KEY_READ_BOOK_MARK = "bookMark";
    public static final String KEY_READ_FONT_SIZE = "fontSize";
    public static final String KEY_READ_PAGER_THEME = "fontSize";

    //豆瓣
    public static final String KEY_COUNT = "count";
    public static final String KEY_START = "start";

    //文件存储路径
    public static final String PATH_BOOK_BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Areading" + File.separator + "books";

    //请求码
    public static final int REQUEST_CODE_REGISTER = 0x0001;
    public static final int REQUEST_CODE_SHOW_BOOKMARK = 0x0002;
    public static final int REQUEST_CODE_CAMERA = 0x0003;
    public static final int REQUEST_CODE_ALBUM = 0x0004;

    //结果码
    public static final int RESULT_CODE_REGISTER = 0x1001;
    public static final int RESULT_CODE_SHOW_BOOKMARK = 0x1002;

    public static final String TITLE_PAGER_NOVEL = "搜狗阅读";
    public static final String TITLE_PAGER_STORE = "书城";
    public static final String TITLE_PAGER_DISCOVERY = "发现";

    //书籍文件对象
    public static final String KEY_BOOK = "book";
    public static final String DATABASE_NAME = "areadingDB";
    public static final String TABLE_NAME_READ_HISTORY = "readHistory";
    public static final String TABLE_NAME_BOOK_NOVEL = "novelTavle";
    public static final String TABLE_NAME_BOOK_MARK = "bookmark";
    public static final String TABLE_NAME_BOOK_CHAPTER = "Chapter";
    public static final String KEY_HOTAUTHOR = "hotAuthor";

    public static String BODY = "楔子   \n" +
            "天圣皇朝立朝百年，帝王睿智，臣子忠心，国富兵强，百姓安居乐业。是神州大陆最大的国家。百年繁华。小国不敢望其项背，岁岁纳贡，年年称臣。\n" +
            "但百年繁华的背后，弊端蛀虫也日益加重。时值新旧政权更替，平静的外表下，是暗潮汹涌。其中以荣王府，云王府，德亲王府，孝亲王府四大皇族势力为最。7Z小说 http://www.7zbook.com\n" +
            "帝王年迈，太子和诸皇子春华正茂。四大皇族王府老一辈王爷渐渐退出历史舞台，新一代翩翩少年纷纷接受祖荫基业崭露头角。\n" +
            "各王府少年公子，俱是文武全才之人。明刀暗箭，血雨腥风，背地里抖得好不热闹。但谁也不捅破那层薄薄的窗户纸。时局因此僵持不动。\n" +
            "李芸，国安局最年轻最具才华的上将，一朝为国身死，灵魂坠入异世，重生在天圣皇朝云王府唯一的嫡女云浅月之身。\n" +
            "她的到来，就是那个突破口。\n" +
            "－－－－－－题外话－－－－－－\n" +
            "阔别俩月，子情终于带着新文爬来，久违了追来新文的妾本老读者，爱你们。新文准备万全，不用再担心断更。既然来了，我相信你们是相信我滴。我也相信自己能够做到。么么。\n" +
            "点进来的新读者，请点击收藏。相信我，选择本文，不会让你失望。群么么！O（∩_∩）O~\n" +
            "全本小说网(http://www.7zbook.com)7X24小时不间段更新最新小说";
}
