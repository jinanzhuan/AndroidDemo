package com.test.androiddemo.bean;

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
public class ExpandBean {
    private String huid;
    private String name;
    private boolean checked;
    private List<ExpandChildBean> childs;
    private OnGroupCheckedChangeListener mChangeListener;
    private List<String> childSelectedList;

    public String getHuid() {
        return huid;
    }

    public void setHuid(String huid) {
        this.huid = huid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked, boolean isClick){
        this.checked = checked;
        makeChildSelected(checked, isClick);
    }

    private void makeChildSelected(boolean checked, boolean isClick) {
        if(isClick && childs != null && !childs.isEmpty()) {
            for(int i = 0; i < childs.size(); i++) {
                childs.get(i).setChecked(checked);
            }
        }
    }

    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    public List<ExpandChildBean> getChilds() {
        setChildSelectedList();
        return childs;
    }

    public int getChildSelectedSize(){
        int num = 0;
        if(childs != null && !childs.isEmpty()) {
            for(int i = 0; i < childs.size(); i++) {
                if(childs.get(i).isChecked()) {
                    num++;
                }
            }
        }
        return num;
    }

    public void setChilds(List<ExpandChildBean> childs) {
        this.childs = childs;
    }

    public void setSingleChildChecked(String huid, boolean isChecked) {
        if(childSelectedList == null) {
            childSelectedList = new ArrayList<>();
        }
        if(isChecked) {
            if(!childSelectedList.contains(huid)) {
                childSelectedList.add(huid);
            }
        }else {
            if(childSelectedList.contains(huid)) {
                childSelectedList.remove(huid);
            }
        }
        if(childs != null) {
            setChecked(childs.size() == childSelectedList.size());
            if(mChangeListener != null) {
                mChangeListener.onGroupCheckedChange(childs.size() == childSelectedList.size(), childSelectedList.size());
            }
        }
    }

    public static class ExpandChildBean {
        private String huid;
        private String name;
        private boolean checked;

        public String getHuid() {
            return huid;
        }

        public void setHuid(String huid) {
            this.huid = huid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    public static List<ExpandBean> getData() {
        List<ExpandBean> parentList = new ArrayList<>();
        ExpandBean parentBean;
        for(int i = 0; i < 20; i++) {
            parentBean = new ExpandBean();
            parentBean.setName("一级列表"+(i+1));
            parentBean.setHuid(i+1+"");
            List<ExpandChildBean> childBeanList = getChildBeanList(parentBean.getName());
            parentBean.setChilds(childBeanList);
            parentList.add(parentBean);
        }
        return parentList;
    }

    private static List<ExpandChildBean> getChildBeanList(String name) {
        List<ExpandChildBean> list = new ArrayList<>();
        ExpandChildBean bean;
        for(int i = 0; i < 10; i++) {
            bean = new ExpandChildBean();
            bean.setName(name + "\u0020二级列表"+(i+1));
            bean.setHuid(i+1+"");
            list.add(bean);
        }
        return list;
    }


    public void setOnGroupCheckedChangeListener(OnGroupCheckedChangeListener checkedChangeListener){
        this.mChangeListener = checkedChangeListener;
    }

    public interface OnGroupCheckedChangeListener {
        /**
         * 一级列表选中状态改变
         * @param isChecked
         * @param size 子类被选中的个数
         */
        void onGroupCheckedChange(boolean isChecked, int size);
    }

    public void setChildSelectedList() {
        childSelectedList = new ArrayList<>();
        if(childs != null && !childs.isEmpty()) {
            for(int i = 0; i < childs.size(); i++) {
                if(childs.get(i).isChecked() && !childSelectedList.contains(childs.get(i).getHuid())) {
                    childSelectedList.add(childs.get(i).getHuid());
                }
            }
        }
    }

}
