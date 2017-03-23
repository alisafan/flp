package com.fanfan.sns326.cons;

import android.os.Environment;

/**
 */
public class Constans {
    // 远程服务器地址(基础地址)  http://115.28.78.90/
    public static final String URL_BASE = "http://www.codepower.cn/flp/";
//    public static final String URL_BASE = "http://115.28.78.90/flp/";
//    http://www.codepower.cn/logindo.php?uname=test&upass=1111
    // 登录访问接口
    public static final String URL_LOGIN = URL_BASE + "login.php";
    // 检查用户名重复的接口
    public static final String URL_CHKUNAME = URL_BASE + "checkuname.php";
    // 注册新用户的接口
    public static final String URL_REG = URL_BASE + "registerdo.php";
    // 当前用户的id
    public static int UID = 0;
    // 当前用户的用户名
    public static String UNAME = "";
    // 当前用户的头像地址
    public static String AVATAR = "";
    // 获取用户信息的接口
    public static final String URL_USER = URL_BASE + "user.php";
    // 获取用户信息的接口
    public static final String URL_AVATAR = URL_BASE + "avatardo.php";
    // 获取用户列表的接口  http://codepower.cn/flp/userlist.php
    public static final String URL_USERLIST = URL_BASE + "userlist.php";

    // 发表帖子的接口   http://codepower.cn/flp/insertpost.php
    public static final String URL_POST_SUBMIT = URL_BASE + "insertpost.php";
    // 获取帖子列表的接口(更宽松的)  http://codepower.cn/flp/postlistall.php
    public static final String URL_POSTLIST_ALL = URL_BASE + "postlistall.php";
    /** 获取回帖列表的接口地址 */  // http://codepower.cn/flp/repostlist.php
    public static final String URL_REPOSTLIST= URL_BASE +"repostlist.php";
    // 分享首选项的文件名
    public static final String SP_FILE = "userlogin";
    // 分享首选项的用户名
    public static final String SP_UNAME = "SP_UNAME";
    // 分享首选项的密码
    public static final String SP_UPASS = "SP_UPASS";
    // 分享首选项的时间
    public static final String SP_TIME = "SP_TIME";

    // 存放临时照片的目录
    public static String TempDir =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/_tempphoto";
   public static String ABSOLUTEPATH;
    /** 请求码: 使用照相机拍照获取图片 */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /** 请求码: 使用相册中的图片 */
    public static final int SELECT_PIC_FROM_ALBUM = 2;
    //系统剪裁界面的请求码
    public static final int PHOTO_REQUEST_CUT = 3;
    /** 使用相册中的图片 */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 4;
    /** 请求码: 区系统剪裁界面 */
    public static final int REQUEST_PHOTO_CUT = 5;
    //更换头像的请求码
    public static final int REQUEST_CHANGE_AVATAR = 6;
    //帖子详细界面的请求码
    public static final int REQUEST_POST_DETAIL = 7;
    //发表回帖界面的请求码
    public static final int REQUEST_REPOST_DETAIL = 8;

    /** 添加关注 接口地址 */
    public static final String URL_GUANZHU= URL_BASE +"insertguanzhu.php" ;
    // 获取是否关注的信息
    public static final String URL_GUANZHU_IF = URL_BASE + "guanzhu.php";
    /** 取消关注 接口地址 */
    public static final String URL_GUANZHU_DEL= URL_BASE +"deleteguanzhu.php" ;

    //主贴实体类
    public static final String KEY_POST = "KEY_POST";
    /**  获取帖子列表的接口 */   // http://codepower.cn/flp/postlistallb.php
    public static final String URL_POSTLIST_SEARCH = URL_BASE + "postlistallb.php";
    /**  用户点赞的接口 */
    public static final String URL_ADD_ZAN = URL_BASE + "insertzan.php";

    public static final int REQUEST_CHANGE_GUANZHU = 9;//关注按钮的事件
    public static final int REQUEST_CHANGE_AVATARANDPASS = 10;//编辑页面的修改头像和密码

}

