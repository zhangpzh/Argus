package com.example.user.argus.App_Information;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 15-8-30.
 */

//应用类 appInfo, 其对象保存应用部分重要信息, 包括: 包名、名称、图标、启动 intent 等...

public class appInfo implements  Comparable<appInfo> {

    private String appPkgName;
    private String appName;
    private Drawable appIcon;
    private Intent appIntent;
    private PackageManager appPackageManager;
    private Context appContext;

    public appInfo(Context context) {
        this.appContext = context;
        appPackageManager = context.getPackageManager();
        appName = null;
        appIcon = null;
        appIntent = null;
        appPkgName = null;
    }

    public appInfo(Context context, String appName , Drawable appIcon ,
                   Intent appIntent, String appPkgName) {
        this.appContext = context;
        this.appPackageManager = context.getPackageManager();
        this.appName = appName;
        this.appIcon = appIcon;
        this.appIntent = appIntent;
        this.appPkgName = appPkgName;
    }

    //根据包名获取应用程序的名称
    public String getAppName(String packagename) {
        appPkgName = packagename;
        if(appName != null)
        {
            return appName;
        }
        appPkgName = packagename;
        try {
            ApplicationInfo info = appPackageManager.getApplicationInfo(packagename,0);
            return appName = info.loadLabel(appPackageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    //根据包名获取应用程序的名称, 针对已经初始化好的 appInfo 对象
    public String getAppName() {
        return appName;
    }

    //根据包名获取应用程序的图标
    public Drawable getAppIcon(String packagename) {
        appPkgName = packagename;
        if(appIcon != null)
        {
            return appIcon;
        }
        try {
            ApplicationInfo info = appPackageManager.getApplicationInfo(packagename,0);
            return appIcon = info.loadIcon(appPackageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    //根据包名获取应用程序的图标, 针对已经初始化好的 appInfo 对象
    public Drawable getAppIcon() {
        return appIcon;
    }

    //根据包名获取应用程序的 intent
    public Intent getAppIntent(String packagename) {
        appPkgName = packagename;
        if(appIntent != null)
        {
            return appIntent;
        }

        //创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packagename);

        //通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveInfoList = appPackageManager.queryIntentActivities(resolveIntent,0);
        ResolveInfo resolveInfo = resolveInfoList.iterator().next();

        if(resolveInfo != null)
        {
            // newPackageName = packagename
            String newPackageName = resolveInfo.activityInfo.packageName;

            appPkgName = newPackageName;

            //找到packagename对应的启动activity的名称
            String className = resolveInfo.activityInfo.name;

            //LAUNCHER Intent
            appIntent = new Intent(Intent.ACTION_MAIN);
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(newPackageName,className);

            appIntent.setComponent(cn);
            return appIntent;
        }
        return appIntent;
    }

    //根据包名获取应用程序的 intent, 针对已经初始化好的 appInfo 对象
    public Intent getAppIntent() {
        return appIntent;
    }

    //获取应用程序的包名, 针对已经初始化好的 appInfo 对象
    public String getAppPkgName() {
        return appPkgName;
    }

    //根据包名初始化 应用程序的 包名、名称、图标、intent
    public void getAll(String pkgName) {
        appPkgName = new String(pkgName);
        getAppName(appPkgName);
        getAppIcon(appPkgName);
        getAppIntent(appPkgName);
    }

    //比较函数，用于 appInfo 类型容器的排序，以appName为字典序进行排序(支持中文)
    @Override
    public int compareTo(appInfo o) {
        RuleBasedCollator collator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);

        CollationKey c1 = collator.getCollationKey(this.appName);
        CollationKey c2 = collator.getCollationKey(o.appName);

        return collator.compare(((CollationKey)c1).getSourceString(),((CollationKey)c2).getSourceString());
    }
}