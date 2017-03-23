package com.fanfan.sns326.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.pop.PopMain;

/**
 * Created by 钧 on 2016/1/2.
 */
public class TitleBar {

    // 右上角的"+"按钮
    ImageView iv_menu;
    //右上角的搜索按钮
    ImageView iv_search;
    Activity context;

    public void initHeadView(Activity context, View view){
        this.context = context;// MainActivity2
        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMenu();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearch();
            }
        });
    }

    private void clickSearch() {
        // 跳转界面 ---> 搜索界面
        Intent it = new Intent(context,SearchActivity.class);
        context.startActivity(it);
    }

    public void clickMenu(){
        //Utils.print("点击了菜单");
        PopMain popMain = new PopMain(context);
        popMain.showPopupWindow(iv_menu);
    }
}
