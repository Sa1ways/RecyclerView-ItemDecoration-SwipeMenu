package cn.shawn.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;
import cn.shawn.view.R;

/**
 * Created by root on 17-6-11.
 */

public class SwipeMenuLayout extends ViewGroup {

    public static final int STYLE_LEFT = 0;

    public static final int STYLE_RIGHT = 1;

    public static final int STYLE_LEFT_RIGHT = 2;

    private int mStyle;

    private boolean sInit;

    private int SCREEN_WIDTH;

    private int TOUCH_SLOP;

    private int mLeftMenuWidth, mRightMenuWidth, mContentHeight;

    private float mResistance = 1.2f;

    private int mLastX, mLastY;

    private Scroller mScroller;

    private static final int DEFAULT_SCROLL_DURATION = 600;

    private static SwipeMenuLayout mViewCache;


    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout);
        mStyle = array.getInt(R.styleable.SwipeMenuLayout_style,STYLE_RIGHT);
        array.recycle();

        WindowManager mg = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mg.getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH = metrics.widthPixels;

        TOUCH_SLOP = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        if(!sInit){
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setClickable(true);
            }
            switch (mStyle){
                case STYLE_LEFT:
                    getChildAt(0).getLayoutParams().height = getChildAt(1).getMeasuredHeight();
                    getChildAt(1).getLayoutParams().width = SCREEN_WIDTH;
                    mLeftMenuWidth = getChildAt(0).getMeasuredWidth();
                    mContentHeight = getChildAt(1).getMeasuredHeight();
                    break;
                case STYLE_RIGHT:
                    getChildAt(1).getLayoutParams().height = getChildAt(0).getMeasuredHeight();
                    getChildAt(0).getLayoutParams().width = SCREEN_WIDTH;
                    mRightMenuWidth = getChildAt(1).getMeasuredWidth();
                    mContentHeight = getChildAt(0).getMeasuredHeight();
                    break;
                case STYLE_LEFT_RIGHT:
                    getChildAt(0).getLayoutParams().height = getChildAt(2).getLayoutParams().height
                            = getChildAt(1).getMeasuredHeight();
                    getChildAt(1).getLayoutParams().width = SCREEN_WIDTH;
                    mLeftMenuWidth = getChildAt(0).getMeasuredWidth();
                    mRightMenuWidth = getChildAt(2).getMeasuredWidth();
                    mContentHeight = getChildAt(0).getMeasuredHeight();
                    break;
            }
            sInit = true;
        }
        setMeasuredDimension(SCREEN_WIDTH+mLeftMenuWidth+mRightMenuWidth,mContentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mStyle){
            case STYLE_LEFT:
                getChildAt(0).layout(0,0,mLeftMenuWidth,getMeasuredHeight());
                getChildAt(1).layout(mLeftMenuWidth,0,mLeftMenuWidth+SCREEN_WIDTH,getMeasuredHeight());
                break;
            case STYLE_RIGHT:
                getChildAt(0).layout(0, 0, SCREEN_WIDTH, getMeasuredHeight());
                getChildAt(1).layout(SCREEN_WIDTH, 0, SCREEN_WIDTH + mRightMenuWidth, getMeasuredHeight());
                break;
            case STYLE_LEFT_RIGHT:
                getChildAt(0).layout(0,0,mLeftMenuWidth,getMeasuredHeight());
                getChildAt(1).layout(mLeftMenuWidth,0,mLeftMenuWidth+SCREEN_WIDTH,getMeasuredHeight());
                getChildAt(2).layout(mLeftMenuWidth+SCREEN_WIDTH,0
                        ,mLeftMenuWidth+SCREEN_WIDTH+mRightMenuWidth,getMeasuredHeight());
                break;
        }
        if(changed){
            scrollTo(mLeftMenuWidth,0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getRawX();
                mLastY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) Math.abs(ev.getRawX() - mLastX);
                int dy = (int) Math.abs(ev.getRawY() - mLastY);
                if( dx > 2*TOUCH_SLOP && dx > dy ){
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if((mViewCache != null && mViewCache != this)){
                    mViewCache.smoothScrollTo(mLeftMenuWidth,0);
                    mViewCache = null;
                    return false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getRawX() - mLastX);
                scrollBy((int) (-dx*mResistance),0);
                adjustBoundary();
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                mViewCache = this;
                return true;
            case MotionEvent.ACTION_UP:
                handleUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void adjustBoundary() {
        int scrollX = getScrollX();
        if(scrollX < 0 ){
            scrollTo(0,0);
        }else if(scrollX > mLeftMenuWidth+mRightMenuWidth){
            scrollTo(mLeftMenuWidth+mRightMenuWidth ,0);
        }
    }

    private void handleUp() {
        int scrollX = getScrollX();
        if(scrollX < mLeftMenuWidth/2){
            smoothScrollTo(0,0);
        } else if(scrollX < mLeftMenuWidth+mRightMenuWidth/2){
            smoothScrollTo(mLeftMenuWidth, 0);
        }else{
            smoothScrollTo(mLeftMenuWidth+mRightMenuWidth,0);
        }
    }

    private void smoothScrollTo(int destX, int destY){
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        mScroller.startScroll(scrollX, 0, deltaX, 0, Math.abs(deltaX));
        invalidate();
    }

    private void smoothScrollTo(int destX, int destY, int duration){
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        mScroller.startScroll(scrollX, 0, deltaX, 0, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void resetLayout(){
        smoothScrollTo(0,0,0);
        mViewCache = null;
    }

}
