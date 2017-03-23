package com.zijunlin.Zxing.Demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.fanfan.sns326.R;
import com.fanfan.sns326.cons.Constans;
import com.fanfan.sns326.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zijunlin.Zxing.Demo.camera.CameraManager;
import com.zijunlin.Zxing.Demo.decoding.CaptureActivityHandler;
import com.zijunlin.Zxing.Demo.decoding.InactivityTimer;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * 打开摄像头,扫描二维码
 */
public class CaptureActivity extends Activity
        implements Callback
{

    // 声明的Handler
    private CaptureActivityHandler handler;

    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface)
        {
            initCamera(surfaceHolder);
        }
        else
        {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
        {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy()
    {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            CameraManager.get().openDriver(surfaceHolder);
        }
        catch (IOException ioe)
        {
            return;
        }
        catch (RuntimeException e)
        {
            return;
        }
        if (handler == null)
        {
            // 实例化Handler
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView()
    {
        return viewfinderView;
    }

    public Handler getHandler()
    {
        return handler;
    }

    public void drawViewfinder()
    {
        viewfinderView.drawViewfinder();

    }

    // 是在摄像头捕捉到二维码画面,并扫描成功之后
    // 被Handlemessage处理, 再回到这里处理
    public void handleDecode(final Result obj, Bitmap barcode)
    {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (barcode == null)
        {
            dialog.setIcon(null);
        }
        else
        {

            Drawable drawable = new BitmapDrawable(barcode);
            dialog.setIcon(drawable);
        }
        // 对话框
        dialog.setTitle("扫描结果");
        dialog.setMessage("是否关注"+obj.getText()+"?");// 打印UID
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //用默认浏览器打开扫描得到的地址
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri content_url = Uri.parse(obj.getText());
//                intent.setData(content_url);
//                startActivity(intent);
                 // 跳转界面
//                Intent intent = new Intent(CaptureActivity.this, MeActivity.class);
//                intent.putExtra("uid",obj.getText());// 二维码
//                //intent.putExtra("guanzhu",user.getWhat());
//                startActivity(intent);
                doGuanzhu(obj.getText());
                // 执行关注的业务
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        dialog.create().show();
    }

    public void doGuanzhu(String uid){
        // 添加关注操作
        Utils.print("自己:" + Constans.UID + ",他人:" + uid);

        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", "" + Constans.UID);
        params.addBodyParameter("bid", "" + uid);

        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                Constans.URL_GUANZHU,
                params,// 传参
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> info) {

                        int id = 0;
                        String res = info.result;
                        Utils.print("关注返回res:" + res);
                        //finish();// 关闭当前的摄像头扫描界面
                        Utils.show("关注成功");

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Utils.show("连接失败:" + s);
                        Log.i("spl", "连接失败:" + s);
                    }
                });
    }

    private void initBeepSound()
    {
        if (playBeep && mediaPlayer == null)
        {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try
            {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            }
            catch (IOException e)
            {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate()
    {
        if (playBeep && mediaPlayer != null)
        {
            mediaPlayer.start();
        }
        if (vibrate)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener()
    {
        public void onCompletion(MediaPlayer mediaPlayer)
        {
            mediaPlayer.seekTo(0);
        }
    };

}