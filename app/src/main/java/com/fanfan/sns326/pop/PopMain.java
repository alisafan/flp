package com.fanfan.sns326.pop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fanfan.sns326.R;
import com.fanfan.sns326.activity.MainActivity;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.pic.PublishedActivity;
import com.fanfan.sns326.utils.Utils;
import com.zijunlin.Zxing.Demo.CaptureActivity;


/**
 * 下拉式菜单, 右上角"+"
 */
public class PopMain extends PopupWindow {
    // 声明一个视图
    private View conentView;
    // 声明它的菜单项
    RelativeLayout re_layout1, re_layout2;

    /**
     * 构造器
     * @param context
     */
    public PopMain(final Activity context) {
        // 反射器
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 反射布局
        conentView = inflater.inflate(R.layout.mn_main, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);

        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        // 实例化菜单项
        re_layout1 = (RelativeLayout) conentView.findViewById(R.id.re_layout1);
        re_layout2 = (RelativeLayout) conentView.findViewById(R.id.re_layout2);

        // 设置监听
        re_layout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ((MainActivity)context).changeFragment(0);// 碎片切回首页

                // 发帖
                Utils.print("发帖");// context -- MainActivity2
                Intent intent = new Intent(context, PublishedActivity.class);
                context.startActivityForResult(intent, Constans.REQUEST_POST_DETAIL);
                PopMain.this.dismiss();
            }

        });
        re_layout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 扫一扫
                Utils.print("扫一扫");
                Intent intent = new Intent(context, CaptureActivity.class);
                context.startActivity(intent);
                PopMain.this.dismiss();
            }

        });

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        // 打开/收起的切换
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);// 锚点,xy偏移量

        } else {
            this.dismiss();
        }

    }
}

