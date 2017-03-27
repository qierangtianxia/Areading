package com.qrtx.areading.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.Comment;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.SimpleCommonCallback;
import com.qrtx.areading.net.responses.Response;
import com.qrtx.areading.ui.adapter.CommentLvAdapter;
import com.qrtx.areading.ui.adapter.CommentRcvAdapter;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.FileUtils;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.SPUtil;
import com.qrtx.areading.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {
    private TextView mTvName, mTvAuthor, mTvSummary, mTvWordCount;
    private Button mBtnDownLoad;
    private EditText mEtComment;
    private ImageButton mBtnSend;
    private Book mCurrentBook;
    private ArrayList<Comment> mCommentList;
    private ListView mLvComments;
    private TextView mViewNoComments;
    private CommentLvAdapter mCommentLvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        setTitle(R.string.bookdetail);

        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mBtnDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
                if (token == null) {
                    ToastUtils.showToast("你没有登陆，请先登陆！");
                    return;
                }
                Button btn = (Button) v;
                CharSequence text = btn.getText();
                if (text.equals("马上看书")) {//跳转看书
                    Intent intent = new Intent(BookDetailActivity.this, ReadActivity.class);
                    intent.putExtra(Constants.KEY_BOOK, mCurrentBook);
                    startActivity(intent);
                    BookUtil.addBookToNovel(mCurrentBook);
                } else {
                    downLoadBook(token, 1);
                }
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mEtComment.getText().toString();
                if (comment == null || comment.length() <= 0) {
                    return;
                }
                String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
                postComment(token, mCurrentBook.bookID, comment);
                mEtComment.setText("");
            }
        });
    }

    private void initData() {
        mCommentList = new ArrayList<>();
        mCurrentBook = (Book) getIntent().getSerializableExtra(Constants.KEY_BOOK);

        String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
        getComments(token, mCurrentBook.bookID);
    }

    private void initView() {
        mTvName = (TextView) findViewById(R.id.id_tv_detail_bookname);
        mViewNoComments = (TextView) findViewById(R.id.id_view_no_comment);
        mLvComments = (ListView) findViewById(R.id.id_lv_comments);
        mTvAuthor = (TextView) findViewById(R.id.id_tv_author);
        mTvSummary = (TextView) findViewById(R.id.id_tv_book_summary);
        mTvWordCount = (TextView) findViewById(R.id.id_tv_wordcount);
        mBtnDownLoad = (Button) findViewById(R.id.id_btn_download);
        mBtnSend = (ImageButton) findViewById(R.id.id_btn_send);
        mEtComment = (EditText) findViewById(R.id.id_et_comment);

        boolean existLocal = BookUtil.hasSaveToLocal(mCurrentBook);
        mBtnDownLoad.setText(existLocal ? "马上看书" : "下载");

        LogUtil.i("hasSaveToLocal = " + BookUtil.hasSaveToLocal(mCurrentBook));
        LogUtil.i("hasAddToNovel = " + BookUtil.hasAddToNovel(mCurrentBook));

        //TODO 将拼接字符串加入资源文件
        mTvName.setText("《" + mCurrentBook.getBookName() + "》");
        mTvAuthor.setText(mCurrentBook.getAuthor() + "\t 著");
        mTvSummary.setText("\t" + mCurrentBook.getSummary());
        mTvWordCount.setText("字数：" + mCurrentBook.getWordCount() + "字");
    }

    private void downLoadBook(final String token, final int catalogueNo) {
        LogUtil.i("DOWNLOAD", "----------downLoadBook------  bookID =" + mCurrentBook.bookID + "   catalogueNo = " + catalogueNo);
        setBtnStatus(true);
        RequestParams params = new RequestParams();
        params.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        params.addBodyParameter(Constants.KEY_USER_BOOK_ID, mCurrentBook.bookID);
        params.addBodyParameter(Constants.KEY_USER_CHAPTER_NO, catalogueNo + "");
        params.setAsJsonContent(true);
        params.setUri(API.URL_BASE_GETBOOK);
        x.http().post(params, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                LogUtil.i("DOWNLOAD", "result = " + result);
                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
                    LogUtil.i("response.content = " + response.content);
                    String bookPath = Constants.PATH_BOOK_BASE + File.separator + mCurrentBook.bookID;

                    File file = new File(bookPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (mCurrentBook.getBookPath() == null) {
                        mCurrentBook.setBookPath(bookPath);
                    }
                    FileUtils.writeFile(bookPath + File.separator + catalogueNo + ".txt", response.content, false);
                    if (catalogueNo + 1 <= mCurrentBook.getChapterCount()) {
                        downLoadBook(token, catalogueNo + 1);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                LogUtil.e("DOWNLOAD", "ex = " + ex);
                mBtnDownLoad.setText("再次下载");
            }

            @Override
            public void onFinished() {
                super.onFinished();
                setBtnStatus(false);
            }
        });
    }

    private void getComments(String token, String bookId) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        params.addBodyParameter(Constants.KEY_USER_BOOK_ID, bookId);
        params.addBodyParameter(Constants.KEY_USER_BOOK_COUNT, 100 + "");
        params.setAsJsonContent(true);
        params.setUri(API.URL_BASE_GETCOMMENT);
        x.http().post(params, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                LogUtil.i("BOOK COMMENT", "result = " + result);
                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray commentsJson = jsonObject.getJSONArray("comments");

                        LogUtil.i("BOOK COMMENT", "commentsJson.len = " + commentsJson.length());
//                        mCommentList = new ArrayList<>();
//                        mCommentList.clear();
                        for (int i = 0; i < commentsJson.length(); i++) {
                            JSONObject jsonComment = commentsJson.optJSONObject(i);
                            String name = jsonComment.optString("username");
                            long time = jsonComment.optLong("time");
                            String content = jsonComment.optString("content");
                            String userhead = jsonComment.optString("userhead");
                            Comment comment = new Comment(name, time, content, userhead);
                            if (mCommentList.contains(comment)) {
                                continue;
                            }
                            mCommentList.add(comment);
                        }
                        initLvComments();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                LogUtil.i("BOOK COMMENT", "ex = " + ex);
            }
        });
    }


    private void postComment(final String token, final String bookId, String comment) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        params.addBodyParameter(Constants.KEY_USER_BOOK_ID, bookId);
        params.addBodyParameter(Constants.KEY_USER_COMMENT, comment);
        params.setAsJsonContent(true);
        params.setUri(API.URL_BASE_POSTCOMMENT);
        x.http().post(params, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                LogUtil.i("BOOK POSTCOMMENT", "result = " + result);

                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
//                    initLvComments();
                    getComments(token, bookId);
                    ToastUtils.showToast("评论成功！");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                LogUtil.i("BOOK COMMENT", "ex = " + ex);
            }
        });
    }

    private void initLvComments() {
        LogUtil.i("mCommentList.size = " + mCommentList.size());
        if (mCommentList.size() <= 0) {
            mViewNoComments.setVisibility(View.VISIBLE);
        } else {
            mViewNoComments.setVisibility(View.GONE);
        }

        if (mCommentLvAdapter == null) {
            mCommentLvAdapter = new CommentLvAdapter(this, mCommentList);
            mLvComments.setAdapter(mCommentLvAdapter);
        } else {
            mCommentLvAdapter.notifyDataSetChanged();
        }

    }


    private void setBtnStatus(boolean isDownloading) {
        mBtnDownLoad.setEnabled(!isDownloading);
        mBtnDownLoad.setText(isDownloading ? "正在下载．．．" : "马上看书");
    }
}
