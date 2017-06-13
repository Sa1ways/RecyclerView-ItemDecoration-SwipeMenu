package cn.shawn.view.recyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.shawn.view.recyclerview.iinterface.OnItemClickListener;
import cn.shawn.view.recyclerview.iinterface.OnItemLongClickListener;
import cn.shawn.view.recyclerview.iinterface.ItemTypeDelegate;
import cn.shawn.view.recyclerview.utils.ItemTypeDelegateManager;
import cn.shawn.view.recyclerview.utils.ViewHolder;

/**
 * Created by root on 17-6-4.
 */

public abstract class BaseMultiAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mData=new ArrayList<>();

    protected ItemTypeDelegateManager<T> mDelegateManager=new ItemTypeDelegateManager<>();

    protected OnItemClickListener mOnItemClickListener;

    protected OnItemLongClickListener mOnItemLongClickListener;

    protected Context mContext;

    public void addDelegate(ItemTypeDelegate<T> delegate){
        if(delegate != null)
            mDelegateManager.addDelegate(delegate);
    }

    public void deleDelegate(ItemTypeDelegate<T> delegate){
        if(delegate != null)
            mDelegateManager.removeDelegate(delegate);
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegateManager.getDelegateViewType(position , mData.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutId=mDelegateManager.getDelegateFromViewType(viewType).getItemLayoutId();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(layoutId ,parent ,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemTypeDelegate<T> delegate=mDelegateManager.getDelegateFromPosition(position,mData.get(position));
        delegate.convert((ViewHolder) holder,position ,mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    public List<T> getData(){
        return mData;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
}
