package cn.shawn.slidemenu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RvTestAdapter.OnItemClickListener {

    private RvTestAdapter mAdapter;
    private RecyclerView mRv;

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
            data.add(String.valueOf(i));
        }
        mAdapter.showData(data);
    }

    private void initView() {
        mAdapter = new RvTestAdapter();
        mAdapter.setOnItemClickListener(this);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRv.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyItemRemoved(position);
    }


}
