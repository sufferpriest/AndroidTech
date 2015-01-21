// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.xg.nguiwidget;

import android.content.Context;
import android.view.VelocityTracker;
import android.view.View;

// Referenced classes of package com.android.deskclock.widget:
//			AbsTranslationController, MiuiViewConfiguration, MotionDetectStrategy

public class VerticalTranslationController extends AbsTranslationController
{

	private static final float LOG_D = (float)Math.log(1.5D);
	private final int mMaxY;
	private final int mMaxYBounce;
	private final int mMinAnchorVelocity;
	private final int mMinY;
	private final int mMinYBounce;
	private int mStartY;
	private final int mTranslateSlop;

	public VerticalTranslationController(Context context, MotionDetectStrategy motiondetectstrategy, int i, int j, int k, int l)
	{
		super(context, motiondetectstrategy);
		mStartY = 0;
		if (k <= i && i < j && j <= l)
		{
			mMinY = i;
			mMinYBounce = k;
			mMaxY = j;
			mMaxYBounce = l;
			//MiuiViewConfiguration miuiviewconfiguration = MiuiViewConfiguration.get(context);
			mTranslateSlop = 180;//miuiviewconfiguration.getScaledTranslateSlop();
			mMinAnchorVelocity = 450;//miuiviewconfiguration.getScaledMinAnchorVelocity();
			return;
		} else
		{
			StringBuilder stringbuilder = (new StringBuilder()).append("minYBounce <= minY < maxY <= maxYBounce is necessary!");
			Object aobj[] = new Object[4];
			aobj[0] = Integer.valueOf(k);
			aobj[1] = Integer.valueOf(i);
			aobj[2] = Integer.valueOf(j);
			aobj[3] = Integer.valueOf(l);
			throw new IllegalArgumentException(stringbuilder.append(String.format("%d %d %d %d", aobj)).toString());
		}
	}

	private int computDistance(int i, int j, int k)
	{
		if (i == 0 || j == k)
			return 0;
		else
			return 10 * (int)(Math.log(i) / (double)LOG_D);
	}

	protected int computVelocity(VelocityTracker velocitytracker)
	{
		if (velocitytracker != null)
		{
			velocitytracker.computeCurrentVelocity(1000, mMaximumVelocity);
			return (int)velocitytracker.getYVelocity();
		} else
		{
			return 0;
		}
	}

	protected int getAnchorPostion(View view, int i, int j, int k, int l, int i1)
	{
		int j1;
		int k1;
		j1 = 0x7fffffff;
		k1 = (int)view.getY();
		if (Math.abs(i1) >= mMinAnchorVelocity) {
			if (j1 == 0x7fffffff)
			{
				int l1 = k1 + computDistance(i1, mStartY, k1);
				int i2 = mMinY - l1;
				int j2 = mMaxY - l1;
				if (Math.abs(i2) >= Math.abs(j2)) {
				} else {
					j1 = mMinY;
					return j1;
				}
			}
		}
		if (k1 >= mStartY || k1 >= mMaxY - mTranslateSlop) {
			if (k1 > mStartY && k1 > mMinY + mTranslateSlop)
				j1 = mMaxY;
			if (j1 == 0x7fffffff)
			{
				int l1 = k1 + computDistance(i1, mStartY, k1);
				int i2 = mMinY - l1;
				int j2 = mMaxY - l1;
				if (Math.abs(i2) >= Math.abs(j2)) {
				} else {
				    j1 = mMinY;
					return j1;
				}
			}
		}

		j1 = mMinY;
		if (j1 == 0x7fffffff)
		{
			int l1 = k1 + computDistance(i1, mStartY, k1);
			int i2 = mMinY - l1;
			int j2 = mMaxY - l1;
			j1 = mMinY;
			return j1;
		}
		if (k1 > mStartY && k1 > mMinY + mTranslateSlop)
			j1 = mMaxY;
		return mMaxY;
	}

	/*protected int getAnchorPostion(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
	    int i = 2147483647;
	    int j = (int)paramView.getY();
	    if (Math.abs(paramInt5) < this.mMinAnchorVelocity)
	      if ((j >= this.mStartY) || (j >= this.mMaxY - this.mTranslateSlop))
	        break label116;
	    for (i = this.mMinY; ; i = this.mMaxY)
	      label116: 
	      do
	      {
	        if (i == 2147483647)
	        {
	          int k = j + computDistance(paramInt5, this.mStartY, j);
	          int m = this.mMinY - k;
	          int n = this.mMaxY - k;
	          if (Math.abs(m) >= Math.abs(n))
	            break;
	          i = this.mMinY;
	        }
	        return i;
	      }
	      while ((j <= this.mStartY) || (j <= this.mMinY + this.mTranslateSlop));
	    return this.mMaxY;
    }*/

	protected int getInertiaPosition(View view, int i, int j, int k, int l, int i1)
	{
		int j1 = (int)view.getY();
		int k1 = j1 + computDistance(i1, mStartY, j1);
		if (k1 < mMinYBounce)
			k1 = mMinYBounce;
		else
		if (k1 > mMaxYBounce)
			return mMaxYBounce;
		return k1;
	}

	protected int getValidMovePosition(View view, int i, int j, int k, int l)
	{
		int i1 = (int)((view.getTranslationY() + (float)j) - (float)l);
		if (i1 < mMinYBounce)
			return mMinYBounce;
		if (i1 > mMaxYBounce)
			return mMaxYBounce;
		else
			return i1;
	}

	public void onMoveStart(View view, int i, int j)
	{
		super.onMoveStart(view, i, j);
		mStartY = (int)view.getY();
	}

	public void setMotionDetectStrategy(MotionDetectStrategy motiondetectstrategy)
	{
		mListener = motiondetectstrategy;
	}

	protected void translate(View view, float f)
	{
		view.setTranslationY(f);
	}

}
