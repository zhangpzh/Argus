package com.example.user.argus.Adapter_and_GridViewItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.argus.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.user.argus.App_Information.storeGlobalValue;

/**
 * Created by user on 15-9-2.
 */

public class MyAdapter extends BaseAdapter {
    private List<HashMap<String, GridViewItem>> listItems;
    Context context;
    private LayoutInflater layoutInflater;
    private GridViewItem tempGridViewItem;

    private String callActivityName; //用来判别是哪个 Activity

    public MyAdapter(Context context,
                     List<HashMap<String, GridViewItem>> listItems, String callActivityName) {
        this.context = context;
        this.listItems = new ArrayList<HashMap<String, GridViewItem>>(listItems);
        layoutInflater = LayoutInflater.from(context);
        this.callActivityName = callActivityName;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //重写 Don't forget to implement it !

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = null;

        if(layoutInflater != null)
        {
            view = layoutInflater.inflate(R.layout.grid_cell,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
            TextView textView = (TextView) view.findViewById(R.id.app_label);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.app_checkBox);

            //获取自定义的类实例
            tempGridViewItem = listItems.get(position).get("item");
            imageView.setImageBitmap(tempGridViewItem.bitmap);
            textView.setText(tempGridViewItem.label);
            checkBox.setChecked(tempGridViewItem.checkedState);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                //为每个checkBox注册 check 事件，以更新全局布尔数组 checkConditionsOfMainActivity 和 checkConditionsOfSelectAppsActivity
                //用以监控 MainActivity 和 SelectAppsActivity 这两个 Activity 中 的 gridView 中的 checkBox 的勾选情况.
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(callActivityName.equals("MainActivity"))
                    {
                        storeGlobalValue.checkConditionsOfMainActivity[position] = isChecked;
                    }
                    else if(callActivityName.equals("SelectAppsActivity"))
                    {
                        storeGlobalValue.checkConditionsOfSelectAppsActivity[position] = isChecked;
                    }
                }
            });
        }
        return view;
    }
}