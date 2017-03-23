package com.wesley.quickactionbar.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 快速索引
 * 
 * 用于根据字母快速定位联系人
 * 
 */
public class QuickIndexBar extends View {

	private static final String[] LETTERS = new String[] {"#", "A", "B", "C", "D",
			"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private static final int letterLength = LETTERS.length;// 获取字母集合的长度

	private Paint mPaint;

	private int cellWidth;// 每个字母控件的宽度

	private float cellHeight;// 高度

	public QuickIndexBar(Context context) {
		super(context);
		init();
	}

	public QuickIndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	// 初始化画笔
	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 去掉锯齿
		mPaint.setColor(Color.GRAY);
//		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setTextSize(25);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (int i = 0; i < letterLength; i++) {
			String text = LETTERS[i];
			// 计算坐标
			int x = (int) (cellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
			// 获取文本的高度
			Rect bounds = new Rect();// 矩形
			mPaint.getTextBounds(text, 0, text.length(), bounds);
			int textHeight = bounds.height();
			int y = (int) (cellHeight / 2.0f + textHeight / 2.0f + i
					* cellHeight);

			//设置文本颜色
			mPaint.setColor( Color.BLACK);

			// 绘制文本A-Z
			canvas.drawText(text, x, y, mPaint);
		}
	}

	int touchIndex = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int index = -1;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// 获取当前触摸到的字母索引
			index = (int) (event.getY() / cellHeight);
			if (index >= 0 && index < LETTERS.length) {
				// 判断是否跟上一次触摸到的一样
				if (index != touchIndex) {

					if (listener != null) {
						listener.onLetterUpdate(LETTERS[index]);
					}

					touchIndex = index;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				listener.onFinished();
			}
			touchIndex = -1;
			break;

		default:
			break;
		}
		invalidate();

		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 获取单元格的宽和高

		cellWidth = getMeasuredWidth();

		int mHeight = getMeasuredHeight();
		cellHeight = mHeight * 1.0f / LETTERS.length;

	}

	/**
	 * 暴露一个字母的监听
	 */
	public interface OnLetterUpdateListener {
		void onLetterUpdate(String letter);
		void onFinished();
	}

	private OnLetterUpdateListener listener;

	/**
	 * 设置字母更新监听
	 * 
	 * @param listener
	 */
	public void setListener(OnLetterUpdateListener listener) {
		this.listener = listener;
	}

}
