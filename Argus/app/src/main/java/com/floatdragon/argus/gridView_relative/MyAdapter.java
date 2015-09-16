package com.floatdragon.argus.gridView_relative;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.floatdragon.argus.R;
/**
 * Created by user on 15-9-14.
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

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = null;

        if(layoutInflater != null)
        {
            view = layoutInflater.inflate(R.layout.grid_cell,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
            TextView textView = (TextView) view.findViewById(R.id.app_label);

            //获取自定义的类实例
            tempGridViewItem = listItems.get(position).get("item");
            imageView.setImageDrawable(tempGridViewItem.drawable);
            textView.setText(tempGridViewItem.label);
        }
        return view;
    }
}
