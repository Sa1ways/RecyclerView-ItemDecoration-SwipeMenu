package cn.shawn.view.recyclerview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by root on 17-6-4.
 */

public class WrapperRvAdapter extends BaseMultiAdapter {

    public static final String TAG=WrapperRvAdapter.class.getSimpleName();

    private RecyclerView.Adapter mInnerAdapter;

    private SparseArray<View> mHeaderViews=new SparseArray<>();

    private SparseArray<View> mFooterViews=new SparseArray<>();

    private static final int HEADER_TYPE_INDEX = 0x1000;

    private static final int FOOTER_TYPE_INDEX = 0x2000;

    public WrapperRvAdapter(RecyclerView.Adapter innerAdapter) {
        this.mInnerAdapter = innerAdapter;
    }

    public void addHeaderView(View view){
        int index=mHeaderViews.indexOfValue(view);
        if(index < 0 && view != null){
            mHeaderViews.put(HEADER_TYPE_INDEX+mHeaderViews.size(),view);
        }
        notifyDataSetChanged();
    }

    public void addFooterView(View view){
        int index=mFooterViews.indexOfValue(view);
        if(index < 0 && view != null){
            mFooterViews.put(FOOTER_TYPE_INDEX+mFooterViews.size(),view);
        }
        Log.i(TAG, "addFooterView: "+mFooterViews.size()+"footerView index"+mFooterViews.indexOfValue(view));
        notifyDataSetChanged();
    }


    public void removeFooterView(View footerView) {
        int index = mFooterViews.indexOfValue(footerView);
        Log.i(TAG, "removeFooterView: "+mFooterViews.size()+"footerView index"+mFooterViews.indexOfValue(footerView));
        if(index >= 0){
            mFooterViews.removeAt(index);
        }
        notifyDataSetChanged();
    }

    public void removeHeaderView(View headerView){
        int index = mHeaderViews.indexOfValue(headerView);
        if(index >= 0){
            mHeaderViews.removeAt(index);
        }
        notifyDataSetChanged();
    }

    private boolean isHeaderType(int position){
        return position < mHeaderViews.size();
    }

    private boolean isFooterType(int position){
        position -= mHeaderViews.size() + mInnerAdapter.getItemCount();
        return (position <= mFooterViews.size() && position>=0);
    }



    @Override
    public int getItemViewType(int position) {
        if(isHeaderType(position)){
            return mHeaderViews.keyAt(position);
        }else if(isFooterType(position)){
            return mFooterViews.keyAt( position - mHeaderViews.size() - mInnerAdapter.getItemCount());
        }
        return mInnerAdapter.getItemViewType(position-mHeaderViews.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderViews.indexOfKey(viewType) >=0 ){
            return new RecyclerView.ViewHolder(mHeaderViews.get(viewType)) {};
        }else if(mFooterViews.indexOfKey(viewType) >= 0){
            return new RecyclerView.ViewHolder(mFooterViews.get(viewType)) {};
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if( !isHeaderType(position) && !isFooterType(position)){
            mInnerAdapter.onBindViewHolder(holder,position-mHeaderViews.size());
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderViews.size()+mFooterViews.size()+mInnerAdapter.getItemCount();
    }

    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter =
                            isHeaderType(position) || isFooterType(position);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }


}
