package com.qrtx.areading.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.responses.Response;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.SPUtil;
import com.qrtx.areading.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEtAccount, mEtPassword, mEtNick;
    private Button mBtnConfirm, mBtnCancle;
    private ProgressDialog mLoginDialog;
    private RadioGroup mSexSelect;
    private ToggleButton mToggleButton;
    private int mSex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.register);
        initDatas();
        initViews();
        initEvents();
    }

    private void initEvents() {
        mBtnConfirm.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
        mSexSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSex = checkedId == R.id.id_rbtn_boy ? 1 : 0;
            }
        });

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEtPassword.setSelection(mEtPassword.length());
            }
        });
    }

    private void initDatas() {
        mLoginDialog = ProgressDialogUtil.creatProgressDialog(this, "正在注册．．．");
    }

    private void initViews() {
        mToggleButton = (ToggleButton) findViewById(R.id.id_tbtn_register);
        mEtAccount = (EditText) findViewById(R.id.id_et_phone);
        mEtPassword = (EditText) findViewById(R.id.id_et_register_pwd);
        mEtNick = (EditText) findViewById(R.id.id_et_nick);
        mSexSelect = (RadioGroup) findViewById(R.id.id_sex_radio);
        mSexSelect.check(R.id.id_rbtn_boy);

        mBtnConfirm = (Button) findViewById(R.id.id_btn_confirm);
        mBtnCancle = (Button) findViewById(R.id.id_btn_cancle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_confirm:
                String phone = mEtAccount.getText().toString();
                String pwd = mEtPassword.getText().toString();
                String nick = mEtNick.getText().toString();
                LogUtil.i("CHECK", "onClick");
                if (!TextUtils.isEmpty(phone)
                        && !TextUtils.isEmpty(pwd)
                        && mSex >= 0
                        && !TextUtils.isEmpty(nick)) {
                    String regex = "^1[3|4|5|8][0-9]\\d{8}$";
                    if (!phone.matches(regex)) {
                        ToastUtils.showToast("请输入正确的手机号码！");
                        LogUtil.i("CHECK", "phone = " + phone);
                        return;
                    }
                    register(phone, pwd, nick, mSex, null);
                } else {
                    ToastUtils.showToast("请将资料填写完整！");
                }
                break;
            case R.id.id_btn_cancle:
                finish();
                break;
        }
    }

    private void register(final String phone, final String pwd, String nick, int sex, String icon) {
        RequestParams entity = new RequestParams();
        entity.setAsJsonContent(true);
        entity.addBodyParameter(Constants.KEY_USER_PHONE, phone);
        entity.addBodyParameter(Constants.KEY_USER_NICK, nick);
        entity.addBodyParameter(Constants.KEY_USER_SEX, sex + "");
        entity.addBodyParameter(Constants.KEY_USER_PWD, pwd);
        entity.setUri(API.URL_BASE_REGISTER);

        mLoginDialog.show();
        x.http().post(entity, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("register result＝" + result);

                Response response = Response.obtionBaseResponse(result);
                mLoginDialog.dismiss();
                if (response.responseCode != Response.RESPONSE_OK) {
                    ToastUtils.showToast(response.responseMsg);
                } else {

                    ToastUtils.showToast("注册成功");
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_USER_PHONE, phone);
                    intent.putExtra(Constants.KEY_USER_PWD, pwd);
                    setResult(Constants.RESULT_CODE_REGISTER, intent);
                    finish();
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
}
