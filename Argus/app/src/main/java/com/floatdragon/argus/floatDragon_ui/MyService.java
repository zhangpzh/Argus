package com.floatdragon.argus.floatDragon_ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.floatdragon.argus.R;

/**
 * Created by zouyun on 15/8/13.
 */
public class MyService extends Service {

    private View[] layout;

    private FloatView floatView;

    private WindowManager windowManager;

    private WindowManager.LayoutParams windowManagerParas;

    private static MyService myService=null;
    public MyService(){
        myService=this;
    }
    public static MyService  getMyService() {
        return myService;
    }

    @Override
    public void onCreate()
    {
        Log.i("Service", "go");
        StaticData.layout = new View[4];
        super.onCreate();
        creatView();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void creatView() {
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        StaticData.screenWidth = metrics.widthPixels;
        StaticData.screenHeight = metrics.heightPixels;
        StaticData.barRadius = StaticData.screenHeight / 4;

        windowManagerParas = ((FloatApplication)getApplication()).getWindowParams();
        windowManagerParas.type = WindowManager.LayoutParams.TYPE_PHONE;
        windowManagerParas.format = PixelFormat.RGBA_8888;
        windowManagerParas.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManagerParas.gravity = Gravity.LEFT | Gravity.TOP;
        windowManagerParas.x = 0;
        windowManagerParas.y = StaticData.screenHeight / 2 - StaticData.barRadius;
        windowManagerParas.width = StaticData.barRadius;
        windowManagerParas.height = StaticData.barRadius * 2;
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        StaticData.layout[2] = inflater.inflate(R.layout.left_bar, null);
        windowManager.addView(StaticData.layout[2], windowManagerParas);
        windowManagerParas.width = StaticData.barRadius * 2;
        windowManagerParas.height = StaticData.barRadius * 4;
        windowManagerParas.x = StaticData.screenWidth - StaticData.barRadius;
        StaticData.layout[3] = inflater.inflate(R.layout.right_bar, null);
        windowManager.addView(StaticData.layout[3], windowManagerParas);

        windowManagerParas.width = StaticData.barRadius * 2;
        windowManagerParas.height = StaticData.barRadius;
        windowManagerParas.x = StaticData.screenWidth / 2 - StaticData.barRadius;
        windowManagerParas.y = -StaticData.len;
        StaticData.layout[0] = inflater.inflate(R.layout.top_bar, null);
        windowManager.addView(StaticData.layout[0], windowManagerParas);
        windowManagerParas.y = StaticData.screenHeight - StaticData.barRadius / 2;
        StaticData.layout[1] = inflater.inflate(R.layout.bottom_bar, null);
        windowManager.addView(StaticData.layout[1], windowManagerParas);

        for (int i = 0; i < 4; i ++) {
            StaticData.layout[i].setVisibility(View.GONE);
        }
        StaticData.init();
        windowManagerParas.x = StaticData.pos.first;
        windowManagerParas.y = StaticData.pos.second;
        windowManagerParas.height = StaticData.circleSize;
        windowManagerParas.width = StaticData.circleSize;
        floatView = new FloatView(getApplicationContext());
        floatView.setImageAlpha(100);
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  mPop.setAnchorView(floatView);
                //  mPop.show();
            }
        });
        floatView.setImageResource(R.drawable.btn_normal);
        windowManager.addView(floatView, windowManagerParas);


    }

    public void isShow(boolean show){
        if(show == true) {
            Intent intent = new Intent(MyService.this, FxService.class);
            startService(intent);
        }
        else{
            Intent intent = new Intent(MyService.this, FxService.class);
            stopService(intent);
        }
    }
    public void isShow2(boolean show){
        if(show == true) {
            Intent intent = new Intent(MyService.this, ItService.class);
            startService(intent);
        }
        else{
            Intent intent = new Intent(MyService.this, ItService.class);
            stopService(intent);
        }
    }
    public void isShow3(boolean show){
        if(show == true) {
            Intent intent = new Intent(MyService.this, NotepadService.class);
            startService(intent);
        }
        else{
            Intent intent = new Intent(MyService.this, NotepadService.class);
            stopService(intent);
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (floatView != null)
            windowManager.removeView(floatView);
        for (int i = 0; i < 4; i ++) {
            if (StaticData.layout[i] != null)
                windowManager.removeView(StaticData.layout[i]);
        }
    }


}
