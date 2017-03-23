package com.fanfan.sns326.pic.view;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * 工具类
 */
public class PicUtils {

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

    public static void initDir(String dir) {
        // 声明目录
        File tempDir = new File(dir);
        if(!tempDir.exists()){
            tempDir.mkdirs();// 创建目录
        }

    }
}
