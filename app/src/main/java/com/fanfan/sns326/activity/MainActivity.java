package com.fanfan.sns326.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.fragment.Fragment_Home;
import com.fanfan.sns326.fragment.Fragment_List;
import com.fanfan.sns326.fragment.Fragment_Me;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fanfan.sns326.pic.view.FileUtils.saveBitmap;


public class MainActivity extends FragmentActivity {

    TitleBar titleBar=new TitleBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        setContentView(view);
        this.titleBar.initHeadView(this,view);

        initView();
        initFragment();
        change(fragments[0]);// "我"
        checkHighLight(0);// 设定高亮
    }
    private void initView() {
        arr_tv = new TextView[3];
        arr_tv[0] = (TextView) findViewById(R.id.tv_home);
        arr_tv[1] = (TextView) findViewById(R.id.tv_list);
        arr_tv[2] = (TextView) findViewById(R.id.tv_me);
        arr_iv = new ImageView[3];
        arr_iv[0] = (ImageView) findViewById(R.id.iv_home);
        arr_iv[1] = (ImageView) findViewById(R.id.iv_list);
        arr_iv[2] = (ImageView) findViewById(R.id.iv_me);


    }
    /**
     * 编辑用户资料 : Fragment_me.xml "clickAvatar"
     * @param view
     */
    public void clickAvatar(View view){
        // 跳转个人资料
        Intent intent = new Intent(this, EditorActivity.class);
        // 等待返回值:
        startActivityForResult(intent,Constans.REQUEST_CHANGE_AVATAR);
    }
    //+  wo碎片中的 ：退出当前账号
    public void line1Click(View v) {
        //退出当前账号
        if (v.getId() == R.id.line1back) {
            // 删除分享首选项
            Utils.remove(Constans.SP_FILE, Constans.SP_UNAME);
            Utils.remove(Constans.SP_FILE,Constans.SP_UPASS);
            // 跳转
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /*+*/
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory()
            ,getPhotoFileName());

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private String getPhotoFileName2() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'ava'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".png";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.print("Main 接收到requestCode="+requestCode
                +"\nMain 接收到resultCode="+resultCode);

        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
        switch (requestCode) {
            case Constans.SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
                //doPhoto(requestCode, data);
                if (data != null) {
                    startPhotoZoom(data.getData(), 150);
                }
                break;
            case Constans.SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
                //doPhoto(requestCode, data);
                startPhotoZoom(Uri.fromFile(tempFile), 150);
                break;
            case Constans.PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                break;
            case Constans.REQUEST_POST_DETAIL:// 发完贴,回来了!!!!!!
                // 数据更新
                ((Fragment_Home)fragment_home).onRefresh();// 刷新帖子列表
                break;
            case Constans.REQUEST_CHANGE_GUANZHU:// 关注有修改,回来了!!!!!!
                // 数据更新
                ((Fragment_List)fragment_list).updateFL();// 刷新帖子列表
                break;
            case Constans.REQUEST_CHANGE_AVATAR:
                // 更新"个人中心"
                Utils.print("个人中心");
                ((Fragment_Me)fragment_me).getUserInfo(Constans.UID);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*+*/
    /**
     * 发送Intent打开系统剪裁界面
     * @param uri
     * @param size
     */
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);// 宽高比例1:1
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, Constans.PHOTO_REQUEST_CUT);
    }
    /*+*/
    //将进行剪裁后的图片显示到UI界面上
    // 上传
    /** 获取到的图片路径 */
    private String picPath = "";
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");


            picPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/"+ getPhotoFileName2();
            saveBitmap(photo,picPath);// 保存
            uploadAvatar(picPath);// 搞定图片路径: bitmap-->File

        }
    }
    /*+*/
    private void uploadAvatar(final String path) {
        Utils.print("上传路径:"+path);
        File pic = new File(path);
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid",""+Constans.UID);
        params.addBodyParameter("avatar",pic);

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_AVATAR,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        String result = info.result.trim();
                        Utils.print("图片上传:"+result);
                        result = result.substring(result.indexOf("{")+1);
                        //Utils.show(result);
                        if (result.equals("0")){
                            Utils.show("图片上传失败");
                        }else {
                            Utils.show("图片上传成功");
                            Constans.AVATAR = path;
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:"+s);
                    }
                }
        );
    }

    // 切换某个碎片
    public void changeFragment(int index){
        change(fragments[index]);
        checkHighLight(index);
    }

    /**--------------------------------------------------------*/

    /*---------------------------------------------------------*/


    // 声明碎片: 首页, 通讯录,发现,我
    Fragment fragment_home,fragment_list,fragment_me;
    Fragment[] fragments;// 碎片数组
    TextView[] arr_tv;// 文本的数组
    ImageView[] arr_iv;// 图标的数组

    // 下面的两个数组是分别装资源ID
    // 点击的ID
    int[] arr_id_box = {
            R.id.box1,
            R.id.box2,
            R.id.box3
    };
    // 默认的ID(白色的图片)
    int[] arr_id_default = {
            R.drawable.shouye,
            R.drawable.tongxunlu,
            R.drawable.wo
    };
    // 选中的ID(绿色的图片)
    int[] arr_id_selected = {
            R.drawable.shouyef,
            R.drawable.tongxunluf,
            R.drawable.wof
    };
    /**
     * 初始化碎片
     */
    private void initFragment(){
        fragment_home = new Fragment_Home();
        fragment_list = new Fragment_List();
        fragment_me = new Fragment_Me();

        fragments = new Fragment[3];
        fragments[0] = fragment_home;
        fragments[1] = fragment_list;
        fragments[2] = fragment_me;
    }

    /**
     * 切换碎片的方法
     * @param f
     */
    private void change(Fragment f){
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content,f)
                .commit();// 提交事务

    }

    // 点击按钮
    public void clickBox(View v){
        // 循环遍历,检查id
        for (int i = 0; i< arr_id_box.length; i++){
            if (v.getId() == arr_id_box[i]){
                change(fragments[i]);// 切换碎片
                checkHighLight(i);// 添加高亮
            }
        }
    }

    /**
     * 选中高亮
     * @param index
     */
    private void checkHighLight(int index){
        // 高亮字体
        for (int i = 0; i< arr_tv.length; i++){
            arr_tv[i].setTextColor(Color.BLACK);// 黑色
        }
        arr_tv[index].setTextColor(getResources().getColor(R.color.colornopress));//绿色
        // 高亮图标
        // 全部的图标要先统一程默认的图标
        setAllImageDefault();
        arr_iv[index].setImageResource(arr_id_selected[index]);
    }

    // 设置默认图片资源
    private void setAllImageDefault() {
        for (int i = 0; i< arr_iv.length; i++){
            arr_iv[i].setImageResource(arr_id_default[i]);
        }
    }
    /*------------------------------------------*/
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
