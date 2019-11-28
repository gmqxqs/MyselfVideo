package com.shuyu.gsyvideoplayer.video;

public class DanmuBean {
    private float displayTime;
    private int type;
    private int fontSize;
    private long fontColor;
    private String danmuText;

    public String getDanmuText() {
        return danmuText;
    }

    public void setDanmuText(String danmuText) {
        this.danmuText = danmuText;
    }

    public float getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(float displayTime) {
        this.displayTime = displayTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public long getFontColor() {
        return fontColor;
    }

    public void setFontColor(long fontColor) {
        this.fontColor = fontColor;
    }

    public DanmuBean(){

    }

    public DanmuBean( float displayTime,int type,int fontSize, long fontColor,String danmuText){
        this.displayTime = displayTime;
        this.type = type;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.danmuText = danmuText;
    }

}
