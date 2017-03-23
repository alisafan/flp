package com.fanfan.sns326.photos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;

/**
 * 本地相册选择和拍照的辅助类
 */
public class PhotoHelper {

    Activity mContext;//当前的上下文
    ImageView picImg;// 选择图片框
    /** 声明一个拍照结果的临时文件 */
    File tempFile;
    boolean hasPic = false; // 还未选择图片

    public PhotoHelper(){}
    public PhotoHelper(Activity mContext,ImageView picImg){
        this.mContext = mContext;
        this.picImg = picImg;


        initDir();
    }
    public File getTempFile(){
        return tempFile;
    }

    public boolean selectedPic(){
        return hasPic;
    }

    /**
     * 创建临时文件夹 _tempphoto
     */
    private void initDir() {
        // 声明目录
        Utils.initDir(Constans.TempDir);// 创建路径
        tempFile = new File(Constans.TempDir, Utils.getPhotoFileName());// 生成临时文件
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

    /**
     * 从相册选照片(媒体库)
     */
    public void selectAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");// 图片类型的限定
        intent.setAction(Intent.ACTION_GET_CONTENT);// 动作类型
        mContext.startActivityForResult(intent, Constans.SELECT_PIC_FROM_ALBUM);// 携带请求码
    }



    public void onResult(
            int requestCode, // 当初的请求码
            int resultCode, // 返回码: OK(-1)
            Intent data // 携带的数据
    ) {
        // 拍照成功 RESULT_OK= -1
        if (requestCode==Constans.SELECT_PIC_BY_TACK_PHOTO
                &&   resultCode== mContext.RESULT_OK ){
            swithCrop(Uri.fromFile(tempFile));
        }
        // 如果是直接从相册获取
        if (requestCode==Constans.SELECT_PIC_FROM_ALBUM
                &&   resultCode== mContext.RESULT_OK
                && data != null) {

            final Uri uri = data.getData();//返回相册图片的Uri
            // 添加一个"是否"对话框
            swithCrop(uri);
        }
        // 接收剪裁回来的结果
        if (requestCode==Constans.REQUEST_PHOTO_CUT
                &&   resultCode== mContext.RESULT_OK ){// 剪裁加工成功
            //让剪裁结果显示到图片框
            setPicToView(data);
        }
    }

    /**
     * 给用户一个选择:是否剪裁
     * @param uri
     */
    private void swithCrop(final Uri uri){
        // 添加一个"是否"对话框
        new AlertDialog.Builder(mContext)
                .setTitle("请选择")
                .setMessage("是否对照片进行剪裁?")
                .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 对照片进行加工
                        startPhotoZoom(uri, 150);
                    }
                })
                .setNegativeButton("否",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Utils.show(mContext,"否");
                        setFullPicToView(uri);// 全图
                    }
                })
                .create().show();
    }


    /**
     * 不进行剪裁-全图
     * @param uri
     */
    private void setFullPicToView(Uri uri) {

        hasPic = true;// 已经选图了
        Log.i("spl","全图地址:"+uri.getPath());

        ImageLoader.getInstance().displayImage(
                uri.toString(),// 图片地址--------------------
                picImg, // 容器
                Utils.getImageOption(),
                myImageListener // 添加监听器对象 打开注释:旋转90°
        );// 显示选项

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    /**
     * 发送Intent打开系统剪裁界面
     * @param uri 原图地址
     * @param size 要剪裁完后的大小
     */
    private void startPhotoZoom(Uri uri, int size) {
        // 封装Intent Action:com.android.camera.action.CROP
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);// 宽高比例1:1
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高 像素px
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        // 等待返回
        mContext.startActivityForResult(intent, Constans.REQUEST_PHOTO_CUT);
    }

    /**
     * 将进行剪裁后的图片显示到UI界面上(小图)
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        // 获取对象
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            //Parcelable-->序列化好的对象
            Bitmap photo = bundle.getParcelable("data");
            // 显示在图片控件上 Bitmap photo
            picImg.setImageBitmap(photo);
            hasPic = true;// 已经选图了
            try {
                Utils.saveImageFile(photo,tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ImageLoadingListener myImageListener = new MyDisplayListener();
    private class MyDisplayListener extends SimpleImageLoadingListener {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                int w = loadedImage.getWidth();
                int h = loadedImage.getHeight();
                Utils.show("w="+w+"h="+h);
                //Utils.show(mContext,"图片路径="+imageUri);
                try {
                    Utils.saveImageFile(loadedImage,tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

               // ImageView imageView = (ImageView) view;
                // 90度的旋转
                //Bitmap bmp= rotateBitmap(loadedImage,90);
                //imageView.setImageBitmap(bmp);
            }
        }
    }

    /**
     * 旋转一个图片
     * @param myBmp
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap myBmp, float angle){

        //获取图片的原始宽高
        int bmpWidth = myBmp.getWidth();
        int bmpHeight = myBmp.getHeight();
        //实例化matrix
        Matrix matrix = new Matrix();
        //顺时针旋转度
        matrix.postRotate(angle);
        //根据Matrix的设定产生新的Bitmap对象
        Bitmap newBmp = Bitmap.createBitmap(myBmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return newBmp;
    }

}
