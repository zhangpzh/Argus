package com.floatdragon.argus.floatDragon_ui.float_circle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.floatdragon.argus.R;

/**
 * Created by zouyun on 15/9/3.
 */
public class AppButton extends ImageButton {
    public AppButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.app);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.item_onClick();
                Log.i("item", "call");
            }
        });
    }


    public AppButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.app);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                OnClickMethod.item_onClick();
                Log.i("item", "call");
            }
        });
    }

}
