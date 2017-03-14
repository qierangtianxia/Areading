package com.qrtx.areading.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.qrtx.areading.R;

public class PersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_three_toolbar);

        setSupportActionBar(toolbar);
    }
}
