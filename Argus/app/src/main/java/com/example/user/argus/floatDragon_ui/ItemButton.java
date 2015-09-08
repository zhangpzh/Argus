package com.example.user.argus.floatDragon_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.user.argus.R;

/**
 * Created by zouyun on 15/9/3.
 */
public class ItemButton extends ImageButton {
    public ItemButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.items);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("item", "call");
            }
        });
    }


    public ItemButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.items);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("item", "call");
            }
        });
    }

}
