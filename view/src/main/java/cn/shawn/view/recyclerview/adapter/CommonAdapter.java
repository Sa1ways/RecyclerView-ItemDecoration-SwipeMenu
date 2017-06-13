package cn.shawn.view.recyclerview.adapter;

import java.util.List;

import cn.shawn.view.recyclerview.iinterface.ItemTypeDelegate;
import cn.shawn.view.recyclerview.utils.ViewHolder;

/**
 * Created by root on 17-6-4.
 */

public abstract class CommonAdapter<T> extends BaseMultiAdapter<T> {

    public CommonAdapter() {
        mDelegateManager.addDelegate(new ItemTypeDelegate<T>() {
            @Override
            public int getItemLayoutId() {
                return CommonAdapter.this.getItemLayoutId();
            }

            @Override
            public boolean isTypeForItem(int position, T t) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, int position, T info) {
                CommonAdapter.this.convert(holder ,position ,info);
            }
        });
    }

    public void showData(List<T> data){
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void appendData(List<T> data){
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public abstract int getItemLayoutId();

    public abstract void convert(ViewHolder holder , int position ,T info);

}
