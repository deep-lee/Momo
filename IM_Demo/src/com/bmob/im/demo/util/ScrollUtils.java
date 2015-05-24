package com.bmob.im.demo.util;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class ScrollUtils {
	/**
	 * 速度追踪对象
	 */
	private VelocityTracker velocityTracker;
	public ScrollUtils() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 添加用户的速度跟踪器
	 * @param event
	 */
	public void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}
	/**
	 * 获取X方向的滑动速度,大于0向右滑动，反之向左
	 * @return
	 */
	public int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}
	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	public void scrollByDistanceX(View _view) {
		_view.scrollTo(0, 0);
	}
	/**
	 * 移除用户速度跟踪器
	 */
	public void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}
}
