package com.example.user.argus.floatDragon_ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.user.argus.R;

/**
 * Created by zouyun on 15/8/13.
 */
public class MyService extends Service {

    private View[] layout;

    private FloatView floatView;

    private WindowManager windowManager;

    private WindowManager.LayoutParams windowManagerParas;


    @Override
    public void onCreate()
    {
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


        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManagerParas = ((FloatApplication)getApplication()).getWindowParams();
        windowManagerParas.type = WindowManager.LayoutParams.TYPE_PHONE;
        windowManagerParas.format = PixelFormat.RGBA_8888;
        windowManagerParas.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManagerParas.gravity = Gravity.LEFT | Gravity.TOP;

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        StaticData.screenWidth = metrics.widthPixels;
        StaticData.screenHeight = metrics.heightPixels;

        windowManagerParas.x = 0;
        windowManagerParas.y = StaticData.screenHeight / 2 - 360;
        windowManagerParas.width = 360;
        windowManagerParas.height = 720;
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        StaticData.layout[2] = inflater.inflate(R.layout.left_bar, null);
        windowManager.addView(StaticData.layout[2], windowManagerParas);
        windowManagerParas.x = StaticData.screenWidth - 360;
        StaticData.layout[3] = inflater.inflate(R.layout.right_bar, null);
        windowManager.addView(StaticData.layout[3], windowManagerParas);

        windowManagerParas.width = 720;
        windowManagerParas.height = 360;
        windowManagerParas.x = StaticData.screenWidth / 2 - 360;
        windowManagerParas.y = -StaticData.len;
        StaticData.layout[0] = inflater.inflate(R.layout.top_bar, null);
        windowManager.addView(StaticData.layout[0], windowManagerParas);
        windowManagerParas.y = StaticData.screenHeight - 180;
        StaticData.layout[1] = inflater.inflate(R.layout.bottom_bar, null);
        windowManager.addView(StaticData.layout[1], windowManagerParas);

        for (int i = 0; i < 4; i ++) {
            StaticData.layout[i].setVisibility(View.GONE);
        }
        StaticData.init();
        windowManagerParas.x = StaticData.pos[StaticData.position].first;
        windowManagerParas.y = StaticData.pos[StaticData.position].second;
        System.out.println(floatView.getMeasuredHeight());
        windowManagerParas.height = StaticData.circleSize;
        windowManagerParas.width = StaticData.circleSize;
        windowManager.addView(floatView, windowManagerParas);


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
