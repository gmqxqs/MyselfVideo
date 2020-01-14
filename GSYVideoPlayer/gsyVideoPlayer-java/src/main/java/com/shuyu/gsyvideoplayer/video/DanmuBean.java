package com.shuyu.gsyvideoplayer.video;

public class DanmuBean {
    private long displayTime;
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

    public long getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(long displayTime) {
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

    public DanmuBean(long displayTime,int type,int fontSize, long fontColor,String danmuText){
        this.displayTime = displayTime;
        this.type = type;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.danmuText = danmuText;
    }

    @Override
    public String toString() {
        return "DanmuBean{" +
                "displayTime=" + displayTime +
                ", type=" + type +
                ", fontSize=" + fontSize +
                ", fontColor=" + fontColor +
                ", danmuText='" + danmuText + '\'' +
                '}';
    }
}
