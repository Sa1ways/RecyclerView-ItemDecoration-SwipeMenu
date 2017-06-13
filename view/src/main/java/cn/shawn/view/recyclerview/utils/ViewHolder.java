package cn.shawn.view.recyclerview.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by root on 17-6-4.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = ViewHolder.class.getSimpleName();

    private SparseArray<View> mViewContainers;

    private View mConvertView;

    public ViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViewContainers=new SparseArray<>();
    }

    public View getView(int id){
        View view=mViewContainers.get(id);
        if(view == null){
            view = mConvertView.findViewById(id);
            mViewContainers.put( id , view);
        }
        return view;
    }

}
