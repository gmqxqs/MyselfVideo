package com.shuyu.gsyvideoplayer.listener;

import com.shuyu.gsyvideoplayer.video.DanmuBean;

public class SendDanmuEvent {
    private DanmuBean danmuBean;

    public DanmuBean getDanmuBean() {
        return danmuBean;
    }

    public void setDanmuBean(DanmuBean danmuBean) {
        this.danmuBean = danmuBean;
    }

    public SendDanmuEvent(DanmuBean danmuBean) {
        this.danmuBean = danmuBean;
    }
}
