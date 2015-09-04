package com.example.user.argus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.user.argus.Adapter_and_GridViewItem.*;
import com.example.user.argus.App_Information.*;
import com.example.user.argus.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//加入界面 Activity
public class SelectAppsActivity extends Activity {

    GridView selectAppsActivityGridView;
    Button selectAppsAcitivityCancel;
    Button selectAppsAcitivityConfirm;

    List<String> leftAppsPkgName;                                         //没设置快捷访问的应用包名列表

    final String FAST_ACCESS_REGISTERED = "FAST_ACCESS_REGISTERED";       //设置文件-存储已设置快捷访问应用包名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_apps_activity_show_grid);

        selectAppsActivityGridView = (GridView) findViewById(R.id.SelectAppsActivityGridView);
        selectAppsAcitivityCancel = (Button) findViewById(R.id.SelectAppsActivityCancel);
        selectAppsAcitivityConfirm = (Button) findViewById(R.id.SelectAppsActivityConfirm);

        //初始化全局静态 boolean 数组 storeGlobalValue.checkConditionsOfSelectAppsActivity[], 监控 selectAppsActivityGridView 中的打勾情况
        storeGlobalValue usedForUpdateCheckConditionsOfSelectAppsActivity = new storeGlobalValue();
        usedForUpdateCheckConditionsOfSelectAppsActivity.updateCheckConditionsOfSelectAppsActivity(storeGlobalValue.appsNotRegistered.size());

        //获得 全局静态列表 storeGlobalValue.appsNotRegistered 中的包名
        leftAppsPkgName = new ArrayList<String>();
        for(Iterator it = storeGlobalValue.appsNotRegistered.iterator() ; it.hasNext() ;)
        {
            appInfo tmpAppInfo = (appInfo)it.next();
            leftAppsPkgName.add(tmpAppInfo.getAppPkgName());
        }

        //用 全局警惕啊列表 storeGlobalValue.appsNotResgistered中 中的信息 填充 gridView
        wrapAppInfosIntoAdapter willGetAdapter = new wrapAppInfosIntoAdapter(SelectAppsActivity.this,"SelectAppsActivity");
        MyAdapter newMyAdapter = willGetAdapter.getAdapter(storeGlobalValue.appsNotRegistered);
        selectAppsActivityGridView.setAdapter(newMyAdapter);


        //为 cancel button 与 confirm button 设置监听器
        selectAppsAcitivityCancel.setOnClickListener(new cancelButtonClickedListener());
        selectAppsAcitivityConfirm.setOnClickListener(new confirmButtonClickedListener());
    }

    //finish当前 Activity，回到 MainActivity
    private class cancelButtonClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    //将打勾的条目加入文件 "FAST_ACCESS_REGISTERED"，重新启动 MainActivity (新启动的它会读取最新的设置文件)
    private class confirmButtonClickedListener implements View.OnClickListener {
        List<String> selectedPkgs;

        @Override
        public void onClick(View v) {

            selectedPkgs = new ArrayList<String>();

            //将打了勾的应用对应的包名，加入到 selectedPkgs 中
            for(int i = 0 ; i < leftAppsPkgName.size() ; i ++)
            {
                String tmpPkgName = leftAppsPkgName.get(i);

                if(storeGlobalValue.checkConditionsOfSelectAppsActivity[i] == true)
                {
                    selectedPkgs.add(tmpPkgName);

                    //更新 全局数组 storeGlobalValue.appsNotRegistered

                    //storeGlobalValue.appsNotRegistered.remove(tmpAppInfo);
                    //appInfo类型 像上一条语句一样，直接remove 不太保险，因为字段太多，项目代码繁杂起来，可能出问题，简单地根据 先根据包名 更保险
                    for(Iterator it = storeGlobalValue.appsNotRegistered.iterator() ; it.hasNext();)
                    {
                        appInfo tempInfo = (appInfo)it.next();
                        if(tempInfo.getAppPkgName().equals(tmpPkgName))
                        {
                            storeGlobalValue.appsNotRegistered.remove(tempInfo);
                            break;
                        }
                    }
                }
            }

            //什么都没有选中, 则什么都不做
            if(selectedPkgs.size() == 0)
            {
                return;
            }
            //更新 全局数组 checkConditionsOfSelectAppsActivity 的大小，并重置其元素为 false
            storeGlobalValue usedForUpdateCheckConditionsOfSelectAppsActivity = new storeGlobalValue();
            usedForUpdateCheckConditionsOfSelectAppsActivity.updateCheckConditionsOfSelectAppsActivity(
                    leftAppsPkgName.size()-selectedPkgs.size());

            //将 selectedPkgs中的包名 加入到 preferences文件 "FAST_ACCESS_REGISTERED"中
            //加入的key-value pair: pkgNamei-包名, 包名-pkgNamei, 最后更新包名数 count

            SharedPreferences preferences;
            SharedPreferences.Editor editor;

            preferences = getSharedPreferences(FAST_ACCESS_REGISTERED,MODE_PRIVATE);
            editor = preferences.edit();

            int cnt = preferences.getInt("count", -1);


            //打 log
            //System.out.println("原来文件中包名的个数: " + cnt);

            for(int i = 0 ; i < selectedPkgs.size() ; i ++)
            {
                String name = selectedPkgs.get(i);
                cnt ++;
                editor.putString("pkgName"+cnt,name);
            }

            editor.putInt("count", cnt);
            editor.commit();

            //打 log
            //System.out.println("现在文件中包名的个数: "+preferences.getInt("count",-1));

            Intent goBackIntent = new Intent();
            goBackIntent.setClass(SelectAppsActivity.this,MainActivity.class);
            startActivity(goBackIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_apps, menu);
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