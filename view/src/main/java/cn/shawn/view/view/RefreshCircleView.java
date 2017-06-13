package cn.shawn.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import cn.shawn.view.R;

/**
 * Created by root on 17-6-9.
 */

public class RefreshCircleView extends View {

    private Paint mPaint;

    private int mCircleRadius;

    private int mCircleColor;

    private int mCircleWidth;

    private int mStartAngle;

    private float mSwipeAngle;

    private RectF mBounds, mArrowBounds;

    private Bitmap mArrowDownBitmap;

    public RefreshCircleView(Context context) {
        this(context,null);
    }

    public RefreshCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(mCircleWidth);

        mBounds =new RectF();
        mArrowBounds = new RectF();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshCircleView);
        mCircleColor = array.getColor(R.styleable.RefreshCircleView_CircleColor, Color.WHITE);
        mCircleRadius = array.getDimensionPixelSize(R.styleable.RefreshCircleView_CircleRadius,dp2px(8));
        mCircleWidth = array.getDimensionPixelSize(R.styleable.RefreshCircleView_CircleWidth,dp2px(1.5f));
        mStartAngle = array.getInt(R.styleable.RefreshCircleView_CircleStartAngle,-90);
        array.recycle();
        mArrowDownBitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.down_arrow);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBounds.left = mCircleWidth ;
        mBounds.right = getWidth()-mCircleWidth;
        mBounds.top = mCircleWidth;
        mBounds.bottom = getHeight()-mCircleWidth;
        mArrowBounds.left = mBounds.left+dp2px(5);
        mArrowBounds.top = mBounds.top +dp2px(5);
        mArrowBounds.right = mBounds.right -dp2px(5);
        mArrowBounds.bottom = mBounds.bottom - dp2px(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mBounds, mStartAngle, mSwipeAngle, false, mPaint);
        canvas.drawBitmap(mArrowDownBitmap,null,mArrowBounds,null);
    }

    public void setSwipeFactor(float factor){
        if(factor<0 || factor >1) return;
        mSwipeAngle = - factor*360;
        postInvalidate();
    }

    private int dp2px(float value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,
                getContext().getResources().getDisplayMetrics());
    }
}
