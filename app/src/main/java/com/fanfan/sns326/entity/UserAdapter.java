package com.fanfan.sns326.entity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * 用户列表的适配器
 */
public class UserAdapter extends BaseAdapter{

    LayoutInflater inflater;
    List<User> list;

    public void setList(List<User> list) {
        this.list = list;
    }

    BitmapUtils bitmapUtils;

    public UserAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        bitmapUtils = Utils.getInstance();
    }

    @Override
    public int getCount() {
        return (list == null)? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder  holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, null);
            holder = new ViewHolder();
            holder.mIndex = (TextView) convertView.findViewById(R.id.tv_index);
            holder.image = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nickname = (TextView) convertView.findViewById(R.id.uname);
            holder.guanzhu = (TextView) convertView.findViewById(R.id.btn_guanzhu);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final User item = list.get(position);

        String str = null;
        String currentLetter = item.getPinyin().charAt(0) + "";
        // 根据上一个首字母,决定当前是否显示字母
        if (position == 0) {
            str = currentLetter;
        } else {
            // 上一个人的拼音的首字母
            String preLetter = list.get(position - 1).getPinyin().charAt(0)
                    + "";
            if (!TextUtils.equals(preLetter, currentLetter)) {
                str = currentLetter;
            }
        }

        // 根据str是否为空,决定是否显示索引栏
        holder.mIndex.setVisibility(str == null ? View.GONE : View.VISIBLE);
        holder.mIndex.setText(currentLetter);

        if (item.getAvatar()!=null && item.getAvatar().contains("http")) {
            bitmapUtils.display(holder.image, item.getAvatar());
        } else {
            bitmapUtils.display(holder.image, Constans.URL_BASE + item.getAvatar());
        }
        holder.nickname.setText(item.getNickname());

        //判断是否被当前用户所关注: 根据布尔值显示按钮上的文字
        if (item.getWhat()!=null && item.getWhat().equals("1")){
            holder.guanzhu.setVisibility(View.VISIBLE);
        }else{
            holder.guanzhu.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 声明定义点击按钮事件的处理接口
     *
     */
    public void setControl(IControl control) {
        this.control = control;
    }

    private IControl control;

    public interface IControl{
        void clickGuanzhu(String uid);
    }

    public static class ViewHolder{
        TextView mIndex;
        ImageView image;
        TextView nickname;
        TextView guanzhu;// 新添加的关注按钮

    }


}
