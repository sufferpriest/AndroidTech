// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.xg.nguiwidget;

import android.animation.*;
import android.content.Context;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import java.lang.ref.WeakReference;

// Referenced classes of package com.android.deskclock.widget:
//			MotionDetectListener, MiuiViewConfiguration, MotionDetectStrategy

public abstract class AbsTranslationController
	implements android.animation.Animator.AnimatorListener, MotionDetectListener
{
	public static interface OnTranslateListener
	{

		public abstract void onTranslate(View view, float f);

		public abstract void onTranslateStateChanged(int i);
	}

	protected class TranslateAnimationListener
		implements android.animation.ValueAnimator.AnimatorUpdateListener
	{

		private final int mDelta;
		private final int mFrom;
		private final boolean mSpringBack;
		private final WeakReference mViewRef;
		final AbsTranslationController this$0;

		public View getView()
		{
			return (View)mViewRef.get();
		}

		public boolean needSpringBack()
		{
			return mSpringBack;
		}

		public void onAnimationUpdate(ValueAnimator valueanimator)
		{
			View view = (View)mViewRef.get();
			if (view != null)
			{
				float f = ((Float)valueanimator.getAnimatedValue()).floatValue();
				onTranslate(view, (float)mFrom + f * (float)mDelta);
			}
		}

		public TranslateAnimationListener(View view, int i, int j, boolean flag)
		{
			super();
			this$0 = AbsTranslationController.this;
			mViewRef = new WeakReference(view);
			mFrom = i;
			mDelta = j;
			mSpringBack = flag;
		}
	}


	static final String TAG = AbsTranslationController.class.getName();
	private TranslateAnimationListener mAnimListener;
	private ValueAnimator mAnimator;
	private int mLastAnchorPostion;
	protected MotionDetectStrategy mListener;
	private final int mMaxAnchorDuration;
	protected final int mMaximumVelocity;
	protected final int mMinimumVelocity;
	private OnTranslateListener mTranslateListener;

	public AbsTranslationController(Context context, MotionDetectStrategy motiondetectstrategy)
	{
		mListener = motiondetectstrategy;
		ViewConfiguration viewconfiguration = ViewConfiguration.get(context);
		mMinimumVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
		mMaxAnchorDuration = 300;//MiuiViewConfiguration.get(context).getMaxAnchorDuration();
	}

	private void fling(View view, int i, int j, int k, boolean flag, boolean flag1)
	{
		if (mAnimator != null)
		{
			mAnimListener = null;
			mAnimator.cancel();
			mAnimator = null;
		}
		if (j != 0)
			if (flag)
			{
				mAnimator = ObjectAnimator.ofFloat(new float[] {
					0.0F, 1.0F
				});
				mAnimator.setInterpolator(new DecelerateInterpolator());
				mAnimator.setDuration(getDuration(j, k));
				mAnimator.addListener(this);
				mAnimListener = new TranslateAnimationListener(view, i, j, flag1);
				mAnimator.addUpdateListener(mAnimListener);
				mAnimator.start();
			} else
			{
				onTranslate(view, i + j);
			}
		if (mAnimator == null && flag1)
		{
			springBack(view);
			return;
		}
		if (mAnimator != null)
		{
			onTranslateStateChanged(2);
			return;
		} else
		{
			onTranslateStateChanged(0);
			return;
		}
	}

	private boolean springBack(View view)
	{
		int i = (int)view.getX();
		int j = (int)view.getY();
		int k = getAnchorPostion(view, i, j, i, j, 0);
		if (k != j)
		{
			fling(view, j, k - j, 0, true, false);
			return true;
		} else
		{
			return false;
		}
	}

	protected abstract int computVelocity(VelocityTracker velocitytracker);

	protected void fling(View view, int i, int j, int k, int l, int i1)
	{
		int j1 = (int)view.getY();
		fling(view, j1, getAnchorPostion(view, i, j, k, l, i1) - j1, i1, true, false);
	}

	protected abstract int getAnchorPostion(View view, int i, int j, int k, int l, int i1);

	protected int getDuration(int i, int j)
	{
		int k = Math.abs(i);
		int l = (-1 + (1000 + Math.abs(j))) / 1000;
		if (l > 0)
			return Math.min(mMaxAnchorDuration, (k * 2) / l);
		else
			return mMaxAnchorDuration;
	}

	protected abstract int getInertiaPosition(View view, int i, int j, int k, int l, int i1);

	public int getLastAnchorPosition()
	{
		return mLastAnchorPostion;
	}

	protected abstract int getValidMovePosition(View view, int i, int j, int k, int l);

	public boolean isMovable(View view, int i, int j, int k, int l)
	{
		if (mListener != null)
			return mListener.isMovable(view, i, j, k, l);
		else
			return true;
	}

	public boolean moveImmediately(View view, int i, int j)
	{
		return false;
	}

	public void onAnimationCancel(Animator animator)
	{
	}

	public void onAnimationEnd(Animator animator)
	{
		TranslateAnimationListener translateanimationlistener = mAnimListener;
		boolean flag = false;
		if (translateanimationlistener != null)
		{
			boolean flag1 = mAnimListener.needSpringBack();
			flag = false;
			if (flag1)
			{
				View view = mAnimListener.getView();
				flag = false;
				if (view != null)
					flag = springBack(view);
			}
		}
		if (!flag)
		{
			onTranslateStateChanged(0);
			mAnimator = null;
			mAnimListener = null;
		}
	}

	public void onAnimationRepeat(Animator animator)
	{
	}

	public void onAnimationStart(Animator animator)
	{
	}

	public boolean onMove(View view, int i, int j, int k, int l)
	{
		boolean flag = isMovable(view, i, j, k, l);
		if (flag)
			onTranslate(view, getValidMovePosition(view, i, j, k, l));
		return flag;
	}

	public void onMoveCancel(View view, int i, int j)
	{
		fling(view, i, j, i, j, 0);
	}

	public void onMoveFinish(View view, int i, int j, int k, int l, VelocityTracker velocitytracker)
	{
		int i1 = computVelocity(velocitytracker);
		mLastAnchorPostion = getAnchorPostion(view, i, j, k, l, i1);
		if (Math.abs(i1) > mMinimumVelocity)
		{
			int j1 = (int)view.getY();
			fling(view, j1, getInertiaPosition(view, i, j, k, l, i1) - j1, i1, true, true);
			return;
		} else
		{
			fling(view, i, j, k, l, 0);
			return;
		}
	}

	public void onMoveStart(View view, int i, int j)
	{
		mLastAnchorPostion = 0x7fffffff;
		if (mAnimator != null)
		{
			mAnimListener = null;
			mAnimator.cancel();
			mAnimator = null;
		}
		onTranslateStateChanged(1);
	}

	protected void onTranslate(View view, float f)
	{
		translate(view, f);
		if (mTranslateListener != null)
			mTranslateListener.onTranslate(view, f);
	}

	protected void onTranslateStateChanged(int i)
	{
		if (mTranslateListener != null)
			mTranslateListener.onTranslateStateChanged(i);
	}

	public void setTranslateListener(OnTranslateListener ontranslatelistener)
	{
		mTranslateListener = ontranslatelistener;
	}

	protected abstract void translate(View view, float f);

}
