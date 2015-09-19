package com.floatdragon.argus.floatDragon_ui.float_circle;
/**
 * Created by zouyun on 15/8/10.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.floatdragon.argus.R;
import com.floatdragon.argus.floatDragon_ui.StaticData;

/**
 * Created by zouyun on 15/8/5.
 */
public class FloatView extends ImageView {

    private float rawX;
    private float rawY;
    private float mTouchx;
    private float mTouchy;
    private float x;
    private float y;
    private float mStartx;
    private float mStarty;
    private double angle = 0;
    private OnClickListener mClickListen;
    private float scale;
    private int bottom;
    private int top;
    private int addx, addy;
    private WindowManager windowManager = (WindowManager) getContext().getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams windowManagerParams = ((FloatApplication) getContext()
            .getApplicationContext()).getWindowParams();

    public FloatView(final Context context) {
        super(context);
        scale = context.getResources().getDisplayMetrics().density;
        rawX = StaticData.pos.first;
        rawY = StaticData.pos.second + StaticData.circleSize / 2;
        bottom = (int)(48 * scale + 0.5f);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statuBarHeight = frame.top;
        bottom = StaticData.screenHeight - frame.bottom;
        Log.i("bottom", bottom + "");
        Log.i("bottom", ""+ bottom);
        float mx, my;
        top = statuBarHeight;
        x = event.getRawX();
        y = event.getRawY() - statuBarHeight;
        mTouchx = event.getX();
        mTouchy = event.getY();
        float degree;
        Log.i("this", "sd");
        float dis = 0;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartx = x;
                mStarty = y;
     //           this.setImageResource(R.drawable.btn_press);
                break;
            case MotionEvent.ACTION_MOVE:
                if (StaticData.move) {
                    updateViewPosition();
                    break;
                }
                mx = x - rawX;
                my = y - rawY;
                dis = getDis(mx, my);
                if (dis > StaticData.barRadius / 4) {
                    StaticData.layout[StaticData.position].setVisibility(VISIBLE);
                    if (StaticData.position > 1) {
                        angle = Math.atan((double) (mx / my));
                        degree = (float)(Math.atan((double)(my / mx)) / Math.PI * 180);
                    } else {
                        angle = Math.atan((double) (my / mx));
                        degree = (float)-(Math.atan((double)(mx / my)) / Math.PI * 180);
                    }
                    Matrix matrix = new Matrix();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),StaticData.icon[StaticData.position]);
                    matrix.setRotate(degree);
                    Bitmap newBit = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getHeight(),
                            bitmap.getHeight(), matrix, true);
                    BitmapDrawable bd = new BitmapDrawable(newBit);
                    this.setImageDrawable(bd);

                    if (dis > StaticData.barRadius / 4 * 3) {
                        StaticData.stateChange(angle);
                        Log.i("angle", angle + "");
                    } else {
                        StaticData.lay[StaticData.position][0].setHovered(false);
                        StaticData.lay[StaticData.position][1].setHovered(false);
                        StaticData.lay[StaticData.position][2].setHovered(false);
                        StaticData.lay[StaticData.position][3].setHovered(false);
                        StaticData.lay[StaticData.position][4].setHovered(false);
                    }
                    if (StaticData.lay[StaticData.position][2].isHovered() && dis > StaticData.barRadius * 1.1) {
                        StaticData.move = true;
                        StaticData.layout[StaticData.position].setVisibility(GONE);
                        setImageResource(R.drawable.btn_normal);
                        windowManager.updateViewLayout(this, windowManagerParams);
                    }
                } else {
                    StaticData.layout[StaticData.position].setVisibility(GONE);
                }
                break;
            case MotionEvent.ACTION_UP:
                addx = 0;
                addy = 0;
                if (StaticData.move) {
                    StaticData.move = false;
                    alignSide(x, y);
                    StaticData.layout[StaticData.position].setVisibility(GONE);
                    this.setImageResource(StaticData.icon[StaticData.position]);
                    break;
                }
                mx = x - rawX;
                my = y - rawY;
                dis = getDis(mx, my);
                if (dis > StaticData.barRadius / 4 * 3) {
                    StaticData.doIt(angle);
                }
                mTouchx = mTouchy = 0;
                if ((x - mStartx) < 5 && (y - mStarty) < 5) {
                    mClickListen.onClick(this);
                }
                StaticData.layout[StaticData.position].setVisibility(GONE);
                this.setImageResource(StaticData.icon[StaticData.position]);
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListen = l;
    }

    public float getDis(float x1, float y1)
    {
        //  float sum = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        float sum = x1 * x1 + y1 * y1;
        float re = (float)Math.sqrt(sum);
        Log.i("dis", re + "");
        return re;
    }

    private void updateViewPosition() {
        windowManagerParams.x = (int) (x);
        windowManagerParams.y = (int) (y);
        windowManager.updateViewLayout(this, windowManagerParams);
    }

    private void alignSide(float x1, float y1) {
//        int index = min((int)x1, (int)y1);
        //      windowManagerParams.x = StaticData.pos[index].first;
        //   windowManagerParams.y = StaticData.pos[index].second;
        int index = minIndex((int)x1, (int)y1);
        StaticData.position = index;
        windowManagerParams.x = (int) (x - mTouchx);
        windowManagerParams.y = (int) (y - mTouchy);
        switch (index) {
            case 0:
                if (x - mTouchx < StaticData.barRadius - StaticData.circleSize / 2)
                    windowManagerParams.x = StaticData.barRadius - StaticData.circleSize / 2 ;
                if (x - mTouchx > StaticData.screenWidth - StaticData.barRadius)
                    windowManagerParams.x = StaticData.screenWidth - StaticData.barRadius - StaticData.circleSize / 2;
                windowManagerParams.y = 0;
                rawY = 0;
                rawX = windowManagerParams.x + StaticData.circleSize / 2;
                break;
            case 1:
                if (x - mTouchx < StaticData.barRadius - StaticData.circleSize / 2)
                    windowManagerParams.x = StaticData.barRadius - StaticData.circleSize / 2 ;
                if (x - mTouchx > StaticData.screenWidth - StaticData.barRadius)
                    windowManagerParams.x = StaticData.screenWidth - StaticData.barRadius - StaticData.circleSize / 2 ;
                windowManagerParams.y = StaticData.screenHeight;
                rawY = StaticData.screenHeight;
                rawX = windowManagerParams.x + StaticData.circleSize / 2;
                break;
            case 2:
                if (y - mTouchy < StaticData.barRadius)
                    windowManagerParams.y = StaticData.barRadius - StaticData.circleSize / 2 ;
                if (y - mTouchy > StaticData.screenHeight - StaticData.barRadius - bottom - StaticData.circleSize / 2)
                    windowManagerParams.y = StaticData.screenHeight - StaticData.barRadius -  StaticData.circleSize;
                windowManagerParams.x = 0;
                rawX = 0;
                rawY = windowManagerParams.y;
                Log.i("RawY", rawY + "");
                rawY += StaticData.circleSize;
                Log.i("RawY", rawY + "");
                break;
            case 3:
                if (y - mTouchy < StaticData.barRadius)
                    windowManagerParams.y = StaticData.barRadius - StaticData.circleSize / 2 ;
                if (y - mTouchy > StaticData.screenHeight - StaticData.barRadius - bottom - StaticData.circleSize / 2)
                    windowManagerParams.y = StaticData.screenHeight - StaticData.barRadius -  StaticData.circleSize;
                windowManagerParams.x = StaticData.screenWidth;
                rawX = StaticData.screenWidth;
                rawY = windowManagerParams.y + StaticData.circleSize;
        }
        Log.i("align", index+"");
        StaticData.pos = new Pair<>(windowManagerParams.x, windowManagerParams.y);
        windowManager.updateViewLayout(this, windowManagerParams);
        switch (index) {
            case 0:
            case 1:
                windowManagerParams.height = StaticData.barRadius;
                windowManagerParams.width = StaticData.barRadius * 2;
                windowManagerParams.x = (int)(x - mTouchx + StaticData.circleSize / 2 - StaticData.barRadius);
                break;
            case 2:
            case 3:
                windowManagerParams.height = StaticData.barRadius * 2;
                windowManagerParams.width = StaticData.barRadius;
                windowManagerParams.y = (int)(y - mTouchy + StaticData.circleSize / 2- StaticData.barRadius);
        }

        windowManager.updateViewLayout(StaticData.layout[index], windowManagerParams);
        windowManagerParams.width = StaticData.circleSize;
        windowManagerParams.height = StaticData.circleSize;


    }


    private int minIndex(int x, int y) {
        int min = 99999999;
        int index = -1;
        if (Math.abs(y) < min) {
            min = Math.abs(y - 0);
            index = 0;
        }
        if (Math.abs(y - StaticData.screenHeight) < min) {
            min = Math.abs(y - StaticData.screenHeight);
            index = 1;
        }
        if (Math.abs(x) < min) {
            min = Math.abs(x);
            index = 2;
        }
        if (Math.abs(x - StaticData.screenWidth) < min) {
            min = Math.abs(x - StaticData.screenWidth);
            index = 3;
        }
        return index;
    }

}
