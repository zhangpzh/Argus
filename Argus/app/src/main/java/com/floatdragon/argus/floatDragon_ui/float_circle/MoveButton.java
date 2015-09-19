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
public class MoveButton extends ImageButton {

    public MoveButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.move);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.move_onClick();
                Log.i("add_app", "call");
            }
        });
    }


    public MoveButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.move);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.move_onClick();
                Log.i("add_app", "call");
            }
        });
    }

}
