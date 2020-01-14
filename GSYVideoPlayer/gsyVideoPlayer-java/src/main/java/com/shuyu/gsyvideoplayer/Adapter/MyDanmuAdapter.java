package com.shuyu.gsyvideoplayer.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.danmu.DanmuAdapter;

/**
 * Description: 弹幕适配器
 * Created by jia on 2017/9/25.
 * 人之所以能，是相信能
 */
public class MyDanmuAdapter extends DanmuAdapter<MyDanmuModel> {

    private Context context;

    private int textSize = 15;

    private float alpha = 1.0f;

    public MyDanmuAdapter(Context c) {
        super();
        context = c;
    }

    @Override
    public int[] getViewTypeArray() {
        int type[] = {0, 1, 2, 3};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
        //指定行高
        view.measure(0, 0);

        return view.getMeasuredHeight();
    }

    @Override
    public View getView(final MyDanmuModel entry, View convertView) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, (ViewGroup) convertView, true);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.tv_danmu);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        vh.tv.setText(entry.getContent() + "");
        vh.tv.setTextSize(textSize);

        vh.tv.setAlpha(alpha);
        finalVh = vh;

        vh.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onClick: "+entry.isGood );
                if (entry.isGood()) {
                    return;
                }
                entry.setGood(true);
                finalVh.tv.setTextColor(Color.RED);

            }
        });

        return convertView;
    }

    @Override
    public View getView(final MyDanmuModel entry, View convertView,int textSize,long textColor) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, (ViewGroup) convertView, true);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.tv_danmu);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        vh.tv.setText(entry.getContent() + "");

        int textColor2 = (int)textColor;
        switch (textSize){
            case 20:
                vh.tv.setTextSize(20);
                break;
            case 18:
                default:
                    vh.tv.setTextSize(18);

        }
        switch (textColor2){
            case 2:
                ColorStateList cs2 = context.getResources().getColorStateList(R.color.purple);
                vh.tv.setTextColor(cs2);
                break;
            case 3:
                ColorStateList cs3 = context.getResources().getColorStateList(R.color.blue);
                vh.tv.setTextColor(cs3);
                break;
            case 4:
                ColorStateList cs4 = context.getResources().getColorStateList(R.color.pink);
                vh.tv.setTextColor(cs4);
                break;
            case 5:
                ColorStateList cs5 = context.getResources().getColorStateList(R.color.blue2);
                vh.tv.setTextColor(cs5);
                break;
            case 6:
                ColorStateList cs6 = context.getResources().getColorStateList(R.color.green);
                vh.tv.setTextColor(cs6);
                break;
            case 7:
                ColorStateList cs7 = context.getResources().getColorStateList(R.color.yellow);
                vh.tv.setTextColor(cs7);
                break;
            case 8:
                ColorStateList cs8 = context.getResources().getColorStateList(R.color.red);
                vh.tv.setTextColor(cs8);
                break;
            case 1:
                default:
                ColorStateList cs1 = context.getResources().getColorStateList(R.color.white);
                vh.tv.setTextColor(cs1);

        }


        vh.tv.setAlpha(alpha);


        finalVh = vh;

      /*  vh.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onClick: "+entry.isGood );
                if (entry.isGood()) {
                    return;
                }
                entry.setGood(true);
              *//*  finalVh.tv_good_num.setText((entry.getGoodNum() + 1) + "");
                finalVh.tv_good_num.setTextColor(Color.RED);*//*
                finalVh.tv.setTextColor(Color.RED);
                //  finalVh.iv_danmu_good.setImageResource(R.mipmap.good_on);

              *//*  GoodView goodView = new GoodView(context);
                goodView.setTextInfo("+1", Color.parseColor("#f66467"), 12);
                goodView.show(view);*//*
            }
        });*/

        return convertView;
    }

    ViewHolder finalVh;

    public ViewHolder getFinalVh() {
        return finalVh;
    }

    class ViewHolder {
      //  ImageView iv_danmu_img;
      public  TextView tv;
        /*ImageView iv_danmu_good;
        TextView tv_good_num;*/


    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;

    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
