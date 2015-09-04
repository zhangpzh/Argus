package com.example.user.argus.Adapter_and_GridViewItem;

import android.graphics.Bitmap;

/**
 * Created by user on 15-9-2.
 */

//GridViewItem 类, 用于保存 gridView 的一个 cell 的组件的填充值
public class GridViewItem {
    public Bitmap bitmap;
    public String label;
    public boolean checkedState;

    public GridViewItem() {
    }

    public GridViewItem(Bitmap bitmap , String label , boolean checkedState) {
        super();
        this.bitmap = bitmap;
        this.label = label;
        this.checkedState = checkedState;
    }
}