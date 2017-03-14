package com.qrtx.areading.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.utils.ToastUtils;
import com.recker.flybanner.FlyBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-2-28.
 */

public class DiscoveryFragment extends BaseFragment {
    private FlyBanner mBannerLocal;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionTitle(Constants.TITLE_PAGER_DISCOVERY);
        View bootView = addContentView(R.layout.fragment_discovery);
        initView(bootView);
    }

    private void initView(View bootView) {
        initLocalBanner(bootView);
    }

    /**
     * 加载本地图片
     */
    private void initLocalBanner(View bootView) {
        mBannerLocal = (FlyBanner) bootView.findViewById(R.id.banner_1);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.a);
        images.add(R.drawable.b);
        images.add(R.drawable.c);
        images.add(R.drawable.d);
        images.add(R.drawable.e);
        mBannerLocal.setImages(images);

        mBannerLocal.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastUtils.showToast("点击了第" + position + "张图片");
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActionTitle(Constants.TITLE_PAGER_DISCOVERY);
        }
    }

    /**
     * 加载网页图片
     */
//    private void initNetBanner() {
//        mBannerNet = (FlyBanner) findViewById(R.id.banner_2);
//
//        List<String> imgesUrl = new ArrayList<>();
//        for (int i = 0; i < mImagesUrl.length; i++) {
//            imgesUrl.add(mImagesUrl[i]);
//        }
//        mBannerNet.setImagesUrl(imgesUrl);
//
//        mBannerNet.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                toast("点击了第" + position + "张图片");
//            }
//        });
//    }

}
