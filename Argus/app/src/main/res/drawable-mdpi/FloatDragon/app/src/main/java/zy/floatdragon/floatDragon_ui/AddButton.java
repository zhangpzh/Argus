package zy.floatdragon.floatDragon_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import zy.floatdragon.R;

/**
 * Created by zouyun on 15/9/3.
 */
public class AddButton extends ImageButton {

    public AddButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.add);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.move = true;
                Log.i("add", "call");
            }
        });
    }


    public AddButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.add);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.move = true;
                Log.i("add", "call");
            }
        });
    }

}
