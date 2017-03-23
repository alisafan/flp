package com.fanfan.sns326.pic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.pic.view.Bimp;
import com.fanfan.sns326.pic.view.FileUtils;
import com.fanfan.sns326.pic.view.PhotoActivity;
import com.fanfan.sns326.pic.view.PhotoHelper;
import com.fanfan.sns326.pic.view.TestPicActivity;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublishedActivity extends Activity {

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private Button activity_selectimg_send;
	EditText ed_content;
	List<String> list;//图片的集合
	PhotoHelper helper;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectimg);
		Init();
	}
	public void Init() {
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		helper  = new PhotoHelper(this,noScrollgridview);
		ed_content = (EditText) findViewById(R.id.ed_content);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublishedActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublishedActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
		activity_selectimg_send = (Button) findViewById(R.id.activity_selectimg_send);
		activity_selectimg_send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				list = new ArrayList<String>();
				for (int i = 0; i < Bimp.drr.size(); i++) {
					String str = Bimp.drr.get(i);
					list.add(str);
					Log.i("flpflp","======PublishedActivity图片的地址str:"+str);
				}
				// 高清的压缩图片全部就在  list 路径里面了
				// 高清的压缩过的 bmp 对象  都在 Bimp.bmp里面
				// 完成上传服务器后 .........
				clickSubmit();
				FileUtils.deleteDir();
			}
		});
	}

	/**
	 * 提交帖子
	 */
	public void clickSubmit() {
	// 非空验证
		if (TextUtils.isEmpty(ed_content.getText())
				|| (list.toString()==null &&list.toString().equals(""))) {

			activity_selectimg_send.setEnabled(false);
		} else {
			activity_selectimg_send.setEnabled(true);
			String content = ed_content.getText().toString();

			submitPost(content);// 线程
		}
	}


	/**
	 * +完成帖子提交
	 */
	private void submitPost(String content) {
		Utils.show("进入提交帖子");
		Utils.print("进入提交帖子");
		RequestParams params = new RequestParams();
//		if (mainPost!=null){
//			params.addBodyParameter("pid", ""+0);// 发回贴
//		}else {
			params.addBodyParameter("pid", "0");// 发主贴
//		}
		params.addBodyParameter("content", content);
		params.addBodyParameter("uid", "" + Constans.UID);
		params.addBodyParameter("uname", Constans.UNAME);
		params.addBodyParameter("tags", "");
		Utils.show("进入提交帖子   gfgf"+list.toString());
		Utils.print("进入提交帖子dfgdf=="+list.toString());
		if (list.toString()!=null && !list.toString().equals("")) {

			for (int i=0;i<list.size();i++) {
				if (i == 0) {
					params.addBodyParameter("pic", new File(list.get(i)));
				}
				if (i == 1) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 2) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 3) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 4) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 5) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 6) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 7) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
				if (i == 8) {
					params.addBodyParameter("pic"+i, new File(list.get(i)));
				}
			}
		}

		// 去激活"提交"按钮
		activity_selectimg_send.setEnabled(false);
		// 发送请求
		new HttpUtils().send(HttpRequest.HttpMethod.POST,
				Constans.URL_POST_SUBMIT,
				params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> info) {

						String result = info.result.trim();
						result = result.substring(result.indexOf("{") + 1);
						Utils.show("php返回结果："+result);
						Utils.print("php返回结果："+result);
						activity_selectimg_send.setEnabled(true);
						if (result.equals("0")) {
							Utils.show("发帖失败");
							Utils.print("发帖失败");
						} else {
							Utils.show("发帖成功");
							Utils.print("发帖成功");
							// 关闭
							PublishedActivity.this.setResult(RESULT_OK);
							finish();// 关闭
						}
					}

					@Override
					public void onFailure(HttpException e, String s) {
						Utils.show("连接失败:" + s);
						Utils.print("连接失败:" + s);
						activity_selectimg_send.setEnabled(true);
					}
				}
		);
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					helper.takePhoto();// 去拍照<----
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishedActivity.this,
							TestPicActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	@Override
	protected void onActivityResult(
			int requestCode, // 当初的请求码
			int resultCode, // 返回码: OK(-1)
			Intent data // 携带的数据
	) {
		// <------------------- 处理
		helper.onResult(requestCode,resultCode,data);
		super.onActivityResult(requestCode, resultCode, data);
	}

}
