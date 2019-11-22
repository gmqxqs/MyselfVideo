package com.shuyu.gsyvideoplayer.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.shuyu.gsyvideoplayer.R;


public class LoadingDialog2 extends Dialog {

    private Context context;
    private moe.codeest.enviews.ENDownloadView enDownloadView;
    public LoadingDialog2(Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_loading_dialog2, null);
        setContentView(view);
      /*  enDownloadView =findViewById(R.id.loading);
        if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
            enDownloadView.start();
        }*/
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}