package com.qrtx.areading.ui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qrtx.areading.R;
import com.qrtx.areading.ui.activity.MainActivity;
import com.qrtx.areading.utils.LogUtil;

/**
 * Created by user on 17-2-28.
 */

public class BaseFragment extends Fragment {
    protected FragmentActivity mBootActivity;
    protected FrameLayout mFragmentContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBootActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mFragmentContainer = (FrameLayout) view.findViewById(R.id.id_container_fragment);
        LogUtil.i(getClass().getSimpleName()+"--onCreateView");
        return view;
    }


    /**
     * 添加内容布局
     *
     * @param view
     * @return
     */
    protected View addContentView(View view) {
        if (view != null) {
            mFragmentContainer.addView(view);
            return view;
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * 添加内容布局
     *
     * @param id
     */
    protected View addContentView(@LayoutRes int id) {
        View view = View.inflate(mBootActivity, id, null);
        return addContentView(view);
    }

    /**
     * 更改顶部标题
     *
     * @param title
     */
    public void setActionTitle(CharSequence title) {
        if (mBootActivity instanceof MainActivity) {
            ((MainActivity) mBootActivity).setActionTitle(title);
        }
    }
}
