package com.example.user.argus.floatDragon_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import com.example.user.argus.R;

/**
 * Created by zx on 2015/9/14.
 */
public class DeleteAdapter extends SimpleAdapter {

    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private String[] mFrom;
    private int[] mTo;
    private LayoutInflater mInflater;
    private boolean [] mChecked;

    public DeleteAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                         String[] from, int[] to, boolean [] checked) {
        super(context, data, resource, from, to);
        mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mChecked = checked;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        //如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            //自定义的一个类用来缓存convertview
            holder = new ViewHolder();

            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.delete_items, null);

            holder.date = (TextView)convertView.findViewById(R.id.dates);
            holder.string = (TextView)convertView.findViewById(R.id.strings);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkboxes);

            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.date.setText((String)mData.get(position).get("date"));
        holder.string.setText((String) mData.get(position).get("string"));
        if (mChecked[position]) {
            holder.checkBox.setChecked(true);
            convertView.setBackgroundResource(R.drawable.selectedbackground);
            convertView.getBackground().setAlpha(100);
        }
        else {
            holder.checkBox.setChecked(false);
            convertView.setBackgroundColor(0);
        }
        return convertView;
    }
    public void setcheckedall (boolean checked) {
        for (int i = 0; i < mChecked.length; i ++) {
            mChecked[i] = checked;
        }
    }
    public void setcheckedat (boolean checked, int position) {
        mChecked[position] = checked;
    }
    public boolean getcheckedat (int position) {
        return mChecked[position];
    }
}
