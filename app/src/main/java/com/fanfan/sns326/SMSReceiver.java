package com.fanfan.sns326;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.fanfan.sns326.inter.ICallBack;
import com.fanfan.sns326.utils.Utils;

/**
 * Created by Administrator on 2017/1/28.
 */

public class SMSReceiver extends BroadcastReceiver {

    public SMSReceiver(){}
    // 编写构造器,传入接口
    public SMSReceiver(ICallBack callBack){this.callBack=callBack;}
    ICallBack callBack;// 声明接口
    // 下面的模板,可以匹配4位的数字
    private String patternCoder = "(?<!\\d)\\d{4}(?!\\d)";// 正则表达式
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";// "接收到短信"的广播
    @Override
    public void onReceive(Context context, Intent intent) {
        //Context context 上下文: 启动服务
        //Intent intent: 再次识别广播的类型; 可以携带参数
        // 目标: 1. 截获短信 2. 读取内容 3. 解析内容
        if (intent.getAction().equals(ACTION)) {
            // 获取Intent中的携带数据
            // 获取Bundle是intent中的数据载体,它是键值对Map
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.size() > 0) {
                // 获取短信的集合
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object obj : pdus) {
                    // pdu=1条短信
                    byte[] pdu = (byte[]) obj;// 将对象转成pdu
                    // 获取短信对象SmsMessage byte[]--> SmsMessage
                    SmsMessage message = SmsMessage.createFromPdu(pdu);
                    // 短信的内容body
                    String body = message.getMessageBody();
                    // 短信的发送者(手机号),可以自动回复
                    String sender = message.getDisplayOriginatingAddress();
                    String code = Utils.patternCode(body,patternCoder);
                    // 返回数字,显示到UI中
                    callBack.setCode(code);
                    Log.i("ffffff", "截获短信内容:" + body);
                    Log.i("ffffff ", "验证码:" + code);
                    Log.i("ffffff ", "发送者:" + sender);
                }
            }
        }
    }
}

