package com.qrtx.areading.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.SimpleCommonCallback;
import com.qrtx.areading.net.responses.CommonCallback;
import com.qrtx.areading.net.responses.Response;
import com.qrtx.areading.utils.BitmapUtil;
import com.qrtx.areading.utils.FileUtils;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.MD5Util;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.SPUtil;
import com.qrtx.areading.utils.StringUtils;
import com.qrtx.areading.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PersionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvNike, mTvSex, mTvAge, mTvPhone, mTvHabby;
    private Button mBtnLogout;
    private RelativeLayout mRlSetIcon;
    private PopupWindow mPopupWindow;
    private WindowManager.LayoutParams mLayoutParams;
    private Window mWindow;
    private ImageView mIvIcon;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion);
        setTitle("用户中心");
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        mTvNike.append(SPUtil.getString(Constants.KEY_USER_NICK, ""));
        mTvPhone.append(SPUtil.getString(Constants.KEY_USER_PHONE, ""));
        int sex = SPUtil.getInt(Constants.KEY_USER_SEX, -1);
        if (sex == 1) {
            mTvSex.append("男");
        } else if (sex == 2) {
            mTvSex.append("女");
        }
    }

    private void initEvent() {
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.remove(Constants.KEY_USER_TOKEN);
                SPUtil.remove(Constants.KEY_USER_PHONE);
                SPUtil.remove(Constants.KEY_USER_NICK);
                SPUtil.remove(Constants.KEY_USER_SEX);
                finish();
            }
        });

        mRlSetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
    }


    private void initView() {
        mTvNike = (TextView) findViewById(R.id.id_tv_nike_pro);
        mTvSex = (TextView) findViewById(R.id.id_tv_sex_pro);
        mTvAge = (TextView) findViewById(R.id.id_tv_age_pro);
        mTvPhone = (TextView) findViewById(R.id.id_tv_phone_pro);
        mTvHabby = (TextView) findViewById(R.id.id_tv_habby_pro);
        mRlSetIcon = (RelativeLayout) findViewById(R.id.id_rl_set_icon);
        mIvIcon = (ImageView) findViewById(R.id.id_iv_icon_pro);

        mBtnLogout = (Button) findViewById(R.id.id_btn_logout_pro);

        createPopupWindow();

        String resId = SPUtil.getString(Constants.KEY_USER_ICON, null);
        LogUtil.i("  resId  =  " + resId);
        if (resId != null) {
            getIcon(19 + "");
        }
//        x.image().bind(mIvIcon, API.URL_BASE_GETICON + 19);
    }

    private void createPopupWindow() {
        View view = View.inflate(this, R.layout.view_seticon_popunwindow, null);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();

        View btn1 = view.findViewById(R.id.id_btn1);
        View btn2 = view.findViewById(R.id.id_btn2);
        View btn3 = view.findViewById(R.id.id_btn3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    private void hidePopupWindow() {
        if (mPopupWindow == null) {
            return;
        }
        mPopupWindow.dismiss();
        mLayoutParams.alpha = 1.0f;
        mWindow.setAttributes(mLayoutParams);
    }

    private void showPopupWindow(View v) {
        if (mPopupWindow == null) {
            createPopupWindow();
        }
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        mLayoutParams.alpha = 0.5f;
        mWindow.setAttributes(mLayoutParams);
    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            hidePopupWindow();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn1://打开相机
                mPhotoPath = openCamera();

                break;
            case R.id.id_btn2://打开图册
                openAlbum();
                break;
            case R.id.id_btn3://取消
                break;
        }
        hidePopupWindow();
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.REQUEST_CODE_ALBUM);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA) {

            } else if (requestCode == Constants.REQUEST_CODE_ALBUM) {
                try {
                    Uri uri = data.getData();
                    mPhotoPath = getAbsolutePath(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            LogUtil.i("mPhotoPath = " + mPhotoPath);

            File file = new File(mPhotoPath);
            String token = SPUtil.getString(Constants.KEY_USER_TOKEN, null);
            if (token != null) {
                postIcon(token, file);
            }

        }
    }

    private ProgressDialog progressDialog;

    private void postIcon(final String token, File file) {
        progressDialog =
                ProgressDialogUtil.showProgressDialog(this, "正在上传...");
        RequestParams entity = new RequestParams();
        entity.setMultipart(true);
        entity.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        try {
            entity.addBodyParameter(Constants.KEY_FILE, new FileOutputStream(file), "multipart/form-data", file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        entity.setUri(API.URL_BASE_POSTICON);

        x.http().post(entity, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("postIcon  你访问成功　　result＝" + result);

                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int iconID = jsonObject.getInt(Constants.KEY_USER_ICON_ID);
                        postIconBindUser(token, iconID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("xutils3  你访问失败 " + ex);
                ToastUtils.showToast("上传失败" + ex);
                ToastUtils.showToast(ex.toString());
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }

    private void postIconBindUser(String token, final int resId) {
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.addBodyParameter(Constants.KEY_USER_TOKEN, token);
        entity.addBodyParameter(Constants.KEY_USER_ICON_ID, resId + "");
        entity.setUri(API.URL_BASE_BINDICON);

        x.http().post(entity, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("postIconBindUser  你访问成功　　result＝" + result);

                Response response = Response.obtionBaseResponse(result);
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {

                    Bitmap bitmap = BitmapUtil.getSmallBitmap(mPhotoPath, 150, 150);
                    mIvIcon.setImageBitmap(bitmap);
                    SPUtil.putString(Constants.KEY_USER_ICON, resId + "");
                    ToastUtils.showToast("上传成功");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("xutils3  你访问失败 " + ex);
                ToastUtils.showToast("上传失败" + ex);
                ToastUtils.showToast(ex.toString());
            }

            @Override
            public void onFinished() {
                super.onFinished();
                progressDialog.dismiss();
            }
        });
    }

    private void getIcon(String resId) {
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.setUri(API.URL_BASE_GETICON + resId);
        LogUtil.e("getIcon ");
        x.http().get(entity, new CommonCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                LogUtil.e("getIcon  你访问成功　　result＝" + result.getClass().getSimpleName());

                Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                mIvIcon.setImageBitmap(bitmap);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("xutils3 getIcon 你访问失败 " + ex);
                ToastUtils.showToast(ex.toString());
            }

            @Override
            public void onFinished() {
                super.onFinished();
                LogUtil.e("getIcon  　　onFinished");
            }
        });
    }


    public String getAbsolutePath(Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private String openCamera() {
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
            // 把文件地址转换成Uri格式
            Uri uri = Uri.fromFile(outFile);
            LogUtil.d("getAbsolutePath=" + outFile.getAbsolutePath());
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // 此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            this.startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
            return outFile.getAbsolutePath();
        } else {
            Toast.makeText(this, "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
