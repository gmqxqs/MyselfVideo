package com.shuyu.gsyvideoplayer.listener;

import com.shuyu.gsyvideoplayer.video.DanmuBean;

import java.util.List;

public class InitDanmuEvent {
   List<DanmuBean> danmuBeanList;

    public List<DanmuBean> getDanmuBeanList() {
        return danmuBeanList;
    }

    public void setDanmuBeanList(List<DanmuBean> danmuBeanList) {
        this.danmuBeanList = danmuBeanList;
    }

    public InitDanmuEvent(List<DanmuBean> danmuBeanList) {
        this.danmuBeanList = danmuBeanList;
    }
}
