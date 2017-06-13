package cn.shawn.view.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by root on 17-6-4.
 */

public class FscLoadingView extends View {

    public static final String TAG = FscLoadingView.class.getSimpleName();

    private Paint mPaint ,mShadowPaint;

    private int mMainColor = Color.parseColor("#f26f85");

    private int mGrayColor = Color.LTGRAY;

    private  int mPadding = dp2px(12);

    private  int mInnerPadding = dp2px(4);

    private int mCenterX , mCenterY, mCircleCenterX, mCircleCenterY;

    private int mShadowWidth, mShadowHeight;

    private int mCircleRadius , mArchRadius;

    private RectF mArchBounds ,mShadowBounds ,mOriginArchBounds ,mOriginShadowBounds;

    private ValueAnimator mAnimator;

    public FscLoadingView(Context context) {
        this(context, null);
    }

    public FscLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FscLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(mMainColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mShadowPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setDither(true);
        mShadowPaint.setColor(mGrayColor);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.INNER));

        mOriginArchBounds=new RectF();
        mOriginShadowBounds=new RectF();
        mArchBounds=new RectF();
        mShadowBounds=new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawArch(canvas);
        drawShadow(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        int min =Math.min(getMeasuredHeight(),getMeasuredWidth());

        mArchRadius = min/2 - mPadding;
        mCircleRadius = min / 8 ;
        mCircleCenterX = mCenterX = getMeasuredWidth() / 2;
        mCircleCenterY = mCenterY = getMeasuredHeight() / 2;

        mOriginArchBounds.left = mCenterX-mArchRadius;
        mOriginArchBounds.top = mCenterY-mArchRadius;
        mOriginArchBounds.right = mCenterX+mArchRadius;
        mOriginArchBounds.bottom = mCenterY+mArchRadius;

        mArchBounds.left = mOriginArchBounds.left;
        mArchBounds.top = mOriginArchBounds.top;
        mArchBounds.right = mOriginArchBounds.right;
        mArchBounds.bottom = mOriginArchBounds.bottom;

        mOriginShadowBounds.left = mCenterX-mArchRadius;
        mOriginShadowBounds.top = mCenterY+mArchRadius+mInnerPadding;
        mOriginShadowBounds.right = mCenterX+mArchRadius;
        mOriginShadowBounds.bottom = getMeasuredHeight()-mInnerPadding;

        mShadowBounds.left = mOriginShadowBounds.left;
        mShadowBounds.top = mOriginShadowBounds.top;
        mShadowBounds.right = mOriginShadowBounds.right;
        mShadowBounds.bottom = mOriginShadowBounds.bottom;

        mShadowWidth = (int) mShadowBounds.width();
        mShadowHeight = (int) mShadowBounds.height();
    }

    private int measureSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int result;
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = Math.max(300, size);
        }
        return result;
    }

    private void drawShadow(Canvas canvas) {
        canvas.drawArc(mShadowBounds, 0, 360, false, mShadowPaint);
    }

    private void drawArch(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleRadius/3);
        canvas.drawArc(mArchBounds,0,180,false,mPaint);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCircleCenterX ,mCircleCenterY ,mCircleRadius ,mPaint);
    }

    public void startAnimation(){
        if(mAnimator != null){
            if(mAnimator.isRunning()){
                mAnimator.cancel();
            }
            mAnimator = null ;
        }
        mAnimator = ValueAnimator.ofFloat(1f, 0.2f, 1f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(-1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "onAnimationUpdate: ");
                processFactor((float)animation.getAnimatedValue());
            }
        });
        mAnimator.start();
    }

    private void processFactor(float factor) {

        float translateY=(1-factor)*mArchRadius;
        mArchBounds.top = mOriginArchBounds.top - translateY;
        mArchBounds.bottom = mOriginArchBounds.bottom - translateY;
        mCircleCenterY = (int) (mCenterY - translateY);

        mShadowBounds.top = mOriginShadowBounds.top + mShadowHeight /  2 *(1 - factor);
        mShadowBounds.bottom =mOriginShadowBounds.bottom - mShadowHeight / 2 *(1 - factor);
        mShadowBounds.left = mOriginShadowBounds.left + mShadowWidth / 2 * (1 -factor);
        mShadowBounds.right =mOriginShadowBounds.right - mShadowWidth / 2 * (1 - factor);

        invalidate();
    }

    public void stopAnimation(){
        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.cancel();
        }
    }

    private int dp2px(int value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,
                getContext().getResources().getDisplayMetrics());
    }
}
