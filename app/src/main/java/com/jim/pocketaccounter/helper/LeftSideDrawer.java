package com.jim.pocketaccounter.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
public class LeftSideDrawer extends FrameLayout {
    private final Window mWindow;
    private final ViewGroup mAboveView;
    private final BehindLinearLayout mBehindView;
    private final LinearLayout mLeftBehindBase;
    private final LinearLayout mRightBehindBase;
    private final View mOverlay;
    private Scroller mScroller;
    private int mDurationLeft;
    private int mDurationRight;
    private int mLeftBehindViewWidth;
    private int mRightBehindViewWidth;
    private abstract class DragAction {
        private float mLastMotionX = 0f;
        private boolean mOpening = false;
        private boolean mDraggable = false;
        abstract public boolean onTouchEvent(MotionEvent event); 
    }
    private DragAction mLeftDragAction = new DragAction() {
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
            switch (action) {
            case MotionEvent.ACTION_DOWN:
            {
                float x = ev.getX();
                mLeftDragAction.mLastMotionX = x;
                mLeftDragAction.mDraggable = mAboveView.getScrollX() != 0;
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if (mLeftDragAction.mDraggable) {
                    int currentX = mAboveView.getScrollX();
                    int diffX = 0;
                    if (mLeftDragAction.mOpening) {
                        diffX = -(mLeftBehindViewWidth + currentX);
                    } else {
                        diffX = -currentX;
                    }
                    mScroller.startScroll(currentX, 0, diffX, 0, mDurationLeft);
                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!mLeftDragAction.mDraggable) return false;
                float newX = ev.getX();
                float diffX = -(newX - mLeftDragAction.mLastMotionX);
                int x = mAboveView.getScrollX();
                mLeftDragAction.mOpening = mLeftDragAction.mLastMotionX < newX;
                mLeftDragAction.mLastMotionX = newX;
                float nextX = x + diffX;
                if (0 < nextX) {
                    mAboveView.scrollTo(0, 0);
                } else {
                    if (nextX < -mLeftBehindViewWidth) {
                        mAboveView.scrollTo(-mLeftBehindViewWidth, 0);
                    } else {
                        mAboveView.scrollBy((int) diffX, 0);
                    }
                }
                break;
            }
            return false;
        }
    };
    private DragAction mRightDragAction = new DragAction() {
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
            switch (action) {
            case MotionEvent.ACTION_DOWN:
            {
                float x = ev.getX();
                mRightDragAction.mLastMotionX = x;
                mRightDragAction.mDraggable = mAboveView.getScrollX() != 0;
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if (mRightDragAction.mDraggable) {
                    int currentX = mAboveView.getScrollX();
                    int diffX = 0;
                    if (mRightDragAction.mOpening) {
                        diffX = mRightBehindViewWidth - currentX;
                    } else {
                        diffX = -currentX;
                    }
                    mScroller.startScroll(currentX, 0, diffX, 0, mDurationRight);
                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!mRightDragAction.mDraggable) return false;
                float newX = ev.getX();
                float diffX = -(newX - mRightDragAction.mLastMotionX);
                int x = mAboveView.getScrollX();
                mRightDragAction.mOpening = newX < mRightDragAction.mLastMotionX;
                mRightDragAction.mLastMotionX = newX;
                float nextX = x + diffX;
                if (nextX < 0) {
                    mAboveView.scrollTo(0, 0);
                } else {
                    if (nextX < mRightBehindViewWidth) {
                        mAboveView.scrollBy((int) diffX, 0);
                    } else {
                        mAboveView.scrollTo(mRightBehindViewWidth, 0);
                    }
                }
                break;
            }
            return false;
        }
    };
    public LeftSideDrawer(Activity act) {
        this(act, new DecelerateInterpolator(0.9f), 180);
    }
    public LeftSideDrawer(Activity act, Interpolator ip, int duration) {
        super(act.getApplicationContext());
        final Context context = act.getApplicationContext();
        mDurationLeft = duration;
        mDurationRight = duration;
        mWindow = act.getWindow();
        mScroller = new Scroller(context, ip);
        final int fp = LayoutParams.FILL_PARENT;
        final int wp = LayoutParams.WRAP_CONTENT;
        //behind
        mBehindView = new BehindLinearLayout(context);
        mBehindView.setLayoutParams(new LinearLayout.LayoutParams(fp, fp));
        mBehindView.setOrientation(LinearLayout.HORIZONTAL);
        //left-behind base
        mLeftBehindBase = new BehindLinearLayout(context);
        mBehindView.addView(mLeftBehindBase, new LinearLayout.LayoutParams(wp, fp));
        //behind adjusting view
        mBehindView.addView(new View(context), new LinearLayout.LayoutParams(0, fp, 1));
        //right-behind base
        mRightBehindBase = new BehindLinearLayout(context);
        mBehindView.addView(mRightBehindBase, new LinearLayout.LayoutParams(wp, fp));

        addView(mBehindView);
        
        //above
        mAboveView = new FrameLayout(context);
        mAboveView.setLayoutParams(new LayoutParams(fp, fp));
        //overlay is used for controlling drag action, slid to close/open.
        mOverlay = new OverlayView(getContext());
        mOverlay.setLayoutParams(new LayoutParams(fp, fp, Gravity.BOTTOM));
        mOverlay.setEnabled(true);
        mOverlay.setVisibility(View.GONE);
        mOverlay.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if ( mLeftBehindBase.getVisibility() != View.GONE ) {
                    closeLeftSide();
                } else if ( mRightBehindBase.getVisibility() != View.GONE ){
                    closeRightSide();
                }
            }
        });
        
        ViewGroup decor = (ViewGroup) mWindow.getDecorView();
        ViewGroup above = (ViewGroup) decor.getChildAt(0);//including actionbar
        decor.removeView(above);
        above.setBackgroundDrawable(decor.getBackground());
        mAboveView.addView(above);
        mAboveView.addView(mOverlay);
        decor.addView(this);
        
        addView(mAboveView);
    }
    public View getLeftBehindView() {
        return mLeftBehindBase.getChildAt(0);
    }
    public View getRightBehindView() {
        return mRightBehindBase.getChildAt(0);
    }
    public View setBehindContentView(int leftBehindLayout) {
        return setLeftBehindContentView(leftBehindLayout);
    }
    public View setLeftBehindContentView(int leftBehindLayout) {
        final View content = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(leftBehindLayout, mLeftBehindBase);
        return content;
    }
    public View setRightBehindContentView(int rightBehindLayout) {
        final View content = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(rightBehindLayout, mRightBehindBase);
        return content;
    }
    public void setScrollInterpolator(Interpolator ip) {
        mScroller = new Scroller(getContext(), ip);
    }
    public void setAnimationDuration(int msec) {
        setAnimationDurationLeft(msec);
    }
    public void setAnimationDurationLeft(int msec) {
        mDurationLeft = msec;
    }
    public void setAnimationDurationRight(int msec) {
        mDurationRight = msec;
    }
    public void close() {
        closeLeftSide();
    }
    public void closeLeftSide() {
        int curX = -mLeftBehindViewWidth;//mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -curX, 0, mDurationLeft);
        invalidate();
    }
    public void closeRightSide() {
        int curX = mRightBehindViewWidth;//mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -curX, 0, mDurationRight);
        invalidate();
    }
    public void open() {
        openLeftSide();
    }
    public void openLeftSide() {
        mLeftBehindBase.setVisibility( View.VISIBLE );
        mRightBehindBase.setVisibility( View.GONE );
            
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -mLeftBehindViewWidth, 0, mDurationLeft);
        invalidate();
    }
    
    public void openRightSide() {
        mRightBehindBase.setVisibility( View.VISIBLE );
        mLeftBehindBase.setVisibility( View.GONE );
        
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, mRightBehindViewWidth, 0, mDurationRight);
        invalidate();
    }
    public void toggleDrawer() {
        toggleLeftDrawer();
    }
    public void toggleLeftDrawer() {
        if (isClosed()) {
            openLeftSide();
        } else {
            closeLeftSide();
        }
    }
    public void toggleRightDrawer() {
        if (isClosed()) {
            openRightSide();
        } else {
            closeRightSide();
        }
    }
    public boolean isClosed() {
        return mAboveView != null && mAboveView.getScrollX() == 0;
    }
    private boolean isLeftSideOpened() {
        return mLeftBehindBase.getVisibility() == View.VISIBLE && mRightBehindBase.getVisibility() == View.GONE;
    }
    
    private boolean isRightSideOpened() {
        return mRightBehindBase.getVisibility() == View.VISIBLE && mLeftBehindBase.getVisibility() == View.GONE;
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLeftBehindViewWidth = mLeftBehindBase.getMeasuredWidth();
        mRightBehindViewWidth = mRightBehindBase.getMeasuredWidth();

        //adjust the behind display area
        ViewGroup decor = (ViewGroup) mWindow.getDecorView();
        Rect rect = new Rect();
        decor.getWindowVisibleDisplayFrame(rect);
        mBehindView.fitDisplay(rect);
    }
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mAboveView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            if (mAboveView.getScrollX() == 0) {
                mOverlay.setVisibility(View.GONE);
                mLeftBehindBase.setVisibility(View.GONE);
                mRightBehindBase.setVisibility(View.GONE);
            } else {
                mOverlay.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isLeftSideOpened()) {
            return mLeftDragAction.onTouchEvent(ev);
        } else if (isRightSideOpened()) {
            return mRightDragAction.onTouchEvent(ev);
        } else {
            return true;
        }
    }
    private class BehindLinearLayout extends LinearLayout {
        public BehindLinearLayout(Context context) {
            super(context);
        }
        public void fitDisplay(Rect rect) {
            mBehindView.setPadding(rect.left, rect.top, 0, 0);
            requestLayout();
        }
    }
    private class OverlayView extends View {
        private static final float CLICK_RANGE = 3;
        private float mDownX;
        private float mDownY;
        private OnClickListener mClickListener;
        public OverlayView(Context context) {
            super(context);
        }
        
        public void setOnClickListener(OnClickListener listener) {
            mClickListener = listener;
            super.setOnClickListener(listener);
        }
        
        public boolean onTouchEvent(MotionEvent ev) {
            ev.setLocation(ev.getX() - mAboveView.getScrollX(), 0);
            LeftSideDrawer.this.onTouchEvent(ev);
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
                float x = ev.getX();
                float y = ev.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                mDownX = x;
                mDownY = y;
            } else if (action == MotionEvent.ACTION_UP) {
                if (mClickListener != null) {
                    if (Math.abs(mDownX - x) < CLICK_RANGE && Math.abs(mDownY - y) < CLICK_RANGE) {
                        mClickListener.onClick(this);
                    }
                }
            }
            return true;
        }
    }
}