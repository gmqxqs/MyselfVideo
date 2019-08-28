
package com.example.gsyvideoplayer.myself;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.danikula.videocache.HttpProxyCacheServer;
import com.example.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.MyWebViewActivity;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
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
        ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = videoPlayer.getUrls();
        //广告1
        urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
                "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_AD));
        //正式内容
        urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("https://apissources.bamasoso.com/video/rvz3YaO8Gbxq/0e5fdab9c4935093877390e6db94443c.mp4",
                "测试视频", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));
    //    videoPlayer.setAdUp(urls, true, 0);
        videoPlayer.setAutoFullWithSize(true);
        videoPlayer.setShowFullAnimation(false);
        videoPlayer.getSurface().setBackground(getResources().getDrawable(R.drawable.xxx1));

        videoPlayer.measure(0,0);
     /*   width = videoPlayer.getLayoutParams().width;
        height = videoPlayer.getLayoutParams().height;*/
        width = videoPlayer.getMeasuredWidth(); ;
     //   height = videoPlayer.getMeasuredHeight();
        height = videoPlayer.getLayoutParams().height;
        System.out.println("width:" + width);
        System.out.println("height:" + height);
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
               // videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
              //  videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
                //videoPlayer.measure(0,0);

                if(videoPlayer.isAdImage){
                    System.out.println("图片广告");
                    CommonUtil.hideSupportActionBar(MyselfActivity.this,true,true);
                   /* DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.heightPixels);
                    videoPlayer.getSurface().setLayoutParams(params);*/

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
                    lp.setMargins(0, 0, 0, 0);
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                   // lp.gravity = Gravity.CENTER;
                    videoPlayer.setLayoutParams(lp);
                    videoPlayer.setIfCurrentIsFullscreen(true);



                } else {
                    videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
                }



                // videoPlayer.getMadImageView().setLayoutParams(new RelativeLayout.LayoutParams(300,236));

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
        //   videoPlayer.getMadImageView().setBackground(getResources().getDrawable(R.drawable.vedio_stop_ad));
        videoPlayer.bmp = BitmapFactory.decodeResource(this.getResources(),R.drawable.vedio_stop_ad);
        videoPlayer.displayAd();
        videoPlayer.setAdUrl("http://www.baidu.com/");
        //点击暂停广告图片跳转
        videoPlayer.getMadImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyselfActivity.this, MyWebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle = new Bundle();
                bundle.putString("adUrl",videoPlayer.getAdUrl());
                intent.putExtras(bundle);
                startActivity(intent);
                //JumpUtils.gotoControl(MyselfActivity.this);
            }
        });

        //解决拖动视频会弹回来,因为ijk的FFMPEG对关键帧问题。
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);

        GSYVideoManager.instance().setOptionModelList(list);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        // PlayerFactory.setPlayManager(IjkPlayerManager.class);
        ProxyCacheManager proxyCacheManager = new ProxyCacheManager();
        HttpProxyCacheServer proxy =  proxyCacheManager.getProxy(this);
        Random random = new Random();
        int result=random.nextInt(10);
        result += 1;
        //videoPlayer.setAdUp(urls, true, 0);
       //videoPlayer.getSurface().setBackground(getResources().getDrawable(R.drawable.xxx1));


    /*    if (proxy.isCached(url)){
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.startPlayLogic();
        } else {
            videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.showWifiDialog();
        }*/



        if(result % 2 == 0){
            //videoPlayer.setBackground(getResources().getDrawable(R.drawable.xxx1));
            urls.remove(urls.get(0));
            videoPlayer.isAdImage = true;
            timeCount.start();
           // videoPlayer.setAdUp(urls, true, 0);
            videoPlayer.getSurface().setBackground(getResources().getDrawable(R.drawable.xxx1));


         /*   if (proxy.isCached(url)){
                videoPlayer.setAdUp(urls, true, 0);
                videoPlayer.startPlayLogic();
            } else {
                videoPlayer.setAdUp(urls, true, 0);
                videoPlayer.showWifiDialog();
            }*/
        } else {
            videoPlayer.setAdUp(urls, true, 0);
           /* if (proxy.isCached(url)){
                videoPlayer.startPlayLogic();
            } else {
                videoPlayer.showWifiDialog();
            }*/
            videoPlayer.startPlayLogic();
        }

        /*if (proxy.isCached(url)){
            videoPlayer.startPlayLogic();
        } else {
            videoPlayer.showWifiDialog();
        }*/


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
            System.out.println("全屏返回");
            if(!videoPlayer.isAdImage){
                videoPlayer.getLockScreen().setVisibility(View.GONE);
                videoPlayer.getControllerbottom().setVisibility(View.GONE);
                videoPlayer.getPlaystart().setVisibility(View.GONE);
                videoPlayer.getCurrentTimeTextView().setVisibility(View.VISIBLE);
                videoPlayer.getTotalTimeTextView().setVisibility(View.VISIBLE);
                videoPlayer.getFullscreenButton().setVisibility(View.VISIBLE);
            }

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            lp.height = height;
            lp.width = width;
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
