package cn.shawn.slidemenu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-6-13.
 */

public class RvTestAdapter extends RecyclerView.Adapter<RvTestAdapter.ViewHolder> {
String TAG  = RvTestAdapter.class.getSimpleName();

    private List<String> mData =new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void showData(List<String> data){
        this.mData.clear();
        this.mData.addAll(data);
        notifyItemRangeInserted(0,data.size());
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,
                parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvContent,tvDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDelete = (TextView) itemView.findViewById(R.id.delete);
        }

        void bind( int position){
            tvContent.setText(mData.get(position));
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
