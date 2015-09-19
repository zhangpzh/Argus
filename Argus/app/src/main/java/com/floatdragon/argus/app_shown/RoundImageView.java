package com.floatdragon.argus.app_shown;

/**
 * Created by user on 15-9-14.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.floatdragon.argus.R;


/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 * 设置颜色在xml布局文件中由自定义属性配置参数指定
 */
public class RoundImageView extends ImageView {

    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;

    //存储xml中定义的边框的情况
    private int storedBorderOutsideColor = 0;
    private int storedBorderInsideColor = 0;
    private int storedBorderThickness = 0;

    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private boolean notDefault = false;

    //是否有显示的图片
    private boolean isEmpty = true;

    //图像
    private Drawable mDrawable;

    //为当前这个圆形图片设置编号 (从0开始数)
    public int number;

    //圆圈的 border 是否已变绿
    public boolean isGreen;

    //是否只需要重画 border
    public boolean drawBorderOnly = false;

    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a.getColor(R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(R.styleable.roundedimageview_border_inside_color, defaultColor);

        isGreen = false;

        //存储 xml 中 border 的信息, 以便日后调用 turnNormal 方法想变回来时有门路
        storedBorderThickness = mBorderThickness;
        storedBorderOutsideColor = mBorderOutsideColor;
        storedBorderInsideColor = mBorderInsideColor;
    }

    //将圆圈的 border 画成粗绿色
    public void turnGreen() {
        isGreen = true;
        mBorderThickness = 4;
        mBorderOutsideColor = Color.GREEN;
        mBorderInsideColor = Color.GREEN;

        //只用画内外 border 就好, 用不着整个 RoundImage 重画 */
        drawBorderOnly = true;
        invalidate();
        drawBorderOnly = false;
    }

    //把圆圈的 border 变回原来的样子
    public void turnNormal() {
        isGreen = false;
        mBorderThickness = storedBorderThickness;
        drawGreyLayerOnly();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable;
        if (notDefault == false)
            drawable = getDrawable();
        else
            drawable = mDrawable;

        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        int radius = 0;

        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor)
        {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        }

        else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor)
        {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
        }

        else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor)
        {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
        }

        else
        {// 没有边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        if(drawBorderOnly == false)
        {
            Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
            canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
        }
    }

    /**
     * 获取裁剪后的圆形图片
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
		/* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
		/* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    //更改圆形中显示的图像
    public void setDrawable(Drawable tmpDrawable) {
        notDefault = true;
        mDrawable = tmpDrawable;
        invalidate();
    }

    //只画内层黑色 border
    public void drawBlackLayerOnly() {
        mBorderOutsideColor = defaultColor;
        mBorderInsideColor = 0xFF000000;
        drawBorderOnly = true;
        invalidate();
        drawBorderOnly = false;
    }

    //只画外层为灰色的 border
    public void drawGreyLayerOnly() {
        mBorderOutsideColor = 0xFFA9A9A9;
        mBorderInsideColor = defaultColor;
        drawBorderOnly = true;
        invalidate();
        drawBorderOnly = false;
    }
}