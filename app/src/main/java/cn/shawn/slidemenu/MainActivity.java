package cn.shawn.slidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.shawn.view.recyclerview.adapter.CommonAdapter;
import cn.shawn.view.recyclerview.iinterface.OnItemClickListener;
import cn.shawn.view.recyclerview.utils.ViewHolder;

public class MainActivity extends AppCompatActivity implements
        OnItemClickListener, AppBarLayout.OnOffsetChangedListener, TitleItemDecoration.TitleFetcher {

    public static final String TAG =MainActivity.class.getSimpleName();



    public static final String TITLES[] = {"group 1","group 2","group 3","group 4","group 5"};

    private String[][] mData ={{"A","B","C","D","E"},{"F","G","H","I","J"},
            {"K","L","M","N","O"},{"P","R","S","T","U"}};

    public static final int GROUP_SIZE = 5;

    private RvMainAdapter mAdapter;

    private RecyclerView mRv;

    private AppBarLayout mAppLayout;

    private TextView mTvHeader;

    private FrameLayout mLayoutHead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        final List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("content"+String.valueOf(i));
        }
        mAdapter.showData(data);
    }

    private void initView() {
        mLayoutHead = (FrameLayout) findViewById(R.id.layout_header);
        mTvHeader = (TextView) findViewById(R.id.tv_scroll);
        mAppLayout = (AppBarLayout) findViewById(R.id.app_layout);
        mAppLayout.addOnOffsetChangedListener(this);

        mAdapter = new RvMainAdapter();
        mAdapter.setOnItemClickListener(this);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.addItemDecoration(new TitleItemDecoration(this, this));
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int tag, int position, Object message) {
        mAdapter.getData().remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int height = mLayoutHead.getHeight();
        float fraction =  verticalOffset / (float)height;
        mLayoutHead.setAlpha(1 + fraction);
    }

    @Override
    public String getTitle(int position) {
        return String.valueOf(position / GROUP_SIZE);
    }

    @Override
    public String getContent(int position) {
        return mData[position / GROUP_SIZE][position % GROUP_SIZE];
    }

    @Override
    public int getGroupType(int position) {
        return position / GROUP_SIZE;
    }

    class RvMainAdapter extends CommonAdapter<String>{

        @Override
        public int getItemLayoutId() {
            return R.layout.layout_item;
        }

        @Override
        public void convert(final ViewHolder holder, int position, String info) {
            ((TextView)holder.getView(R.id.tv_content)).setText(info);
            ((TextView)holder.getView(R.id.tv_content)).setTextColor(Color.BLACK);
            holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(0, holder.getAdapterPosition(), null);
                }
            });
        }
    }

}
