package cn.shawn.slidemenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by root on 17-6-15.
 */

public class TitleItemDecoration extends RecyclerView.ItemDecoration {

    public static final String TAG =TitleItemDecoration.class.getSimpleName();

    private TitleFetcher mFetcher;

    private Context mContext;

    private int mTitleHeight;

    private int mTitleTextSize ;

    private Paint mTitlePaint;

    private int mTextColor = Color.RED;

    private int mBackgroundColor = Color.LTGRAY;

    public void setFetcher(TitleFetcher fetcher) {
        this.mFetcher = fetcher;
    }

    public TitleItemDecoration(Context context) {
        init(context);
    }

    public TitleItemDecoration(Context context, TitleFetcher titleFetcher) {
        this.mFetcher = titleFetcher;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mTitleTextSize = sp2px(12);
        mTitlePaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextSize(mTitleTextSize);
        mTitlePaint.setStyle(Paint.Style.FILL);

        mTitleHeight = dp2px(20);
        mTitleTextSize = sp2px(12);

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getLeft() + parent.getPaddingLeft();
        int right = parent.getRight() - parent.getPaddingRight();
        int top = 0;
        int bottom = mTitleHeight;
        View topView = parent.getChildAt(0);
        int currPosition = parent.getChildAdapterPosition(topView);
        if(hasTitle(currPosition+1)){
            View titleChild = parent.getChildAt(1);
            int childTop = titleChild.getTop();
            if( childTop < 2*mTitleHeight){
                top = -(2*mTitleHeight)+childTop;
                bottom = childTop - mTitleHeight;
            }
        }
        mTitlePaint.setColor(mBackgroundColor);
        c.drawRect(left, top, right, bottom, mTitlePaint);
        String title = mFetcher.getTitle(currPosition);
        Paint.FontMetrics metrics = mTitlePaint.getFontMetrics();
        int baseLine = (int) ((bottom + top) / 2 + (metrics.bottom - metrics.top)/2 - metrics.bottom);
        drawText(c, baseLine, title);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getLeft()+parent.getPaddingLeft();
        int right = parent.getRight()-parent.getPaddingRight();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child  = parent.getChildAt(i);
            int bottom = child.getTop();
            int top = bottom - mTitleHeight;
            int currPosition = parent.getChildAdapterPosition(child);
            if(hasTitle(currPosition)){
                mTitlePaint.setColor(mBackgroundColor);
                c.drawRect(left, top, right, bottom, mTitlePaint);
                Paint.FontMetrics metrics = mTitlePaint.getFontMetrics();
                int baseLine = (int) ((bottom + top) / 2 + (metrics.bottom - metrics.top)/2 - metrics.bottom);
                String title = mFetcher.getTitle(currPosition);
                drawText(c, baseLine, title);
            }
        }
    }

    private void drawText(Canvas c, int baseline, String text){
        mTitlePaint.setColor(mTextColor);
        c.drawText(text, 60, baseline, mTitlePaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int currPosition = parent.getChildAdapterPosition(view);
        if(hasTitle(currPosition)){
            outRect.top = mTitleHeight;
        }
    }

    private boolean hasTitle(int position){
        return position == 0 || mFetcher.getGroupType(position) != mFetcher.getGroupType(position-1);
    }

    private int dp2px(int value){
        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP,value,mContext.getResources().getDisplayMetrics());
    }

    private int sp2px(int value){
        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_SP,value,mContext.getResources().getDisplayMetrics());
    }

    public interface TitleFetcher{

        String getTitle(int position);

        int getGroupType(int position);
    }
}
