package com.fanfan.sns326.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.sns326.MyApplication;
import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/1/30.
 */
public class MeActivity extends Activity {
    ImageView avatar,avatar_top;
    String uid;
    Button btn_guanzhu;

    TextView tv_uname,tv_no_post,tv_no_guanzhu,tv_no_fans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.activityList.add(this);// 添加到集合中

        setContentView(R.layout.activity_main_activity__me);

        uid = getIntent().getStringExtra("uid");
        //guanzhu = getIntent().getStringExtra("guanzhu");
        initViews();
        getUserInfo(uid);
        getGuanzhu(uid);
    }

    /**
     * 获取是否关注的信息
     */
    private void getGuanzhu(final String uid) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid",""+Constans.UID);// 自己的ID
        params.addBodyParameter("bid",uid);// 对方的ID

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_GUANZHU_IF,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{")+1);
                        Utils.show(result);
                        if (result.equals("1")){
                            btn_guanzhu.setText("取消关注");
                            btn_guanzhu.setEnabled(true);
                        }else{
                            btn_guanzhu.setText("+关注");
                            btn_guanzhu.setEnabled(true);
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
        avatar = (ImageView) findViewById(R.id.iv_avatar);
        avatar_top = (ImageView) findViewById(R.id.avatarImg);

        tv_no_post = (TextView) findViewById(R.id.tv_no_post);
        tv_no_guanzhu = (TextView) findViewById(R.id.tv_no_guanzhu);
        tv_no_fans = (TextView) findViewById(R.id.tv_no_fans);
        tv_uname = (TextView) findViewById(R.id.tv_uname);
        btn_guanzhu = (Button) findViewById(R.id.btn_guanzhu);


        Utils.getInstance().display(avatar_top, Constans.AVATAR);

    }



    /**
     *
     * @param uid
     */
    private void getUserInfo(String uid) {

        //SELECT * FROM `tc_user` WHERE `uid`=1
        String url = Constans.URL_USER + "?uid=" + uid+"&t="+System.currentTimeMillis();
        Log.i("spl", "打赌:url=" + url);

        new HttpUtils().send(
                HttpRequest.HttpMethod.GET,
                url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String strJson = info.result.trim();
                        //Utils.show("连接成功:" + strJson);
                        //Log.i("spl", "连接成功:" + strJson);
                        try {

                            JSONObject juser = new JSONObject(strJson);
                            String uname = juser.getString("nickname");// **************
                            Constans.UNAME = uname;
                            String path = juser.getString("avatar");
                            path =Utils.getImagePath(path);

                            String postno = juser.getString("postno");
                            String guanzhuno = juser.getString("guanzhuno");
                            String fanso = juser.getString("fanso");

                            tv_uname.setText(uname);
                            tv_no_post.setText(postno);
                            tv_no_guanzhu.setText(guanzhuno);
                            tv_no_fans.setText(fanso);

                            // 显示头像
                            BitmapUtils bitmapUtils = Utils.getInstance();
                            bitmapUtils.display(avatar, path);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                        Log.i("spl", "连接失败:" + s);
                    }
                });
    }

    public void clickGuanzhu(View view){

        if(btn_guanzhu.getText().toString().equals("取消关注")) {

            // 取消关注操作

            // 添加关注操作
            Utils.print("取消关注自己:" + Constans.UID + ",他人:" + uid);

            RequestParams params = new RequestParams();
            params.addBodyParameter("uid", "" + Constans.UID);
            params.addBodyParameter("bid", "" + uid);

            new HttpUtils().send(HttpRequest.HttpMethod.POST,
                    Constans.URL_GUANZHU_DEL,
                    params,// 传参
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> info) {
                            String res = info.result;
                            Utils.print("关注返回res:" + res);
                            // 添加取消关注的测试
                            btn_guanzhu.setText("+关注");
                            MeActivity.this.setResult(RESULT_OK);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Utils.show("连接失败:" + s);
                            Log.i("spl", "连接失败:" + s);
                        }
                    });

        }else{
            // 添加关注操作
            Utils.print("自己:" + Constans.UID + ",他人:" + uid);

            RequestParams params = new RequestParams();
            params.addBodyParameter("uid", "" + Constans.UID);
            params.addBodyParameter("bid", "" + uid);

            new HttpUtils().send(HttpRequest.HttpMethod.POST,
                    Constans.URL_GUANZHU,
                    params,// 传参
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> info) {
                            String res = info.result;
                            Utils.print("关注返回res:" + res);
                            btn_guanzhu.setText("取消关注");
                            MeActivity.this.setResult(RESULT_OK);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Utils.show("连接失败:" + s);
                            Log.i("spl", "连接失败:" + s);
                        }
                    });
        }
    }


}
