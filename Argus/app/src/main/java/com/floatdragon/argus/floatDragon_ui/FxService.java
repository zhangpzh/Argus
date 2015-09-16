package com.floatdragon.argus.floatDragon_ui;

/**
 * Created by Administrator on 2015/9/8.
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import com.floatdragon.argus.R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FxService extends Service
{
    int ClickT = 0;
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

    private static FxService fxService=null;
    public FxService(){
        fxService=this;
    }
    public static FxService  getFxService() {
        return fxService;
    }


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
//        Drawable sound_on = getResources().getDrawable(R.drawable.sound_on);
//        Drawable sound_off = getResources().getDrawable(R.drawable.sound_off);
//        Drawable vibration = getResources().getDrawable(R.drawable.vibration);
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

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (GridLayout) inflater.inflate(R.layout.float_layout, null);


        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮

        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);
        mFloatView.setVisibility(View.VISIBLE);
        AudioManager audio =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(getR_Mode(audio) == 2){
            mFloatView.setBackground(getResources().getDrawable(R.drawable.sound_on));
        }
        if(getR_Mode(audio) == 1){
            mFloatView.setBackground(getResources().getDrawable(R.drawable.vibration));
        }
        if(getR_Mode(audio) == 0){
            mFloatView.setBackground(getResources().getDrawable(R.drawable.sound_off));
        }
        mFloatView2 = (Button)mFloatLayout.findViewById(R.id.float_id2);
        mFloatView2.setVisibility(View.VISIBLE);
        mFloatView3 = (Button)mFloatLayout.findViewById(R.id.float_id3);
        mFloatView3.setVisibility(View.VISIBLE);
        mFloatView4 = (Button)mFloatLayout.findViewById(R.id.float_id4);
        mFloatView4.setVisibility(View.VISIBLE);
        if(getMobileDataStatus("getMobileDataEnabled") == true){
            mFloatView4.setBackground(getResources().getDrawable(R.drawable.apn_on));
        }
        else{
            mFloatView4.setBackground(getResources().getDrawable(R.drawable.apn_off));
        }

        mFloatView5 = (Button)mFloatLayout.findViewById(R.id.float_id5);
        mFloatView5.setVisibility(View.VISIBLE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(FxService.this,"找不到蓝牙设备",Toast.LENGTH_SHORT).show();
        }
        else if(mBluetoothAdapter.isEnabled() == false) {
           mFloatView5.setBackground(getResources().getDrawable(R.drawable.bluetooth_off));
        }
        else {
            mFloatView5.setBackground(getResources().getDrawable(R.drawable.bluetooth_on));
        }
        mFloatView6 = (Button)mFloatLayout.findViewById(R.id.float_id6);
        mFloatView6.setVisibility(View.VISIBLE);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_enabled));
        } else {
            mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_disabled));
        }


        mFloatView7 = (Button)mFloatLayout.findViewById(R.id.float_id7);
        mFloatView7.setVisibility(View.VISIBLE);
        mFloatView7.setBackground(getResources().getDrawable(R.drawable.power));
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);

//        mFloatLayout.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                StaticData.isShow = 0;
//                MyService.getMyService().isShow(false);
//                return false;
//            }
//        });



        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AudioManager audio =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if(getR_Mode(audio) == 2 ) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    mFloatView.setBackground(getResources().getDrawable(R.drawable.vibration));
                    Toast.makeText(FxService.this, "震动模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getR_Mode(audio) == 1){
                    audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    mFloatView.setBackground(getResources().getDrawable(R.drawable.sound_off));
                    Toast.makeText(FxService.this, "静音模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getR_Mode(audio) == 0){
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mFloatView.setBackground(getResources().getDrawable(R.drawable.sound_on));
                    Toast.makeText(FxService.this, "音量模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(FxService.this, "RingerMode Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mFloatView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                Toast.makeText(FxService.this, "进入微信", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();

                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
            }
        });
        mFloatView3.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(FxService.this, "HOME", Toast.LENGTH_SHORT).show();
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(mHomeIntent);

            }
        });
        mFloatView4.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
              if(getMobileDataStatus("getMobileDataEnabled") == true){
                  setMobileDataStatus(mContext,false);
                  Toast.makeText(FxService.this,"移动数据已关闭",Toast.LENGTH_SHORT).show();
                  mFloatView4.setBackground(getResources().getDrawable(R.drawable.apn_off));
              }
               else{
                  setMobileDataStatus(mContext,true);
                  Toast.makeText(FxService.this,"移动数据已开启",Toast.LENGTH_SHORT).show();
                  mFloatView4.setBackground(getResources().getDrawable(R.drawable.apn_on));
              }

            }
        });
        mFloatView5.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // Toast.makeText(FxService.this, "555onClick", Toast.LENGTH_SHORT).show();
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(FxService.this,"找不到蓝牙设备",Toast.LENGTH_SHORT).show();
                }
                else if(mBluetoothAdapter.isEnabled() == false) {
                    Toast.makeText(FxService.this,"蓝牙已开启",Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.enable();
                    mFloatView5.setBackground(getResources().getDrawable(R.drawable.bluetooth_on));
                }
                else {
                    Toast.makeText(FxService.this,"蓝牙已关闭",Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.disable();
                    mFloatView5.setBackground(getResources().getDrawable(R.drawable.bluetooth_off));
                }



            }
        });
        mFloatView6.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(FxService.this, "Wifi已关闭", Toast.LENGTH_SHORT).show();
                    mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_disabled));
                } else {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(FxService.this, "Wifi已启用", Toast.LENGTH_SHORT).show();
                    mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_enabled));
                }
            }
        });

        mFloatView7.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(FxService.this, "退出", Toast.LENGTH_SHORT).show();
                StaticData.isShow = 0;
                MyService.getMyService().isShow(false);
            }
        });

    }


    public int getR_Mode(AudioManager audio){
        return audio.getRingerMode();
    }


    public void setMobileDataStatus(Context context,boolean enabled)

    {

        ConnectivityManager conMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //ConnectivityManager类

        Class<?> conMgrClass = null;

        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try
        {

            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        }

        catch(ClassNotFoundException e)
        {

            e.printStackTrace();
        }
        catch(NoSuchFieldException e)
        {

            e.printStackTrace();
        }

        catch(SecurityException e)
        {
            e.printStackTrace();

        }
        catch(NoSuchMethodException e)

        {
            e.printStackTrace();
        }

        catch(IllegalArgumentException e)
        {

            e.printStackTrace();
        }

        catch(IllegalAccessException e)
        {

            e.printStackTrace();
        }

        catch(InvocationTargetException e)

        {

            e.printStackTrace();

        }

    }



    //获取移动数据开关状态



    public boolean getMobileDataStatus(String getMobileDataEnabled)

    {

        ConnectivityManager cm;

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        Class cmClass = cm.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try
        {

            Method method = cmClass.getMethod(getMobileDataEnabled, argClasses);

            isOpen = (Boolean)method.invoke(cm, argObject);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return isOpen;

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