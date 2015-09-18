package com.floatdragon.argus.floatDragon_ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.floatdragon.argus.MainActivity;

import junit.framework.Test;

import java.util.concurrent.locks.Lock;

/**
 * Created by zouyun on 15/9/8.
 */
public class OnClickMethod {

    public static void setting_onClick() {
        // 跳出系统开关
        if(StaticData.isShow == 0) {
            MyService.getMyService().isShow(true);
            StaticData.isShow = 1;
            return;
        }
        if(StaticData.isShow == 1) {
            MyService.getMyService().isShow(false);
            StaticData.isShow = 0;
        }
    }

    public static void item_onClick() {
        // 跳出快捷应用
        if(StaticData.isShow2 == 0) {
            MyService.getMyService().isShow2(true);
            StaticData.isShow2 = 1;
            return;
        }
        if(StaticData.isShow2 == 1) {
            MyService.getMyService().isShow2(false);
            StaticData.isShow2 = 0;
        }
    }

    public static void lock_onClick(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, LockReceiver.class);
        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void note_onClick() {

        if(StaticData.isShow3 == 0) {
            MyService.getMyService().isShow3(true);
            StaticData.isShow3 = 1;
            return;
        }
        if(StaticData.isShow3 == 1) {
            MyService.getMyService().isShow3(false);
            StaticData.isShow3 = 0;
        }

    }

    public static void move_onClick() {
        StaticData.move = true;
    }
}
