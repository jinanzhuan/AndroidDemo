package com.test.androiddemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;


/**
 * <pre>
 *     author :
 *     e-mail :
 *     time   :
 *     desc   : 参考https://www.jianshu.com/p/2d63442d01b1
 *     modify :
 * </pre>
 */
public class LevitateHeaderExpandListView extends ExpandableListView implements AbsListView.OnScrollListener {
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

    public LevitateHeaderExpandListView(Context context) {
        this(context,null);
    }

    public LevitateHeaderExpandListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevitateHeaderExpandListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView();
    }

    private void iniView() {
        registerListener();
    }

    public void setHeaderView(View view) {
        mHeaderView = view;
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }

        requestLayout();
    }

    private void registerListener() {
        setOnScrollListener(this);
    }

    /**
     * 重写onMeasure计算悬浮条的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    private int mOldState = -1;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
        final int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
        final int childPos = ExpandableListView.getPackedPositionChild(flatPostion);

        if(mAdapter != null) {
            int state = mAdapter.getHeaderState(groupPos, childPos);
            if (mHeaderView != null &&state != mOldState){
                mOldState = state;
                mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            }
        }

        configureHeaderView(groupPos, childPos);
    }

    public void configureHeaderView(int groupPosition, int childPosition) {
        if (mHeaderView == null || mAdapter == null
                || ((ExpandableListAdapter) mAdapter).getGroupCount() == 0) {
            return;
        }

        int state = mAdapter.getHeaderState(groupPosition, childPosition);

        switch (state) {
            case HeaderAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case HeaderAdapter.PINNED_HEADER_VISIBLE: {
                mAdapter.configureHeader(mHeaderView, groupPosition, childPosition, MAX_ALPHA);

                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }

                mHeaderViewVisible = true;

                break;
            }

            case HeaderAdapter.PINNED_HEADER_PUSHED_UP: {
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

                mAdapter.configureHeader(mHeaderView, groupPosition, childPosition, alpha);

                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }

                mHeaderViewVisible = true;
                break;
            }
        }
    }

    /**
     * 列表界面更新时调用该方法(如滚动时)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            //分组栏是直接绘制到界面中，而不是加入到ViewGroup中
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //获取该item在listview中的原始position
        // (the raw position of an item (child or group) in the list)
        final long flatPos = getExpandableListPosition(firstVisibleItem);

        //返回经过包装后的group的position，即当前的group在所有group中的第几个(从0开始)。
        // 参数是getExpandableListPosition()的返回值
        int groupPosition = ExpandableListView.getPackedPositionGroup(flatPos);

        //返回经过包装后的child的position，即在该group中该child的下标(从0开始)。
        // 如果该group中没有child或者显示的是group界面，那么返回的是-1
        int childPosition = ExpandableListView.getPackedPositionChild(flatPos);

        configureHeaderView(groupPosition, childPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (HeaderAdapter) adapter;
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
         *
         * @param groupPosition
         * @param childPosition
         * @return PINNED_HEADER_GONE, PINNED_HEADER_VISIBLE, PINNED_HEADER_PUSHED_UP 其中之一
         */
        int getHeaderState(int groupPosition, int childPosition);

        /**
         * 配置 Header, 让 Header 知道显示的内容
         *
         * @param header
         * @param groupPosition
         * @param childPosition
         * @param alpha
         */
        void configureHeader(View header, int groupPosition, int childPosition, int alpha);

    }
}
