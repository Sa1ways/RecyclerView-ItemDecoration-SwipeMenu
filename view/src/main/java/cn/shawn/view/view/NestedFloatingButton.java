package cn.shawn.view.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by root on 17-6-5.
 */

public class NestedFloatingButton extends ViewGroup {
    
    public static final String TAG=NestedFloatingButton.class.getSimpleName();

    private int mDownY, mDownX;

    protected int mLeft , mTop;

    private int mScreenWidth;

    private boolean sChanged = false;

    private static final int TOUCH_SLOP = 16 ;

    public NestedFloatingButton(Context context) {
        this(context, null);
    }

    public NestedFloatingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedFloatingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        WindowManager mg = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mg.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(getChildCount() == 0) {
                    Log.i(TAG, "onInterceptTouchEvent: child count 0");
                    //return true;
                }
                mDownY = (int) ev.getRawY();
                mDownX = (int) ev.getRawX();
                Log.i(TAG, "onInterceptTouchEvent: down "+mDownX +" downY"+mDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (ev.getRawY() -mDownY);
                int deltaX = (int) (ev.getRawX() - mDownX);
                if( Math.abs(deltaX) > TOUCH_SLOP || Math.abs(deltaY) > TOUCH_SLOP ){
                    Log.i(TAG, "onInterceptTouchEvent: true");
                    return true;
                }
                else {
                    Log.i(TAG, "onInterceptTouchEvent: false");
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initLeftTop();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: down");
                mDownY = (int) event.getRawY();
                mDownX= (int) event.getRawX();
                //if(getChildCount() == 0) return true;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: move");
                int deltaY = (int) (event.getRawY() - mDownY);
                int deltaX = (int) (event.getRawX() - mDownX);
                adjustLayout(deltaX, deltaY);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLeft += event.getRawX() - mDownX;
                mTop += event.getRawY() - mDownY;
                adjustBoundary();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 奇葩 在onLayout中获取 有问题
     */
    private void initLeftTop() {
        if(!sChanged){
            mLeft = getLeft();
            mTop = getTop();
            sChanged = true;
        }
    }

    private void adjustLayout(int deltaX , int deltaY) {
        setX(mLeft + deltaX);
        setY(mTop + deltaY);
    }

    private void adjustBoundary() {
        //left right border Animation
        ValueAnimator animator;
        if(mLeft >= mScreenWidth/2){
            animator = ValueAnimator.ofFloat(mLeft, mScreenWidth-getWidth());
            animator.setDuration(Math.abs(mLeft-mScreenWidth+getWidth())/2);
            mLeft = mScreenWidth -getWidth();
        }else{
            animator = ValueAnimator.ofFloat(mLeft, 0);
            animator.setDuration(Math.abs(mLeft)/2);
            mLeft = 0;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setX((Float) animation.getAnimatedValue());
            }
        });
        animator.start();

        // top bottom boundary animation
        float destTop;
        if( mTop < 0 ){
            destTop = 0;
        }else if(mTop > getTop()){
            destTop = getTop();
        }else {
            return;
        }
        ValueAnimator topAnimator = ValueAnimator.ofFloat(mTop, destTop);
        topAnimator.setDuration((int) Math.abs(mTop - destTop));
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setY((Float) animation.getAnimatedValue());
            }
        });
        topAnimator.start();
        mTop= (int) destTop;
    }

}
