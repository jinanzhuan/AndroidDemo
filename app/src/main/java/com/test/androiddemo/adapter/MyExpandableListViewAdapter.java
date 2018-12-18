package com.test.androiddemo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.test.androiddemo.R;
import com.test.androiddemo.bean.ExpandBean;
import com.test.androiddemo.view.LevitateHeaderExpandListView;

import java.util.List;


/**
 * <pre>
 *     author : created by ljn
 *     e-mail : liujinan@edreamtree.com
 *     time   : 2018/12/18
 *     desc   :
 *     modify :
 * </pre>
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter implements LevitateHeaderExpandListView.HeaderAdapter {
    private List<ExpandBean> mData;
    ExpandableListView listView;

    public MyExpandableListViewAdapter(ExpandableListView listView, List<ExpandBean> data) {
        mData = data;
        this.listView = listView;
    }

    /**
     * 获取分组个数
     * @return
     */
    @Override
    public int getGroupCount() {
        return mData == null?0:mData.size();
    }

    /**
     * 获取指定分组中子选项的个数
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getChilds()==null?0:mData.get(groupPosition).getChilds().size();
    }

    /**
     * 获取指定的分组数据
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    /**
     * 获取指定分组中的指定子选项数据
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getChilds().get(childPosition);
    }

    /**
     * 获取指定分组的ID, 这个ID必须是唯一的
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取子选项的ID, 这个ID必须是唯一的
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 获取显示指定分组的视图
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ParentViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_parent, null);
            holder = new ParentViewHolder();
            holder.mParentName = convertView.findViewById(R.id.tv_group_name);
            holder.mcbGroup = convertView.findViewById(R.id.cb_group);
            convertView.setTag(holder);
        }else {
            holder = (ParentViewHolder) convertView.getTag();
        }
        final ExpandBean bean = mData.get(groupPosition);
        holder.mParentName.setText(bean.getName());
        holder.mcbGroup.setChecked(bean.isChecked());
        holder.mcbGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean.setChecked(!bean.isChecked(), true);
                holder.mcbGroup.setChecked(!bean.isChecked());
                notifyDataSetInvalidated();
            }
        });
        return convertView;
    }

    /**
     * 获取显示指定分组中的指定子选项的视图
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_child, null);
            holder = new ChildViewHolder();
            holder.mChildView = convertView.findViewById(R.id.tv_child_name);
            holder.mCbChild = convertView.findViewById(R.id.cb_child);
            convertView.setTag(holder);
        }else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        ExpandBean parentBean = mData.get(groupPosition);
        final ExpandBean.ExpandChildBean childBean = parentBean.getChilds().get(childPosition);
        if(parentBean.isChecked()) {
            holder.mCbChild.setChecked(true);
        }else {
            holder.mCbChild.setChecked(childBean.isChecked());
        }

        holder.mChildView.setText(childBean.getName());
        holder.mCbChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childBean.setChecked(!childBean.isChecked());
                holder.mCbChild.setChecked(!childBean.isChecked());
            }
        });
        parentBean.setOnGroupCheckedChangeListener(new ExpandBean.OnGroupCheckedChangeListener() {
            @Override
            public void onGroupCheckedChanged(boolean isChecked, boolean isClick) {
                if(isClick) {
                    holder.mCbChild.setChecked(isChecked);
                    childBean.setChecked(isChecked);
                }else {
                    if(isChecked) {
                        holder.mCbChild.setChecked(true);
                        childBean.setChecked(true);
                    }
                }

            }
        });
        return convertView;
    }

    /**
     * 指定位置上的子元素是否可选中
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getHeaderState(int groupPosition, int childPosition) {
        int childCount = getChildrenCount(groupPosition);
        if (childPosition == childCount - 1) {
            return PINNED_HEADER_PUSHED_UP;
        } else if (childPosition == -1
                && !listView.isGroupExpanded(groupPosition)) {
            return PINNED_HEADER_GONE;
        } else {
            return PINNED_HEADER_VISIBLE;
        }
    }

    @Override
    public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
        String groupData = ((ExpandBean)getGroup(groupPosition)).getName();//悬浮条的显示信息
        TextView tvName = header.findViewById(R.id.tv_header_name);
        tvName.setText(groupData);
    }

    public class ParentViewHolder {
        public TextView mParentName;
        CheckBox mcbGroup;
    }

    public class ChildViewHolder {
        public TextView mChildView;
        CheckBox mCbChild;
    }
}
