package com.example.gsyvideoplayer.myself;

import android.content.Context;
import android.content.Intent;
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
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.MySelfGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.PauseImageAdWebViewActivity;
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
    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<>();
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

       // File file = new File("file:///storage/emulated/0/Android/data/com.example.gsyvideoplayer/cache/video-cache/");
      // url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
      //  String tempUrl = "static://2/7/2d09d6d01e841029aaf8a0d80a6bdf29/index.m3u8http://v1.bjssmd.net/20190715/yXfbhmdr/index.m3u8";
      //  String tempUrl = "static://storage/emulated/0/Android/data/com.example.gsyvideoplayer/files/d/1/62afc49f55985d7a550edc9f2864aa/d162afc49f55985d7a550edc9f2864aa/index.m3u8https://youku.com-ok-pptv.com/20190901/6570_497d32b7/index.m3u8";
        String tempUrl = "https://youku.com-ok-pptv.com/20190901/6570_497d32b7/index.m3u8";
        ArrayList<String> listUrl = new ArrayList<String>();
        listUrl = videoPlayer.subString(tempUrl);
        System.out.println("list:" + listUrl);
   //     String url = "https://apd-1f573461e2849c2dff8de8011848088b.v.smtcdns.com/moviets.tc.qq.com/A-pfo_cZrdx-q2vFBqnpS4xcOM5Jb9Q8r8GgdIs8r8P0/uwMROfz2r5zAoaQXGdGnC2df644E7D3uP8M8pmtgwsRK9nEL/h3Ir07Asx9wg0_yDFrgKal0z4RSuVdCBIljI9eWOBAODkcEcQByGBAJMGF42Hkd48Gmf8rSFFPW5hh53XONL7LmdZpg9INujHPIJA-Y8gtK6W5P2XvMvBxKABOCWelv-mebHpqBSSTBUz6uDLGuHFhLeXt8dESEIn7_tnDs0CpU/z00310ev4nq.321002.ts.m3u8?ver=4";

        urls = videoPlayer.getUrls();
        System.out.println("listUrl.size():" + listUrl.size());
        urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
                "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_AD));
        if(listUrl.size() >= 2){
            urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel(listUrl.get(0),
                    "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_DOWN));
            urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel(listUrl.get(1),
                    "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));
        } else{
            urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel(listUrl.get(0),
                    "", MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));
        }

        videoPlayer.setAutoFullWithSize(true);
        videoPlayer.setShowFullAnimation(false);
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

        //   videoPlayer.setImageAdUrl("http://www.baidu.com/");
        videoPlayer.setVideoAdUrl("http://xm.ganji.com/");
        //设置暂停图片广告的跳转地址
        videoPlayer.setPauseAdImageUrl("https://www.suning.com/");
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
        System.out.println("urls:" + urls);
        videoPlayer.setAdUp(urls,true,0);
       //   videoPlayer.setUp("http://v1.bjssmd.net/20190715/yXfbhmdr/index.m3u8",true,null,"");


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

    public void  titleClick(View v){
        onBackPressed();
    }

    public void toFull(View v){
        orientationUtils.resolveByClick();
        //    orientationUtils.setRotateWithSystem(false);
        videoPlayer.startWindowFullscreen(MyselfActivity.this, true, true);
    }


}