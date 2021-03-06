package com.floatdragon.argus.floatDragon_ui.float_circle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.floatdragon.argus.R;
import com.floatdragon.argus.floatDragon_ui.float_circle.OnClickMethod;

/**
 * Created by zouyun on 15/9/3.
 */
public class SettingButton extends ImageButton {

    public SettingButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.setting);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.setting_onClick();
                Log.i("setting", "call");
            }
        });
    }


    public SettingButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.setting);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.setting_onClick();
                Log.i("setting", "call");
            }
        });
    }


}
