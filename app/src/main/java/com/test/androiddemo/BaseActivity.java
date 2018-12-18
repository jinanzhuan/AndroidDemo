package com.test.androiddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * <pre>
 *     author : created by ljn
 *     e-mail : liujinan@edreamtree.com
 *     time   : 2018/7/30
 *     desc   : 作为activity的基类
 *     modify :
 * </pre>
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        if(initState()) {
            return;
        }
        setContentView(getLayoutId());
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化状态，以决定是否加载布局，或者跳转到其他页面
     * @return
     */
    public boolean initState() {
        return false;
    }

    /**
     * 获取intent
     */
    public void initIntent() {}

    /**
     * 初始化布局
     */
    public void initView() {}

    /**
     * 初始化监听
     */
    public void initListener() {}

    /**
     * 初始化数据
     */
    public void initData() {}

    /**
     * 获取布局
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化toolbar
     * @param toolbar
     */
    public void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//有返回
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示title
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
