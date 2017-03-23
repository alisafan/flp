package com.fanfan.sns326;

import android.app.Activity;
import android.app.Application;

import com.fanfan.sns326.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/24.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Utils.init(getApplicationContext());
        super.onCreate();
        DisplayImageOptions defaultOptions = new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.empty_photo)
                .showImageOnFail(R.drawable.empty_photo)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)//缓存一百张图片
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
    // 活动列表
    public static List<Activity> activityList = new ArrayList<Activity>();

    public static void finishAll(){
        for (int i=0; i<activityList.size(); i++){
            if (activityList.get(i)!= null){
                activityList.get(i).finish();
            }
        }
    }
}
