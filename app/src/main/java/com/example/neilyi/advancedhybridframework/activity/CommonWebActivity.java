package com.example.neilyi.advancedhybridframework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.neilyi.advancedhybridframework.R;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;
import com.example.neilyi.advancedhybridframework.fragment.CommonWebFragment;
import com.example.neilyi.advancedhybridframework.utils.ToastUtil;
import com.example.neilyi.web.BaseWebFragment;

/**
 * Created by NeilYi on 2017/11/17.
 */
public class CommonWebActivity extends WebBaseActivity {

    private String mUrl;
    private boolean mDisablePullRefresh = false;

    protected CommonWebFragment mWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_page);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mDisablePullRefresh = intent.getBooleanExtra("disableRefresh", false);

        setWebFragment();
    }

    /**
     * 设置WebFragment
     *
     * Created by liyi on 2016/9/22.
     */
    protected void setWebFragment() {
        mWebFragment = new CommonWebFragment();
        mWebFragment.setUrl(mUrl);
        mWebFragment.setSupprotPullRefresh(mDisablePullRefresh);
        mWebFragment.setOnTitleBarTextListener(new BaseWebFragment.OnTitleBarTextListener() {
            @Override
            public void onTitleBarText(String title) {
//                ToastUtil.showLong("onTitleBarText finished title=" + title);
            }
        });

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.common_web_content_layout, mWebFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
