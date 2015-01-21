package com.xg.nguiwidget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class VerticalMotionFrameLayout extends FrameLayout
{
  private InertiaListener mInertiaListener;
  private final int mMaximumVelocity;
  private long mModifiedTime;
  private int mModifiedX;
  private int mModifiedY;
  private final VerticalMotionDetector mMotionDetector = new VerticalMotionDetector(this);
  private final int mTouchSlop;
  private VelocityTracker mVelocityTracker;

  public VerticalMotionFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }

  public VerticalMotionFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public VerticalMotionFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
  }

  private static int bound(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < paramInt2)
      return paramInt2;
    if (paramInt1 > paramInt3)
      return paramInt3;
    return paramInt1;
  }

  private void initVelocityTracker()
  {
    if (this.mVelocityTracker == null)
    {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    }
    this.mVelocityTracker.clear();
  }

  private void recycleVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    int k = MotionEvent.ACTION_MASK & paramMotionEvent.getAction();
    boolean flag  = false;
    switch (k)
    {	
    case MotionEvent.ACTION_DOWN: {
      initVelocityTracker();
      VerticalMotionDetector.trackMovement(this.mVelocityTracker, paramMotionEvent);
      flag = false;
      break;
    }
    case MotionEvent.ACTION_MOVE: {
      if ((this.mMotionDetector.isBeingDragged()) && (!this.mMotionDetector.isMovable(i, j)))
      {
        paramMotionEvent.setAction(3);
        super.dispatchTouchEvent(paramMotionEvent);
        paramMotionEvent.setAction(0);
        this.mModifiedX = i;
        this.mModifiedY = j;
        this.mModifiedTime = SystemClock.uptimeMillis();
      }
      VerticalMotionDetector.trackMovement(this.mVelocityTracker, paramMotionEvent);
      flag = false;
      break;
    }
    case MotionEvent.ACTION_UP: {
		if (mModifiedTime != 0L)
		{
			if (Math.abs(j - mModifiedY) < mTouchSlop || Math.abs(i - mModifiedX) < mTouchSlop)
				if (mVelocityTracker != null)
				{
					float f = (float)(SystemClock.uptimeMillis() - mModifiedTime) / 1000F;
					mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					float f1 = mVelocityTracker.getXVelocity();
					float f2 = mVelocityTracker.getYVelocity();
					int j1 = bound(i + (int)(f1 * f), getLeft(), getRight());
					int k1 = bound(j + (int)(f2 * f), getTop(), getBottom());
					if (Math.abs(k1 - mModifiedY) < mTouchSlop || Math.abs(j1 - mModifiedX) < mTouchSlop)
					{
						paramMotionEvent.setAction(3);
					} else
					{
						paramMotionEvent.setAction(2);
						paramMotionEvent.setLocation(j1, k1);
						super.dispatchTouchEvent(paramMotionEvent);
						paramMotionEvent.setAction(1);
					}
				} else
				{
					paramMotionEvent.setAction(3);
				}
			mModifiedTime = 0L;
		} else
		{
			InertiaListener inertialistener = mInertiaListener;
			flag = false;
			if (inertialistener != null)
			{
				boolean flag1 = mMotionDetector.isBeingDragged();
				flag = false;
				if (flag1)
				{
					VelocityTracker velocitytracker = mVelocityTracker;
					flag = false;
					if (velocitytracker != null)
					{
						super.dispatchTouchEvent(paramMotionEvent);
						flag = true;
						mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
						int l = (int)mVelocityTracker.getYVelocity();
						int i1 = mMotionDetector.getMotionStrategy().getLastAnchorPosition();
						mInertiaListener.onInertiaMotion(l, i1);
					}
				}
			}
		}
		recycleVelocityTracker();
		  break;
    }
    case MotionEvent.ACTION_CANCEL:
		mModifiedTime = 0L;
		recycleVelocityTracker();
		flag = false;
		break;
    }
	if (flag)
		return true;
	else
		return super.dispatchTouchEvent(paramMotionEvent);
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mMotionDetector.onInterceptTouchEvent(paramMotionEvent);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mMotionDetector.onTouchEvent(paramMotionEvent);
  }

  public void setMotionStrategy(MotionDetectListener paramMotionDetectListener)
  {
    this.mMotionDetector.setMotionStrategy(paramMotionDetectListener);
  }

  public static abstract interface InertiaListener
  {
    public abstract void onInertiaMotion(int paramInt1, int paramInt2);
  }
}

/* Location:           D:\work\MiUi\DeskClock\classes-dex2jar.jar
 * Qualified Name:     com.android.deskclock.widget.VerticalMotionFrameLayout
 * JD-Core Version:    0.6.0
 */