package cn.shawn.view.recyclerview.iinterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 17-6-4.
 */

public interface RefreshViewCreator {

    View getRefreshView(Context context , ViewGroup parent);

    void onPull(int pullHeight , int totalHeight , int state);

    void onRefreshing();

    void onRefreshFinish();

}
