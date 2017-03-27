package com.qrtx.areading.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.BookType;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.SimpleCommonCallback;
import com.qrtx.areading.net.responses.Response;
import com.qrtx.areading.ui.activity.LoginActivity;
import com.qrtx.areading.ui.adapter.BookTypeAdapter;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.SPUtil;
import com.qrtx.areading.utils.ToastUtils;
import com.recker.flybanner.FlyBanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by user on 17-2-28.
 */

public class StoreFragment extends BaseFragment {
    private FlyBanner mBannerNet;//顶部轮播图
    private TabLayout mTlReadType;//阅读类别导航
    private ViewPager mVpBookType;
    private TextView mTvError;
    private ArrayList<BookType> mBookTypes;
    private HashMap<Integer, ArrayList<Book>> bookMap;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionTitle(Constants.TITLE_PAGER_STORE);
        View bootView = addContentView(R.layout.fragment_store);


        initData();
        initView(bootView);

        //服务器有问题
        String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
        if (token != null) {//用户已经登陆
            getBookType(token);
        } else {
//            //服务器有问题　　本地模拟数据
//            mBookTypes = getBookTypeFromLocal();
//            initPager();
            mTvError.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {

    }

    private void initView(View bootView) {
        initNetBanner(bootView);
        mTlReadType = (TabLayout) bootView.findViewById(R.id.id_tably_read_type);
        mVpBookType = (ViewPager) bootView.findViewById(R.id.id_vp_type);
        mTvError = (TextView) bootView.findViewById(R.id.id_tv_error);

        mTvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
//                if (token != null) {
//                    getBookType(token);
//                }
                SPUtil.remove(Constants.KEY_USER_TOKEN);
                startActivity(new Intent(mBootActivity, LoginActivity.class));
            }
        });
    }

    /**
     * 加载网页图片
     */
    private void initNetBanner(View bootView) {
        mBannerNet = (FlyBanner) bootView.findViewById(R.id.banner_2);

        ArrayList<String> imgesUrl = new ArrayList<>();
        imgesUrl.add("http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg");
        imgesUrl.add("http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg");
        imgesUrl.add("http://pic18.nipic.com/20111215/577405_080531548148_2.jpg");
        imgesUrl.add("http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg");
        imgesUrl.add("http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg");
        mBannerNet.setImagesUrl(imgesUrl);

        mBannerNet.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
    }


    private void setErrorPager(boolean isError) {
        if (isError) {
            mTvError.setVisibility(View.VISIBLE);
            mTlReadType.setVisibility(View.GONE);
            mVpBookType.setVisibility(View.GONE);
        } else {
            mTvError.setVisibility(View.GONE);
            mTlReadType.setVisibility(View.VISIBLE);
            mVpBookType.setVisibility(View.VISIBLE);
        }
    }

    private void setErrorPager(String mes) {
        setErrorPager(true);
        mTvError.setText(mes);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActionTitle(Constants.TITLE_PAGER_STORE);
        }
    }

    /**
     * 从服务器获取书籍分类
     *
     * @return
     */
    public ArrayList<String> getBookType(final String token) {
        LogUtil.i("NET", "token = " + token);
        final ProgressDialog progressDialog = ProgressDialogUtil.showProgressDialog(mBootActivity, "正在加载．．．");
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        entity.setUri(API.URL_BASE_BOOKTYPE);
        x.http().post(entity, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("NET", "result = " + result);

                progressDialog.dismiss();
                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                    setErrorPager(response.responseMsg);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray types = jsonObject.getJSONArray("types");
                        mBookTypes = new ArrayList<>();
                        for (int i = 0; i < types.length(); i++) {
                            JSONObject typeJson = types.optJSONObject(i);
                            String name = typeJson.optString("name");
                            if ("测试".equals(name)) {
                                name = "异世";
                            }
                            BookType bookType = new BookType(typeJson.optInt("id"), name);
                            mBookTypes.add(bookType);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //根据获取出来的小说分类，获取小说
                    getBookList(token, 20);

//                    if (mBookTypes != null && mBookTypes.size() > 0) {
//                        initPager();
//                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
                setErrorPager(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
        return null;
    }

    private ArrayList<BookType> getBookTypeFromLocal() {
        String[] list = {"武侠", "修真", "言情", "玄幻"};
        ArrayList<BookType> bookTypes = new ArrayList<>();

        for (int i = 0; i < list.length; i++) {
            BookType bookType = new BookType(i, list[i]);
            bookTypes.add(bookType);
        }
        return bookTypes;
    }


    private void initPager() {
        mVpBookType.setAdapter(new BookTypeAdapter(mBootActivity, mBookTypes, bookMap));
        mTlReadType.setupWithViewPager(mVpBookType);
    }

    private void getBookList(String token, int count) {
        LogUtil.i("GET BOOK", " -----------getBookList");
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        entity.addBodyParameter(Constants.KEY_USER_BOOK_COUNT, count + "");
        entity.addBodyParameter(Constants.KEY_USER_BOOK_PAGE, 0 + "");
        entity.setUri(API.URL_BASE_GETBOOKLIST);
        x.http().post(entity, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                LogUtil.i("GET BOOK", "result = " + result);
                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray bookListJson = jsonObject.getJSONArray("booklist");

                        bookMap = new HashMap<Integer, ArrayList<Book>>();
                        for (BookType bookType : mBookTypes) {
                            bookMap.put(bookType.id, new ArrayList<Book>());
                        }
                        Set<Integer> typeIdSet = bookMap.keySet();
                        for (int i = 0; i < bookListJson.length(); i++) {
                            JSONObject bookJson = bookListJson.optJSONObject(i);
//                            String bookID = MD5Util.MD5(bookJson.optString("bookID"));
                            String bookID = bookJson.optString("bookID");
                            String bookname = bookJson.optString("bookname");
                            String author = bookJson.optString("author");
                            String wordCount = bookJson.optString("wordCount");
                            String summary = bookJson.optString("summary");
                            String iconUrl = bookJson.optString("iconUrl");
                            String type = bookJson.optString("type");
                            //获取小说章节
                            JSONArray catalogsJson = bookJson.optJSONArray("catalogue");
//                            for (int j = 0; j < catalogsJson.length(); j++) {
//                                JSONObject catalogJson = catalogsJson.optJSONObject(j);
//                                new Book.Chapter(j + "", catalogJson.toString());
//                            }

                            Book book = new Book(bookID, bookname, type, author, summary, wordCount, catalogsJson.length());
                            Integer typeInt = Integer.valueOf(type);
                            if (typeIdSet.contains(typeInt)) {
                                bookMap.get(typeInt).add(book);
                            }
                        }

                        if (bookMap != null && bookMap.size() > 0) {
                            initPager();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                LogUtil.e("GET BOOK", "ex = " + ex);
            }
        });
    }
}
