package cn.shawn.view.recyclerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.shawn.view.recyclerview.iinterface.OnRefreshLoadListener;
import cn.shawn.view.recyclerview.iinterface.RefreshViewCreator;

/**
 * Created by root on 17-6-4.
 */

public class RefreshRecyclerView extends WrapperRecyclerView {

    public static final String TAG=RefreshRecyclerView.class.getSimpleName();

    private RefreshViewCreator mRefreshViewCreator;

    private View mRefreshView;

    private int mRefreshViewHeight;

    private int mDownY;

    private boolean sRefreshDragged;

    private int mRefreshState;

    protected float mResistanceIndex = 0.25f;

    protected OnRefreshLoadListener mRlListener;

    public static final int REFRESH_STATE_NORMAL = 0;

    public static final int REFRESH_STATE_PULL = 1;

    public static final int REFRESH_STATE_REFRESHING = 2;

    public static final int REFRESH_STATE_LOOSE_TO_REFRESH = 3;

    private boolean sEnableRefresh = true;


    public RefreshRecyclerView(Context context) {
        super(context);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRefreshViewCreator(RefreshViewCreator creator){
        if(creator != null ){
            this.mRefreshViewCreator=creator;
        }
        addRefreshView();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        // guarantee the refresh view above the added headers
        addRefreshView();
    }

    private void addRefreshView() {
        if(mRefreshViewCreator != null && getAdapter() != null){
            mRefreshView=mRefreshViewCreator.getRefreshView(getContext(),this);
            if(mRefreshView != null){
                addHeaderView(mRefreshView);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed && mRefreshView != null && mRefreshViewHeight == 0){
            mRefreshViewHeight=mRefreshView.getMeasuredHeight();
            setRefreshViewTopMargin(-mRefreshViewHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY= (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if(sRefreshDragged){
                    restoreRefreshView();
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(canScrollDown() || mRefreshState == REFRESH_STATE_REFRESHING
                        || mRefreshView == null || !sEnableRefresh){
                    return super.onTouchEvent(e);
                }
                if(sRefreshDragged){
                    scrollToPosition(0);
                }
                int deltaY= (int) (e.getRawY()-mDownY);
                if(deltaY > 0 ){
                    int distance= (int) (deltaY*mResistanceIndex-mRefreshViewHeight);
                    setRefreshViewTopMargin(distance);
                    updateRefreshState(distance);
                    sRefreshDragged = true;
                    return false;
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private void updateRefreshState(int topMargin) {
        if(topMargin < 1-mRefreshViewHeight){
            mRefreshState = REFRESH_STATE_NORMAL;
        }else if(topMargin < 0 ){
            mRefreshState = REFRESH_STATE_PULL;
        }else{
            mRefreshState = REFRESH_STATE_LOOSE_TO_REFRESH;
        }
        if(mRefreshViewCreator != null)
            mRefreshViewCreator.onPull(topMargin , mRefreshViewHeight , mRefreshState);
    }

    private void restoreRefreshView() {
        final MarginLayoutParams params = (MarginLayoutParams) mRefreshView.getLayoutParams();
        int currY = params.topMargin;
        int destY = 1 - mRefreshViewHeight;
        if(mRefreshState == REFRESH_STATE_LOOSE_TO_REFRESH){
            mRefreshState = REFRESH_STATE_REFRESHING;
            destY = 0;
            if(mRlListener != null){
                mRlListener.onRefreshing();
            }
            if(mRefreshViewCreator != null){
                mRefreshViewCreator.onRefreshing();
            }
        }
        ValueAnimator animator=ValueAnimator.ofInt(currY , destY);
        animator.setDuration((long) (Math.abs(currY-destY)*1.5f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin= (int) animation.getAnimatedValue();
                mRefreshView.setLayoutParams(params);
            }
        });
        animator.start();
        sRefreshDragged =false;
    }

    private void setRefreshViewTopMargin(int topMargin) {
        Log.i(TAG, "setRefreshViewTopMargin: "+topMargin);
        MarginLayoutParams params = (MarginLayoutParams) mRefreshView.getLayoutParams();
        if(topMargin < 1 - mRefreshViewHeight){
            topMargin = 1- mRefreshViewHeight;
        }
        params.topMargin = topMargin;
        mRefreshView.setLayoutParams(params);
    }

    public void setRefreshLoadListener(OnRefreshLoadListener listener){
        if(listener != null){
            mRlListener=listener;
        }
    }

    public boolean canScrollDown(){
        return ViewCompat.canScrollVertically(this , -1);
    }

    public void stopRefresh(){
        mRefreshState = REFRESH_STATE_NORMAL;
        restoreRefreshView();
        if(mRefreshViewCreator != null){
            mRefreshViewCreator.onRefreshFinish();
        }
    }

    public void setRefreshEnable(boolean enable){
        this.sEnableRefresh=enable;
    }

}
