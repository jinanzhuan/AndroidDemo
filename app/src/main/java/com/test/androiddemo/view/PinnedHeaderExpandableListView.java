package com.test.androiddemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.test.androiddemo.R;

/**
 * <pre>
 *     author : created by ljn
 *     e-mail : liujinan@edreamtree.com
 *     time   : 2018/12/19
 *     desc   :
 *     modify :
 * </pre>
 */
public class PinnedHeaderExpandableListView extends ExpandableListView implements AbsListView.OnScrollListener,ExpandableListView.OnGroupClickListener {

    private int mGroupPosition;

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        registerListener();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListener();
    }

    public PinnedHeaderExpandableListView(Context context) {
        super(context);
        registerListener();
    }

    /**
     * Adapter 接口 . 列表必须实现此接口 .
     */
    public interface HeaderAdapter {
       int PINNED_HEADER_GONE = 0;
       int PINNED_HEADER_VISIBLE = 1;
       int PINNED_HEADER_PUSHED_UP = 2;

        /**
         * 获取 Header 的状态
         * @param groupPosition
         * @param childPosition
         * @return PINNED_HEADER_GONE,PINNED_HEADER_VISIBLE,PINNED_HEADER_PUSHED_UP 其中之一
         */
        int getHeaderState(int groupPosition, int childPosition);

        /**
         * 配置 Header, 让 Header 知道显示的内容
         * @param header
         * @param groupPosition
         * @param childPosition
         * @param alpha
         */
        void configureHeader(View header, int groupPosition, int childPosition, int alpha);

        /**
         * 设置组按下的状态
         * @param groupPosition
         * @param status
         */
        void setGroupClickStatus(int groupPosition, int status);

        /**
         * 获取组按下的状态
         * @param groupPosition
         * @return
         */
        int getGroupClickStatus(int groupPosition);

    }

    private static final int MAX_ALPHA = 255;

    private HeaderAdapter mAdapter;

    /**
     * 用于在列表头显示的 View,mHeaderViewVisible 为 true 才可见
     */
    private View mHeaderView;

    /**
     * 列表头是否可见
     */
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;

    private int mHeaderViewHeight;

    public void setHeaderView(View view) {
        mHeaderView = view;
        mHeaderView.findViewById(R.id.iv_arrow).setVisibility(VISIBLE);
        mHeaderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGroupExpanded(mGroupPosition)) {
                    collapseGroup(mGroupPosition);
                    setSelection(mGroupPosition);
                }else {
                    expandGroup(mGroupPosition);
                }
            }
        });
    }

    private void registerListener() {
        setOnScrollListener(this);
        setOnGroupClickListener(this);
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (HeaderAdapter) adapter;
    }

    /**
     *
     * 点击了 Group 触发的事件 , 要根据根据当前点击 Group 的状态来
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent,View v,int groupPosition,long id) {
        if (mAdapter.getGroupClickStatus(groupPosition) == 0) {
            mAdapter.setGroupClickStatus(groupPosition, 1);
            parent.expandGroup(groupPosition);
            //Header自动置顶
            //parent.setSelectedGroup(groupPosition);

        } else if (mAdapter.getGroupClickStatus(groupPosition) == 1) {
            mAdapter.setGroupClickStatus(groupPosition, 0);
            parent.collapseGroup(groupPosition);
        }

        // 返回 true 才可以弹回第一行 , 不知道为什么
        return true;
    }


    public void configureHeaderView(int groupPosition, int childPosition) {
        if (mHeaderView == null || mAdapter == null
                || ((ExpandableListAdapter) mAdapter).getGroupCount() == 0) {
            return;
        }

        int state = mAdapter.getHeaderState(groupPosition, childPosition);

        switch (state) {
            case HeaderAdapter.PINNED_HEADER_GONE: {
                mHeaderView.setVisibility(GONE);
                break;
            }

            case HeaderAdapter.PINNED_HEADER_VISIBLE: {
                mHeaderView.setVisibility(VISIBLE);
                if (mHeaderView.getTop() != 0){
                    mHeaderView.layout(0, 0, mHeaderView.getWidth(), mHeaderView.getHeight());
                }
                mAdapter.configureHeader(mHeaderView, groupPosition,childPosition, MAX_ALPHA);
                mHeaderViewVisible = true;


                break;
            }

            case HeaderAdapter.PINNED_HEADER_PUSHED_UP: {
                mHeaderView.setVisibility(VISIBLE);
                View firstView = getChildAt(0);
                int bottom = firstView.getBottom();

                // intitemHeight = firstView.getHeight();
                int headerHeight = mHeaderView.getHeight();

                int y;

                int alpha;

                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                } else {
                    y = 0;
                    alpha = MAX_ALPHA;
                }

                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderView.getWidth(), mHeaderView.getHeight() + y);
                }
                mAdapter.configureHeader(mHeaderView, groupPosition,childPosition, alpha);
                mHeaderViewVisible = true;

                break;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final long flatPos = getExpandableListPosition(firstVisibleItem);
        mGroupPosition = ExpandableListView.getPackedPositionGroup(flatPos);
        int childPosition = ExpandableListView.getPackedPositionChild(flatPos);

        configureHeaderView(mGroupPosition, childPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}