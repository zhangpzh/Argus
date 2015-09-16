package com.floatdragon.argus.floatDragon_ui;

/**
 * Created by Administrator on 2015/9/9.
 */
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.floatdragon.argus.MainActivity;
import com.floatdragon.argus.app_information.appInfo;

import com.floatdragon.argus.R;

import java.util.ArrayList;

public class ItService extends Service
{
    int ClickT = 0;
    int appInfoSize = 0;

    //定义浮动窗口布局
    GridLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    Button mFloatView,mFloatView2,mFloatView3;
    Button mFloatView4,mFloatView5,mFloatView6,mFloatView7;

    private static final String TAG = "FxService";
    private Context mContext = null;
    private WifiManager wifiManager;

    private static ItService itService=null;
    public ItService(){
        itService=this;
    }
    public static ItService  getItService() {
        return itService;
    }

    ArrayList<appInfo> appInfos = null;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "oncreat");
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void createFloatView()
    {

        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER | Gravity.CENTER;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //设置悬浮窗口长宽数据
        wmParams.width = 600;
        wmParams.height = 600;

        final LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (GridLayout) inflater.inflate(R.layout.float_layout, null);
        mFloatLayout.getBackground().setAlpha(100);

        appInfos =  MainActivity.getMainActivity().getRegisteredAppInfos();
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);



        mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
        mFloatView2 = (Button) mFloatLayout.findViewById(R.id.float_id2);
        mFloatView3 = (Button) mFloatLayout.findViewById(R.id.float_id3);
        mFloatView4 = (Button) mFloatLayout.findViewById(R.id.float_id4);
        mFloatView5 = (Button) mFloatLayout.findViewById(R.id.float_id5);
        mFloatView6 = (Button) mFloatLayout.findViewById(R.id.float_id6);
        mFloatView7 = (Button) mFloatLayout.findViewById(R.id.float_id7);
      //  Toast.makeText(ItService.this, "appInfos.size()" + appInfos.size(), Toast.LENGTH_SHORT).show();
        //浮动窗口按钮
        appInfoSize = appInfos.size();

        switch (appInfoSize) {
            default:
                mFloatView7.setVisibility(View.VISIBLE);
                mFloatView7.setBackground(appInfos.get(6).getAppIcon());
            case 6:
                mFloatView.setVisibility(View.VISIBLE);
                mFloatView.setBackground(appInfos.get(0).getAppIcon());
            case 5:
                mFloatView2.setVisibility(View.VISIBLE);
                mFloatView2.setBackground(appInfos.get(1).getAppIcon());
            case 4:
                mFloatView4.setVisibility(View.VISIBLE);
                mFloatView4.setBackground(appInfos.get(2).getAppIcon());
            case 3:
                mFloatView6.setVisibility(View.VISIBLE);
                mFloatView6.setBackground(appInfos.get(3).getAppIcon());
            case 2:
                mFloatView5.setVisibility(View.VISIBLE);
                mFloatView5.setBackground(appInfos.get(4).getAppIcon());
            case 1:
                mFloatView3.setVisibility(View.VISIBLE);
                mFloatView3.setBackground(appInfos.get(5).getAppIcon());
        }

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);

        mFloatLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                StaticData.isShow2 = 0;
                MyService.getMyService().isShow2(false);
                return false;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(ItService.this, "001", Toast.LENGTH_SHORT).show();
//                if(appInfoSize == 0) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(0).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }

            }
        });
        mFloatView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if(appInfoSize == 1) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(1).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });
        mFloatView3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if(appInfoSize == 2 ) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(2).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });
        mFloatView4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if(appInfoSize == 3) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(3).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });
        mFloatView5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if(appInfoSize == 4) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(4).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });
        mFloatView6.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
//                if(appInfoSize == 5) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(5).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });

        mFloatView7.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
//                if(appInfoSize == 6) {
//                    Intent intent = new Intent(ItService.this,SelectAppsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else{
                    Intent intent = appInfos.get(6).getAppIntent();
                    if(intent == null)
                        return;
                    startActivity(intent);
//                }
            }
        });

    }




    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

}