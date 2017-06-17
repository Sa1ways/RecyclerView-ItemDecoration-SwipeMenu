package cn.shawn.slidemenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by root on 17-6-14.
 * recycler中获取当前第一个item的位置方式有两种
 * 1,recyclerView.getChildAdapterPosition(childView);
 * 2,若layoutManager为linearLayoutManager则linearLayoutManager.findFirstVisibleItemPosition();
 *
 * 由于recyclerView  Item 的复用机制,recyclerView.getChildAt(0);表示获取当前可见的第一个item的itemView
 * 在recyclerView.getChildCount();表示当前显示的所有item的个数
 *
 * View的 left right ,top ,bottom是相对于父布局的坐标而言的，是指view 的矩形边界相对于父坐标的值
 */

public class IndicatorItemDecoration extends RecyclerView.ItemDecoration {

    public static final String TAG = IndicatorItemDecoration.class.getSimpleName();

    private int mIndicatorHeight = 60;

    private TypeFetcher mFetcher;

    private Paint mPaint;

    private Rect mBounds = new Rect();

    public void setFetcher(TypeFetcher mFetcher) {
        this.mFetcher = mFetcher;
    }

    public IndicatorItemDecoration(TypeFetcher fetcher) {
        Context context = (Context) fetcher;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(context,12));
        this.mFetcher = fetcher;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right =parent.getWidth() - parent.getPaddingRight();
        int top = 0;
        int bottom = mIndicatorHeight;
        LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
        int currPosition = manager.findFirstVisibleItemPosition();
        if(mFetcher.getType(currPosition) != mFetcher.getType(currPosition+1)){
            View child = parent.getChildAt(0);
            int marginTop = child.getBottom();
            if(marginTop < mIndicatorHeight){
                top = marginTop - mIndicatorHeight;
                bottom = marginTop;
            }
        }

        mPaint.setColor(Color.LTGRAY);
        c.drawRect(left,top,right,bottom,mPaint);

        mPaint.setColor(Color.WHITE);
        drawText(c, mFetcher.getTypeTitle(currPosition), (top+bottom)/2);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        /*LinearLayoutManager mg = (LinearLayoutManager) parent.getLayoutManager();
        int currPosition = mg.findFirstVisibleItemPosition();*/
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int currentPosition = parent.getChildAdapterPosition(child);
            int bottom = child.getTop();
            int top = bottom - mIndicatorHeight;
            if(currentPosition == 0 || mFetcher.getType(currentPosition) != mFetcher.getType(currentPosition-1)) {
                mPaint.setColor(Color.LTGRAY);
                c.drawRect(left, top, right, bottom, mPaint);
                //
                String content = mFetcher.getTypeTitle(currentPosition);
                int centerY = (bottom + top)/2;
                drawText(c, content, centerY);
            }
        }
    }

    private void drawText(Canvas c, String content, int centerY) {
        mPaint.setColor(Color.WHITE);
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        int baseLineY = (int) (centerY + (metrics.bottom-metrics.top)/2 - metrics.bottom);
        c.drawText(content, 50, baseLineY, mPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int currPosition = parent.getChildAdapterPosition(view);
        if(currPosition == 0 || mFetcher.getType(currPosition) != mFetcher.getType(currPosition-1)) {
            outRect.top = mIndicatorHeight;
        }
    }

    private int sp2px(Context context, int value){
        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP,value,context.getResources().getDisplayMetrics());
    }

    public interface TypeFetcher {
        int getType(int position);
        String getTypeTitle(int position);
        String getContent(int position);
    }
}
