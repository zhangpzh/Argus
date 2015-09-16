package com.floatdragon.argus.floatDragon_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.floatdragon.argus.R;


/**
 * Created by zouyun on 15/9/10.
 */
public class LockButton extends ImageButton {

    public LockButton(final Context context) {
        super(context);
        setBackgroundResource(R.drawable.lock);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.lock_onClick(context);
                Log.i("move", "call");
            }
        });
    }


    public LockButton(final Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.lock);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.lock_onClick(context);
                Log.i("move", "call");
            }
        });
    }

}
