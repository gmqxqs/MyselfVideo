package com.shuyu.gsyvideoplayer.MyselfView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * Created by tanjinc on 16/10/13.
 */
public class BatteryView extends View {

    private Paint mPaint;//文字的画笔
    private boolean isShowText = true;
    private float textWidth = 14;//电量文字长

    private int mMargin = 5;    //电池内芯与边框的距离
    private int mBoder = 2;     //电池外框的宽带
    private int mWidth = 64;    //总长
    private int mHeight = 32;   //总高
    private int mHeadWidth = 6;
    private int mHeadHeight = 10;

    private RectF mMainRect;
    private RectF mHeadRect;
    private float mRadius = 10f;   //圆角
    private float mPower;

    private boolean mIsCharging;    //是否在充电
    private TextView textView;


    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BatteryView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mHeadRect = new RectF(0, (mHeight - mHeadHeight)/2, mHeadWidth, (mHeight + mHeadHeight)/2);

        float left = mHeadRect.width();
        float top = mBoder;
        float right = mWidth-mBoder;
        float bottom = mHeight-mBoder;
        mMainRect = new RectF(left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint1 = new Paint();

        //画电池头
     /*   paint1.setStyle(Paint.Style.FILL);  //实心
        paint1.setColor(Color.WHITE);
        canvas.drawRect(mHeadRect, paint1);*/

        //画外框
        paint1.setStyle(Paint.Style.STROKE);    //设置空心矩形
        paint1.setStrokeWidth(mBoder);          //设置边框宽度
        paint1.setColor(Color.WHITE);
        canvas.drawRoundRect(mMainRect, mRadius, mRadius, paint1);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(21);

        //画电池芯
        Paint paint = new Paint();
        if (mIsCharging) {
            paint.setColor(Color.GREEN);
        } else {
            if (mPower < 0.1) {
                paint.setColor(Color.RED);
                mPaint.setColor(Color.RED);
            } else {
                paint.setColor(Color.DKGRAY);
                mPaint.setColor(Color.WHITE);
            }
        }

        /**
         * 设置文字画笔
         */


        int width   = (int) (mPower * (mMainRect.width() - mMargin*2));
        int left    = (int) (mMainRect.right - mMargin - width);
        int right   = (int) (mMainRect.right - mMargin);
        int top     = (int) (mMainRect.top + mMargin);
        int bottom  = (int) (mMainRect.bottom - mMargin);
        Rect rect = new Rect(left,top,right, bottom);
        canvas.drawRect(rect, paint);

        if(isShowText){
            int powText = (int)(mPower * 100);
            String  textString = powText+"";
            Rect textRect = new Rect();
            mPaint.getTextBounds(textString,0,textString.length(), textRect);
            mPaint.setColor(Color.WHITE);
            //Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textWidth = textRect.width();
      //      L.e("textWidth："+textWidth);
            float textHeight = textRect.height();
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float topFont = fontMetrics.top;//为基线到字体上边框的距离
            float bottomFont = fontMetrics.bottom;//为基线到字体下边框的距离
            int baseLineY = (int) (mHeight / 2 - topFont / 2 - bottomFont / 2);//基线中间点的y轴计算公式
            // canvas.drawText(textString, (int) (width*0.5), baseLineY, mPaint);
            //(-5  距离最右边5)
            canvas.drawText(textString, mWidth-textWidth-15, baseLineY, mPaint);

        }else{
            textWidth = 0;
        }



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    private void setPower(float power) {
        mPower = power;
        invalidate();
    }

    private BroadcastReceiver mPowerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            setPower(((float) level)/scale);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        getContext().registerReceiver(mPowerConnectionReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mPowerConnectionReceiver);
        super.onDetachedFromWindow();
    }
}
