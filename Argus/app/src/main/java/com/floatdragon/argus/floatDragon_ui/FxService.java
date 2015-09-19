package com.floatdragon.argus.floatDragon_ui;

/**
 * Created by Administrator on 2015/9/8.
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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
    boolean longClicked,longClicked2;
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    PercentRelativeLayout mGridLayout;
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

    //bright
    private Button mBrightness,mBrightness2;
    private static final int LIGHT_NORMAL = 64;
    private static final int LIGHT_50_PERCENT = 127;
    private static final int LIGHT_75_PERCENT = 191;
    private static final int LIGHT_100_PERCENT = 255;
    private static final int LIGHT_AUTO = 0;
    private static final int LIGHT_ERR = -1;
    private boolean flashLight = false;
    private RotationObserver obverse;

    private android.hardware.Camera camera = null;


    private BrightObserver mBrightObserver;
    private PowerManager mPowerManager;


    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "oncreat");
        obverse = new RotationObserver(new Handler());
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
        wmParams.horizontalWeight = 0;
        wmParams.verticalWeight = 0;
        //设置悬浮窗口长宽数据
        //设置为全屏
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        //设置悬浮窗口长宽数据
        // wmParams.width = 600;
        // wmParams.height = 600;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout_big, null);
        mGridLayout = (PercentRelativeLayout) mFloatLayout.findViewById(R.id.gridLayout);
        mGridLayout.getBackground().setAlpha(100);

        ViewGroup.LayoutParams lp = mGridLayout.getLayoutParams();
        lp.width = StaticData.screenWidth / 5 * 3;
        lp.height = lp.width ;
        mGridLayout.setLayoutParams(lp);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮

        mFloatView = (Button)mFloatLayout.findViewById(R.id.button);
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
        mFloatView2 = (Button)mFloatLayout.findViewById(R.id.button2);
        // mFloatView2.setVisibility(View.VISIBLE);
        mFloatView3 = (Button)mFloatLayout.findViewById(R.id.button3);
        // mFloatView3.setVisibility(View.VISIBLE);
        mFloatView4 = (Button)mFloatLayout.findViewById(R.id.button4);
        mFloatView4.setVisibility(View.VISIBLE);
        if(getRotationStatus(mContext)){
            mFloatView4.setBackgroundResource(R.drawable.rotation_on);
        }
        else{
            mFloatView4.setBackgroundResource(R.drawable.rotation_off);
        }

        mFloatView5 = (Button)mFloatLayout.findViewById(R.id.button5);
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
        mFloatView6 = (Button)mFloatLayout.findViewById(R.id.button6);
        mFloatView6.setVisibility(View.VISIBLE);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_enabled));
        } else {
            mFloatView6.setBackground(getResources().getDrawable(R.drawable.wifi_disabled));
        }

        mPowerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mBrightObserver = new BrightObserver(new Handler());
        mBrightness = (Button)mFloatLayout.findViewById(R.id.button3);
        mBrightness2 = (Button)mFloatLayout.findViewById(R.id.button2);
        mBrightness.setVisibility(View.VISIBLE);
        mBrightness2.setVisibility(View.VISIBLE);
        mBrightness.setBackground(getResources().getDrawable(R.drawable.brightness_most));
        mBrightness2.setBackground(getResources().getDrawable(R.drawable.brightness_more));
        refreshButton();
        mBrightness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    longClicked = true;
                    //mBrightness.setBackgroundResource (R.drawable.?图标不够用了，我要去P个更亮的图标);//有这个函数你能不能别用setBackground了
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            while (longClicked) {
                                try {
                                    setBrightStatus();
                                    Thread.sleep(330);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    t.start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // mBrightness.setBackgroundResource (R.drawable.);//
                    longClicked = false;
//                    StaticData.isShow = 0;
//                    MyService.getMyService().isShow(false);
                }
                return true;

            }
        });
        mBrightness2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    longClicked2 = true;
                    //mBrightness.setBackgroundResource (R.drawable.?图标不够用了，我要去P个更亮的图标);//有这个函数你能不能别用setBackground了
                    Thread t2 = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            while (longClicked2) {
                                try {
                                    setBrightStatus2();
                                    Thread.sleep(330);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    t2.start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // mBrightness.setBackgroundResource (R.drawable.);//
                    longClicked2 = false;
//                    StaticData.isShow = 0;
//                    MyService.getMyService().isShow(false);
                }
                return true;

            }
        });


        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                StaticData.isShow = 0;
                MyService.getMyService().isShow(false);
                return true;
            }
        });
        mGridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


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
//        mFloatView2.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                // TODO Auto-generated method stub
//                Toast.makeText(FxService.this, "进入微信", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//
//                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
//                intent.setAction(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setComponent(cmp);
//                startActivity(intent);
//            }
//        });
//        mFloatView3.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                Toast.makeText(FxService.this, "HOME", Toast.LENGTH_SHORT).show();
//                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
//
//                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
//                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                startActivity(mHomeIntent);
//
//            }
//        });
        /*
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
        }); */
        mFloatView4.setOnClickListener(new OnClickListener()  {

        @Override
        public void onClick(View v)
        {
            if (getRotationStatus(mContext)) {
                setRotationStatus(getContentResolver(), 0);
                v.setBackgroundResource(R.drawable.rotation_off);
            }
            else {
                setRotationStatus(getContentResolver(), 1);
                v.setBackgroundResource(R.drawable.rotation_on);
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

//        mFloatView7.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                Toast.makeText(FxService.this, "退出", Toast.LENGTH_SHORT).show();
//                StaticData.isShow = 0;
//                MyService.getMyService().isShow(false);
//            }
//        });

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

    //更新按钮
    private void refreshButton()
    {
        switch (getBrightStatus())
        {
            case LIGHT_NORMAL:
                mBrightness.setText("light_50percent");
                break;
            case LIGHT_50_PERCENT:
                mBrightness.setText("light_75percent");
                break;
            case LIGHT_75_PERCENT:
                mBrightness.setText("light_100percent");
                break;
            case LIGHT_100_PERCENT:
                mBrightness.setText("string.light_auto");
                break;
            case LIGHT_AUTO:
                mBrightness.setText("string.light_normal");
                break;
            case LIGHT_ERR:
                mBrightness.setText("light_err");
                break;
        }
    }

    //得到当前亮度值状态
    private int getBrightStatus()
    {

        // TODO Auto-generated method stub
        int light = 0;
        boolean auto = false;
        ContentResolver cr = getContentResolver();

        try
        {
            auto = Settings.System.getInt(cr,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            if (!auto)
            {
                light = android.provider.Settings.System.getInt(cr,
                        Settings.System.SCREEN_BRIGHTNESS, -1);
                if (light > 0 && light <= LIGHT_NORMAL)
                {
                    return LIGHT_NORMAL;
                }
                else if (light > LIGHT_NORMAL && light <= LIGHT_50_PERCENT)
                {
                    return LIGHT_50_PERCENT;
                }
                else if (light > LIGHT_50_PERCENT && light <= LIGHT_75_PERCENT)
                {
                    return LIGHT_75_PERCENT;
                }
                else if (light > LIGHT_75_PERCENT && light <= LIGHT_100_PERCENT)
                {
                    return LIGHT_100_PERCENT;
                }
            }
            else
            {
                return LIGHT_AUTO;
            }
        }
        catch (Settings.SettingNotFoundException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return LIGHT_ERR;

    }


    private void setBrightStatus()
    {
        int light = 0;

        switch (getBrightStatus())
        {
            case LIGHT_NORMAL:
                light = LIGHT_50_PERCENT - 1;
                break;
            case LIGHT_50_PERCENT:
                light = LIGHT_75_PERCENT - 1;
                break;
            case LIGHT_75_PERCENT:
                light = LIGHT_100_PERCENT - 1;
                break;
            case LIGHT_100_PERCENT:
                light = LIGHT_100_PERCENT - 1;
                //   startAutoBrightness(getContentResolver());
                break;
            case LIGHT_AUTO:
                light = LIGHT_100_PERCENT - 1;
                //  stopAutoBrightness(getContentResolver());
                break;
            case LIGHT_ERR:
                light = LIGHT_100_PERCENT - 1;
                break;

        }

        setLight(light);
        setScreenLightValue(getContentResolver(), light);
    }
    private void setBrightStatus2()
    {
        int light = 0;

        switch (getBrightStatus())
        {
            case LIGHT_NORMAL:
                light = LIGHT_NORMAL - 1;
                // startAutoBrightness(getContentResolver());
//                light = LIGHT_50_PERCENT - 1;
                break;
            case LIGHT_50_PERCENT:
                light = LIGHT_NORMAL - 1;
//                light = LIGHT_75_PERCENT - 1;
                break;
            case LIGHT_75_PERCENT:
                light = LIGHT_50_PERCENT - 1;
//                light = LIGHT_100_PERCENT - 1;
                break;
            case LIGHT_100_PERCENT:
                light = LIGHT_75_PERCENT - 1;
//                startAutoBrightness(getContentResolver());
                break;
            case LIGHT_AUTO:
                light = LIGHT_NORMAL - 1;
//                light = LIGHT_NORMAL - 1;
                // stopAutoBrightness(getContentResolver());
                break;
            case LIGHT_ERR:
                light = LIGHT_NORMAL - 1;
//                light = LIGHT_NORMAL - 1;
                break;
        }

        setLight(light);
        setScreenLightValue(getContentResolver(), light);
    }



    /*因为PowerManager提供的函数setBacklightBrightness接口是隐藏的，
         * 所以在基于第三方开发调用该函数时，只能通过反射实现在运行时调用
         */
    private void setLight(int light)
    {
        try
        {
            //得到PowerManager类对应的Class对象
            Class<?> pmClass = Class.forName(mPowerManager.getClass().getName());
            //得到PowerManager类中的成员mService（mService为PowerManagerService类型）
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            //实例化mService
            Object iPM = field.get(mPowerManager);
            //得到PowerManagerService对应的Class对象
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            /*得到PowerManagerService的函数setBacklightBrightness对应的Method对象，
             * PowerManager的函数setBacklightBrightness实现在PowerManagerService中
             */
            Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);
            method.setAccessible(true);
            //调用实现PowerManagerService的setBacklightBrightness
            method.invoke(iPM, light);
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//        @Override
//        public void onClick(View v)
//        {
//            // TODO Auto-generated method stub
//            setBrightStatus();
//        }

    //启动自动调节亮度
    public void startAutoBrightness(ContentResolver cr)
    {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    //关闭自动调节亮度
    public void stopAutoBrightness(ContentResolver cr)
    {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    //设置改变亮度值
    public void setScreenLightValue(ContentResolver resolver, int value)
    {
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                value);
    }

    private class BrightObserver extends ContentObserver
    {
        ContentResolver mResolver;

        public BrightObserver(Handler handler)
        {
            super(handler);
            mResolver = getContentResolver();
        }

        @Override
        public void onChange(boolean selfChange)
        {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            refreshButton();
            Toast.makeText(FxService.this, "亮度设置有改变", Toast.LENGTH_SHORT).show();
        }

    }
//    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub

        if (camera != null) {
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
            camera.stopPreview();
        }
        if (obverse != null)
            obverse.stopObserver();
        super.onDestroy();
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    private void setRotationStatus(ContentResolver resolver, int status)
    {
        //得到uri
        Uri uri = android.provider.Settings.System.getUriFor("accelerometer_rotation");
        //沟通设置status的值改变屏幕旋转设置
        android.provider.Settings.System.putInt(resolver, "accelerometer_rotation", status);
        //通知改变
        resolver.notifyChange(uri, null);
    }

    private boolean getRotationStatus(Context context)
    {
        int status = 0;
        try
        {
            status = android.provider.Settings.System.getInt(getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        }
        catch (Settings.SettingNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return status == 1;
    }
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(android.os.Handler handler)
        {
            super(handler);
            mResolver = getContentResolver();
            // TODO Auto-generated constructor stub
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange)
        {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            //更新按钮状态
        }

        public void startObserver()
        {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        public void stopObserver()
        {
            mResolver.unregisterContentObserver(this);
        }
    }

}