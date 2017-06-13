package cn.shawn.view.recyclerview.adapter;

import android.os.Handler;

import java.util.List;

/**
 * Created by root on 17-6-4.
 */
public abstract class MultiTypeAdapter<T> extends BaseMultiAdapter<T> {

    public MultiTypeAdapter() {
        addItemTypeDelegate();
    }

    public void showData(final List<T> data){
        /**
         * 确保 LoadMoreView在onLayout前添加进布局
         */
        postDelay(new Runnable() {
            @Override
            public void run() {
                mData.clear();
                mData.addAll(data);
                notifyDataSetChanged();
            }
        },100);
    }

    public void appendData(List<T> data) {
        this.mData.addAll(data);
        notifyItemRangeInserted(mData.size(),mData.size()+data.size());
    }


    public abstract void addItemTypeDelegate();

    private void postDelay(Runnable runnable,int delay){
        new Handler().postDelayed(runnable,delay);
    }
}
