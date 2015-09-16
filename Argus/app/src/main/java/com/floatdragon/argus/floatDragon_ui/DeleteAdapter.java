package com.floatdragon.argus.floatDragon_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import com.floatdragon.argus.R;

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

        //����convertViewΪ�գ�����Ҫ����View
        if (convertView == null) {
            //�Զ����һ������������convertview
            holder = new ViewHolder();

            //����Զ����Item���ּ��ز���
            convertView = mInflater.inflate(R.layout.delete_items, null);

            holder.date = (TextView)convertView.findViewById(R.id.dates);
            holder.string = (TextView)convertView.findViewById(R.id.strings);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkboxes);

            //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
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
