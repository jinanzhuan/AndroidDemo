package com.test.androiddemo.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.test.androiddemo.BaseActivity;
import com.test.androiddemo.R;
import com.test.androiddemo.adapter.MyExpandableListViewAdapter;
import com.test.androiddemo.bean.ExpandBean;
import com.test.androiddemo.view.LevitateHeaderExpandListView;

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
    private LevitateHeaderExpandListView elv_list;
    private ExpandableListAdapter mAdapter;

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
        elv_list.setHeaderView(View.inflate(this, R.layout.item_header_msg_listview, null));
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
//        elv_list.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.e("TAG", "firstVisibleItem="+firstVisibleItem+"\u3000visibleItemCount="+visibleItemCount+"\u3000totalItemCount="+totalItemCount);
//            }
//        });
    }

    @Override
    public void initData() {
        super.initData();
        mAdapter = new MyExpandableListViewAdapter(elv_list, ExpandBean.getData());
        elv_list.setAdapter(mAdapter);
    }
}
