package com.example.neilyi.web.view.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.neilyi.web.R;
import com.example.neilyi.web.view.ptr.header.MaterialHeader;
import com.example.neilyi.web.view.ptr.util.PtrLocalDisplay;


/**
 * 下拉刷新控件
 *
 * Created by liyi on 2016/9/20.
 */
public class PullToRefreshFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PullToRefreshFrameLayout";

    private MaterialHeader mPtrMaterialHeader;

    public PullToRefreshFrameLayout(Context context) {
        super(context);
        initViews(context);
    }

    public PullToRefreshFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public PullToRefreshFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(final Context context) {
        if (context == null) {
            return;
        }

        mPtrMaterialHeader = new MaterialHeader(context);
		try {
			int[] colors = context.getResources().getIntArray(R.array.ptr_material_colors);
        	mPtrMaterialHeader.setColorSchemeColors(colors);
        	mPtrMaterialHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        	mPtrMaterialHeader.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        	mPtrMaterialHeader.setPtrFrameLayout(this);
		} catch(Exception e) {
			e.printStackTrace();
		}

        setHeaderView(mPtrMaterialHeader);
        addPtrUIHandler(mPtrMaterialHeader);
    }

    public MaterialHeader getPtrHeaderView() {
        return mPtrMaterialHeader;
    }

    public void addViewForPtrFrameLayout(final View view) {
        if (null == view) {
            return;
        }

        super.addView(view);

        if (null == super.getContentView()) {
            //获取不到子View时再调用一次onFinishInfalte()
            super.onFinishInflate();
        }
    }

    public void releaseResource() {
        if (null != mPtrMaterialHeader) {
            mPtrMaterialHeader.releaseResource();
            mPtrMaterialHeader = null;
        }

        try {
            this.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
