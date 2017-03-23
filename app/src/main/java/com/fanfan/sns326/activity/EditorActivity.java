package com.fanfan.sns326.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.sns326.MyApplication;
import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.photos.PhotoHelper;
import com.fanfan.sns326.photos.SelectPicPopupWindow;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * EditorActivity 用户信息编辑页面
 *
 * @Title: UploadActivity.java
 * @Package com.tangcco.upload
 * @Description:更换头像,修改密码,登出
 * @version V1.0
 */
public class EditorActivity extends Activity implements OnClickListener {
	private Context mContext;
	private Button backBtn;
	private Button funBtn;
	private TextView titleTxt,tv_uname;
	private ImageView picImg;// 选择图片框
	private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框

	private Uri photoUri;
	/** 使用照相机拍照获取图片 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/** 使用相册中的图片 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	/** 获取到的图片路径 */
	private String picPath = "";
	private static ProgressDialog pd;
	private String resultStr = ""; // 服务端返回结果集
	private String imgUrl = "";

    EditText ed_pwd1,ed_pwd2;

    PhotoHelper helper;//******************

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        MyApplication.activityList.add(this);// 添加到集合中
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_editor);


		mContext = EditorActivity.this;

		initViews();
        getUserinfo();
        helper = new PhotoHelper(this,picImg);//*************
	}


    private void getUserinfo() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid",""+ Constans.UID);

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_USER,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{"));
                        //Utils.show(result);
                        // 解析json
                        try {
                            JSONObject user = new JSONObject(result);
                            String avatar = user.optString("avatar");
                            String pwd = user.optString("upass");// 密码
                            Constans.AVATAR = Utils.getImagePath(avatar);
//                            Utils.show(Constans.AVATAR);
                            Log.i("flpflp","AVATAR="+Constans.AVATAR+" ,pwd="+pwd);
                            // 显示头像
                            Utils.getInstance().display(picImg,Constans.AVATAR);
                            // 显示用户名
                            tv_uname.setText("更换头像 用户名:"+Constans.UNAME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:"+s);
                    }
                }
        );
    }

    /**
	 * 初始化页面控件
	 */
	private void initViews() {

		picImg = (ImageView) findViewById(R.id.picImg);
		picImg.setOnClickListener(this);

        tv_uname = (TextView) findViewById(R.id.tv_uname);
        // 初始化
        ed_pwd1 = (EditText) findViewById(R.id.ed_pwd1);
        ed_pwd2 = (EditText) findViewById(R.id.ed_pwd2);
	}
    boolean flag=false;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.picImg:// 添加图片点击事件
            flag=true;
            // 测试
            //jumpTo(com.spl.monkeyxbb.photos.MainActivity2.class);

			// 从页面底部弹出一个窗体，选择拍照还是从相册选择已有图片
			menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
			menuWindow.showAtLocation(findViewById(R.id.uploadLayout),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		default:
			break;
		}
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
			case R.id.item_popupwindows_camera:// 拍照<------------------
				helper.takePhoto();// ********************
				break;
			case R.id.item_popupwindows_Photo:// 相册选择图片<--------------
				helper.selectAlbum();// ******************
				break;
			case R.id.item_popupwindows_cancel:// 取消
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		helper.onResult(requestCode,resultCode,data);//******************

		super.onActivityResult(requestCode, resultCode, data);
	}

    //修改密码   "提交"按钮
     public void clickSubmit(View v){
        // 获取用户密码, 先验证密码的一致性
        String pwd1 = ed_pwd1.getText().toString().trim();
        String pwd2 = ed_pwd2.getText().toString().trim();
        if (!pwd1.equals(pwd2)){
            Utils.show("两次密码输入不一致");
            return;
        }
        // 请求服务器更新接口
        if (flag) {
            updateUser(pwd1, helper.getTempFile());// ***************
        } else {
            updateUser(pwd1,new File(Constans.ABSOLUTEPATH) );// ***************
        }
    }
    // -->请求
    private void updateUser(String pwd, File tempFile) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid",""+Constans.UID);
        params.addBodyParameter("upass",pwd);
        params.addBodyParameter("avatar",tempFile);// 上传头像

        new HttpUtils().send(
                HttpRequest.HttpMethod.POST,
                Constans.URL_AVATAR,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{")+1);
                        Utils.show(result);
                        if (result.equals("0")){
                            Utils.show("失败");
                        }else{
                            Utils.show("更新成功");
                            // 显示用户信息
                            getUserinfo();

                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:"+s);
                    }
                }
        );
    }


}
