package com.qrtx.areading.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.responses.Response;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.SPUtil;
import com.qrtx.areading.utils.ToastUtils;
import com.qrtx.areading.utils.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LoginActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private EditText mEtAccount, mEtPassword;
    private Button mBtnLogin, mBtnRegister, mBtnSkipLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserUtil.checkIsLogin()) {
            openMainPager();
        }

        setContentView(R.layout.activity_login);

        initDatas();
        initViews();
        initEvents();
    }

    private void initDatas() {
        mLoginDialog = ProgressDialogUtil.creatProgressDialog(this, "正在登陆．．．");
    }

    private void initEvents() {
        mEtAccount.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);

        mBtnLogin.setOnClickListener(this);
        mBtnSkipLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);

        mBtnRegister.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mEtAccount.setText("15291773347");
                mEtPassword.setText("123456");
                return true;
            }
        });
    }

    private void initViews() {
        mEtAccount = (EditText) findViewById(R.id.id_et_account);
        mEtPassword = (EditText) findViewById(R.id.id_et_pwd);
        mBtnLogin = (Button) findViewById(R.id.id_btn_login);
        mBtnRegister = (Button) findViewById(R.id.id_btn_register);
        mBtnSkipLogin = (Button) findViewById(R.id.id_btn_skip_login);
    }

    private void login(String account, String pwd) {
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.addBodyParameter(Constants.KEY_USER_PHONE, account);
        entity.addBodyParameter(Constants.KEY_USER_PWD, pwd);
        entity.setUri(API.URL_BASE_LOGIN);

        LogUtil.i("登陆---->账号：" + account + "     密码：" + pwd);

        x.http().post(entity, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("xutils3  你访问成功　　result＝" + result);

                Response response = Response.obtionBaseResponse(result);
                mLoginDialog.dismiss();
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject user = jsonObject.getJSONObject("user");
                        SPUtil.putString(Constants.KEY_USER_TOKEN, user.getString(Constants.KEY_USER_TOKEN));
                        SPUtil.putString(Constants.KEY_USER_PHONE, user.getString(Constants.KEY_USER_PHONE));
                        SPUtil.putInt(Constants.KEY_USER_SEX, user.getInt(Constants.KEY_USER_SEX));
                        SPUtil.putString(Constants.KEY_USER_ICON, user.getString(Constants.KEY_USER_ICON));
                        SPUtil.putString(Constants.KEY_USER_NICK, user.getString(Constants.KEY_USER_NICK));
                        ToastUtils.showToast("登陆成功");

                        openMainPager();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("xutils3  你访问失败 " + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mLoginDialog.dismiss();
            }
        });
    }


    public void openMainPager() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtAccount.getText().toString().length() <= 0 || mEtPassword.getText().toString().length() <= 0) {
            mBtnLogin.setEnabled(false);
        } else {
            mBtnLogin.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_login://登陆

                login(mEtAccount.getText().toString(), mEtPassword.getText().toString());
                break;
            case R.id.id_btn_register://注册
                startActivityForResult(new Intent(this, RegisterActivity.class), Constants.REQUEST_CODE_REGISTER);
                break;
            case R.id.id_btn_skip_login://跳过登陆
                openMainPager();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.RESULT_CODE_REGISTER) {
            String phone = data.getStringExtra(Constants.KEY_USER_PHONE);
            String pwd = data.getStringExtra(Constants.KEY_USER_PWD);
            login(phone, pwd);
        }
    }

    private ProgressDialog mLoginDialog;
}
