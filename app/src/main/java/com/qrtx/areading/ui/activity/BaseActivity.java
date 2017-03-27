package com.qrtx.areading.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 17-3-21.
 */

public class BaseActivity extends AppCompatActivity {
    String name = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LogUtil.i("ALIVE", name + "  <---------onCreate---------->");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        LogUtil.i("ALIVE", name + "  <---------onStart---------->");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        LogUtil.i("ALIVE", name + "  <---------onRestart---------->");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LogUtil.i("ALIVE", name + "  <---------onResume---------->");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LogUtil.i("ALIVE", name + "  <---------onPause---------->");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        LogUtil.i("ALIVE", name + "  <---------onStop---------->");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtil.i("ALIVE", name + "  <---------onDestroy---------->");
    }
}
