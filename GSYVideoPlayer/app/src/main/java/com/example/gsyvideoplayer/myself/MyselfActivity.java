package com.example.gsyvideoplayer.myself;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.DanmuInitCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.DanmuBean;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MyselfActivity extends AppCompatActivity {

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @BindView(R.id.detail_player)
    MySelfGSYVideoPlayer videoPlayer;

    LinearLayout danmu;
    EditText edit_danmu;
    static int count = 0;
    private boolean isPlay;
    private boolean isPause;

    private GSYVideoOptionBuilder gsyVideoOption;
    private OrientationUtils orientationUtils;
    private String url ="";
    public String getUrl(){
        return url;
    }
    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);
     //   DanmuBean danmuBean1 = new DanmuBean(5.0f,1,25,16777215,"；九年级看看\uD83D\uDE18");
        videoPlayer =  (MySelfGSYVideoPlayer) findViewById(R.id.video_player);
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
               videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);

            }
        });



        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videoPlayer.ismError()){
                    videoPlayer.setmError(true);
                }
                onBackPressed();
            }
        });
        //解决拖动视频会弹回来,因为ijk的FFMPEG对关键帧问题。
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video"); //根据媒体类型来配置
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");  // 无限读
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");  // 无限读
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"start-on-prepared",0);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"http-detect-range-support",0);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC,"skip_loop_filter",48);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC,"skip_loop_filter",8);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzemaxduration",100);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzemaxduration",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"probesize",4096);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"flush_packets",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"packet-buffering",0);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"opensles",0);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"overlay-format",IjkMediaPlayer.SDL_FCC_RV32);
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);

        GSYVideoManager.onResume(false);
        GSYVideoType.setRenderType(GSYVideoType.TEXTURE);
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
        videoPlayer.setTimeCycle(60);
        gsyVideoOption = new GSYVideoOptionBuilder();
        String temp = "https://youku.com-ok-pptv.com/20190901/6570_497d32b7/index.m3u8";
        gsyVideoOption.setUrl(temp)
                .setVideoTitle("测试视频")
                .setCacheWithPlay(false)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setDanmuCallBack(new DanmuInitCallBack(){
                    @Override
                    public void onSetDanmu(int minutes) {
                        Log.e("初始化弹幕1",minutes+"");
                       /* List<DanmuBean> danmuBeanList = new ArrayList<>();
                            DanmuBean danmuBean1 = new DanmuBean(15000,1,25,16777215,"第三条弹幕");
                            DanmuBean danmuBean2 = new DanmuBean(15000,1,25,16711935,"第四条弹幕");
                            DanmuBean danmuBean3 = new DanmuBean(15001,1,25,16711935,"第五条弹幕");
                            DanmuBean danmuBean4 = new DanmuBean(15002,1,25,16711935,"第六条弹幕");
                            danmuBeanList.add(danmuBean1);
                            danmuBeanList.add(danmuBean2);
                            danmuBeanList.add(danmuBean3);
                            danmuBeanList.add(danmuBean4);
                            videoPlayer.setDanmuBeanListAll(danmuBeanList);
                            return;
                        }*/


                  /*      DanmuBean danmuBean3 = new DanmuBean(65000,1,25,146114,"第三条弹幕");
                        DanmuBean danmuBean4 = new DanmuBean(65000,1,25, 16737996,"第四条弹幕");
                        danmuBeanList.add(danmuBean3);
                        danmuBeanList.add(danmuBean4);
                        videoPlayer.setDanmuBeanListAll(danmuBeanList);*/
                        List<DanmuBean> danmuBeanList = new ArrayList<>();
                        long time = minutes * 60 * 1000 + 5000;
                        if(minutes == 0){
                            time = 5000;
                        }
                      /*  DanmuBean danmuBean3 = new DanmuBean(time,1,25,146114,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean4 = new DanmuBean(time,1,25, 16737996,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean5 = new DanmuBean(time,1,25,1667233,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean6 = new DanmuBean(time,1,25,255255,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean7 = new DanmuBean(time,1,25,10458123,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean8 = new DanmuBean(time,1,25,16711680,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean9 = new DanmuBean(time,1,25,16711680,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean10 = new DanmuBean(time,1,25,16711680,"第"+minutes+"条弹幕");*/
                     /*   DanmuBean danmuBean3 = new DanmuBean(time,1,25,146114,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean4 = new DanmuBean(time,1,25, 16737996,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean5 = new DanmuBean(time+1000,1,25,1667233,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean6 = new DanmuBean(time+2000,1,25,255255,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean7 = new DanmuBean(time+3000,1,25,10458123,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean8 = new DanmuBean(time+4000,1,25,16711680,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean9 = new DanmuBean(time+5000,1,25,16711680,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean10 = new DanmuBean(time+6000,1,25,16711680,"第"+minutes+"条弹幕");*/

                        DanmuBean danmuBean3 = new DanmuBean(time,1,25,1,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean33 = new DanmuBean(time,1,25,1,"第33条弹幕");
                        DanmuBean danmuBean4 = new DanmuBean(time+1000,1,25, 2,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean5 = new DanmuBean(time+2000,1,25,3,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean6 = new DanmuBean(time+3000,1,25,4,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean7 = new DanmuBean(time+4000,1,25,5,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean8 = new DanmuBean(time+5000,1,25,6,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean9 = new DanmuBean(time+6000,1,25,7,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean10 = new DanmuBean(time+7000,1,25,8,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean11 = new DanmuBean(time+8000,1,25,8,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean12 = new DanmuBean(time+9000,1,25,8,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean13 = new DanmuBean(30000,1,25,8,"第n条弹幕");

                        danmuBeanList.add(danmuBean3);
                        danmuBeanList.add(danmuBean33);
                        danmuBeanList.add(danmuBean4);
                        danmuBeanList.add(danmuBean5);
                        danmuBeanList.add(danmuBean6);
                        danmuBeanList.add(danmuBean7);
                        danmuBeanList.add(danmuBean8);
                        danmuBeanList.add(danmuBean9);
                        danmuBeanList.add(danmuBean10);
                        danmuBeanList.add(danmuBean11);
                        danmuBeanList.add(danmuBean12);
                        danmuBeanList.add(danmuBean13);

                    /*    DanmuBean danmuBean3 = new DanmuBean(5000,1,25,1,"第"+minutes+"条弹幕");
                        DanmuBean danmuBean4 = new DanmuBean(18000,1,25, 2,"第"+minutes+"条弹幕");
                        danmuBeanList.add(danmuBean3);
                        danmuBeanList.add(danmuBean4);*/
                        videoPlayer.setDanmuBeanListAll(danmuBeanList);

                      /*  DanmuBean danmuBean1 = new DanmuBean(5000,1,25,16777215,"第一条弹幕");
                        DanmuBean danmuBean2 = new DanmuBean(5000,1,25,16711935,"第二条弹幕");
                        danmuBeanList.add(danmuBean1);
                        danmuBeanList.add(danmuBean2);
                        videoPlayer.setDanmuBeanListAll(danmuBeanList);*/
                    }

                    @Override
                    public void onClickSend(DanmuBean danmuBean) {
                        super.onClickSend(danmuBean);
                        Log.e("输入弹幕数据",danmuBean.getDanmuText());
                        Log.e("输入弹幕数据",danmuBean.getDisplayTime()+"");
                        Log.e("输入弹幕数据",danmuBean.getFontColor()+"");
                        Log.e("输入弹幕数据",danmuBean.getFontSize()+"");
                        Log.e("输入弹幕数据",danmuBean.getType()+"");
                    }
                })
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        Log.e("onPrepared",videoPlayer.getErrorPosition()+"");
                        videoPlayer.setSeekOnStart(videoPlayer.getErrorPosition());
                        super.onPrepared(url, objects);
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        orientationUtils.setEnable(true);
                    }
                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Log.e("点击后退","点击后退");
                        if (orientationUtils != null) {
                            Log.e("旋转","旋转");
                            orientationUtils.setEnable(false);
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            orientationUtils.setEnable(!lock);
                        }
                    }
                }).build(videoPlayer);

        videoPlayer.setUp(temp,true,"测试视频");
        videoPlayer.startPlayLogic();
    }


    @Override
    public void onBackPressed() {

        Log.e("ismError",videoPlayer.ismError()+"");
      /*  if(!videoPlayer.ismError()){
            return;
        }*/

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }





    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();

    }

    @Override
    protected void onResume(){
        getCurPlay().onVideoResume(false);
        super.onResume();
        videoPlayer.getDanmu().resume();

    }
                                                

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        /*if (isPlay) {
            getCurPlay().release();
        }*/
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        videoPlayer.releaseDanmuCallBack();
       // isDestory = true;
    }





    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }




    private void resolveNormalVideoUI() {
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
    }

    private GSYVideoPlayer getCurPlay() {

        if (videoPlayer.getFullWindowPlayer() != null) {
            return  videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
    }




}