package com.dt5000.ischool.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class SwipeDismissListView extends ListView {

	/** 形成滑动的最小距离 */
	private int mSlop;
	/** 滑动的最小速度 */
	private int mMinFlingVelocity;
	/** 滑动的最大速度 */
	private int mMaxFlingVelocity;
	/** 标记用户是否正在滑动中 */
	private boolean mSwiping;
	/** 标记用户是否正在执行删除操作 */
	private boolean dismissing = false;
	/** 滑动速度检测类 */
	private VelocityTracker mVelocityTracker;
	/** 当前按下的item的位置 */
	private int mDownPosition;
	/** 按下的item对应的View */
	private View mDownView;
	/** 按下时X坐标 */
	private float mDownX;
	/** 按下时Y坐标 */
	private float mDownY;
	/** item的宽度 */
	private int mViewWidth;
	/** 当Item滑出界面时的回调接口 */
	private OnDismissListener onDismissListener;

	/** 设置删除回调 */
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	public SwipeDismissListView(Context context) {
		this(context, null);
	}

	public SwipeDismissListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeDismissListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		ViewConfiguration vc = ViewConfiguration.get(context);
		mSlop = vc.getScaledTouchSlop();
		// 获取滑动的最小速度
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8;
		// 获取滑动的最大速度
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handleActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			return handleActionMove(ev);
		case MotionEvent.ACTION_UP:
			handleActionUp(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}

	/** MotionEvent.ACTION_DOWN处理 */
	private void handleActionDown(MotionEvent ev) {
		if (dismissing) {// 假如正在执行上一个删除后的动画或者回调过程，则屏蔽操作
			return;
		}

		// 获取当前按下的X和Y坐标
		mDownX = ev.getX();
		mDownY = ev.getY();

		// 定位当前按下的位置
		mDownPosition = pointToPosition((int) mDownX, (int) mDownY);
		if (mDownPosition == AdapterView.INVALID_POSITION) {
			return;
		}

		// 根据按下的位置获取当前item的viwe
		mDownView = getChildAt(mDownPosition - getFirstVisiblePosition());

		// 获取当前需要操作的该view的宽度
		if (mDownView != null) {
			mViewWidth = mDownView.getWidth();
		}

		// 加入速度检测
		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
	}

	/** MotionEvent.ACTION_MOVE处理 */
	private boolean handleActionMove(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null) {
			return super.onTouchEvent(ev);
		}

		// 实时获取当前X和Y方向的便宜距离
		float deltaX = ev.getX() - mDownX;
		float deltaY = ev.getY() - mDownY;

		if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < mSlop) {// X方向滑动的距离大于mSlop并且Y方向滑动的距离小于mSlop，表示可以滑动
			// 标识正在滑动
			mSwiping = true;

			// 当手指滑动item，需要取消item的点击事件，不然会伴随着item点击事件的发生
			MotionEvent cancelEvent = MotionEvent.obtain(ev);
			cancelEvent
					.setAction(MotionEvent.ACTION_CANCEL
							| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			onTouchEvent(cancelEvent);
			cancelEvent.recycle();
		}

		if (mSwiping) {
			// 滑动中执行X方向的移动动画
			ViewHelper.setTranslationX(mDownView, deltaX);

			// 滑动中执行透明度渐变动画
			ViewHelper.setAlpha(
					mDownView,
					Math.max(
							0f,
							Math.min(1f, 1f - 2f * Math.abs(deltaX)
									/ mViewWidth)));

			// 此时返回true，表示SwipeDismissListView自己处理onTouchEvent，其他的就交给父类来处理
			return true;
		}

		return super.onTouchEvent(ev);
	}

	/** MotionEvent.ACTION_UP处理 */
	private void handleActionUp(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null || !mSwiping) {
			return;
		}

		float deltaX = ev.getX() - mDownX;

		// 通过滑动的距离计算出X,Y方向的速度
		mVelocityTracker.computeCurrentVelocity(1000);
		float velocityX = Math.abs(mVelocityTracker.getXVelocity());
		float velocityY = Math.abs(mVelocityTracker.getYVelocity());

		// 标识item是否要滑出屏幕
		boolean dismiss = false;
		// 标识是否往右边删除
		boolean dismissRight = false;

		// 当拖动item的距离大于item的一半，item滑出屏幕
		if (Math.abs(deltaX) > mViewWidth / 2) {
			dismiss = true;
			dismissRight = deltaX > 0;
		} else if (mMinFlingVelocity <= velocityX
				&& velocityX <= mMaxFlingVelocity && velocityY < velocityX) {// 手指在屏幕滑动的速度在某个范围内，也使得item滑出屏幕
			dismiss = true;
			dismissRight = mVelocityTracker.getXVelocity() > 0;
		}

		if (dismiss) {
			// 满足删除条件后，标识开始执行删除过程
			dismissing = true;

			// 执行X轴方向移动动画
			ViewPropertyAnimator.animate(mDownView)
					.translationX(dismissRight ? mViewWidth : -mViewWidth)
					.alpha(0).setDuration(200)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Item滑出界面之后执行删除
							performDismiss(mDownView, mDownPosition);
						}
					});
		} else {
			// 将item滑动至开始位置
			ViewPropertyAnimator.animate(mDownView).translationX(0).alpha(1)
					.setDuration(200).setListener(null);
		}

		// 移除速度检测
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		mSwiping = false;
	}
	
	/** item删除之后，其他的item向上或者向下滚动，并且将position回调 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		
		ViewHelper.setAlpha(dismissView, 1f);
		ViewHelper.setTranslationX(dismissView, 0);
		if (onDismissListener != null) {
			onDismissListener.onDismiss(dismissPosition);
		}
		dismissing = false;
		
		/*
		
		// 获取item的布局参数
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		// 获取item的高度
		final int originalHeight = dismissView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(150);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (onDismissListener != null) {
					onDismissListener.onDismiss(dismissPosition);
				}

				// 标识已经执行完删除过程
				dismissing = false;

				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0，所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

		animator.start();
		
		*/
	}

	/** 删除回调监听 */
	public interface OnDismissListener {
		public void onDismiss(int dismissPosition);
	}

}
