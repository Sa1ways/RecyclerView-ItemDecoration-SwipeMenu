package cn.shawn.view.recyclerview.iinterface;

import cn.shawn.view.recyclerview.utils.ViewHolder;

/**
 * Created by root on 17-6-4.
 */

public interface ItemTypeDelegate<T> {

    /**
     * 获取布局id
     * @return
     */
    int getItemLayoutId();

    /**
     * 当多布局时 判断item的type , 该方法在 Adapter中getItemViewType()中使用
     * @param position
     * @param t
     * @return
     */
    boolean isTypeForItem(int position , T t);

    /**
     * 填充数据
     * @param holder
     * @param position
     * @param info
     */
    void convert(ViewHolder holder , int position , T info);
}
