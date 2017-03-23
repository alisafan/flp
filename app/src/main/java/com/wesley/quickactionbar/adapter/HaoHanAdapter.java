package com.wesley.quickactionbar.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.wesley.quickactionbar.bean.Person;

import java.util.ArrayList;

public class HaoHanAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Person> persons;

	public HaoHanAdapter(Context mContext, ArrayList<Person> persons) {
		this.mContext = mContext;
		this.persons = persons;
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return persons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_list2, null);
			holder = new ViewHolder();
			holder.mIndex = (TextView) convertView.findViewById(R.id.tv_index);
			holder.mName = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Person p = persons.get(position);

		String str = null;
		String currentLetter = p.getPinyin().charAt(0) + "";
		// 根据上一个首字母,决定当前是否显示字母
		if (position == 0) {
			str = currentLetter;
		} else {
			// 上一个人的拼音的首字母
			String preLetter = persons.get(position - 1).getPinyin().charAt(0)
					+ "";
			if (!TextUtils.equals(preLetter, currentLetter)) {
				str = currentLetter;
			}
		}

		// 根据str是否为空,决定是否显示索引栏
		holder.mIndex.setVisibility(str == null ? View.GONE : View.VISIBLE);
		holder.mIndex.setText(currentLetter);
		holder.mName.setText(p.getName());

		return convertView;
	}

	static class ViewHolder {
		TextView mIndex;
		TextView mName;
		ImageView mImage;
	}

}
