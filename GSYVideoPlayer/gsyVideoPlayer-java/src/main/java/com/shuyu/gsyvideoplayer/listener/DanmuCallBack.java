package com.shuyu.gsyvideoplayer.listener;


import com.shuyu.gsyvideoplayer.video.DanmuBean;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;

import java.util.List;

public interface DanmuCallBack {

    public void onSetDanmu(int minutes);
    public void onClickSend(DanmuBean danmuBean);
}
