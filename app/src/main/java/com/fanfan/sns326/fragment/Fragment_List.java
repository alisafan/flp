package com.fanfan.sns326.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.activity.MeActivity;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.entity.User;
import com.fanfan.sns326.entity.UserAdapter;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wesley.quickactionbar.ui.QuickIndexBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by spl on 2015/12/17.
 */
public class Fragment_List extends Fragment
        implements UserAdapter.IControl,
        AdapterView.OnItemClickListener {
    List<User> list;
    //视图
    ListView lv_user;
    QuickIndexBar bar;
    private TextView tv_center;
    ///视图
    UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);

        initView(view);
        initListener();
        initDataFL();
        return view;
    }

    private void initView(View view) {

        list = new ArrayList<>();

        lv_user = (ListView) view.findViewById(R.id.lv);
        bar = (QuickIndexBar) view.findViewById(R.id.bar);
        tv_center = (TextView) view.findViewById(R.id.tv_center);
        adapter = new UserAdapter(getActivity());
        adapter.setControl(this);
        lv_user.setAdapter(adapter);


    }

    private void initListener() {
        //行点击事件
        lv_user.setOnItemClickListener(this);
        // 设置监听
        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                showLetter(letter);
                // 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
                for (int i = 0; i < list.size(); i++) {
                    User user = list.get(i);
                    String l = user.getPinyin().charAt(0) + "";
                    if (TextUtils.equals(letter, l)) {
                        // 匹配成功
                        lv_user.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onFinished() {
                tv_center.setVisibility(View.GONE);
            }
        });
    }

    /*
     * +显示字母
     */

    protected void showLetter(String letter) {
        tv_center.setVisibility(View.VISIBLE);
        tv_center.setText(letter);

    }
    //更新数据
    public void updateFL(){
        Utils.print("更新数据");
        initDataFL();
    }

    /**
     * 请求数据
     */

    public void initDataFL() {

        new HttpUtils().send(HttpRequest.HttpMethod.GET,// GET缓存要注意
                Constans.URL_USERLIST + "?uid=" + Constans.UID + "&t=" + System.currentTimeMillis(),
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String strJson = info.result;
                        strJson = strJson.substring(strJson.indexOf("{"));
                        Utils.print("strJson ++-" + strJson);
                        try {

                            JSONObject jsonObject = new JSONObject(strJson);
                            // 获取一级属性
                            JSONArray jsons = jsonObject.getJSONArray("list");
                            list = new ArrayList<User>();
                            // 循环遍历
                            for (int i = 0; i < jsons.length(); i++) {

                                JSONObject json = jsons.getJSONObject(i);
                                User user = new User();
                                user.setUid(json.get("uid").toString());
                                user.setUname(json.get("uname").toString());
                                user.setNickname(json.get("nickname").toString());
                                user.setUpass(json.get("upass").toString());
                                user.setAvatar(json.get("avatar").toString());
                                user.setUstate(json.get("ustate").toString());
                                user.setUstate(json.get("ustate").toString());
                                user.setWhat(json.get("what").toString());
                                user = new User(user.getUid(), user.getUname(), user.getNickname(),
                                        user.getUpass(), user.getAvatar(), user.getUstate(), user.getWhat());
                                list.add(user);
                            }

                            fillAndSortData(list);
                            lv_user.setAdapter(adapter);
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
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

    //+
    private void fillAndSortData(List<User> users) {
        List<String> nameList = new ArrayList<>();
        // 填充数据
        for (int i = 0; i < nameList.size(); i++) {
            String name = nameList.get(i);
            users.add(new User(name));
        }
        // 进行排序
        Collections.sort(users);
    }

    @Override
    public void clickGuanzhu(String uid) {
        // 添加关注操作
        Utils.print("自己:" + Constans.UID + ",他人:" + uid);
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", "" + Constans.UID);
        params.addBodyParameter("bid", "" + uid);
        params.addBodyParameter("dateline", "" + (System.currentTimeMillis() / 1000));

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_GUANZHU,
                params,// 传参
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        int id = 0;
                        String res = info.result;
                        Utils.print("关注返回res:" + res);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                        Log.i("spl", "连接失败:" + s);
                    }
                });
    }

    //-+行点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) parent.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), MeActivity.class);
        intent.putExtra("uid", user.getUid());
        //intent.putExtra("guanzhu",user.getWhat());
        getActivity().startActivityForResult(intent,Constans.REQUEST_CHANGE_GUANZHU);
        //+MainActivity
    }



}
