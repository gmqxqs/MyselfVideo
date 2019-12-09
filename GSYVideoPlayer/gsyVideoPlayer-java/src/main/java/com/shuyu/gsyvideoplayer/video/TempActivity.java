package com.shuyu.gsyvideoplayer.video;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TempActivity extends  Activity{

    public static Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = TempActivity.this;
    }


    public void print() {

    }


}
