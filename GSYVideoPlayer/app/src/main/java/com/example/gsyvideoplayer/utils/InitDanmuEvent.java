package com.example.gsyvideoplayer.utils;

import com.shuyu.gsyvideoplayer.video.DanmuBean;

import java.util.List;

public class InitDanmuEvent {
    private List<DanmuBean> list;
    private String fileName;

    public List<DanmuBean> getList() {
        return list;
    }

    public void setList(List<DanmuBean> list) {
        this.list = list;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InitDanmuEvent(List<DanmuBean> list, String fileName) {
        this.list = list;
        this.fileName = fileName;
    }
}
