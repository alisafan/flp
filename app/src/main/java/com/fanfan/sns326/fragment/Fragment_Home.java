package com.fanfan.sns326.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanfan.sns326.R;
import com.fanfan.sns326.activity.MeActivity;
import com.fanfan.sns326.activity.PostDetailActivity;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lyk.weixin.image.image.FridListAdapter;
import com.lyk.weixin.image.image.Image;
import com.lyk.weixin.image.image.InfoBean;

import org.com.cctest.view.XListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fanfan.sns326.cons.Constans.URL_POSTLIST_ALL;


/**
 * Created by spl on 2015/12/17.
 */
public class Fragment_Home extends Fragment implements
        XListView.IXListViewListener,
        FridListAdapter.IControl {

    private FridListAdapter mAdapter;
    XListView lv;
    ArrayList<InfoBean> listInfo;
    ArrayList<InfoBean> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        initView(view);
        initData(pageno);

        return view;
    }

    private void initView(View view) {
        lv = (XListView) view.findViewById(R.id.xListView);
        mAdapter = new FridListAdapter(getActivity());
        mAdapter.setControl(this);
        lv.setAdapter(mAdapter);

        lv.setPullLoadEnable(true);// 开关加载更多
        lv.setPullRefreshEnable(true);// 开关刷新功能
        lv.setXListViewListener(this);// 设置监听


    }

    int n = 10;

    public void initData(int pno) {
        int p = (pno - 1) * n;
        RequestParams params = new RequestParams();
        params.addBodyParameter("p", "" + p);
        params.addBodyParameter("n", "" + n);
//        params.addBodyPrametarameter("uid","1059");//查看自己的帖子
        new HttpUtils().send(
                HttpRequest.HttpMethod.POST,
                URL_POSTLIST_ALL, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String strJson = info.result;
                        // 下面的代码是为了解决php的输出Bug
                        strJson = strJson.substring(strJson.indexOf("{"));

                        Utils.print("strJson=====:" + strJson);

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
                            mAdapter.setmList(list);
                            onLoadCompleted();
                            mAdapter.notifyDataSetChanged();
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

    /**
     * 更新数据
     */
    public void update() {
        Utils.print("更新数据");
        initData(pageno);
    }

    /**
     * ----------------------下拉刷新----------------------------------------------------------
     */
    int pageno = 1;// 页码

    @Override
    public void onRefresh() {
        // 更新

        pageno = 1;// 页码归为
        list.clear();// 记录清空-------------
        initData(pageno);// 重新调用异步请求
    }

    @Override
    public void onLoadMore() {
        // 加载更多(翻页)
        pageno++;// 往下翻页
        initData(pageno);
        // 追加下一页的数据

    }

    // 上次加载时间
    long loadTime = System.currentTimeMillis();

    Handler mHandler = new Handler();

    private void onLoadCompleted() {
        mHandler.postDelayed(new Runnable() {//延迟调用
            @Override
            public void run() {
                // 加载完毕,处理UI
                lv.stopRefresh();// 停止更新
                lv.stopLoadMore();// 停止加载更多
                lv.setRefreshTime(Utils.getTime(loadTime));// 显示更新时间
                loadTime = System.currentTimeMillis();// 上次更新时间
            }
        }, 500);

    }

    /**----------------------/下拉刷新----------------------------------------------------------*/
    /**
     * ----------------------接口----------------------------------------------------------
     */

    // 点击回复图标"...",到达回帖的界面(发帖)
    @Override
    public void repost(InfoBean item, int position) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(Constans.KEY_POST,item);
        startActivityForResult(intent,Constans.REQUEST_POST_DETAIL);
    }

    @Override
    public void guanzhu(int uid, int position) {
        Intent intent = new Intent(getActivity(), MeActivity.class);
        intent.putExtra("uid", "" + uid);
        getActivity().startActivity(intent);

    }

    @Override
    public void zanClick(InfoBean item) {
        // 点赞的方法
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", "" + Constans.UID);// uid
        params.addBodyParameter("tid", "" + item.getTid());// tid
        //params.addBodyParameter("pic","1");// 显示由图片附件的结果

        new HttpUtils().send(
                HttpRequest.HttpMethod.POST,
                Constans.URL_ADD_ZAN,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        String strJosn = objectResponseInfo.result;
                        //Utils.show("========================="+strJosn);// 打印JSON
                        // 下面的代码是为了解决php的输出Bug
                        String zid = strJosn.substring(strJosn.indexOf("{") + 1);// zid
                        int izid = Integer.parseInt(zid);
                        if (izid > 0) {
                            // 更新数据
                            onRefresh();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                }
        );
    }
}

