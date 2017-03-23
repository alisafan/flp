package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.db.User;
import com.fanfan.sns326.db.UserDao;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

public class StartActivity extends Activity {

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 如果当前activity已经退出，那么我就不处理handler中的消息
            if (isFinishing()) {
                return;
            }
            // 判断进入主页面还是登录页面
            checkSP();
        }
    };
    // 检查分享首选项
    private void checkSP() {
        // 先检查之前是否有存过用户名
        String uname = Utils.readData(Constans.SP_FILE, Constans.SP_UNAME);
        if (!uname.equals("")){
            // 至少第2次登陆
            String upass = Utils.readData(Constans.SP_FILE,Constans.SP_UPASS);
            // 自动登录
            connection(uname, upass);
        }else{
            // 是第一次进入App
            // 跳转
            Intent intent = new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent);
            // 跳转Activity动画
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
            finish();
        }
    }
    // 判断进入主页面还是登录页面
    // 检查数据库
    private void checkDB() {
        UserDao dao = new UserDao();
        // 先检查数据表中的记录
        List<User> list = dao.selectAll(this);

        if (list.size()>0){
            User user=null;
            for(int i=0;i<list.size();i++) {
                user = list.get(i);
                // 至少第2次登陆
            }
            connection(user.uname, user.upass);
        }else{
            // 是第一次进入App
            // 跳转
            Intent intent = new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent);
            // 跳转Activity动画
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
            finish();
        }
    }
    /**
     * 测试服务器连接
     */
    private void connection(final String uname, final String upass) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uname",uname);
        params.addBodyParameter("upass",upass);

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_LOGIN,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        result = result.substring(result.indexOf("{")+1);
                        if (result.equals("0")){
                            // 删除首选项
                            Utils.remove(Constans.SP_FILE,Constans.SP_UNAME);
                            Utils.remove(Constans.SP_FILE,Constans.SP_UPASS);
                            // 跳转-->Login
                            Intent intent = new Intent(StartActivity.this,LoginActivity.class);
                            startActivity(intent);
                            // 跳转Activity动画
                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                            finish();
                        }else{
                            Constans.UID = Integer.parseInt(result);
                            Constans.UNAME = uname;
                            // 存入首选项
                            Utils.writeData(Constans.SP_FILE,Constans.SP_UNAME,uname);
                            Utils.writeData(Constans.SP_FILE,Constans.SP_UPASS,upass);
                            // 添加时间的保存 Sp, DB
                            UserDao dao = new UserDao();
                            // 更新时间
                            dao.update(StartActivity.this, System.currentTimeMillis());
                            // 跳转
                            Intent intent = new Intent(StartActivity.this,MainActivity.class);
                            startActivity(intent);
                            // 跳转Activity动画
                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:"+s);
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // 发送2s钟的延时消息
        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
