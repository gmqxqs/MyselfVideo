
package com.example.gsyvideoplayer.myself;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;


import com.danikula.videocache.HttpProxyCacheServer;
import com.example.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.PauseImageAdWebViewActivity;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MyselfActivity extends AppCompatActivity {

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @BindView(R.id.detail_player)
    MySelfGSYVideoPlayer videoPlayer;


    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;
    private String url ="";
    public String getUrl(){
        return  url;
    }
    private  WebView webView ;
    MySelfGSYVideoPlayer.TimeCount timeCount;
    int width;
    int height;
    ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_myself);
        webView = new WebView(MyselfActivity.this);
        videoPlayer =  (MySelfGSYVideoPlayer) findViewById(R.id.video_player);
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        timeCount = videoPlayer.new TimeCount(10000,1000);
        urls = videoPlayer.getUrls();
        //广告1
     /*   urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
                "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_AD));*/
        //正式内容
       /* urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
                "测试视频", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));*/
        urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("https://letv.com-v-letv.com/20180802/7097_e793eb8c/index.m3u8",
                "测试视频", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));
        //    videoPlayer.setAdUp(urls, true, 0);
        videoPlayer.setAutoFullWithSize(true);
        videoPlayer.setShowFullAnimation(false);
        //  videoPlayer.getSurface().setBackground(getResources().getDrawable(R.drawable.xxx1));
        videoPlayer.measure(0,0);
        width = videoPlayer.getMeasuredWidth();
        height = videoPlayer.getLayoutParams().height;
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                if(videoPlayer.isImageAd){
                 /*   CommonUtil.hideSupportActionBar(MyselfActivity.this,true,true);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
                    lp.setMargins(0, 0, 0, 0);
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    *//*System.out.println("lp.height:" + lp.height);
                    System.out.println("lp.width:" + lp.width);*//*
                    videoPlayer.setLayoutParams(lp);
                    videoPlayer.setIfCurrentIsFullscreen(true);*/
                    videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
                } else {
                    videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
                }
            }
        });
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  videoPlayer.changeAdUIState();
                onBackPressed();
                // videoPlayer.getMadImageView().setLayoutParams(new RelativeLayout.LayoutParams(150,118));
            }
        });
        videoPlayer.getAdimage_skip().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCount.onFinish();
                timeCount.cancel();
            }
        });

     //   videoPlayer.setImageAdUrl("http://www.baidu.com/");
        videoPlayer.setVideoAdUrl("http://xm.ganji.com/");
        //设置暂停图片广告的跳转地址
        //videoPlayer.setPauseAdImageUrl("https://www.suning.com/");
        //点击暂停广告图片跳转
        videoPlayer.getMadImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyselfActivity.this, PauseImageAdWebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle = new Bundle();
                bundle.putString("pauseImageAdUrl",videoPlayer.getPauseAdImageUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //解决拖动视频会弹回来,因为ijk的FFMPEG对关键帧问题。
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
    //   CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
     /*   String cachepath = Environment.getExternalStorageDirectory() + "/tsvideo/cache";
        File file = new File(cachepath);
        if (!file.exists()) {
            file.mkdir();
        }*/
        // PlayerFactory.setPlayManager(IjkPlayerManager.class);
        ProxyCacheManager proxyCacheManager = new ProxyCacheManager();
        HttpProxyCacheServer proxy =  proxyCacheManager.getProxy(this);

        proxy.isCached(url);
        /*if (proxy.isCached(url)){
            videoPlayer.startPlayLogic();
        } else {
            videoPlayer.showWifiDialog();
        }*/

        // videoPlayer.startPlayLogic();
        startPlay();
    }


    //设置暂停图片的方法
    public void setPauseImage(){
        if(videoPlayer != null){
            videoPlayer.getAdClose().setVisibility(View.VISIBLE);
            videoPlayer.getAd().setVisibility(View.VISIBLE);
            videoPlayer.bmp = BitmapFactory.decodeResource(this.getResources(),R.drawable.vedio_stop_ad);
            videoPlayer.displayAd();
        }
    }

    //设置片头图片的方法
    public void setImageAd(){
        videoPlayer.isImageAd = true;
        videoPlayer.getSurface().setBackground(getResources().getDrawable(com.shuyu.gsyvideoplayer.R.drawable.xxx1));
    }



    public void startPlay(){
        //暂停广告，片头视频广告，片头图片广告的连接地址都为空
        if(TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            if(urls.size() >= 2){
                urls.remove(urls.get(0));
            }
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.startPlayLogic();
        }
        //片头视频广告，片头图片广告的连接地址都为空 ,暂停广告的连接地址不为空
        if(TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && !TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            setPauseImage();
            if(urls.size() >= 2){
                urls.remove(urls.get(0));
            }
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.startPlayLogic();
        }
        //暂停广告，片头图片广告的连接地址都为空 ,片头视频广告的连接地址不为空
        if(TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && !TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.startPlayLogic();
        }

        //暂停广告，片头视频广告的连接地址都为空 ,片头图片广告的连接地址不为空
        if(!TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            if(urls.size() >= 2){
                urls.remove(urls.get(0));
            }
            setImageAd();
            timeCount.start();
        }

        //暂停广告，片头视频广告的连接地址都不为空 ,片头图片广告的连接地址为空
        if(TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && !TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && !TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            setPauseImage();
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.startPlayLogic();
        }

        //暂停广告,片头图片广告的连接地址为不空，片头视频广告的连接地址为空
        if(!TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && !TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            setPauseImage();
            if(urls.size() >= 2){
                urls.remove(urls.get(0));
            }
            setImageAd();
            timeCount.start();
        }

        //片头图片广告，片头视频广告的连接地址不为空，暂停广告的连接地址为空
        if(!TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && !TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            setImageAd();
            Random random = new Random();
            int result=random.nextInt(10);
            result += 1;
            if(result % 2 == 0){
                if(urls.size() >= 2){
                    urls.remove(urls.get(0));
                }
                videoPlayer.isImageAd = true;
              //  videoPlayer.setBackground(getResources().getDrawable(com.shuyu.gsyvideoplayer.R.drawable.xxx1));
                timeCount.start();
                // videoPlayer.setAdUp(urls, true, 0);

            } else {
                videoPlayer.isImageAd = false;
                videoPlayer.setAdUp(urls, true, 0);
                videoPlayer.startPlayLogic();
            }
        }


        //片头图片广告，片头视频广告,暂停广告的连接地址不为空
        if(!TextUtils.isEmpty(videoPlayer.getImageAdUrl()) && !TextUtils.isEmpty(videoPlayer.getVideoAdUrl()) && !TextUtils.isEmpty(videoPlayer.getPauseAdImageUrl())){
            setImageAd();
            setPauseImage();
            Random random = new Random();
            int result=random.nextInt(10);
            result += 1;
            if(result % 2 == 0){
                if(urls.size() >= 2){
                    urls.remove(urls.get(0));
                }
                videoPlayer.isImageAd = true;
              //  videoPlayer.setBackground(getResources().getDrawable(com.shuyu.gsyvideoplayer.R.drawable.xxx1));
                timeCount.start();
                // videoPlayer.setAdUp(urls, true, 0);

            } else {
                videoPlayer.isImageAd = false;
                videoPlayer.setAdUp(urls, true, 0);
                videoPlayer.startPlayLogic();
            }
        }






    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }

        if(videoPlayer.isIfCurrentIsFullscreen()){
            if(!videoPlayer.isImageAd){
                videoPlayer.getLockScreen().setVisibility(View.GONE);
                videoPlayer.getControllerbottom().setVisibility(View.GONE);
                videoPlayer.getPlaystart().setVisibility(View.VISIBLE);
                videoPlayer.getCurrentTimeTextView().setVisibility(View.VISIBLE);
                videoPlayer.getTotalTimeTextView().setVisibility(View.VISIBLE);
                videoPlayer.getFullscreenButton().setVisibility(View.VISIBLE);
            }
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            lp.height = height;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            // lp.gravity = Gravity.CENTER;
            videoPlayer.setLayoutParams(lp);
            //videoPlayer.backToNormal();
            videoPlayer.setIfCurrentIsFullscreen(false);

            return;
        } else{
            if(timeCount != null){
                timeCount.cancel();
            }
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
        // System.out.println("破坏");
        super.onDestroy();
       /* if(timeCount != null){
            timeCount.cancel();
        }*/
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    protected  void onRestart(){
        super.onRestart();
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
            // videoPlayer.setAutoFullWithSize(true);
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

