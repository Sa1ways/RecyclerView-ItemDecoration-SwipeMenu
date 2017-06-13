package cn.shawn.view.recyclerview.manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by root on 17-6-5.
 */

public class NsLinearLayoutManager extends LinearLayoutManager {

    private boolean sCanScrollVertical=true;

    public NsLinearLayoutManager(Context context) {
        super(context);
    }

    public NsLinearLayoutManager(Context context , boolean canScrollVertical){
        super(context);
        this.sCanScrollVertical = canScrollVertical;
    }

    public NsLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NsLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canScrollVertically() {
        return sCanScrollVertical && super.canScrollVertically() ;
    }
}
