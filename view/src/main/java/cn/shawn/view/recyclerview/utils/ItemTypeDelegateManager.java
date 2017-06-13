package cn.shawn.view.recyclerview.utils;

import android.util.SparseArray;

import cn.shawn.view.recyclerview.iinterface.ItemTypeDelegate;

/**
 * Created by root on 17-6-4.
 */

public class ItemTypeDelegateManager<T> {

    private SparseArray<ItemTypeDelegate<T>> mDelegates = new SparseArray<>();

    /**
     * 向manage中添加代理,用index作为item的viewType
     * @param delegate
     */
    public void addDelegate(ItemTypeDelegate<T> delegate){
        if(mDelegates.indexOfValue(delegate) < 0){
            mDelegates.put(mDelegates.size() , delegate);
        }
    }

    /**
     * 删除一种类型
     * @param delegate
     */
    public void removeDelegate(ItemTypeDelegate<T> delegate){
        if(mDelegates.indexOfValue(delegate) >= 0){
            mDelegates.removeAt(mDelegates.indexOfValue(delegate));
        }
    }

    /**
     * 通过manager获取item对应位置的viewType
     * @param position
     * @param t
     * @return
     */
    public int getDelegateViewType(int position ,T t){
        for (int i = 0; i < mDelegates.size(); i++) {
            ItemTypeDelegate<T> delegate=mDelegates.valueAt(i);
            if(delegate.isTypeForItem(position,t)){
                return mDelegates.keyAt(i);
            }
        }
        throw new IllegalArgumentException(" itemViewType can not be found for the position"+position);
    }

    /**
     * 通过manager获取item对应位置的代理
     * @param position
     * @param t
     * @return
     */
    public ItemTypeDelegate<T> getDelegateFromPosition(int position , T t){
        for (int i = 0; i < mDelegates.size(); i++) {
            ItemTypeDelegate<T> delegate=mDelegates.valueAt(i);
            if(delegate.isTypeForItem(position,t)){
                return delegate;
            }
        }
        throw new IllegalArgumentException("itemViewType can not be found for the position"+position);
    }

    /**
     * 获取Delegate 用于onCreateViewHolder中
     * @param viewType
     * @return
     */
    public ItemTypeDelegate<T> getDelegateFromViewType(int viewType){
        ItemTypeDelegate<T> delegate=mDelegates.get(viewType);
        if(delegate != null){
            return delegate;
        }
        throw new IllegalArgumentException(" itemViewType can not be found for the type"+viewType);
    }

}
