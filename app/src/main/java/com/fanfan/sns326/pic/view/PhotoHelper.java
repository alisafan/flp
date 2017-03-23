package com.fanfan.sns326.pic.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.GridView;

import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * 本地相册选择和拍照的辅助类
 */
public class PhotoHelper {

    Activity mContext;//当前的上下文
    GridView picImg;// 选择图片框
    /** 声明一个拍照结果的临时文件 */
    File tempFile;

    public PhotoHelper(){}
    public PhotoHelper(Activity mContext, GridView picImg){
        this.mContext = mContext;
        this.picImg = picImg;

        initDir();
    }
    public File getTempFile(){
        return tempFile;
    }

    /**
     * 创建临时文件夹 _tempphoto
     */
    private void initDir() {
        // 声明目录
        PicUtils.initDir(Constans.TempDir);// 创建路径
        tempFile = new File(Constans.TempDir, PicUtils.getPhotoFileName());// 生成临时文件
    }

    /**
     * 去系统的拍照界面
     */
    public void takePhoto() {
        // 添加Action类型：MediaStore.ACTION_IMAGE_CAPTURE
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片(结果)的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        // 等待返回结果,同时发送请求码
        mContext.startActivityForResult(intent, Constans.SELECT_PIC_BY_TACK_PHOTO);
    }





    public void onResult(
            int requestCode, // 当初的请求码
            int resultCode, // 返回码: OK(-1)
            Intent data // 携带的数据
    ) {
        // 拍照成功 RESULT_OK= -1
        if (requestCode==Constans.SELECT_PIC_BY_TACK_PHOTO
                &&   resultCode== mContext.RESULT_OK ){
            /** todo 图片地址的封装*/
            setPicToView(data);
            Bimp.drr.add(tempFile.getPath());
            Log.i("fffffff","===PhotoHelper拍照图片的地址是：");

        }
    }

    private void setPicToView(Intent picdata) {
        // 获取对象
        Bundle bundle=null;
        if (picdata != null) {
            bundle= picdata.getExtras();
        }

        if (bundle != null) {
            //Parcelable-->序列化好的对象
            Bitmap photo = bundle.getParcelable("data");
            try {
                Utils.saveImageFile(photo,tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}
