package com.shuyu.gsyvideoplayer.listener;


import com.shuyu.gsyvideoplayer.screening.bean.DeviceInfo;
import com.shuyu.gsyvideoplayer.video.DanmuBean;

import java.util.List;

public interface TouPinCallBack {

    public void onCastScreen(List<DeviceInfo> deviceInfos);
}
