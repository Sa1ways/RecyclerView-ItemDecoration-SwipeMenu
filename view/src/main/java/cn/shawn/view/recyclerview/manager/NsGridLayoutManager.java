package cn.shawn.view.recyclerview.manager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
/**
 * Created by root on 17-6-5.
 */

public class NsGridLayoutManager extends GridLayoutManager {

    private boolean sCanScrollVertical = true;

    public NsGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NsGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public NsGridLayoutManager(Context context, int spanCount , boolean canScrollVertical){
        this(context, spanCount);
        this.sCanScrollVertical=canScrollVertical;
    }

    public NsGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        return sCanScrollVertical && super.canScrollVertically() ;
    }
}
