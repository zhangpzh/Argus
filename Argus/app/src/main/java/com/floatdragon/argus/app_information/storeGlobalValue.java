package com.floatdragon.argus.app_information;

import android.widget.Switch;

import java.util.ArrayList;

/**
 * Created by user on 15-9-14.
 */

/*  存储全局数据  */

public class storeGlobalValue {

    //MainActivity 中圆圈的模式 circleMode: 0->addMode, 1->removeMode.
    public static final int addMode = 0;
    public static final int removeMode = 1;
    public static int circleMode;

    //拟删除的 app 对应数组 toBeRemoved[], 和拟删除的 app 的数目 int removeAppsNum
    public static boolean toBeRemoved[];
    public static int removeAppsNum;

    public static ArrayList<appInfo> appsNotRegistered;             //没有注册快捷访问的应用列表
    public static ArrayList<appInfo> appsRegistered;                //已注册快捷访问的应用列表 (不含 "NONE"--空包名, 使用ownAppInfos更新)
    public static ArrayList<appInfo> allAppsInfo;                   //手机中所有应用的列表

    public storeGlobalValue() {
        circleMode = addMode;   //初始化为 addMode -- 增加模式
        toBeRemoved = new boolean[6];
        for(int i = 0 ; i < 6 ; i ++)
            toBeRemoved[i] = false;
        removeAppsNum = 0;
    }

    public void updateAppsNotRegistered(ArrayList<appInfo> list) {
        appsNotRegistered = new ArrayList<appInfo>(list);
    }

    public void updateAppsRegistered(ArrayList<appInfo> list) {
        appsRegistered = new ArrayList<appInfo>(list);
    }

    public void updateAllApssInfo(ArrayList<appInfo> list) {
        allAppsInfo = new ArrayList<appInfo>(list);
    }

    //boolean toBeRemoved[] 全部设为 false, int removeAppsNum 设为 0
    public void cleanAboutRemove() {
        for(int i = 0 ; i < 6 ; i ++)
        {
            toBeRemoved[i] = false;
        }
        removeAppsNum = 0;
    }
}