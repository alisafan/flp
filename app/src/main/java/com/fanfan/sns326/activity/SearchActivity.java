package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.db.SearchDao;
import com.fanfan.sns326.view.MyListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lyk.weixin.image.image.FridListAdapter;
import com.lyk.weixin.image.image.Image;
import com.lyk.weixin.image.image.InfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fanfan.sns326.cons.Constans.URL_POSTLIST_SEARCH;

/**
 * Created by Administrator on 2017/2/5.
 */
public class SearchActivity extends Activity 
        implements 
        AdapterView.OnItemClickListener
        ,FridListAdapter.IControl
        , View.OnClickListener 
{

    EditText ed_key;// 搜索框
    ImageView iv_search;//搜索按钮
    String key = ""; // 当前搜索的关键词

    ArrayList<InfoBean> list = new ArrayList<InfoBean>();// 数据
    ArrayList<InfoBean> listInfo = new ArrayList<InfoBean>();// 数据

    MyListView listView; // 列表
    private TextView tv_tip;
    private TextView tv_clear;
    private BaseAdapter adapter;// 适配器
    private SearchDao dao;// 负责数据库的操作
//    FridListAdapter adapter; // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new SearchDao(this);
        setContentView(R.layout.activity_search);
        initView();
        initListener();
    }

    // 初始化组件
    private void initView() {
        ed_key = (EditText) findViewById(R.id.ed_key);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        listView = (MyListView) findViewById(R.id.listview);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
    }

    private void initListener() {
        //行点击事件
        listView.setOnItemClickListener(this);
        // 清空搜索历史
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.deleteData();// 清空数据
                queryData("");// 做一个全查询
            }
        });
        // 搜索框的键盘搜索键点击回调(软件盘)
        ed_key.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键

            // 监听用户对键盘的操作
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER // "回车键|搜索键"
                        && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE))// InputMethodManager服务代理
                            .hideSoftInputFromWindow(// 隐藏软键盘窗体
                                    getCurrentFocus().getWindowToken(), // windowToken
                                    InputMethodManager.HIDE_NOT_ALWAYS);// Flag
                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    key = ed_key.getText().toString().trim();
                    boolean isKey = dao.hasData(key);
                    if (!isKey) {
                        dao.insertData(key);// 添加这个key到数据库
                    }
                    tv_tip.setVisibility(View.GONE);
                    tv_clear.setVisibility(View.GONE);
                    initData();
                }
                return false;
            }
        });

        // 搜索框的文本变化实时监听
        ed_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    tv_tip.setText("搜索历史");
                } else {
                    tv_tip.setText("搜索结果");
                }
                String tempName = ed_key.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                queryData(tempName);
                tv_tip.setVisibility(View.VISIBLE);
                tv_clear.setVisibility(View.VISIBLE);

            }
        });
        //搜索按钮关联事件
        iv_search.setOnClickListener(this);

        // 第一次进入查询所有的历史记录
        queryData("");
        tv_tip.setVisibility(View.VISIBLE);
        tv_clear.setVisibility(View.VISIBLE);
    }
    /**
     * +模糊查询数据
     */
    private void queryData(String tempName) {
        Cursor cursor = dao.queryData(tempName);
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{"name"},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    //行点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        String name = textView.getText().toString();
        ed_key.setText(name);
        key = ed_key.getText().toString().trim();
        tv_tip.setVisibility(View.GONE);
        tv_clear.setVisibility(View.GONE);
        initData();
    }

    @Override
    public void onClick(View v) {
        //搜索按钮关联事件
        if (v.getId() == R.id.iv_search) {

            // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
            key = ed_key.getText().toString().trim();
            boolean isKey = dao.hasData(key);
            if (!isKey) {
                dao.insertData(key);// 添加这个key到数据库
            }
            tv_tip.setVisibility(View.GONE);
            tv_clear.setVisibility(View.GONE);
            initData();
        }
    }


    // 获取数据
    private void initData() {

        adapter = new FridListAdapter(SearchActivity.this);
        ((FridListAdapter)adapter).setControl(this);
//        ((FridListAdapter)adapter).setmList(list);

        RequestParams params = new RequestParams();
        params.addBodyParameter("key", key);// 加入关键词
        new HttpUtils().send(
                HttpRequest.HttpMethod.POST,
                URL_POSTLIST_SEARCH, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String strJson = info.result;
                        // 下面的代码是为了解决php的输出Bug
                        strJson = strJson.substring(strJson.indexOf("{"));

                        try {
                            JSONObject jsonObject = new JSONObject(strJson);
                            // 获取一级属性
                            JSONArray jsons = jsonObject.getJSONArray("list");
                            listInfo = new ArrayList<InfoBean>();
                            // 循环遍历
                            for (int i = 0; i < jsons.length(); i++) {
                                JSONObject json = jsons.getJSONObject(i);
                                InfoBean bean = new InfoBean();
                                bean.setNickname(json.get("nickname").toString());
                                bean.setUid(Integer.valueOf(json.get("uid").toString()));
                                bean.setTid(Integer.valueOf(json.get("tid").toString()));
                                bean.setPid(Integer.valueOf(json.get("pid").toString()));
                                bean.setZan(Integer.valueOf(json.get("zan").toString()));
                                bean.setContent(json.get("content").toString());
                                bean.setAvatar(json.get("avatar").toString());
                                Long dateline = Long.valueOf(String.valueOf(json.get("dateline")));
                                bean.setDateline(dateline);
                                List<Image> images = new ArrayList<Image>();
                                Image image;
                                if (!json.get("pic").toString().equals("")) {
                                    image = new Image();
                                    String url = Constans.URL_BASE + json.get("pic").toString();
                                    image.setUrl(url);
                                    images.add(image);
                                }
                                if (!json.get("pic1").toString().equals("")) {
                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic1").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic2").toString().equals("")) {

                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic2").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic3").toString().equals("")) {

                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic3").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic4").toString().equals("")) {

                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic4").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic5").toString().equals("")) {

                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic5").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic6").toString().equals("")) {

                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic6").toString());
                                    images.add(image);
                                }

                                if (!json.get("pic7").toString().equals("")) {
                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic7").toString());
                                    images.add(image);
                                }
                                if (!json.get("pic8").toString().equals("")) {
                                    image = new Image();
                                    image.setUrl(Constans.URL_BASE + json.get("pic8").toString());
                                    images.add(image);
                                }
                                String[] strimage = new String[images.size()];
                                for (int h = 0; h < images.size(); h++) {
                                    strimage[h] = String.valueOf(images.get(h));
                                }
                                bean.setUrls(strimage);

                                Log.i("flpflp", "images.toString():" + images.toString());
                                Log.i("flpflp", "images.size():" + images.size());
                                for (int j = 0; j < listInfo.size(); j++) {
                                    strimage = new String[images.size()];
                                    for (int h = 0; h < images.size(); h++) {
                                        strimage[h] = String.valueOf(images.get(h));
                                    }
                                    bean.setUrls(strimage);
                                    Log.i("flpflp", "strimage.toString()++++" + strimage.toString());
                                }
                                listInfo.add(bean);
                            }
                            list.addAll(listInfo);
//                            adapter = new FridListAdapter(SearchActivity.this);
//                            ((FridListAdapter)adapter).setControl(this);
                            ((FridListAdapter)adapter).setmList(list);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Log.i("flpflp", "手动,实体类集合=" + list.toString());
                            Log.i("flpflp", "list=========" + list.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("flpflp", "catch块-------" + e);

                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.i("spl", "连接失败:" + s);
                    }
                });

    }

    @Override
    public void repost(InfoBean item, int position) {
        Intent intent = new Intent(this, ReplyPostActivity.class);
        intent.putExtra(Constans.KEY_POST, item);// 传入当前的主贴对象
        startActivityForResult(intent, Constans.REQUEST_REPOST_DETAIL);
    }

    @Override
    public void guanzhu(int uid, int position) {
        Intent intent = new Intent(this, MeActivity.class);
        intent.putExtra("uid", "" + uid);
        this.startActivity(intent);
    }
//// TODO: 2017/2/11  
    @Override
    public void zanClick(InfoBean item) {
        
    }


}
