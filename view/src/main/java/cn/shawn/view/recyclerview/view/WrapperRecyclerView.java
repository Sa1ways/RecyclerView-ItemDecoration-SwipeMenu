package cn.shawn.view.recyclerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.shawn.view.recyclerview.adapter.WrapperRvAdapter;

/**
 * Created by root on 17-6-4.
 */

public class WrapperRecyclerView extends RecyclerView {

    private RecyclerView.Adapter mAdapter;

    private WrapperRvAdapter mWrapperAdapter;

    public WrapperRecyclerView(Context context) {
        super(context);
    }

    public WrapperRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(mAdapter != null){
            mAdapter.unregisterAdapterDataObserver(mObserver);
            mAdapter=null;
        }
        if(adapter instanceof WrapperRvAdapter){
            mWrapperAdapter= (WrapperRvAdapter) adapter;
        }else{
            mWrapperAdapter=new WrapperRvAdapter(adapter);
        }
        super.setAdapter(mWrapperAdapter);
        mAdapter=adapter;
        mAdapter.registerAdapterDataObserver(mObserver);
        mWrapperAdapter.adjustSpanSize(this);
    }

    public void addHeaderView(View headerView){
        if(getAdapter() == null) throw new IllegalArgumentException("setAdapter first ! ");
        mWrapperAdapter.addHeaderView(headerView);
    }

    public void addFooterView(View footerView){
        if(getAdapter() == null) throw new IllegalArgumentException("setAdapter first ! ");
        mWrapperAdapter.addFooterView(footerView);
    }

    public void removeFooterView(View footerView) {
        if(getAdapter() == null) throw new IllegalArgumentException("setAdapter first ! ");
        mWrapperAdapter.removeFooterView(footerView);
    }

    public void removeHeaderView(View headerView) {
        if(getAdapter() == null) throw new IllegalArgumentException("setAdapter first ! ");
        mWrapperAdapter.removeHeaderView(headerView);
    }

    public static final String TAG=WrapperRecyclerView.class.getSimpleName();

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if(mAdapter == null) return;
            if(mWrapperAdapter != mAdapter){
                mWrapperAdapter.notifyDataSetChanged();
                Log.i(TAG, "onChanged: ");
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapperAdapter != mAdapter)
                mWrapperAdapter.notifyItemRemoved(positionStart);
            Log.i(TAG, "onItemRangeRemoved: ");
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapperAdapter != mAdapter)
                mWrapperAdapter.notifyItemMoved(fromPosition, toPosition);
            Log.i(TAG, "onItemRangeMoved: ");
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapperAdapter != mAdapter)
                mWrapperAdapter.notifyItemChanged(positionStart);
            Log.i(TAG, "onItemRangeChanged: ");
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            if (mWrapperAdapter != mAdapter)
                mWrapperAdapter.notifyItemChanged(positionStart,payload);
            Log.i(TAG, "onItemRangeChanged: ");
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapperAdapter != mAdapter)
                mWrapperAdapter.notifyItemInserted(positionStart);
            Log.i(TAG, "onItemRangeInserted: ");
        }
    };


}
