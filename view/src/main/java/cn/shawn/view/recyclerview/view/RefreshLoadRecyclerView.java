package cn.shawn.view.recyclerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cn.shawn.view.recyclerview.iinterface.LoadMoreViewCreator;

/**
 * Created by root on 17-6-4.
 */

public class RefreshLoadRecyclerView extends RefreshRecyclerView {

    public static final String TAG=RefreshLoadRecyclerView.class.getSimpleName();

    private LoadMoreViewCreator mLoadMoreViewCreator;

    private View mLoadMoreView;

    private int mLoadMoreViewHeight;

    private int mDownY;

    private int mLoadMoreState;

    private boolean sDragged;

    private boolean sEnableLoadMore = true;

    public static final int LOAD_STATE_NORMAL = 0;

    public static final int LOAD_STATE_PULL =1 ;

    public static final int LOAD_STATE_LOOSE_TO_LOAD =2;

    public static final int LOAD_STATE_LOADING = 3;

    public RefreshLoadRecyclerView(Context context) {
        super(context);
    }

    public RefreshLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLoadMoreViewCreator(LoadMoreViewCreator creator){
        if(creator != null){
            this.mLoadMoreViewCreator=creator;
        }
        addLoadMoreView();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addLoadMoreView();
    }

    private void addLoadMoreView() {
        if(getAdapter() != null && mLoadMoreViewCreator != null){
            mLoadMoreView = mLoadMoreViewCreator.getLoadMoreView(getContext(),this);
            if(mLoadMoreView != null){
                addFooterView(mLoadMoreView);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            if( mLoadMoreView != null && mLoadMoreViewHeight==0){
                mLoadMoreViewHeight = mLoadMoreView.getMeasuredHeight();

                if(mLoadMoreViewHeight==0){
                    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
                    mLoadMoreView.measure(widthMeasureSpec,heightMeasureSpec);
                }
                mLoadMoreViewHeight = mLoadMoreView.getMeasuredHeight();
                Log.i(TAG, "onLayout: "+mLoadMoreViewHeight);
                setLoadMoreViewBottomMargin(-mLoadMoreViewHeight);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if(sDragged){
                    restoreLoadMoreView();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_MOVE){
            if(canScrollUp() || mLoadMoreView == null ||
                    mLoadMoreState == LOAD_STATE_LOADING){
               return super.onTouchEvent(e);
            }
            if(sDragged){
                scrollToPosition(getAdapter().getItemCount()-1);
            }
            int deltaY = (int) (e.getRawY() - mDownY);
            if(deltaY < 0 && sEnableLoadMore ){
                int distance = (int) (- deltaY * mResistanceIndex *2f-mLoadMoreViewHeight);
                setLoadMoreViewBottomMargin(distance);
                updateLoadMoreState(distance);
                sDragged=true;
                return false;
            }
        }
        return super.onTouchEvent(e);
    }
    
    private void restoreLoadMoreView() {
        final MarginLayoutParams params= (MarginLayoutParams) mLoadMoreView.getLayoutParams();
        int currY = params.bottomMargin;
        int destY = 1 - mLoadMoreViewHeight;
        if(mLoadMoreState == LOAD_STATE_LOOSE_TO_LOAD){
            mLoadMoreState = LOAD_STATE_LOADING;
            destY = 0;
            if(mRlListener != null){
                mRlListener.onLoadingMore();
            }
            if(mLoadMoreViewCreator != null){
                mLoadMoreViewCreator.onLoadingMore();
            }
        }
        ValueAnimator animator = ValueAnimator.ofInt(currY , destY );
        animator.setDuration(Math.abs( currY - destY));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.bottomMargin= (int) animation.getAnimatedValue();
                mLoadMoreView.setLayoutParams(params);
            }
        });
        animator.start();
        sDragged=false;
    }

    private void updateLoadMoreState(int bottomMargin){
        if( bottomMargin < 1 - mLoadMoreViewHeight){
            mLoadMoreState = LOAD_STATE_NORMAL;
        }else if( bottomMargin < 0 ){
            mLoadMoreState = LOAD_STATE_PULL;
        }else {
            mLoadMoreState = LOAD_STATE_LOOSE_TO_LOAD;
        }
        if(mLoadMoreViewCreator != null ){
            mLoadMoreViewCreator.onPull(bottomMargin , mLoadMoreViewHeight , mLoadMoreState);
        }
    }

    public void stopLoadingMore(){
        mLoadMoreState = LOAD_STATE_NORMAL;
        restoreLoadMoreView();
        if(mLoadMoreViewCreator != null){
            mLoadMoreViewCreator.onLoadFinish();
        }
    }


    public void setLoadMoreViewBottomMargin(int bottomMargin) {
        Log.i(TAG, "setLoadMoreViewBottomMargin: "+bottomMargin);
        MarginLayoutParams params= (MarginLayoutParams) mLoadMoreView.getLayoutParams();
        if(bottomMargin < 1-mLoadMoreViewHeight){
            bottomMargin = 1 -mLoadMoreViewHeight;
        }
        params.bottomMargin=bottomMargin;
        mLoadMoreView.setLayoutParams(params);
    }

    private boolean canScrollUp() {
        return ViewCompat.canScrollVertically(this , 1);
    }

    public void setLoadMoreEnable(boolean enable){
        this.sEnableLoadMore=enable;
        mLoadMoreView.setVisibility(enable ? VISIBLE : INVISIBLE);
    }

}
