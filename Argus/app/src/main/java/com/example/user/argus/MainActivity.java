package com.example.user.argus;

import com.example.user.argus.Adapter_and_GridViewItem.MyAdapter;
import com.example.user.argus.App_Information.*;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


//设置界面 Activity
public class MainActivity extends Activity {

    private GridView p_gridView;
    ImageView toRegisterApps;
    ImageView toSortRegisteredApps;
    ImageView toRemoveRegisteredApps;

    final String FAST_ACCESS_REGISTERED = "FAST_ACCESS_REGISTERED";      //存储快捷访问应用包名的设置文件

    ArrayList<appInfo> registeredAppInfos;                                      //已设置快捷访问的应用列表
    ArrayList<appInfo> allAppInfos ;                                            //系统中所有应用列表
    ArrayList<appInfo> leftAppInfos;                                            //没有加入快捷访问的应用列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_show_grid);

        //找到布局中的组件
        p_gridView = (GridView) findViewById(R.id.MainActivityGridView);
        toRegisterApps = (ImageView) findViewById(R.id.toRegisterApps);
        toSortRegisteredApps = (ImageView) findViewById(R.id.toSortRegisteredApps);
        toRemoveRegisteredApps = (ImageView) findViewById(R.id.toRemoveResgisteredApps);


        /* 读取已设置访问的包名列表 -> registered_pkgName */
        Set<String> registered_pkgName = readSettings();

        /* 根据包名列表, 初始化 registeredAppInfos -- 存储已设置快捷访问的应用的列表  */
        showRegisteredAppInGridView(registered_pkgName);


        //初始化全局静态 boolean 数组 checkConditionsOfMainActivity[], 以监控 p_gridView 表项 中的打勾情况
        storeGlobalValue usedForUpdateCheckConditionsOfMainActivity = new storeGlobalValue();
        usedForUpdateCheckConditionsOfMainActivity.updateCheckConditionsOfMainActivity(registered_pkgName.size());


        /* 初始化 allAppInfos -- 存储系统中所有的应用(系统应用和第三方应用)的列表 */
        allAppInfos = null;
        queryAllAppsInfo();                                  //allAppsInfo 列表中含有系统中所有应用的信息

        /* 初始化 leftAppInfos -- 存储没有设置快捷访问的应用的列表，是 registeredAppInfo  */
        leftAppInfos = new ArrayList<appInfo>(allAppInfos);    //leftappInfos 列表中含有所有没有加入快捷访问的应用的信息


        //leftAppInfos.removeAll(registeredAppInfos);   removeAll 可能会出问题

        //removeAll 可能会出问题，因为 appInfo 类型字段过多，难保某一个字段没有赋值到相等，保守地只比较字符串比较可靠.
        boolean tag;
        while(leftAppInfos.size() > 0)
        {
            tag = true;
            for(Iterator it = leftAppInfos.iterator() ; it.hasNext();)
            {
                appInfo tmpInfo = (appInfo)it.next();
                String tmpStr = new String(tmpInfo.getAppPkgName());
                if(registered_pkgName.contains(tmpStr))
                {
                    leftAppInfos.remove(tmpInfo);
                    tag = false;
                    break;
                }
            }
            if(tag == false) //继续while循环－－可能还没筛完
                continue;
            else             //筛选完毕
                break;
        }

        /* 使用 leftAppInfos -- 没有加入快捷访问的应用列表, 来更新全局静态列表 storeGlobalValue.appsNotRegistered */
        storeGlobalValue usedForUpdateAppsNotRegistered = new storeGlobalValue();
        usedForUpdateAppsNotRegistered.updateAppsNotRegistered(leftAppInfos);

        //绑定 "注册监听器":  跳转到 SelectAppsActivity 为没有注册快捷访问的应用注册快捷访问
        toRegisterApps.setOnClickListener(new toRegisterAppsListener());

        //绑定 "排序监听器":  对 MainActivity 中的 GridView -- p_gridView 的项进行排序
        toSortRegisteredApps.setOnClickListener(new toSortRegisteredAppsListener());

        //绑定 "移除监听器":  移除gridView中选中的项目 -- 即删除应用的快捷访问
        toRemoveRegisteredApps.setOnClickListener(new toRemoveRegisteredAppsListener());
    }


    //监听器: 跳转到Activity "SelectAppsActivity" 为没有注册快捷访问的应用注册快捷访问
    private class toRegisterAppsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //从MainActivity 跳转到 SelectAppsActivity
            Intent intentForSelectApps = new Intent();
            intentForSelectApps.setClass(MainActivity.this,SelectAppsActivity.class);

            //跳转到 SelectAppsActivity
            startActivity(intentForSelectApps);
        }
    }

    //监听器: 1. 对gridView中的项进行排序，清空配置文件，将排好序的程序的包名写入配置文件(重新启动Activity时顺序也是排好的)
    //       2. 重绘gridView.
    //       3. storeGlobalValue.checkConditionsOfMainActivity[]的值全部置为 false
    private class toSortRegisteredAppsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            List<appInfo> tmpList = new ArrayList<appInfo>(registeredAppInfos);
            Collections.sort(tmpList);

            registeredAppInfos.clear();
            registeredAppInfos = new ArrayList<appInfo>(tmpList);

            //1. 将 tmpList中app的包名 写入 preferences文件 "FAST_ACCESS_REGISTERED" 中(清空原先内容)
            SharedPreferences preferences;
            SharedPreferences.Editor editor;
            preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
            editor = preferences.edit();

            editor.clear();

            editor.putInt("count",tmpList.size());
            for(int i = 1 ; i <= tmpList.size() ; i ++)
            {
                String tmpPkgName = new String(tmpList.get(i-1).getAppPkgName());
                editor.putString("pkgName"+i,tmpPkgName);
            }
            editor.commit();


            //2. 重新绘制 gridView -- p_gridView
            wrapAppInfosIntoAdapter willGetAdapter = new wrapAppInfosIntoAdapter(MainActivity.this,"MainActivity");
            MyAdapter newMyAdapter = willGetAdapter.getAdapter(tmpList);
            p_gridView.setAdapter(newMyAdapter);

            //3. checkConditionsOfMainActivity[]的值全部置为 false
            storeGlobalValue usedForUpdateCheckConditionsOfMainActivity = new storeGlobalValue();
            usedForUpdateCheckConditionsOfMainActivity.updateCheckConditionsOfMainActivity(registeredAppInfos.size());

        }
    }

    //监听器: 移除gridView中选中的项目
    // 1. 将没有打勾的包名写入设置文件中 (依赖checkConditionsOfMainActivity)
    // 2. 将checkConditionsOfMainActivity 数组的大小设为 剩余注册包名 "pkgNameStillRegistered" 的大小，并全部初始化为 false
    // 3. 重绘 p_gridView 显示 剩余的 注册应用 (新创建appInfo对象，传入包名就可以得到资源－－app名称和图标)
    // 4. 更新两个列表: registeredAppInfos、leftAppInfos 和 全局静态列表 appsNotRegistered
    // 5. 更新 Set<String> registered_pkgName

    private class toRemoveRegisteredAppsListener implements View.OnClickListener {

        ArrayList<String> registered_pkgName;
        public toRemoveRegisteredAppsListener() {
            if(registered_pkgName != null)
                registered_pkgName.clear();
            else
                registered_pkgName = new ArrayList<String>();
            for(Iterator it = registeredAppInfos.iterator() ; it.hasNext();)
            {
                String tmpStr = new String(((appInfo)it.next()).getAppPkgName());
                registered_pkgName.add(tmpStr);
            }
        }

        @Override
        public void onClick(View v) {

            //1. 获得剩余的包名列表 并 将其写入preferences 文件中(清空原有文件)
            ArrayList<String> pkgNameStillRegistered = new ArrayList<String>();

            int i;
            System.out.println("registered_pkgName 列表中已注册包名的顺序: ");
            for(i = 0 ; i < registered_pkgName.size() ; i ++)
            {
                String tmpPkgName = new String(registered_pkgName.get(i));
                System.out.println(tmpPkgName);
                if(storeGlobalValue.checkConditionsOfMainActivity[i] == false)
                {
                    pkgNameStillRegistered.add(tmpPkgName);

                }
                if(storeGlobalValue.checkConditionsOfMainActivity[i] == true)
                {
                    System.out.println("将要删除的包名: " + tmpPkgName);
                }
            }


            //没有打勾, 什么也不做
            if(pkgNameStillRegistered.size() == registered_pkgName.size())
                return;


            //打 log
            //System.out.println("仍然注册的app的个数(pkgNameStillRegistered.size())为: "+pkgNameStillRegistered.size());


            SharedPreferences preferences;
            SharedPreferences.Editor editor;
            preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
            editor = preferences.edit();

            //打log
            //System.out.println("删除之前 app 的个数为: "+preferences.getInt("count",-1));


            //打 log
            //System.out.println("删除的app的个数为: "+(registeredAppInfos.size()-pkgNameStillRegistered.size()));


            editor.clear();
            editor.putInt("count",pkgNameStillRegistered.size());

            //打 log
            //System.out.println("删除之后app的个数为: "+(pkgNameStillRegistered.size()));


            for(i = 1 ; i <= pkgNameStillRegistered.size() ; i ++)
            {
                editor.putString("pkgName"+i,new String(pkgNameStillRegistered.get(i-1)));
            }
            editor.commit();

            // 2. 将checkConditionsOfMainActivity 数组的大小设为 剩余注册包名 "pkgNameStillRegistered" 的大小，并全部初始化为 false
            storeGlobalValue usedForUpdateCheckConditionsOfMainActivity = new storeGlobalValue();
            usedForUpdateCheckConditionsOfMainActivity.updateCheckConditionsOfMainActivity(pkgNameStillRegistered.size());

            // 3. 重新绘制 p_gridView 显示 剩余的 仍然设置着快捷访问的 app (新创建appInfo对象，传入包名就可以得到资源－－app名称和图标)
            Set<appInfo> pkgStillRegistered = new HashSet<appInfo>();
            for(i = 0 ; i < pkgNameStillRegistered.size() ; i ++)
            {
                String tmpPkgName = new String (pkgNameStillRegistered.get(i));
                appInfo tmpAppInfo = new appInfo(MainActivity.this);
                tmpAppInfo.getAll(tmpPkgName);
                pkgStillRegistered.add(tmpAppInfo);
            }

            wrapAppInfosIntoAdapter willGetAdapter = new wrapAppInfosIntoAdapter(MainActivity.this,"MainActivity");
            MyAdapter newMyAdapter = willGetAdapter.getAdapter(pkgStillRegistered);
            p_gridView.setAdapter(newMyAdapter);


            // 4. 更新两个列表: registeredAppInfos、leftAppInfos 和 全局静态列表 appsNotRegistered
            Set<appInfo> noRegisteredAnyMore = new HashSet<appInfo>(registeredAppInfos);

            //noRegisteredAnyMore.removeAll(pkgStillRegistered);
            //直接remove 不太保险, 因为 appInfo字段太多，先根据包名，然后再remove比较稳妥

            boolean tag;
            while(noRegisteredAnyMore.size() > 0)
            {
                tag = true;
                for(Iterator it = noRegisteredAnyMore.iterator() ; it.hasNext();)
                {
                    appInfo tmpInfo = (appInfo)it.next();
                    String tmpStr = new String(tmpInfo.getAppPkgName());
                    if(pkgNameStillRegistered.contains(tmpStr))
                    {
                        noRegisteredAnyMore.remove(tmpInfo);
                        tag = false;
                        break;
                    }
                }
                if(tag == false) //继续while循环－－可能还没筛完
                    continue;
                else             //筛选完毕
                    break;
            }

            //更新 registeredAppInfos
            registeredAppInfos.clear();
            registeredAppInfos.addAll(pkgStillRegistered);

            //更新 leftAppInfos 和 class storeAppsNotRegistered 中的全局静态列表 appsNotRegistered (通过创建一个对象实现)
            leftAppInfos.addAll(noRegisteredAnyMore);
            storeGlobalValue usedForUpdate = new storeGlobalValue();
            usedForUpdate.updateAppsNotRegistered(leftAppInfos);

            // 5. 更新 ArrayList<String> registered_pkgName

            // registered_pkgName = new HashSet<String>(pkgNameStillRegistered);

            if(registered_pkgName != null)
                registered_pkgName.clear();
            else
                registered_pkgName = new ArrayList<String>();
            for(Iterator it = registeredAppInfos.iterator() ; it.hasNext();)
            {
                String tmpStr = new String(((appInfo)it.next()).getAppPkgName());
                registered_pkgName.add(tmpStr);
            }

            //打 log
            //System.out.println("现在仍然注册的应用数目为: "+registered_pkgName.size());
        }
    }

    //将registered_pkgName 中的包名对应的 程序图标、程序名称显示在设置界面的 gridView 中，同时也保存了 intent
    //初始化 已设置快捷访问的应用列表 -- registeredAppInfos
    void showRegisteredAppInGridView(Set<String> registered_pkgName) {

        //初始化 registeredAppInfos
        if(registeredAppInfos != null)
            registeredAppInfos.clear();
        else
            registeredAppInfos = new ArrayList<appInfo>();

        for(Iterator it = registered_pkgName.iterator() ; it.hasNext(); )
        {
            String pkgName = it.next().toString();

            //新建一个appInfo类型变量，得到所有信息，加入registeredAppInfos
            appInfo appInformation = new appInfo(MainActivity.this);
            appInformation.getAll(pkgName);
            registeredAppInfos.add(appInformation);
        }

        System.out.println("registeredAppInfos 列表中已注册包名显示的顺序: ");
        for(Iterator it = registeredAppInfos.iterator(); it.hasNext();)
        {
            String pkgName = ((appInfo)it.next()).getAppName();
            System.out.println(pkgName);

        }

        wrapAppInfosIntoAdapter willGetAdapter = new wrapAppInfosIntoAdapter(MainActivity.this,"MainActivity");
        MyAdapter newMyAdapter = willGetAdapter.getAdapter(registeredAppInfos);
        p_gridView.setAdapter(newMyAdapter);
    }

    //初始化 系统中的所有应用列表 -- allAppInfos
    private void queryAllAppsInfo() {
        PackageManager pm = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        //通过查询，获得所有ResolveInfo对象
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,0);

        //根据应用的名称排序
        //该排序很重要，否则只能显示系统应用，而不能显示第三方应用
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        allAppInfos = new ArrayList<appInfo>();

        for(ResolveInfo reInfo : resolveInfos)
        {
            String appName = (String) reInfo.loadLabel(pm);
            Drawable appIcon = reInfo.loadIcon(pm);
            String activityName = reInfo.activityInfo.name;
            String pkgName = reInfo.activityInfo.packageName;

            Intent appIntent = new Intent();
            appIntent.setComponent(new ComponentName(pkgName,activityName));

            appInfo newAppInfo = new appInfo(MainActivity.this,appName,appIcon,appIntent,pkgName);

            allAppInfos.add(newAppInfo);
        }
    }

    //读取设置文件中保存的 已设置快捷访问的应用 的 包名列表
    private Set<String> readSettings() {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        Set<String> records = new HashSet<String>();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}