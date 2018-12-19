package com.test.androiddemo.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.androiddemo.BaseActivity;
import com.test.androiddemo.R;
import com.test.androiddemo.adapter.MyExpandableListViewAdapter;
import com.test.androiddemo.bean.ExpandBean;
import com.test.androiddemo.view.PinnedHeaderExpandableListView;

/**
 * <pre>
 *     author : created by ljn
 *     e-mail : liujinan@edreamtree.com
 *     time   : 2018/12/18
 *     desc   :
 *     modify :
 * </pre>
 */
public class ExpandableListViewActivity extends BaseActivity {
    private PinnedHeaderExpandableListView elv_list;
    private LinearLayout ll_check_all;
    private CheckBox cb_check_all;
    private TextView tv_create_pdf;
    private View mHeaderView;
    private MyExpandableListViewAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_expandable_list_view;
    }

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, ExpandableListViewActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initView() {
        super.initView();
        elv_list = findViewById(R.id.elv_list);
        ll_check_all = findViewById(R.id.ll_check_all);
        cb_check_all = findViewById(R.id.cb_check_all);
        tv_create_pdf = findViewById(R.id.tv_create_pdf);
        mHeaderView = findViewById(R.id.header);
        elv_list.setHeaderView(mHeaderView);
    }

    @Override
    public void initListener() {
        super.initListener();
        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(ExpandableListViewActivity.this, "groupPosition="+groupPosition+"\u3000childPosition="+childPosition, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        elv_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(ExpandableListViewActivity.this, "groupPosition="+groupPosition, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        elv_list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Log.e("TAG", "onGroupCollapse groupPosition="+groupPosition);
            }
        });
        elv_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Log.e("TAG", "onGroupExpand groupPosition="+groupPosition);
            }
        });
        ll_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_check_all.isChecked();
                cb_check_all.setChecked(!checked);
                mAdapter.setAllChecked(!checked);
            }
        });
        tv_create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExpandableListViewActivity.this, "生成PDF", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void initData() {
        super.initData();
        mAdapter = new MyExpandableListViewAdapter(elv_list, ExpandBean.getData());
        elv_list.setAdapter(mAdapter);

        mAdapter.setOnGroupSelectedListener(new MyExpandableListViewAdapter.OnGroupSelectedListener() {
            @Override
            public void onGroupSelect(boolean isAllSelected) {
                cb_check_all.setChecked(isAllSelected);
            }
        });
    }

}
