package com.fanfan.sns326.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by spl on 2015/12/17.
 */
public class Fragment_Me extends Fragment {

    ImageView avatarImg;
    TextView tv_uname_me;
    ImageView iv_btn_qr;
    TextView tv_post_no,tv_guanzhu_no,tv_fans_no;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me,null);

        initViews(view);
        // 获取用户信息-->服务器
        getUserInfo(Constans.UID);

        return view;
    }
    /**
     * 初始化页面控件
     */
    private void initViews(View v) {
        avatarImg = (ImageView) v.findViewById(R.id.avatarImg);
        tv_uname_me = (TextView) v.findViewById(R.id.tv_uname_me);
        iv_btn_qr = (ImageView) v.findViewById(R.id.iv_btn_qr);
        tv_post_no = (TextView) v.findViewById(R.id.tv_post_no);
        tv_guanzhu_no = (TextView) v.findViewById(R.id.tv_guanzhu_no);
        tv_fans_no = (TextView) v.findViewById(R.id.tv_fans_no);
    }

    /**
     * 更新数据
     */
    public void update() {
        // 获取用户信息-->服务器
        getUserInfo(Constans.UID);
    }
    /**
     *
     * @param uid
     */
    public void getUserInfo(int uid) {

        String url = Constans.URL_USER + "?uid=" + uid;
        Log.i("flpflp", "getUserInfo:url=" + url);

        new HttpUtils().send(
                HttpRequest.HttpMethod.GET,
                url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {
                        String strJson = info.result.trim();
                        try {

                            JSONObject juser = new JSONObject(strJson);
                            String uname = juser.getString("nickname");// 昵称代替用户名
                            Constans.UNAME = uname;

                            String path = juser.getString("avatar");
                            if (path.indexOf("http://")==0){

                            }else{
                                path = Constans.URL_BASE + path;
                            }

                            String postno = juser.getString("postno");
                            String guanzhuno = juser.getString("guanzhuno");
                            String fanso = juser.getString("fanso");

                            tv_uname_me.setText(uname);
                            tv_post_no.setText(postno);
                            tv_guanzhu_no.setText(guanzhuno);
                            tv_fans_no.setText(fanso);

                            // 显示头像
                            BitmapUtils bitmapUtils = Utils.getInstance();
                            bitmapUtils.display(avatarImg, path);

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
}
