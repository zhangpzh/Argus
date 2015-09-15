package com.example.user.argus.floatDragon_ui;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;

import com.example.user.argus.R;

import java.util.logging.Handler;

/**
 * Created by zouyun on 15/9/3.
 */
public  class StaticData {
    public static int screenWidth;
    public static int screenHeight;
    public static int len = 0;
    public static View layout[];
    public static ImageButton[] lay[];
    public static boolean move = false;
    public static int position = 2;

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static Pair<Integer, Integer>[] point;
    public static Pair<Integer, Integer>[] pos;

    public static int isShow  = 0;
    public static int isShow2  = 0;
    public static int isShow3 = 0;


    public static int circleSize = 126;

    public static int barRadius = 360;

    public static void init() {
        lay = new ImageButton[4][];
        for (int i = 0; i < 4; i ++)
            lay[i] = new ImageButton[5];
        lay[0][0] = (ImageButton)layout[0].findViewById(R.id.top_left);
        lay[0][1] = (ImageButton)layout[0].findViewById(R.id.top_rb);
        lay[0][2] = (ImageButton)layout[0].findViewById(R.id.top_bottom);
        lay[0][3] = (ImageButton)layout[0].findViewById(R.id.top_lb);
        lay[0][4] = (ImageButton)layout[0].findViewById(R.id.top_right);


        lay[1][0] = (ImageButton)layout[1].findViewById(R.id.bottom_right);
        lay[1][1] = (ImageButton)layout[1].findViewById(R.id.bottom_lt);
        lay[1][2] = (ImageButton)layout[1].findViewById(R.id.bottom_top);
        lay[1][3] = (ImageButton)layout[1].findViewById(R.id.bottom_rt);
        lay[1][4] = (ImageButton)layout[1].findViewById(R.id.bottom_left);


        lay[2][0] = (ImageButton)layout[2].findViewById(R.id.left_top);
        lay[2][1] = (ImageButton)layout[2].findViewById(R.id.left_rb);
        lay[2][2] = (ImageButton)layout[2].findViewById(R.id.left_right);
        lay[2][3] = (ImageButton)layout[2].findViewById(R.id.left_rt);
        lay[2][4] = (ImageButton)layout[2].findViewById(R.id.left_bottom);

        lay[3][0] = (ImageButton)layout[3].findViewById(R.id.right_bottom);
        lay[3][1] = (ImageButton)layout[3].findViewById(R.id.right_lt);
        lay[3][2] = (ImageButton)layout[3].findViewById(R.id.right_left);
        lay[3][3] = (ImageButton)layout[3].findViewById(R.id.right_lb);
        lay[3][4] = (ImageButton)layout[3].findViewById(R.id.right_top);

        pos = new Pair[4];
        pos[0] = Pair.create(screenWidth / 2 - circleSize / 2, 0);
        pos[1] = Pair.create(screenWidth / 2 - circleSize / 2, screenHeight);
        pos[2] = Pair.create(0, screenHeight / 2 - circleSize / 2);
        pos[3] = Pair.create(screenWidth, screenHeight / 2 - circleSize / 2);

        point = new Pair[4];
        point[0] = Pair.create(screenWidth / 2, 0);
        point[1] = Pair.create(screenWidth / 2 , screenHeight);
        point[2] = Pair.create(0, screenHeight / 2);
        point[3] = Pair.create(screenWidth, screenHeight / 2);


    }

    public static void stateChange(double angle) {
        if (angle < (Math.PI / 180 * 20) && angle > 0) {
            StaticData.lay[position][0].setHovered(false);
            StaticData.lay[position][1].setHovered(false);
            StaticData.lay[position][2].setHovered(false);
            StaticData.lay[position][3].setHovered(false);
            StaticData.lay[position][4].setHovered(true);

        } else if (angle >= (Math.PI / 180 * 20) && (angle < (Math.PI / 180* 70))) {
            StaticData.lay[position][0].setHovered(false);
            StaticData.lay[position][1].setHovered(true);
            StaticData.lay[position][2].setHovered(false);
            StaticData.lay[position][3].setHovered(false);
            StaticData.lay[position][4].setHovered(false);

        } else if (angle > (Math.PI / 180* 70) || angle < -(Math.PI / 180 * 70)) {
            StaticData.lay[position][0].setHovered(false);
            StaticData.lay[position][1].setHovered(false);
            StaticData.lay[position][2].setHovered(true);
            StaticData.lay[position][3].setHovered(false);
            StaticData.lay[position][4].setHovered(false);
        } else if (angle >= -(Math.PI / 180 * 70) && angle <=  -(Math.PI / 180 * 20)) {
            StaticData.lay[position][0].setHovered(false);
            StaticData.lay[position][1].setHovered(false);
            StaticData.lay[position][2].setHovered(false);
            StaticData.lay[position][3].setHovered(true);
            StaticData.lay[position][4].setHovered(false);

        } else {
            StaticData.lay[position][0].setHovered(true);
            StaticData.lay[position][1].setHovered(false);
            StaticData.lay[position][2].setHovered(false);
            StaticData.lay[position][3].setHovered(false);
            StaticData.lay[position][4].setHovered(false);
        }
    }

    public static void doIt(double angle) {
        if (angle < (Math.PI / 180 * 20) && angle > 0) {
            StaticData.lay[position][4].performClick();

        } else if (angle >= (Math.PI / 180 * 20) && (angle < (Math.PI / 180* 70))) {
            StaticData.lay[position][1].performClick();

        } else if (angle > (Math.PI / 180* 70) || angle < -(Math.PI / 180 * 70)) {
            StaticData.lay[position][2].performClick();
        } else if (angle >= -(Math.PI / 180 * 70) && angle <=  -(Math.PI / 180 * 20)) {
            StaticData.lay[position][3].performClick();

        } else {
            StaticData.lay[position][0].performClick();
        }
    }
}

