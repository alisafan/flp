package com.fanfan.sns326.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 孙沛林 on 2015/12/7.
 * 工具类
 */
public class Utils {
    private Utils() {
    }
    public static Context context;
    public static void init(Context cxt) {
        context = cxt;
    }
    /**
     * 短时间显示Toast面包块
     */
    public static void show(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void print(String msg) {
        Log.i("flpflp", msg);
    }

    public static void initDir(String dir) {
        // 声明目录
        File tempDir = new File(dir);
        if(!tempDir.exists()){
            tempDir.mkdirs();// 创建目录
        }
    }

    /**xutils 图片的处理*/
    private static BitmapUtils bitmapUtils = null;

    public static BitmapUtils getInstance(){
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
            bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);

            bitmapUtils.configMemoryCacheEnabled(false);
            bitmapUtils.configDiskCacheEnabled(true);
        }

        return bitmapUtils;
    }

    /**和分享首选项中有关的4个方法或者接口*/
    /**
     * 项首选项中存入一个键值对

     * @param filename 文件名
     * @param key
     * @param value
     */
    public static void writeData(String filename,
                                 String key,String value){
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences
                =context.getSharedPreferences(filename, Activity.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor =mySharePreferences.edit();

        //用putString的方法保存数据
        editor.putString(key, value);

        //提交数据
        editor.commit();
    }

    /**
     * 从首选项中取出一个值

     * @param filename 文件名
     * @param key
     * @return
     */
    public static String readData(String filename,
                                  String key){
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences
                =context.getSharedPreferences(filename, Activity.MODE_PRIVATE);
        //用getString获取值
        return mySharePreferences.getString(key, "");
    }

    /**
     * 查询某个key是否已经存在
     * @param filename
     * @param key
     * @return
     */
    public static boolean contains(String filename, String key)
    {
        SharedPreferences sp =
                context.getSharedPreferences(filename,Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 移除某个值
     * @param filename
     * @param key
     */
    public static void remove(String filename, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.clear();
        editor.commit();
    }
    /*时间格式的转化*/
    public static String getFormatDate(long time) {
        java.util.Date date = new java.util.Date(time*1000);
        SimpleDateFormat dateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 通过正则表达式截取短信中的数字
     * @param patternContent
     * @return 只有4位数字的部分
     */
    public static String patternCode(String patternContent,String patternCoder) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);// 匹配模板
        Matcher matcher = p.matcher(patternContent);//匹配器
        if (matcher.find()) {// 寻找字符串中模板
            return matcher.group();
        }
        return null;
    }
    /**
     * 日期格式转换
     * @param millis
     * @return
     */
    public static String getTime(long millis){
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    public static String getImagePath(String path){

        if (path.indexOf("http://")!=0){
            path = Constans.URL_BASE + path;
        }
        return path;
    }

    /**
     * 生产临时照片名称
     * 使用系统当前日期加以调整作为照片的名称
     * @return
     */
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat
                = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 图片显示选项的设置
     * @return
     */
    public static DisplayImageOptions getImageOption(){
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .considerExifParams(true)
                //.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))//圆形特效
                .build();
        return options;
    }

    /**
     * 保存图片文件
     * @param bm
     * @param
     * @throws IOException
     */
    public static void saveImageFile(Bitmap bm, File myCaptureFile) throws IOException {

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Log.i("spl","图片保存在:"+myCaptureFile.getAbsolutePath());
        Constans.ABSOLUTEPATH=myCaptureFile.getAbsolutePath();
    }


}
