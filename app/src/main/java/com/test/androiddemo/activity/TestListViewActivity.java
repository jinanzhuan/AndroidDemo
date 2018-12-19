package com.test.androiddemo.activity;

import android.content.Context;
import android.content.Intent;

import com.test.androiddemo.BaseActivity;
import com.test.androiddemo.R;
import com.test.androiddemo.adapter.TestAdater;
import com.test.androiddemo.bean.ExpandBean;
import com.test.androiddemo.view.LevitateHeaderExpandListView;


/**
 * <pre>
 *     author : created by ljn
 *     e-mail : liujinan@edreamtree.com
 *     time   : 2018/12/19
 *     desc   :
 *     modify :
 * </pre>
 */
public class TestListViewActivity extends BaseActivity {
    private LevitateHeaderExpandListView elv_list;

    @Override
    public int getLayoutId() {
        return R.layout.activity_expandable_list_view;
    }

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, TestListViewActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initView() {
        super.initView();
        elv_list = findViewById(R.id.elv_list);
        elv_list.setHeaderView(getLayoutInflater().inflate(R.layout.item_header_msg_listview, elv_list, false));
        elv_list.setAdapter(new TestAdater(elv_list, ExpandBean.getData()));
    }
}
