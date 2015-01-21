// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.xg.nguiwidget;

import android.util.Log;
import android.view.*;

// Referenced classes of package com.android.deskclock.widget:
//			MotionDetectListener

public class VerticalMotionDetector
{

	static final String TAG = VerticalMotionDetector.class.getName();
	private int mActivePointerId;
	private boolean mIsBeingDragged;
	private int mLastMotionX;
	private int mLastMotionY;
	private int mStartMotionX;
	private int mStartMotionY;
	private MotionDetectListener mStrategy;
	private final int mTouchSlop;
	private VelocityTracker mVelocityTracker;
	private final ViewGroup mView;

	public VerticalMotionDetector(ViewGroup viewgroup)
	{
		mActivePointerId = -1;
		mLastMotionY = 0;
		mLastMotionX = 0;
		mStartMotionY = 0;
		mStartMotionX = 0;
		mIsBeingDragged = false;
		mStrategy = null;
		mView = viewgroup;
		mView.setFocusable(true);
		mView.setDescendantFocusability(0x40000);
		mView.setWillNotDraw(false);
		mTouchSlop = ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
	}

	private void cancelDragging(int i, int j)
	{
		if (mIsBeingDragged)
		{
			mIsBeingDragged = false;
			if (mStrategy != null)
			{
				mStrategy.onMoveCancel(mView, i, j);
				return;
			}
		}
	}

	private void clearVelocityTracker()
	{
		if (mVelocityTracker != null)
			mVelocityTracker.clear();
	}

	private void finishDragging(int i, int j, boolean flag)
	{
		if (mIsBeingDragged)
		{
			mIsBeingDragged = false;
			if (mStrategy != null)
			{
				MotionDetectListener motiondetectlistener = mStrategy;
				ViewGroup viewgroup = mView;
				int k = mStartMotionX;
				int l = mStartMotionY;
				VelocityTracker velocitytracker;
				if (flag)
					velocitytracker = mVelocityTracker;
				else
					velocitytracker = null;
				motiondetectlistener.onMoveFinish(viewgroup, i, j, k, l, velocitytracker);
				return;
			}
		}
	}

	private void initOrResetVelocityTracker()
	{
		if (mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
			return;
		} else
		{
			mVelocityTracker.clear();
			return;
		}
	}

	private void initVelocityTrackerIfNotExists()
	{
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
	}

	private void onSecondaryPointerDown(MotionEvent paramMotionEvent)
	{
		int i = paramMotionEvent.getActionIndex();
		int j = paramMotionEvent.getPointerId(i);
		int k = (int)paramMotionEvent.getY(i);
		int l = (int)paramMotionEvent.getX(i);
		mActivePointerId = j;
		mStartMotionY = k;
		mStartMotionX = l;
		mLastMotionY = k;
		mLastMotionX = l;
		clearVelocityTracker();
	}

	private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
	{
		int i = (0xff00 & paramMotionEvent.getAction()) >> 8;
		if (paramMotionEvent.getPointerId(i) == mActivePointerId)
		{
			int j;
			int k;
			int l;
			if (i == 0)
				j = 1;
			else
				j = 0;
			k = (int)paramMotionEvent.getY(j);
			l = (int)paramMotionEvent.getX(j);
			mStartMotionY = k;
			mStartMotionX = l;
			mLastMotionY = k;
			mLastMotionX = l;
			mActivePointerId = paramMotionEvent.getPointerId(j);
		}
	}

	private void recycleVelocityTracker()
	{
		if (mVelocityTracker != null)
		{
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void startDragging(int i, int j)
	{
		mStartMotionY = j;
		mStartMotionX = i;
		if (!mIsBeingDragged)
		{
			mIsBeingDragged = true;
			if (mStrategy != null)
			{
				mStrategy.onMoveStart(mView, i, j);
				return;
			}
		}
	}

	public static void trackMovement(VelocityTracker velocitytracker, MotionEvent paramMotionEvent)
	{
		float f = paramMotionEvent.getRawX() - paramMotionEvent.getX();
		float f1 = paramMotionEvent.getRawY() - paramMotionEvent.getY();
		paramMotionEvent.offsetLocation(f, f1);
		velocitytracker.addMovement(paramMotionEvent);
		paramMotionEvent.offsetLocation(-f, -f1);
	}

	public MotionDetectListener getMotionStrategy()
	{
		return mStrategy;
	}

	public boolean isBeingDragged()
	{
		return mIsBeingDragged;
	}

	public boolean isMovable(int i, int j)
	{
		if (mStrategy != null)
			return mStrategy.isMovable(mView, i, j, mStartMotionX, mStartMotionY);
		else
			return true;
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
	    MotionDetectListener localMotionDetectListener = this.mStrategy;
	    if (localMotionDetectListener == null)
	      return false;
	    int i = paramMotionEvent.getAction();
	    if ((i == MotionEvent.ACTION_MOVE) && (this.mIsBeingDragged))
	      return true;
	    switch (i & MotionEvent.ACTION_MASK)
	    {

	    case MotionEvent.ACTION_MOVE: {
	      int i3 = this.mActivePointerId;
	      if (i3 == -1)
	        break;
	      int i4 = paramMotionEvent.findPointerIndex(i3);
	      if (i4 < 0)
	      {
	        Log.e(TAG, "Invalid pointerId=" + i3 + " in onInterceptTouchEvent");
	        break;
	      }
	      int i5 = (int)paramMotionEvent.getY(i4);
	      int i6 = (int)paramMotionEvent.getX(i4);
	      if (!localMotionDetectListener.isMovable(this.mView, i6, i5, this.mLastMotionX, this.mLastMotionY))
	      {
	        clearVelocityTracker();
	        break;
	      }
	      int i7 = Math.abs(i5 - this.mLastMotionY);
	      int i8 = Math.abs(i6 - this.mLastMotionX);
	      if ((i7 <= this.mTouchSlop) || (i8 >= i7))
	        break;
	      initVelocityTrackerIfNotExists();
	      trackMovement(this.mVelocityTracker, paramMotionEvent);
	      startDragging(i6, i5);
	      this.mLastMotionY = i5;
	      this.mLastMotionX = i6;
	      ViewParent localViewParent = this.mView.getParent();
	      if (localViewParent == null)
	        break;
	      localViewParent.requestDisallowInterceptTouchEvent(true);
	      break;
	    }
	    
	    case MotionEvent.ACTION_DOWN: {
	      int i1 = (int)paramMotionEvent.getY();
	      int i2 = (int)paramMotionEvent.getX();
	      this.mLastMotionY = i1;
	      this.mLastMotionX = i2;
	      this.mActivePointerId = paramMotionEvent.getPointerId(0);
	      initOrResetVelocityTracker();
	      trackMovement(this.mVelocityTracker, paramMotionEvent);
	      if (localMotionDetectListener.moveImmediately(this.mView, i2, i1))
	      {
	        startDragging(i2, i1);
	        break;
	      }
	      cancelDragging(i2, i1);
	      break;
	    }
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP: {
        	int k = paramMotionEvent.findPointerIndex(mActivePointerId);
    		int l;
    		int i1;
    		if (k < 0)
    		{
    			l = mLastMotionX;
    			i1 = mLastMotionY;
    		} else
    		{
    			l = (int)paramMotionEvent.getX(k);
    			i1 = (int)paramMotionEvent.getY(k);
    		}
    		if (i == 1)
    			finishDragging(l, i1, false);
    		else
    			cancelDragging(l, i1);
    		mActivePointerId = -1;
    		recycleVelocityTracker();
	      break;
	    }
	  case MotionEvent.ACTION_POINTER_UP: {
	      onSecondaryPointerUp(paramMotionEvent);
	      int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
	      this.mLastMotionY = (int)paramMotionEvent.getY(j);
	      this.mLastMotionX = (int)paramMotionEvent.getX(j);
	      break;
	    }
	  }
	  return this.mIsBeingDragged;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
	    MotionDetectListener localMotionDetectListener = this.mStrategy;
	    if (localMotionDetectListener == null)
	      return false;
	    initVelocityTrackerIfNotExists();
	    trackMovement(this.mVelocityTracker, paramMotionEvent);
	    switch (MotionEvent.ACTION_MASK & paramMotionEvent.getAction())
	    {
	    case MotionEvent.ACTION_DOWN: {
	      int i10 = (int)paramMotionEvent.getY();
	      int i11 = (int)paramMotionEvent.getX();
	      this.mLastMotionY = i10;
	      this.mLastMotionX = i11;
	      this.mActivePointerId = paramMotionEvent.getPointerId(0);
	      if (localMotionDetectListener.moveImmediately(this.mView, i11, i10))
	      {
	        startDragging(i11, i10);
	        ViewParent localViewParent2 = this.mView.getParent();
	        if (localViewParent2 == null)
	          break;
	        localViewParent2.requestDisallowInterceptTouchEvent(true);
	        break;
	      }
	      cancelDragging(i11, i10);
	      break;
	    }
	    case MotionEvent.ACTION_MOVE: {
			int k1 = mActivePointerId;
			if (k1 != -1)
			{
				int l1 = paramMotionEvent.findPointerIndex(k1);
				if (l1 < 0)
				{
					Log.e(TAG, (new StringBuilder()).append("Invalid pointerId=").append(k1).append(" in onInterceptTouchEvent").toString());
				} else
				{
					int i2 = (int)paramMotionEvent.getY(l1);
					int j2 = (int)paramMotionEvent.getX(l1);
					int k2 = mLastMotionY - i2;
					boolean flag = mIsBeingDragged;
					int l2 = 0;
					if (!flag)
					{
						int i3 = Math.abs(k2);
						int j3 = mTouchSlop;
						l2 = 0;
						if (i3 > j3)
						{
							startDragging(j2, i2);
							ViewParent viewparent = mView.getParent();
							if (viewparent != null)
								viewparent.requestDisallowInterceptTouchEvent(true);
							if (k2 > 0)
								l2 = -mTouchSlop;
							else
								l2 = mTouchSlop;
						}
					}
					if (mIsBeingDragged)
					{
						if (!localMotionDetectListener.onMove(mView, j2, i2 + l2, mStartMotionX, mStartMotionY))
							clearVelocityTracker();
						mLastMotionY = i2;
						mLastMotionX = j2;
					}
				}
			}
			break;
	    }
	    case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged)
			{
				int l = paramMotionEvent.findPointerIndex(mActivePointerId);
				int i1;
				int j1;
				if (l < 0)
				{
					i1 = mLastMotionX;
					j1 = mLastMotionY;
				} else
				{
					i1 = (int)paramMotionEvent.getX(l);
					j1 = (int)paramMotionEvent.getY(l);
				}
				finishDragging(i1, j1, true);
				mActivePointerId = -1;
				recycleVelocityTracker();
			}
			break;
	    }
	    case MotionEvent.ACTION_CANCEL: {
			if (mIsBeingDragged)
			{
				int i = paramMotionEvent.findPointerIndex(mActivePointerId);
				int j;
				int k;
				if (i < 0)
				{
					j = mLastMotionX;
					k = mLastMotionY;
				} else
				{
					j = (int)paramMotionEvent.getX(i);
					k = (int)paramMotionEvent.getY(i);
				}
				cancelDragging(j, k);
				mActivePointerId = -1;
				recycleVelocityTracker();
			}
			break;
	    }
	    case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(paramMotionEvent);
			break;
	    case MotionEvent.ACTION_POINTER_DOWN:
			onSecondaryPointerDown(paramMotionEvent);
			break;
	    }
	    return true;
    }

	public void setMotionStrategy(MotionDetectListener motiondetectlistener)
	{
		mStrategy = motiondetectlistener;
	}

}
