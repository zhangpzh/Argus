package com.example.user.argus.floatDragon_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.user.argus.R;

/**
 * Created by zouyun on 15/9/10.
 */
public class NoteButton extends ImageButton {

    public NoteButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.note);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.note_onClick();
                Log.i("move", "call");
            }
        });
    }


    public NoteButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.drawable.note);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickMethod.note_onClick();
                Log.i("move", "call");
            }
        });
    }

}
