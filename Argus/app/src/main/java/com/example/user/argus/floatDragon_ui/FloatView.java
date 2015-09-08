package com.example.user.argus.floatDragon_ui;

/**
 * Created by zouyun on 15/8/10.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.user.argus.R;
/**
 * Created by zouyun on 15/8/5.
 */
public class FloatView extends ImageView {
    private float mTouchx;
    private float mTouchy;
    private float x;
    private float y;
    private float mStartx;
    private float mStarty;
    private double angle = 0;
    private OnClickListener mClickListen;
    private WindowManager windowManager = (WindowManager) getContext().getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);;
    private WindowManager.LayoutParams windowManagerParams = ((FloatApplication) getContext()
            .getApplicationContext()).getWindowParams();;

    public FloatView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statuBarHeight = frame.top;
        x = event.getRawX();
        y = event.getRawY() - statuBarHeight;
        float dis = 0;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchx = event.getX();
                mTouchy = event.getY();
                mStartx = x;
                mStarty = y;
                this.setImageResource(R.drawable.btn_press);
                break;
            case MotionEvent.ACTION_MOVE:
                if (StaticData.move) {
                    updateViewPosition();
                    break;
                }
                mTouchx = event.getX();
                mTouchy = event.getY();
                Log.i("raw", "x:" + event.getRawX() + "  y:" + event.getRawY());
                float mx, my;
                mx = event.getRawX() - StaticData.point[StaticData.position].first;
                my = event.getRawY() - StaticData.point[StaticData.position].second;
                dis = getDis(mx, my);
                if (dis > 30) {
                    StaticData.layout[StaticData.position].setVisibility(VISIBLE);
                    if (StaticData.position > 1)
                        angle = Math.atan((double) (mx / my));
                    else
                        angle = Math.atan((double) (my / mx));
                    if (dis < 150)
                        StaticData.layout[StaticData.position].getBackground().setAlpha((int)(dis / 150 * 255));

                    if (dis > 230) {
                        StaticData.stateChange(angle);
                        Log.i("angle", angle + "");
                    } else {

                        StaticData.lay[StaticData.position][0].setHovered(false);
                        StaticData.lay[StaticData.position][2].setHovered(false);
                        StaticData.lay[StaticData.position][4].setHovered(false);
                    }

                } else {
                    StaticData.layout[StaticData.position].setVisibility(GONE);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (StaticData.move) {
                    StaticData.move = false;
                    alignSide(event.getRawX(), event.getRawY());
                    StaticData.layout[StaticData.position].setVisibility(GONE);
                    this.setImageResource(R.drawable.btn_normal);
                    break;
                }
                dis = getDis(mTouchx, mTouchy);
                if (dis > 230) {
                    StaticData.doIt(angle);
                }
                if (StaticData.move) System.out.println("move");
                mTouchx = mTouchy = 0;
                if ((x - mStartx) < 5 && (y - mStarty) < 5) {
                    mClickListen.onClick(this);
                }
                StaticData.layout[StaticData.position].setVisibility(GONE);
                this.setImageResource(R.drawable.btn_normal);
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
        float re = (float) Math.sqrt(sum);
        Log.i("dis", re + "");
        return re;
    }

    private void updateViewPosition() {
        windowManagerParams.x = (int) (x - mTouchx);
        windowManagerParams.y = (int) (y - mTouchy);
        windowManager.updateViewLayout(this, windowManagerParams);
    }

    private void alignSide(float x1, float y1) {
        int index = min((int)x1, (int)y1);
        windowManagerParams.x = StaticData.pos[index].first;
        windowManagerParams.y = StaticData.pos[index].second;
        StaticData.position = index;
        Log.i("align", index + "");
        Log.i("xy", "x:" + StaticData.pos[index].first + " y:" + StaticData.pos[index].second);
        windowManager.updateViewLayout(this, windowManagerParams);
    }

    private int min(int x, int y) {
        float dis = 0;
        int index = 0;
        float min = 99999999;
        for (int i = 0; i < 4; i ++) {
            dis = Math.abs((x - StaticData.pos[i].first) * (x - StaticData.pos[i].first) + (y - StaticData.pos[i].second) * (y - StaticData.pos[i].second));
            if (dis < min) {
                min = dis;
                index = i;
            }
        }
        return index;
    }
}
