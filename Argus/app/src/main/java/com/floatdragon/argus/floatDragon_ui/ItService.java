package com.floatdragon.argus.floatDragon_ui;

/**
 * Created by Administrator on 2015/9/9.
 */
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.floatdragon.argus.R;
import com.floatdragon.argus.app_information.*;

import java.util.ArrayList;
import java.util.Iterator;

public class ItService extends Service
{
    int ClickT = 0;
    int appInfoSize = 0;
    final String FAST_ACCESS_REGISTERED = "FAST_ACCESS_REGISTERED";      //存储快捷访问应用包名的设置文件
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
    ArrayList<appInfo> registeredAppInfos;

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

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (GridLayout) inflater.inflate(R.layout.float_layout, null);
        mFloatLayout.getBackground().setAlpha(100);


       // appInfos =  MainActivity.getMainActivity().getRegisteredAppInfos();

        showRegisteredAppInGridView( readSettings() );
        appInfos = registeredAppInfos;




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
                    Intent intent = appInfos.get(5).getAppIntent();
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
                    Intent intent = appInfos.get(2).getAppIntent();
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
                    Intent intent = appInfos.get(3).getAppIntent();
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
                    startActivity(intent);
//                }
            }
        });

    }


    private ArrayList<String> readSettings() {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        ArrayList<String> records = new ArrayList<String>();

        preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
        editor = preferences.edit();

        int cnt = preferences.getInt("count", -1);

        //preferences 文件不存在 创建一个空的 preferences 文件
        if(cnt == -1)
        {
            editor.putInt("count",0);
            editor.commit();
        }
        //preferences 文件存在 读取其中的 包名
        else
        {
            for(int i = 1 ; i <= cnt ; i ++)
            {
                records.add(preferences.getString("pkgName"+i,"none"));
            }

            /*

            打 log
            System.out.println("records 中显示的包名顺序: ");
            for(Iterator it = records.iterator() ; it.hasNext();)
            {
                String tmpStr = new String((String)it.next());
                System.out.println(tmpStr);
            }

            System.out.println();
            System.out.println("preferences 文件中显示的包名顺序: ");
            System.out.println("已注册的包名的个数为: "+cnt);
            for(int i = 1 ; i <= cnt ; i ++)
            {
                System.out.println(preferences.getString("pkgName"+i,"none"));
            }
            */
        }
        return records;
    }
    void showRegisteredAppInGridView(ArrayList<String> registered_pkgName) {

        //初始化 registeredAppInfos
        if(registeredAppInfos != null)
            registeredAppInfos.clear();
        else
            registeredAppInfos = new ArrayList<appInfo>();

        for(int i = 0 ; i < registered_pkgName.size() ; i ++)
        {
            String tmpPkgName = registered_pkgName.get(i);

            //新建一个appInfo类型变量，得到所有信息，加入registeredAppInfos
            appInfo appInformation = new appInfo(ItService.this);
            appInformation.getAll(tmpPkgName);
            registeredAppInfos.add(appInformation);
        }

        //打 log
        //System.out.println("registeredAppInfos 列表中已注册包名显示的顺序: ");
//        for(Iterator it = registeredAppInfos.iterator(); it.hasNext();)
//        {
//            String pkgName = ((appInfo)it.next()).getAppName();
//            System.out.println(pkgName);
//        }

//        wrapAppInfosIntoAdapter willGetAdapter = new wrapAppInfosIntoAdapter(ItService.this,"ItService");
//        MyAdapter newMyAdapter = willGetAdapter.getAdapter(registeredAppInfos);
//        p_gridView.setAdapter(newMyAdapter);
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