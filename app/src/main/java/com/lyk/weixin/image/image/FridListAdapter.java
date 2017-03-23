package com.lyk.weixin.image.image;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

public class  FridListAdapter extends BaseAdapter{

	private ArrayList<InfoBean> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	BitmapUtils bitmapUtils;

	public FridListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext=context;
		bitmapUtils = Utils.getInstance();

	}

	public void setmList(ArrayList<InfoBean> mList) {
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public InfoBean getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_itemimage, null);
			holder.avator=(ImageView)convertView.findViewById(R.id.avator);
			holder.name=(TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.gridView=(NoScrollGridView)convertView.findViewById(R.id.gridView);
			holder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
			holder.iv_repost = (ImageView) convertView.findViewById(R.id.iv_repost);
			holder.guanzhu=(TextView)convertView.findViewById(R.id.btn_guanzhu);
			holder.ll_zan = (LinearLayout) convertView.findViewById(R.id.ll_zan);
			holder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
			holder.img_zan = (ImageView) convertView.findViewById(R.id.img_zan);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final InfoBean bean = mList.get(position);
		Utils.print("bean.toString()  "+bean.toString());

		if (bean.getAvatar()!=null && bean.getAvatar().contains("http")) {

			bitmapUtils.display(holder.avator, bean.getAvatar());
		} else {
			bitmapUtils.display(holder.avator, Constans.URL_BASE+bean.getAvatar());

		}
		holder.name.setText(bean.getNickname());
		holder.content.setText(bean.getContent());

		holder.tv_zan.setText(""+bean.getZan());// 显示点赞
		// 时间格式的转换: long-->"年月日时分秒"
		holder.tv_time.setText(Utils.getTime(bean.getDateline()*1000));
		if(bean.urls!=null&&bean.urls.length>0){
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new DynamicGridAdapter(bean.urls, mContext));
			holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					imageBrower(position,bean.urls);
				}
			});
		}else{
			holder.gridView.setVisibility(View.GONE);
		}
		// 判断当前用户是否是自己
		if (bean.getUid()==Constans.UID){
			holder.guanzhu.setVisibility(View.GONE);// 不可见
		}else{
			holder.guanzhu.setVisibility(View.VISIBLE);// 可见
		}
		//判断是否被当前用户所关注: 根据布尔值显示按钮上的文字
		if (bean.getWhat()!=null && bean.getWhat().equals("1")){
			holder.guanzhu.setText("√已关注");
		}else{
			holder.guanzhu.setText("");
		}
		holder.avator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.print("adapter中的bean.getUid()：====="+bean.getUid());
				control.guanzhu(bean.getUid(),position);// 点击头像-->个人中心
			}
		});
		// 编写点击"回复"的事件
		holder.iv_repost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用外部接口
				control.repost(bean,position);
			}
		});

		holder.ll_zan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				control.zanClick(bean);
			}
		});

		return convertView;
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
	
	// 优化listview
	private static class ViewHolder {

		public TextView name;
		public ImageView avator;
		TextView content;
		NoScrollGridView gridView;
		TextView tv_time;
		ImageView iv_repost;// 回复图片(按钮)
		TextView guanzhu;//关注
		LinearLayout ll_zan;
		TextView tv_zan;
		ImageView img_zan;
	}
	IControl control;
	public void setControl(IControl control) {
		this.control = control;
	}
	// 定义接口
	public interface IControl{
		void repost(InfoBean item, int position);// 打开回复界面
		void guanzhu(int uid,int position);//关注
		void zanClick(InfoBean item);//点赞

	}
}
