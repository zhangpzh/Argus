package com.floatdragon.argus;

import com.floatdragon.argus.app_information.*;
import com.floatdragon.argus.app_shown.RoundImageView;
import com.floatdragon.argus.floatDragon_ui.*;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//MainActivity
public class MainActivity extends Activity {

    private GridView pGridView;                                                 //选择app的网格
    private RoundImageView circles[];                                           //存储圆圈的数组
    private RoundImageView MiddleDeleteButton;                                  //中间删除按钮
    private int numOfCircleToSelectApp;                                         //触发选择app事件的空圆圈的编号 (从0开始数)

    final String FAST_ACCESS_REGISTERED = "FAST_ACCESS_REGISTERED";             //存储快捷访问应用包名的设置文件

    ArrayList<String> registered_pkgName;                                       //已设置快捷访问的应用包名列表 (包含 "NONE"--空包名)

    ArrayList<appInfo> registeredAppInfos;                                      //已设置快捷访问的应用列表 (包含 "NONE"--空包名)
    ArrayList<appInfo> allAppInfos ;                                            //系统中所有应用列表
    ArrayList<appInfo> leftAppInfos;                                            //没有加入快捷访问的应用列表


    storeGlobalValue usedForUpdateGlobals;                                      //用于更新三个全局变量的对象
    wrapAppInfosIntoAdapter usedForGetAdapter;                                  //用于获取 ArrayList<appInfo>类型的容器 对应的adapter的对象

    long exitTime = System.currentTimeMillis()-2000;                            //与退出应用(仍在后台), 返回桌面的点击事件相关


    /* 在小圆点中启动应用相关 */
    private static MainActivity mainActivity = null;

    public MainActivity() {
        mainActivity=this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public ArrayList<appInfo> getRegisteredAppInfos() {
        return registeredAppInfos;
    }

    //桌面小圆点的开关
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 初始化为-1, 表示没有空圆圈触发选择app事件 */
        numOfCircleToSelectApp = -1;

        /* 初始化 usedForUpdateGlobals、usedForGetAdapter */
        usedForUpdateGlobals = new storeGlobalValue();
        usedForGetAdapter = new wrapAppInfosIntoAdapter(MainActivity.this,"MainActivity");

        /* 找到布局中的组件 */
        pGridView = (GridView) findViewById(R.id.MainActivityGridView);

        circles = new RoundImageView[6];
        circles[0] = (RoundImageView) findViewById(R.id.circle1);
        circles[1] = (RoundImageView) findViewById(R.id.circle2);
        circles[2] = (RoundImageView) findViewById(R.id.circle3);
        circles[3] = (RoundImageView) findViewById(R.id.circle4);
        circles[4] = (RoundImageView) findViewById(R.id.circle5);
        circles[5] = (RoundImageView) findViewById(R.id.circle6);

        /* 找到中间删除按钮, 并注册监听器 */
        MiddleDeleteButton = (RoundImageView) findViewById(R.id.throw_rubbish);
        MiddleDeleteButton.setOnClickListener(new MiddleDeleteButtonOnClickListener());

        /* 初始化圆圈编号、注册监听器*/
        for(int i = 0 ; i < 6 ; i ++)
        {
            circles[i].number = i;
            circles[i].setOnClickListener(new RoundImageViewOnShortClickListener());
            circles[i].setOnLongClickListener(new RoundImageViewOnLongClickListener());
        }

        /* 为 pGridView 中的项目注册监听器 */
        pGridView.setOnItemClickListener(new GridViewOnItemClickListener());

        /* 为桌面小圆点开关绑定监听器 */
        aSwitch = (Switch)findViewById(R.id.serviceSwitchButton);
        if(scaleTheService(MyService.class.getName()))
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    startService(intent);
                    Log.i("service", "start");
                } else {
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    stopService(intent);
                    Log.i("service", "stop");
                }
            }
        });

        /* 读取已设置访问的包名列表 -> registered_pkgName */
        registered_pkgName = readSettings();

        /*
           1. 根据包名列表, 初始化 registeredAppInfos
           2. 将应用的图标显示在 6 个圆圈当中
        */
        showRegisteredAppsInCircles();

        /* 初始化 allAppInfos */
        queryAllAppInfos();

        /* 初始化 leftAppInfos */
        initLeftAppInfos(registered_pkgName);

        /* 更新全局静态变量 appsNotRegistered、appsRegistered、allAppsInfo */
        usedForUpdateGlobals.updateAppsNotRegistered(leftAppInfos);
        usedForUpdateGlobals.updateAppsRegistered(registeredAppInfos);
        usedForUpdateGlobals.updateAllApssInfo(allAppInfos);

        /* 用全局静态列表 storeGlobalValue 中的 appsNotRegistered 中的信息填充 pGridView */
        pGridView.setAdapter(usedForGetAdapter.getAdapter(storeGlobalValue.appsNotRegistered));

    }

    /* 初始化 leftAppInfos */
    private void initLeftAppInfos(ArrayList<String> registered_pkgName) {
        leftAppInfos = new ArrayList<appInfo>(allAppInfos);
        //使用 自定义复合类型容器的 remove 和 removeAll 方法的时候, 记得要重写 equals 方法
        //如下面这条语句, 事先就应在 appInfo 的类定义中定义重写 equals 方法
        leftAppInfos.removeAll(registeredAppInfos);
    }

    /* 读取设置文件中保存的 已设置快捷访问的应用 的 包名列表 */
    private ArrayList<String> readSettings() {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
        editor = preferences.edit();

        int cnt = preferences.getInt("count", -1);
        ArrayList<String> records = new ArrayList<String>();

        //preferences 文件不存在 创建一个空的 preferences 文件
        if(cnt == -1)
        {
            editor.putInt("count",0);
            editor.commit();
            for(int i = 0 ; i < 6 ; i ++)
                records.add("NONE");
            return records;
        }
        //preferences 文件存在 读取其中的 包名
        for(int i = 1 ; i <= cnt ; i ++)
            records.add(preferences.getString("pkgName"+i,"NONE"));
        return records;
    }

    /*
        1.根据包名列表, 初始化 registeredAppInfos
        2.将应用的图标显示在 6 个圆圈当中
    */
    void showRegisteredAppsInCircles() {

        //初始化 registeredAppInfos
        if(registeredAppInfos != null)
            registeredAppInfos.clear();
        else
            registeredAppInfos = new ArrayList<appInfo>();

        for(int i = 0 ; i < registered_pkgName.size() ; i ++)
        {
            String tmpPkgName = registered_pkgName.get(i);

            appInfo appInformation = new appInfo(MainActivity.this);

            //若包名为 "NONE" 则表明对应圆圈为空, 不设图标
            if(tmpPkgName.equals("NONE"))
                appInformation.setEmpty();
            //根据包名得到所有信息
            else
                appInformation.getAll(tmpPkgName);
            registeredAppInfos.add(appInformation);
        }

        //将 registeredAppInfos 中的应用的图标显示在 6 个圆圈当中
        //如果 registeredAppInfos 为空, 说明文件无包名记录,  写入6个空包名, 并显示为 "添加图标"
        if(registeredAppInfos.size() == 0)
        {
            SharedPreferences preferences;
            SharedPreferences.Editor editor;
            preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
            editor = preferences.edit();

            for(int i = 1 ; i <= 6 ; i ++)
            {
                editor.putString("pkgName"+i,"NONE");
            }
            editor.putInt("count",6);
            editor.commit();

            //显示为 "添加图标"
            for(int i = 0 ; i < 6 ; i ++)
            {
                circles[i].setDrawable(getResources().getDrawable(R.drawable.add_file));
            }
        }
        //否则 将对应图标显示在圆圈中, 有则显示图标, 无则显示为 "添加图标"
        else
        {
            for(int i = 0 ; i < registeredAppInfos.size() ; i ++)
            {
                appInfo tmpAppInfo = registeredAppInfos.get(i);
                //无图标
                if(tmpAppInfo.IsEmpty())
                {
                    circles[i].setDrawable(getResources().getDrawable(R.drawable.add_file));
                }
                //有图标
                else
                {
                    circles[i].setDrawable(tmpAppInfo.getAppIcon());
                }
            }
        }
    }

    /* 初始化 系统中的所有应用列表 -- allAppInfos */
    private void queryAllAppInfos() {
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

    /*
        圆圈的短按监听器事件:
            若 circleMode 为 removeMode:
                    若圆圈中为空,无效
                    若圆圈中不空:
                           若圆圈已变绿:
                                    变正常圆圈, 更新 boolean toBeRemoved[], int removeAppsNum
                                    若 removeAppsNum 减少为0:
                                                circleMode 切换至 addMode, 中间删除按钮消失
                           若圆圈未变绿:
                                    圆圈变绿, 更新 boolean toBeRemoved[], int removeAppsNum
            若 circleMode 为 addMode:
                    若圆圈中不空,无效
                    若圆圈中为空:
                            重刷 pGridView 并设置可见, 把当前圆圈的编号记录到 -> numOfCircleToSelectApp 中
     */
    public class RoundImageViewOnShortClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RoundImageView tmpView = (RoundImageView) v;
            if(storeGlobalValue.circleMode == storeGlobalValue.removeMode)
            {
                if(registered_pkgName.get(tmpView.number).equals("NONE"))
                {
                    return;
                }
                else
                {
                    if(tmpView.isGreen)
                    {
                        tmpView.turnNormal();
                        storeGlobalValue.toBeRemoved[tmpView.number] = false;
                        storeGlobalValue.removeAppsNum --;
                        if(storeGlobalValue.removeAppsNum == 0)
                        {
                            storeGlobalValue.circleMode = storeGlobalValue.addMode;
                            MiddleDeleteButton.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        tmpView.turnGreen();
                        storeGlobalValue.toBeRemoved[tmpView.number] = true;
                        storeGlobalValue.removeAppsNum ++;
                    }
                }
            }
            else
            {
                if( registered_pkgName.get( tmpView.number).equals("NONE") == false)   //error
                {
                    return;
                }
                /* 用全局静态列表 storeGlobalValue 中的 appsNotRegistered 中的信息填充 pGridView 并设置可见 */
                pGridView.setAdapter(usedForGetAdapter.getAdapter(storeGlobalValue.appsNotRegistered));
                pGridView.setVisibility(View.VISIBLE);
                numOfCircleToSelectApp = tmpView.number;
            }
        }
    }

    /*
        圆圈的长按监听器事件:
            若 circleMode 为 removeMode, 无效
            若 circleMode 为 addMode:
                    若圆圈中为空,无效
                    若圆圈中不空:
                            circleMode 切换至 removeMode, 中间删除按钮出现
                            当前圆圈变绿, 更新 boolean toBeRemoved[], int removeAppsNum
    */
    public class RoundImageViewOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            RoundImageView tmpView = (RoundImageView) v;
            if(storeGlobalValue.circleMode == storeGlobalValue.removeMode)
            {
                return false;
            }
            else
            {
                if(registered_pkgName.get(tmpView.number).equals("NONE"))
                {
                    return false;
                }
                else
                {
                    storeGlobalValue.circleMode = storeGlobalValue.removeMode;
                    MiddleDeleteButton.setVisibility(View.VISIBLE);
                    tmpView.turnGreen();
                    storeGlobalValue.toBeRemoved[tmpView.number] = true;
                    storeGlobalValue.removeAppsNum ++;
                }
            }
            return false;
        }
    }

    /*
        中间删除按钮的点击事件:
               1. 将 boolean toBeRemoved[] 中对应为 true 的图标在其圆圈中删除 -- 变为 add_file 图片, border变成普通颜色
               2. circleMode 从 removeMode 切换至 addMode
               3. 中间删除按钮消失
               4. 更新列表: registered_pkgName、registeredAppInfos、leftAppInfos
               5. 更新全局静态列表: appsNotRegistered、appsRegistered
               6. 将registered_pkgName 内容(包名,可能有"NONE") 写入到 配置文件 "FAST_ACCESS_REGISTERED" 中
               7. boolean toBeRemoved[] 全部设为 false, int removeAppsNum 设为 0
    */
    public class MiddleDeleteButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //1. 将 boolean toBeRemoved[] 中对应为 true 的图标在其圆圈中删除 -- 变为 add_file 图片, border变成普通颜色
            for(int i = 0 ; i < 6 ; i ++)
            {
                if(storeGlobalValue.toBeRemoved[i] == true)
                {
                    circles[i].setDrawable(getResources().getDrawable(R.drawable.add_file));
                    circles[i].turnNormal();
                }
            }

            //2. circleMode 从 removeMode 切换至 addMode
            storeGlobalValue.circleMode = storeGlobalValue.addMode;

            //3. 中间删除按钮消失
            MiddleDeleteButton.setVisibility(View.INVISIBLE);

            //4. 更新列表: registered_pkgName、registeredAppInfos、leftAppInfos
            for(int i = 0 ; i < 6 ; i ++)
            {
                if(storeGlobalValue.toBeRemoved[i] == true)
                {
                    String tmpPkgName = registered_pkgName.get(i);

                    //registered_pkgName
                    registered_pkgName.set(i,"NONE");

                    //registeredAppInfos
                    appInfo tmpAppInfo = new appInfo(MainActivity.this);
                    tmpAppInfo.setEmpty();
                    registeredAppInfos.set(i,tmpAppInfo);

                    //leftAppInfos
                    tmpAppInfo = new appInfo(MainActivity.this);
                    tmpAppInfo.getAll(tmpPkgName);
                    leftAppInfos.add(tmpAppInfo);
                }
            }

            //5. 更新全局静态列表: appsNotRegistered、appsRegistered、appsRegisteredContainEmpty
            usedForUpdateGlobals.updateAppsRegistered(registeredAppInfos);
            usedForUpdateGlobals.updateAppsNotRegistered(leftAppInfos);

            //6. 将registered_pkgName 内容(包名,可能有"NONE") 写入到 配置文件 "FAST_ACCESS_REGISTERED" 中
            writeSettingsFile();

            //7. boolean toBeRemoved[] 全部设为 false, int removeAppsNum 设为 0
            usedForUpdateGlobals.cleanAboutRemove();
        }
    }

    /*
        监听器: pGridView中 item 的监听事件
            1. 在圆圈 "numOfCircleToSelectApp" 中显示此 item 的 icon
            2. 更新列表: registered_pkgName、registeredAppInfos、allAppInfos、leftAppInfos
            3. 更新全局静态列表: appsNotRegistered、appsRegistered、allAppsInfo
            4. 将registeredAppInfos 中的包名写入到 配置文件 "FAST_ACCESS_REGISTERED" 中
            5. 设置 pGridView 消失不可见
     */
    public class GridViewOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //1. 在圆圈 "numOfCircleToSelectApp" 中显示此 item 的 icon
            ImageView correspondingImageView = (ImageView) view.findViewById(R.id.app_icon);
            Drawable correspondingDrawable = correspondingImageView.getDrawable();
            circles[numOfCircleToSelectApp].setDrawable(correspondingDrawable);

            //2. 更新列表: registered_pkgName、registeredAppInfos、leftAppInfos
            appInfo selectedAppInfo = leftAppInfos.get(i);

            registered_pkgName.set(numOfCircleToSelectApp,selectedAppInfo.getAppPkgName());
            registeredAppInfos.set(numOfCircleToSelectApp,selectedAppInfo);
            leftAppInfos.remove(i);

            //3. 更新全局静态列表: appsNotRegistered、appsRegistered
            usedForUpdateGlobals.updateAppsRegistered(registeredAppInfos);
            usedForUpdateGlobals.updateAppsNotRegistered(leftAppInfos);

            //4. 将registered_pkgName 内容(包名,可能有"NONE") 写入到 配置文件 "FAST_ACCESS_REGISTERED" 中
            writeSettingsFile();

            //5. 设置 pGridView 消失不可见
            pGridView.setVisibility(View.INVISIBLE);
        }
    }

    //将registered_pkgName 内容(包名,可能有"NONE") 写入到 配置文件 "FAST_ACCESS_REGISTERED" 中
    public void writeSettingsFile() {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
        editor = preferences.edit();

        for(int j = 1 ; j <= 6 ; j ++)
        {
            editor.putString("pkgName"+j,registered_pkgName.get(j-1));
        }
        editor.putInt("count",6);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode , KeyEvent event) {
        // TODO 按两次返回键退出应用程序


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 判断间隔时间 小于2秒就退出应用
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // 应用名
                String applicationName = getResources().getString(
                        R.string.app_name);
                String msg = "再按一次返回键退出" + applicationName;
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                // 计算两次返回键按下的时间差
                exitTime = System.currentTimeMillis();
            } else {
                // 关闭应用程序
                finish();
                // 返回桌面操作
                // Intent home = new Intent(Intent.ACTION_MAIN);
                // home.addCategory(Intent.CATEGORY_HOME);
                // startActivity(home);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //锁屏功能
    private void CallForAction() {
        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, LockAdmin.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能哦亲^^");
    }

    //create by zouyun
    //监听service是否运行
    private boolean scaleTheService(String className){
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(30);
        if (!(serviceList.size() > 0))
            return false;
        for (int i = 0; i < serviceList.size(); i ++) {
            if (serviceList.get(i).service.getClassName().equals(className))
                return true;
        }
        return false;
    }
}