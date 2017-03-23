package com.wesley.quickactionbar;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wesley.quickactionbar.adapter.HaoHanAdapter;
import com.wesley.quickactionbar.bean.Person;
import com.wesley.quickactionbar.ui.QuickIndexBar;
import com.wesley.quickactionbar.ui.QuickIndexBar.OnLetterUpdateListener;
import com.wesley.quickactionbar.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity {

	private ListView mMainList;
//	private ArrayList<Person> persons;
	private TextView tv_center;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		initData();
		QuickIndexBar bar = (QuickIndexBar) findViewById(R.id.bar);
		// 设置监听
		bar.setListener(new OnLetterUpdateListener() {
			@Override
			public void onLetterUpdate(String letter) {
				showLetter(letter);
				// 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
				for (int i = 0; i < listInfo.size(); i++) {
					Person person = listInfo.get(i);
					String l = person.getPinyin().charAt(0) + "";
					if (TextUtils.equals(letter, l)) {
						// 匹配成功
						mMainList.setSelection(i);
						break;
					}
				}
			}

			@Override
			public void onFinished() {
				tv_center.setVisibility(View.GONE);
			}
		});
		

		mMainList = (ListView) findViewById(R.id.lv_main);

		listInfo = new ArrayList<Person>();

		// 填充数据 , 排序
//		fillAndSortData(persons);

//		mMainList.setAdapter(new HaoHanAdapter(MainActivity.this, persons));

		tv_center = (TextView) findViewById(R.id.tv_center);

		mMainList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Person person = listInfo.get(position);
				ToastUtils.showToast(MainActivity.this, person.getName());
			}
		});

	}



	/**
	 * 显示字母
	 */
	protected void showLetter(String letter) {
		tv_center.setVisibility(View.VISIBLE);
		tv_center.setText(letter);

	}

	private void fillAndSortData(ArrayList<Person> persons) {
		List<String> nameList = new ArrayList<>();
		Utils.print("nameList.toString()   ========="+nameList.toString());
		// 填充数据
		for (int i = 0; i < nameList.size(); i++) {
			String name = nameList.get(i);
			persons.add(new Person(name));
		}

		// 进行排序
		Collections.sort(persons);

	}


	/**
	 * 请求数据
	 */
	ArrayList<Person> listInfo;
	private void initData() {

		new HttpUtils().send(HttpRequest.HttpMethod.GET,// GET缓存要注意
				Constans.URL_USERLIST+"?uid="+Constans.UID+"&t="+System.currentTimeMillis(),
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> info) {

						String strJosn = info.result;
						//Utils.print("在Fragment_list:"+strJosn);
						// 下面的代码是为了解决php的输出Bug
						strJosn = strJosn.substring(strJosn.indexOf("{"));



						try {
							JSONObject jsonObject = new JSONObject(strJosn);
							// 获取一级属性
							JSONArray jsons = jsonObject.getJSONArray("list");
							listInfo= new ArrayList<Person>();
							// 循环遍历
							for (int i = 0; i < jsons.length(); i++) {
								JSONObject json = jsons.getJSONObject(i);
								Person bean = new Person();
								bean.setName(json.get("nickname").toString());
								bean = new Person(bean.getName());
								listInfo.add(bean);
							}

							fillAndSortData(listInfo);
							Utils.print("list:++++"+listInfo.toString());
							mMainList.setAdapter(new HaoHanAdapter(MainActivity.this, listInfo));

						} catch (JSONException e) {
							e.printStackTrace();
							Log.i("flpflp", "catch块-------" + e);
						}
					}

					@Override
					public void onFailure(HttpException e, String s) {
						Utils.show("连接失败:" + s);
						Log.i("spl", "连接失败:" + s);
					}
				});

	}
}
