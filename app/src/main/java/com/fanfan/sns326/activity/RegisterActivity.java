package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fanfan.sns326.R;
import com.fanfan.sns326.SMSReceiver;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.db.UserDao;
import com.fanfan.sns326.inter.ICallBack;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/1/28.
 */

public class RegisterActivity extends Activity implements View.OnClickListener
        , ICallBack {
    ImageView iv_arrows_reg;
    EditText ed_uname_reg, ed_code_reg;
    Button btn_code_reg;
    EditText ed_upass_reg;
    Button btn_login_reg;
    String name, pwd, code;
    private BroadcastReceiver smsReceiver;// 接收者(短信来了)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 初始化SDK,写入自己的注册的key和SECRET
        SMSSDK.initSDK(this, "1a56177171470", "36b7cef66bb029ea5ea8d8d449f5daf4");
        EventHandler handler = new EventHandler() {// 实现SDK的handler回调(短信验证的处理)
            @Override
            //不能再回调中调用关于UI的操作
            public void afterEvent(int i, int i2, Object o) {

                if (i == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (i2 == SMSSDK.RESULT_COMPLETE) {
                        Log.i("flpflp", "登录成功");// 子线程不能调用UI
                        checkUname(name, pwd);//提交服务器.....-->注册流程
                    } else if (i2 == SMSSDK.RESULT_ERROR) {
                        Log.i("flpflp", "登录失败");
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(handler);// 注册回调接口  注册Handler
        // 动态注册
        // 实例化过滤器
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMSReceiver.ACTION);
        // 设置优先级别; "收到短信的广播-有序广播,按照优先级别"
        filter.setPriority(Integer.MAX_VALUE);//1000
        smsReceiver = new SMSReceiver(this);// 实力化
        // 动态注册广播接受者
        registerReceiver(smsReceiver, filter);

        initView();
        initlistener();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans,R.anim.trans_left_out );
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销所有的Handler
        SMSSDK.unregisterAllEventHandler();
        // 注销接收者
        unregisterReceiver(smsReceiver);
    }

    private void initView() {
        iv_arrows_reg = (ImageView) findViewById(R.id.iv_arrows_reg);
        ed_uname_reg = (EditText) findViewById(R.id.ed_uname_reg);
        ed_code_reg = (EditText) findViewById(R.id.ed_code_reg);
        ed_upass_reg = (EditText) findViewById(R.id.ed_upass_reg);
        btn_code_reg = (Button) findViewById(R.id.btn_code_reg);
        btn_login_reg = (Button) findViewById(R.id.btn_login_reg);
    }

    private void initlistener() {
        iv_arrows_reg.setOnClickListener(this);
        btn_code_reg.setOnClickListener(this);
        btn_login_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //返回到登录页面
        if (v.getId() == R.id.iv_arrows_reg) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans, R.anim.trans_left_out);
            finish();
        }
        // 按钮(发送短信)的事件
        if (v.getId() == R.id.btn_code_reg) {
            if (TextUtils.isEmpty(ed_uname_reg.getText())) {
                Utils.show("请输入手机号");
            } else {
                // 1. 获取手机号
                name = ed_uname_reg.getText().toString().trim();
                // 2.发送手机号给服务器,让服务器发回短信."86"--中国的区号
                SMSSDK.getVerificationCode("86", name);// 目的: 获取验证码
                // 3. 输出消息
                Toast.makeText(RegisterActivity.this, "发送信息成功", Toast.LENGTH_SHORT).show();
            }


        }
        // 按钮(提交注册:连服务器MOB)的事件
        if (v.getId() == R.id.btn_login_reg) {
            if (TextUtils.isEmpty(ed_uname_reg.getText())
                    || TextUtils.isEmpty(ed_upass_reg.getText())) {
                Utils.show("用户名和密码不能为空");
            } else {
                //发送信息到mod服务器
                name = ed_uname_reg.getText().toString().trim();
                code = ed_code_reg.getText().toString().trim();
                pwd = ed_upass_reg.getText().toString().trim();
                SMSSDK.submitVerificationCode("86", name, code);
            }
        }
    }

    private void checkUname(final String uname, final String upass) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uname", uname);
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_CHKUNAME,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{") + 1);
                        Utils.show(result);
                        if (result.equals("0")) {
                            Utils.show("该用户名可用");
                            // 注册
                            register(uname, upass);
                        } else {
                            Utils.show("该用户名不可用");
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                    }
                }
        );
    }

    private void register(final String uname, final String upass) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uname", uname);
        params.addBodyParameter("nickname", uname);
        params.addBodyParameter("upass", upass);

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_REG,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{") + 1);
                        Utils.show(result);
                        if (result.equals("0")) {
                            Log.i("flpflp", result);
                            Toast.makeText(RegisterActivity.this, "注册失败，id是：" + result, Toast.LENGTH_SHORT).show();

                        } else {
                            Log.i("flpflp", result);
                            Toast.makeText(RegisterActivity.this, "注册成功，id是：" + result, Toast.LENGTH_SHORT).show();
                            Constans.UID = Integer.parseInt(result);
                            Constans.UNAME = uname;
                            // 存入首选项
                            Utils.writeData(Constans.SP_FILE, Constans.SP_UNAME, uname);
                            Utils.writeData(Constans.SP_FILE, Constans.SP_UPASS, upass);
                            //获取系统时间
                            long time = System.currentTimeMillis();
                            // 添加时间的保存
                            Utils.writeData(Constans.SP_FILE,Constans.SP_TIME,""+time);
                            // 写入数据库
                            UserDao dao = new UserDao();
                            dao.insert(RegisterActivity.this,uname,upass,time);
                            // 跳转
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                    }
                }
        );
    }

    //接口的实现
    @Override
    public void setCode(String code) {
        if (code != null) {
            // 接收到验证码
            Message msg = osHandler.obtainMessage();
            msg.obj = code;
            msg.what = 1;
            osHandler.sendMessage(msg);// 发送
        }
    }

    Handler osHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String code = msg.obj.toString();
                ed_code_reg.setText(code);
            }
        }
    };

}
