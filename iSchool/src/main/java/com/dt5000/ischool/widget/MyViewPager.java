package com.dt5000.ischool.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义ViewPager，可嵌套在与滑动有冲突的控件中，并模拟点击事件，可执行自动轮播
 * 
 * @author 周锋
 * @date 2016年1月7日 下午2:39:49
 * @ClassInfo com.dt5000.ischool.widget.MyViewPager
 * @Description
 */
public class MyViewPager extends ViewPager {

	private PointF downPoint = new PointF();
	private OnSingleTouchListener onSingleTouchListener;
	private int size = 0;
	private Handler handler;
	private SwitcRunnable switcRunnable;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	/**
	 * 执行自动轮播
	 * 
	 * @param size
	 *            page数量
	 */
	public void startAutoSwitch(int pageSize) {
		if (pageSize <= 1) {
			return;
		}

		this.size = pageSize;

		if (handler == null) {
			handler = new Handler();
		}

		if (switcRunnable == null) {
			switcRunnable = new SwitcRunnable();
		}

		handler.postDelayed(switcRunnable, 3400);
	}

	/** 关闭自动轮播 */
	public void stopAutoSwitch() {
		if (handler != null && switcRunnable != null) {
			handler.removeCallbacks(switcRunnable);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		switch (evt.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 停止轮播
			stopAutoSwitch();

			// 记录按下时候的坐标
			downPoint.x = evt.getX();
			downPoint.y = evt.getY();
			if (this.getChildCount() > 1) {
				// 内容多于1个时通知其父控件，现在进行的是本控件的操作，不允许拦截
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 停止轮播
			stopAutoSwitch();

			if (this.getChildCount() > 1) {
				// 内容多于1个时通知其父控件，现在进行的是本控件的操作，不允许拦截
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			// 启动轮播
			startAutoSwitch(size);

			// 判断按下和松手的坐标是否为一个点，如果是则模拟为点击事件
			if (PointF.length(evt.getX() - downPoint.x, evt.getY()
					- downPoint.y) < (float) 5.0) {
				if (onSingleTouchListener != null) {
					onSingleTouchListener.onSingleTouch(getCurrentItem());
				}
				return true;
			}
			break;
		}
		return super.onTouchEvent(evt);
	}

	public interface OnSingleTouchListener {
		public void onSingleTouch(int currentItem);
	}

	public void setOnSingleTouchListener(
			OnSingleTouchListener onSingleTouchListener) {
		this.onSingleTouchListener = onSingleTouchListener;
	}

	class SwitcRunnable implements Runnable {
		@Override
		public void run() {
			setCurrentItem((getCurrentItem() + 1) % size);
			handler.postDelayed(switcRunnable, 3400);
		}
	}

}
