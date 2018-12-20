package com.test.androiddemo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.androiddemo.R;
import com.test.androiddemo.bean.ExpandBean;
import com.test.androiddemo.view.PinnedHeaderExpandableListView;

import java.util.ArrayList;
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
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter implements PinnedHeaderExpandableListView.HeaderAdapter {
    private List<ExpandBean> mData;
    ExpandableListView listView;
    private List<String> selectedList;
    private OnGroupSelectedListener listener;

    public MyExpandableListViewAdapter(ExpandableListView listView, List<ExpandBean> data) {
        mData = data;
        this.listView = listView;
        initSelectedList();
    }

    private void initSelectedList() {
        if(selectedList == null) {
            selectedList = new ArrayList<>();
        }
        if(mData != null && !mData.isEmpty()) {
            for(int i = 0; i < mData.size(); i++) {
                if(mData.get(i).isChecked() && !selectedList.contains(mData.get(i).getHuid())) {
                    selectedList.add(mData.get(i).getHuid());
                }
            }
        }
        if(this.listener != null) {
            listener.onGroupSelect(selectedList.size() == mData.size());
        }
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
            holder.rl_group_check = convertView.findViewById(R.id.rl_group_check);
            convertView.setTag(holder);
        }else {
            holder = (ParentViewHolder) convertView.getTag();
        }
        final ExpandBean bean = mData.get(groupPosition);
        holder.mcbGroup.setChecked(bean.isChecked());
        int childSelectedSize = bean.getChildSelectedSize();
        if(childSelectedSize == 0) {
            holder.mParentName.setText(bean.getName());
        }else {
            holder.mParentName.setText(bean.getName()+"\u0020("+childSelectedSize+")");
        }
        holder.rl_group_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = bean.isChecked();
                bean.setChecked(!checked, true);
                refreshSelectedList(bean.getHuid(), !checked);
                holder.mcbGroup.setChecked(!checked);
                notifyDataSetChanged();
            }
        });
        bean.setOnGroupCheckedChangeListener(new ExpandBean.OnGroupCheckedChangeListener() {
            @Override
            public void onGroupCheckedChange(boolean isChecked, int size) {
                holder.mcbGroup.setChecked(isChecked);
                if(size == 0) {
                    holder.mParentName.setText(bean.getName());
                }else {
                    holder.mParentName.setText(bean.getName()+"\u0020("+size+")");
                }
            }
        });
        return convertView;
    }

    private void refreshSelectedList(String huid, boolean checked) {
        if(checked) {
            if(!selectedList.contains(huid)) {
                selectedList.add(huid);
            }
        }else {
            if(selectedList.contains(huid)) {
                selectedList.remove(huid);
            }
        }
        if(listener != null) {
            listener.onGroupSelect(selectedList.size() == mData.size());
        }
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
        final ExpandBean parentBean = mData.get(groupPosition);
        final ExpandBean.ExpandChildBean childBean = parentBean.getChilds().get(childPosition);
        if(parentBean.isChecked()) {
            childBean.setChecked(true);
            holder.mCbChild.setChecked(true);
        }else {
            holder.mCbChild.setChecked(childBean.isChecked());
        }

        holder.mChildView.setText(childBean.getName());
        holder.mCbChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = childBean.isChecked();
                childBean.setChecked(!checked);
                holder.mCbChild.setChecked(!checked);
                parentBean.setSingleChildChecked(childBean.getHuid(), !checked);
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


    public void setAllChecked(boolean checked) {
        if(mData != null && !mData.isEmpty()) {
            for(int i = 0; i < mData.size(); i++) {
                mData.get(i).setChecked(checked, true);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getHeaderState(int groupPosition, int childPosition) {
        int childCount = getChildrenCount(groupPosition);
        if (childPosition == childCount - 1) {
            return PINNED_HEADER_PUSHED_UP;
        } else if (childPosition == -1 && !listView.isGroupExpanded(groupPosition)) {
            return PINNED_HEADER_GONE;
        } else {
            return PINNED_HEADER_VISIBLE;
        }
    }

    @Override
    public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
        ParentViewHolder holder = (ParentViewHolder) header.getTag();
        if(holder == null) {
            holder = new ParentViewHolder();
            holder.mcbGroup = header.findViewById(R.id.cb_group);
            holder.mParentName = header.findViewById(R.id.tv_group_name);
            holder.rl_group_check = header.findViewById(R.id.rl_group_check);
        }
        final ExpandBean bean = mData.get(groupPosition);
        holder.mcbGroup.setChecked(bean.isChecked());
        int childSelectedSize = bean.getChildSelectedSize();
        if(childSelectedSize == 0) {
            holder.mParentName.setText(bean.getName());
        }else {
            holder.mParentName.setText(bean.getName()+"\u0020("+childSelectedSize+")");
        }
        final ParentViewHolder finalHolder = holder;
        holder.rl_group_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = bean.isChecked();
                bean.setChecked(!checked, true);
                refreshSelectedList(bean.getHuid(), !checked);
                finalHolder.mcbGroup.setChecked(!checked);
                notifyDataSetChanged();
            }
        });
        final ParentViewHolder finalHolder1 = holder;
        bean.setOnGroupCheckedChangeListener(new ExpandBean.OnGroupCheckedChangeListener() {
            @Override
            public void onGroupCheckedChange(boolean isChecked, int size) {
                finalHolder1.mcbGroup.setChecked(isChecked);
                if(size == 0) {
                    finalHolder1.mParentName.setText(bean.getName());
                }else {
                    finalHolder1.mParentName.setText(bean.getName()+"\u0020("+size+")");
                }
            }
        });
    }

    @Override
    public void setGroupClickStatus(int groupPosition, int status) {

    }

    @Override
    public int getGroupClickStatus(int groupPosition) {
        return 0;
    }

    public class ParentViewHolder {
        RelativeLayout rl_group_check;
        TextView mParentName;
        CheckBox mcbGroup;
    }

    public class ChildViewHolder {
        TextView mChildView;
        CheckBox mCbChild;
    }

    public void setOnGroupSelectedListener(OnGroupSelectedListener listener){
        this.listener = listener;
    }

    public interface OnGroupSelectedListener {
        /**
         * 是否全部选中
         * @param isAllSelected
         */
        void onGroupSelect(boolean isAllSelected);
    }
}
