package com.floatdragon.argus.app_information;

import com.floatdragon.argus.gridView_relative.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 15-9-14.
 */
//根据输入的 appInfo 容器对象, 返回一个MyAdapter对象, 用于列表项显示
public class wrapAppInfosIntoAdapter {

    Context context;
    String callActivityName;        //想要 Adapter 的 Activity 名称

    public wrapAppInfosIntoAdapter(Context context , String callActivityName) {
        this.context = context;
        this.callActivityName = callActivityName;
    }

    //输入 appInfo 的 容器，返回一个 MyAdapter 类型的 adapter
    public MyAdapter getAdapter(Collection<appInfo> barn) {
        List<appInfo> list = new ArrayList<appInfo>(barn);

        List<HashMap<String, GridViewItem>> listItems = new ArrayList<HashMap<String, GridViewItem>>();

        for (int i = 0; i < list.size() ; i++)
        {
            HashMap<String, GridViewItem> listItem = new HashMap<String, GridViewItem>();
            listItem.put("item", getGridViewItem(list.get(i)));
            listItems.add(listItem);
        }
        MyAdapter myAdapter = new MyAdapter(context, listItems,callActivityName);
        return myAdapter;
    }

    //输入 appInfo 类型的对象, 返回一个 GridViewItem 的对象
    public GridViewItem getGridViewItem(appInfo info) {
        Drawable tempDrawable = info.getAppIcon();
        String tempLabel = info.getAppName();
        GridViewItem tmpGridViewItem = new GridViewItem(tempDrawable,tempLabel);
        return tmpGridViewItem;
    }

    //将Drawable 转化为 Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
