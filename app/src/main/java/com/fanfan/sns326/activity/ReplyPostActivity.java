package com.fanfan.sns326.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lyk.weixin.image.image.InfoBean;

/**
 * Created by Administrator on 2017/2/5.
 */
public class ReplyPostActivity extends Activity implements View.OnClickListener {
    Button btn_send;
    EditText ed_replypost;
    private InfoBean mainPost = null;// 主贴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replypost);
        // 接收之前的界面(主贴列表)传递来的Post实体类对象
        // 传递序列化的对象(实体类)
        mainPost =(InfoBean) getIntent().getSerializableExtra(Constans.KEY_POST);

        Utils.print("传递序列化的对象(实体类)"+mainPost);
        initView();
        initlistener();
    }

    private void initView() {
        btn_send = (Button) findViewById(R.id.btn_send);

        ed_replypost = (EditText) findViewById(R.id.ed_replypost);
            btn_send.setEnabled(true);
    }

    String replypost;

    private void initlistener() {
        btn_send.setOnClickListener(this);//发送按钮的点击事件
    }

    //发送按钮的点击事件
    @Override
    public void onClick(View v) {
        replypost = ed_replypost.getText().toString().trim();
        if (v.getId() == R.id.btn_send) {
            if (!replypost.equals("")) {
                submitPost(replypost);// 线程
            } else {
                Utils.show("请输入回复内容");
            }
        }
    }

    /**
     * 完成帖子提交
     */
    private void submitPost(String replypost) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("pid", ""+mainPost.getTid());// 回帖
        params.addBodyParameter("content", replypost);
        long time = System.currentTimeMillis() / 1000;// 这里的处理方式不太妥当
        params.addBodyParameter("dateline", "" + time);
        params.addBodyParameter("uid", "" + Constans.UID);

        // 去激活"提交"按钮, 防止多次提交
        btn_send.setEnabled(false);
        // 发送请求
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_POST_SUBMIT,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{") + 1);
                        Utils.show(result);
                        btn_send.setEnabled(true);
                        if (result.equals("0")) {
                            Utils.show("发帖失败");
                        } else {
                            Utils.show("发帖成功");
                            ReplyPostActivity.this.setResult(RESULT_OK);// 设定返回值
                            finish();// 关闭
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                        btn_send.setEnabled(true);
                    }
                }
        );
    }
}
