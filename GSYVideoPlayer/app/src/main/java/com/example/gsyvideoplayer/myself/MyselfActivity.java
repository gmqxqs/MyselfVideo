package com.example.gsyvideoplayer.myself;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.danikula.videocache.HttpProxyCacheServer;
import com.example.gsyvideoplayer.R;
import com.example.gsyvideoplayer.video.LandLayoutVideo;
import com.google.android.exoplayer2.SeekParameters;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MyselfActivity extends AppCompatActivity {

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @BindView(R.id.detail_player)
    MySelfGSYVideoPlayer videoPlayer;

    LinearLayout danmu;
    EditText edit_danmu;

    private boolean isPlay;
    private boolean isPause;


    private OrientationUtils orientationUtils;
    private String url ="";
    public String getUrl(){
        return url;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);
        videoPlayer =  (MySelfGSYVideoPlayer) findViewById(R.id.video_player);
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        //detailPlayer.setThumbImageView(imageView);
        // resolveNormalVideoUI();
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
       /* Map<String, String> header = new HashMap<>();
        header.put("ee", "33");
        header.put("allowCrossProtocolRedirects", "true");*/
        //   File file = new File("file:///storage/emulated/0/Android/data/com.example.gsyvideoplayer/cache/video-cache/");
        url = "https://youku.com-iqiyi.net/20190303/21817_a6cd96be/index.m3u8";
      //  url = "http://static_api.maogou.vip/9e9e1b7ea4e250af8fe1f1865650d42b/a036cac2531b35692e8975f252229fcf.m3u8";
      //  url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
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
                onBackPressed();
            }
        });
        //解决拖动视频会弹回来,因为ijk的FFMPEG对关键帧问题。
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);

  /*      videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"media",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec",1);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"videotoolbox",0);
        list.add (videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video"); //根据媒体类型来配置
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        list.add(videoOptionModel);
        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        list.add(videoOptionModel);
        videoOptionModel =new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1);
        list.add(videoOptionModel);
*/
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
       videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzemaxduration",2000000);
       list.add(videoOptionModel);
       videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"probesize",4096);
       list.add(videoOptionModel);
       videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"flush_packets",1);
       list.add(videoOptionModel);
       videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"packet-buffering",0);
       list.add(videoOptionModel);
       videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop",1);
       list.add(videoOptionModel);
       GSYVideoManager.instance().setOptionModelList(list);
       PlayerFactory.setPlayManager(Exo2PlayerManager.class);
     //   CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        GSYVideoManager.onResume(false);
    //    GSYVideoType.setRenderType(GSYVideoType.SUFRACE);
    //    GSYVideoType.setRenderType(GSYVideoType.GLSURFACE);
        GSYVideoType.setRenderType(GSYVideoType.TEXTURE);
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);

        // GSYVideoType.enableMediaCodec();
        //GSYVideoType.enableMediaCodecTexture();

        //   IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);
        videoPlayer.setUp(url,true,"");
        videoPlayer.startPlayLogic();
    }


    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
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