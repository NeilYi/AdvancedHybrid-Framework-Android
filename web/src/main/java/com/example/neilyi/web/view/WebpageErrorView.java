package com.example.neilyi.web.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.neilyi.web.R;
import com.example.neilyi.web.utils.ViewUtil;

/**
 * Loading error view
 *
 * Created by NeilYi on 2016/12/1.
 */
public class WebpageErrorView extends RelativeLayout {
    private ImageView mErrorImageAnimIv;
    private Button mReFreshBtn;
    private TextView mNetSettingTv;
    private AnimationDrawable mErrorImageAnim;

    private Context mContext;

    public WebpageErrorView(Context context) {
        super(context);
        mContext = context;
    }

    public WebpageErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public WebpageErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private View.OnClickListener mNetSettingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mContext && mContext instanceof Activity && !((Activity)mContext).isFinishing()) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                mContext.startActivity(intent);
            }
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initView();
    }

    private void initView() {
        mErrorImageAnimIv = (ImageView) findViewById(R.id.web_error_view_iv);
        mReFreshBtn = (Button) findViewById(R.id.web_error_view_btn);
        mNetSettingTv = (TextView) findViewById(R.id.web_error_net_setting_tv);
        mNetSettingTv.setOnClickListener(mNetSettingClickListener);

        try {
            mErrorImageAnimIv.setBackgroundResource(R.drawable.web_load_error);
            mErrorImageAnim = (AnimationDrawable) mErrorImageAnimIv.getBackground();
        } catch (Exception e) {
            if (null == mErrorImageAnim) {
                return;
            }

            mErrorImageAnim.stop();
        }
    }

    /**
     * 开始动画
     */
    public void startImgAnim() {
        if (mErrorImageAnim != null && !mErrorImageAnim.isRunning()) {
            mErrorImageAnim.start();
        }
    }

    /**
     * 结束动画
     */
    public void stopImgAnim() {
        if (mErrorImageAnim != null && mErrorImageAnim.isRunning()) {
            mErrorImageAnim.stop();
        }
    }

    /**
     * 设置刷新按钮点击事件.
     */
    public void setRefreshBtnClickListener(View.OnClickListener refreshClickListener) {
        if (null != refreshClickListener) {
            if (null != mReFreshBtn) {
                mReFreshBtn.setOnClickListener(refreshClickListener);
            }
        }
    }

    /**
     * 资源释放
     */
    public void releaseResource() {
        if (null != mNetSettingClickListener) {
            mNetSettingClickListener = null;
        }

        if (null != mContext) {
            mContext = null;
        }

        if (null != mErrorImageAnimIv) {
            mErrorImageAnimIv.clearAnimation();
            mErrorImageAnimIv.setBackgroundResource(0);
            ViewUtil.setCustomBackground(mErrorImageAnimIv, null);
            mErrorImageAnimIv = null;
        }

        if (null != mErrorImageAnim) {
            mErrorImageAnim.stop();
            mErrorImageAnim = null;
        }

        if (null != mReFreshBtn) {
            mReFreshBtn.setOnClickListener(null);
            mReFreshBtn = null;
        }
    }
}
