package com.shuyu.gsyvideoplayer.listener;

public class ProgressEvent {
    private int minute;

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public ProgressEvent(int minute) {
        this.minute = minute;
    }
}
