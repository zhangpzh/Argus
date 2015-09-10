package com.example.user.argus.floatDragon_ui;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created by zouyun on 15/8/10.
 */
public class FloatApplication extends Application {
    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();


    public WindowManager.LayoutParams getWindowParams() {
        return windowParams;
    }
}
