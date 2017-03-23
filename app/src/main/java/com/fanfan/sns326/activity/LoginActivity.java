package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.db.UserDao;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import cn.bidaround.youtui_template.YtTemplate;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthLogin;
import cn.bidaround.ytcore.login.AuthUserInfo;

public class LoginActivity extends Activity implements View.OnClickListener {
    EditText ed_uname, ed_upass;
    Button loginBtn;
    TextView clickReg;
    TextView loginQQ;
    String Openid,NickName,ImageUrl;//第三方登录的信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initlistener();
        /****************友推SDK的初始化***********************/
        YtTemplate.init(this);// 初始化
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 调用 友推 的释放
        YtTemplate.release(this);
    }

    private void initView() {
        ed_uname = (EditText) findViewById(R.id.ed_uname);
        ed_upass = (EditText) findViewById(R.id.ed_upass);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        clickReg= (TextView) findViewById(R.id.clickReg);
        loginQQ = (TextView) findViewById(R.id.loginQQ);
    }

    private void initlistener() {
        loginBtn.setOnClickListener(this);
        clickReg.setOnClickListener(this);
        loginQQ.setOnClickListener(this);
    }
    //+
    @Override
    public void onClick(View v) {
        //登录按钮
        if (v.getId() == R.id.loginBtn) {
            // 点击登录按钮
            // 非空验证
            if (TextUtils.isEmpty(ed_uname.getText())
                    || TextUtils.isEmpty(ed_upass.getText())) {
                Utils.show("用户名和密码不能为空");
            } else {
                String uname = ed_uname.getText().toString();
                String upass = ed_upass.getText().toString();
                connection(uname, upass);// 验证
            }
        }
        //注册
        if (v.getId() == R.id.clickReg) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            // 跳转Activity动画
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
            finish();
        }
        //qq登录
        if (v.getId() == R.id.loginQQ) {
            /** qq第三方登录 */
            AuthLogin qqLogin = new AuthLogin();
            AuthListener qqListener = new AuthListener() {
                @Override
                public void onAuthSucess(Activity activity, AuthUserInfo authUserInfo) {
                    Openid = authUserInfo.getQqOpenid();
                    NickName=authUserInfo.getQqNickName();
                    ImageUrl=authUserInfo.getQqImageUrl();
                    Log.i("flpflp", "qq性别:" + authUserInfo.getQqGender());
                    Log.i("flpflp", "qqImageUrl-头像地址:" +ImageUrl);
                    Log.i("flpflp", "qqNickName-昵称:" + NickName);
                    Log.i("flpflp", "qqOpenid-开放ID:" + Openid);
                    //把id放入到注册表中
                    register(Openid, "00000");
                }
                @Override
                public void onAuthFail(Activity activity) {
                }
                @Override
                public void onAuthCancel(Activity activity) {
                }
            };
            qqLogin.qqAuth(this, qqListener);
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
                        Utils.show(result);
                        if (result.equals("0")){
                            Utils.show("失败");
                        }else{
                            Utils.show("登录成功");
                            Constans.UID = Integer.parseInt(result);
                            Constans.UNAME = uname;
                            // 存入首选项 SP_UNAME,SP_UPASS
                            Utils.writeData(Constans.SP_FILE,Constans.SP_UNAME,uname);
                            Utils.writeData(Constans.SP_FILE,Constans.SP_UPASS,upass);
                            //获取系统时间
                            long time = System.currentTimeMillis();
                            // 添加时间的保存
                            Utils.writeData(Constans.SP_FILE,Constans.SP_TIME,""+time);
                            // 写入数据库
                            UserDao dao = new UserDao();
                            dao.insert(LoginActivity.this,uname,upass,time);
                            // 跳转
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
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
    //qq登录
    private void register(final String uname, final String upass) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uname", uname);
        params.addBodyParameter("nickname", NickName);
        params.addBodyParameter("avatar", ImageUrl);
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
                            Toast.makeText(LoginActivity.this, "注册失败，id是：" + result, Toast.LENGTH_SHORT).show();

                        } else {
                            Log.i("flpflp", result);
                            Toast.makeText(LoginActivity.this, "注册成功，id是：" + result, Toast.LENGTH_SHORT).show();
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
                            dao.insert(LoginActivity.this,uname,upass,time);
                            // 跳转
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
    private long mExitTime;
    @Override
    public void onBackPressed() {
        //按两次返回键退出
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
