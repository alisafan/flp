package com.fanfan.sns326.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 搜索的关键词的历史记录的列表
 */
public class MyListView extends ListView {
	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// listView和ScrollView之间的冲突
        // 滑动的是ScrollView
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
