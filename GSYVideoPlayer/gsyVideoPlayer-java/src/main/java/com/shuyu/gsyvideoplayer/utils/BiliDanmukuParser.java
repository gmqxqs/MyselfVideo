/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shuyu.gsyvideoplayer.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuyu.gsyvideoplayer.video.DanmuBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuFactory;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

public class BiliDanmukuParser extends BaseDanmakuParser {

  static {
    System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
  }

  protected float mDispScaleX;
  protected float mDispScaleY;

  public Danmakus result = new Danmakus();

  public BaseDanmaku item = null;

  public long tempTime;

  public List<DanmuBean> danmuBeanListAll = new ArrayList<>();

  @Override
  public Danmakus parse() {

    if (mDataSource != null) {
      AndroidFileSource source = (AndroidFileSource) mDataSource;

      try {
        InputStreamReader isr = new InputStreamReader(source.data());
        StringBuilder sb = new StringBuilder();
        int ch = 0;
        while((ch = isr.read())!=-1){
          sb.append((char) ch);
        }
        String str = sb.toString();
        Log.e("dataSourcetest",str);
        Gson gson = new Gson();
        List<DanmuBean> danmuBeanList = gson.fromJson(str, new TypeToken<List<DanmuBean>>(){}.
                getType());
        for(int i = 0; i < danmuBeanList.size(); i++){

          Log.e("dataSourceText",danmuBeanList.get(i).getDanmuText()+"");
         // long time = (long) (danmuBeanList.get(i).getDisplayTime() * 1000); // 出现时间
          long time = (long) (danmuBeanList.get(i).getDisplayTime()); // 出现时间
          for(DanmuBean danmuBean: danmuBeanListAll){
            if(danmuBean != null){
              if(time == danmuBean.getDisplayTime()){
                time = time + 1;
              }
            }
          }
          Log.e("testTime",time+"");
          int type = danmuBeanList.get(i).getType(); // 弹幕类型
          int textSize = danmuBeanList.get(i).getFontSize(); // 字体大小
          int color = (int) ((0x00000000ff000000 | danmuBeanList.get(i).getFontColor()) & 0x00000000ffffffff); // 颜色
          String text = danmuBeanList.get(i).getDanmuText();
          // int poolType = Integer.parseInt(values[5]); // 弹幕池类型（忽略
          item = mContext.mDanmakuFactory.createDanmaku(type, mContext);
          if (item != null) {
            item.setTime(time);
            item.textSize = textSize * (mDispDensity - 0.6f);
            item.textColor = color;
            item.priority = 8;
            item.textShadowColor = color <= Color.BLACK ? Color.WHITE : Color.BLACK;
            item.text = text;
            //DanmuBean(long displayTime,int type,int fontSize, long fontColor,String danmuText)
            DanmuBean danmuBean = new DanmuBean(time,type,textSize,color,text);
            danmuBeanListAll.add(danmuBean);
          }
          item.setTimer(mTimer);
          item.flags = mContext.mGlobalFlagValues;

          result.addItem(item);
          item = null;

        }

        return result;
      }catch (IOException e){
        e.printStackTrace();
      }



    }

    return null;
  }


  private boolean isPercentageNumber(float number) {
    return number >= 0f && number <= 1f;
  }

  @Override
  public BaseDanmakuParser setDisplayer(IDisplayer disp) {
    super.setDisplayer(disp);
    mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH;
    mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT;
    return this;
  }
}
