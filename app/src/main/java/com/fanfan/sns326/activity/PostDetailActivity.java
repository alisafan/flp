package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanfan.sns326.MyApplication;
import com.fanfan.sns326.R;
import com.fanfan.sns326.adapter.PostDetailAdapter;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.fragment.Fragment_Home;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lyk.weixin.image.image.Image;
import com.lyk.weixin.image.image.InfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 帖子详细信息界面(包含回帖列表)
 */
public class PostDetailActivity extends Activity
        implements PostDetailAdapter.IControl,
        AdapterView.OnItemClickListener {


    ArrayList<InfoBean> list=new ArrayList<>(); // ;数据
    ArrayList<InfoBean> listInfo;
    ListView lv;
    // 适配器
    PostDetailAdapter adapter;

    Fragment_Home fragment_home=new Fragment_Home();

    InfoBean bean;// 主贴实体类

    TextView subject,rate;// 标题, 回帖数
    ImageView iv_menu;// 顶部: 右侧的"+", 左侧头像

    View head;// 独立的头部视图

   boolean fromrepost = false;

    TitleBar titleBar=new TitleBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.activityList.add(this);// 添加到集合中

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_post_detail,null);
        setContentView(view);


        fromrepost = false;
        this.titleBar.initHeadView(this,view);
        initData();
        initView();
    }

    private void initView() {
        // 顶部的头像赋值
//        avatarImg = (ImageView) findViewById(R.id.avatarImg);
//        Utils.getInstance().display(avatarImg,
//                Constans.AVATAR);

        lv = (ListView) findViewById(R.id.lv);
        adapter = new PostDetailAdapter(this);
        //lv.addHeaderView(head);// 添加头部必须在后面执行

        adapter.setControl(this);// 传入接口
        // 反射头部(1楼)
        head = getLayoutInflater().inflate(R.layout.item_title,null);
        rate = (TextView) head.findViewById(R.id.rate);
        rate.setText(""+bean.getRate());// 显示回帖数
        lv.addHeaderView(head);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }
    String strJosn;
    private void initData() {
        // 接收之前的界面(主贴列表)传递来的Post实体类对象
        String keyPost = Constans.KEY_POST;
        bean = (InfoBean) getIntent().getSerializableExtra(keyPost);
//        list.clear();
        RequestParams params = new RequestParams();
        params.addBodyParameter("tid",""+bean.getTid());
        params.addBodyParameter("uid",""+Constans.UID);
        String url = Constans.URL_REPOSTLIST;
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                url,params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        strJosn = info.result;
                        // 下面的代码是为了解决php的输出Bug
                        strJosn = strJosn.substring(strJosn.indexOf("{"));

                        try {
                            JSONObject jsonObject = new JSONObject(strJosn);
                            // 获取一级属性
                            JSONArray jsons = jsonObject.getJSONArray("list");
                            list = new ArrayList<InfoBean>();
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
                            rate.setText(""+list.size());
                            list.add(0,bean);// 添加首贴,添加1楼
                            adapter.setmList(list);
//                        PicList = Utils.getPicList(list);// 获取图片列表
                            adapter.notifyDataSetChanged();
                            if (fromrepost){
                                lv.setSelection(list.size() - 1);
                            }
                            fromrepost = false;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("flpflp", "catch块-------" + e);

                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        //Utils.show("连接失败:"+s);
                        Log.i("spl","连接失败:"+s);
                    }
                });
    }

    // 点击回复图标"...",到达回帖的界面(发帖)
    @Override
    public void repost(InfoBean item, int position) {
        if (position == 0) {// 只回复首贴
            Intent intent = new Intent(this, ReplyPostActivity.class);
            intent.putExtra(Constans.KEY_POST, item);// 传入当前的主贴对象
            startActivityForResult(intent, Constans.REQUEST_REPOST_DETAIL);
        }
    }

    @Override
    public void guanzhu(InfoBean item) {
        // 添加关注操作 (点击头像-->关注)
       Intent intent = new Intent(this,MeActivity.class);
        intent.putExtra("uid",""+item.getUid());
        intent.putExtra("guanzhu",item.getWhat());//1-已关注,0-没关注
        startActivity(intent);

    }

   /* @Override
    public void zanClick(InfoBean item) {
        // 点赞的方法
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", "" + Constans.UID);// uid
        params.addBodyParameter("tid", "" + item.getTid());// tid

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
                            updatePA();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                }
        );
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.print("帖子详情 接收到requestCode="+requestCode
                +"\n帖子详情 接收到resultCode="+resultCode);


        if (requestCode == Constans.REQUEST_REPOST_DETAIL){
            initData();
            fromrepost = true;
            Utils.print("fromrepost"+fromrepost);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 更新数据
     */
    public void updatePA() {
        Utils.print("更新数据");
        initData();
    }

}
