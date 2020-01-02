package com.shuyu.gsyvideoplayer.event;

import com.shuyu.gsyvideoplayer.screening.bean.DeviceInfo;

import java.util.List;

public class ScreenEvent {
   private  List<DeviceInfo> deviceInfoList;

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<DeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public ScreenEvent(List<DeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

}
