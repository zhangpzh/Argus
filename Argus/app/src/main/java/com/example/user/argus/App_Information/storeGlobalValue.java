package com.example.user.argus.App_Information;

import java.util.ArrayList;

/**
 * Created by user on 15-8-31.
 */

/*  存储全局数据  */

public class storeGlobalValue {
    public static ArrayList<appInfo> appsNotRegistered;                   /* appsNotRegistered -> 没有设置快捷访问的应用列表, 为什么
                                                                       要把它设为全局呢, 因为 SelectAppsActivity 中的 GridView 需要
                                                                       用它来显示表项, 而且让 MainActivity 通过 intent 把 leftAppInfos
                                                                       传给 SelectAppsActivity 比较麻烦. 故有此策
                                                                       */
    public static boolean checkConditionsOfMainActivity[];          //监控 MainActivity 中的 p_gridView 的打勾情况
    public static boolean checkConditionsOfSelectAppsActivity[];    //监控 SelectAppsActivity 中的 selectAppsActivityGridView 的打勾情况

    public storeGlobalValue() {
    }

    public void updateAppsNotRegistered(ArrayList<appInfo> list) {
        appsNotRegistered = new ArrayList<appInfo>(list);
    }

    public void updateCheckConditionsOfMainActivity(int size) {
        checkConditionsOfMainActivity = new boolean[size];
        for(int i = 0 ; i < size ; i ++)
            checkConditionsOfMainActivity[i] = false;
    }

    public void updateCheckConditionsOfSelectAppsActivity(int size) {
        checkConditionsOfSelectAppsActivity = new boolean[size];
        for(int i = 0 ; i < size ; i ++)
            checkConditionsOfSelectAppsActivity[i] = false;
    }
}