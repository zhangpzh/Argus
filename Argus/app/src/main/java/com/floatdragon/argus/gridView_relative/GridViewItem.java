package com.floatdragon.argus.gridView_relative;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 15-9-14.
 */

//GridViewItem 类, 用于保存 gridView 的一个cell的组件的填充值
public class GridViewItem {
    public Drawable drawable;
    public String label;

    public GridViewItem() {
    }

    public GridViewItem(Drawable drawable , String label) {
        super();
        this.drawable = drawable;
        this.label = label;
    }
}