package com.qrtx.areading.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.qrtx.areading.R;

import java.util.Timer;
import java.util.TimerTask;

public class GuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);


        ImageView imageView = (ImageView) findViewById(R.id.animation_iv);
        imageView.setImageResource(R.drawable.fram_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();

        animationDrawable.start();


        Animation alpha = AnimationUtils.loadAnimation(this,
                R.anim.lead_anim);

        ImageView imageViewBag = (ImageView) findViewById(R.id.image_bag);
        imageViewBag.startAnimation(alpha);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 5000);
    }
}
