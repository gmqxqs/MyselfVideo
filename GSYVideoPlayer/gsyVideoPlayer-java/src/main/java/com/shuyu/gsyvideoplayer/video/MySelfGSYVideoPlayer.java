package com.shuyu.gsyvideoplayer.video;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.MyselfView.BatteryView;

import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;

import com.shuyu.gsyvideoplayer.utils.Md5Utils;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.codeest.enviews.ENDownloadView;
import moe.codeest.enviews.ENPlayView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.showNavKey;
import static com.shuyu.gsyvideoplayer.utils.CommonUtil.showSupportActionBar;

public class MySelfGSYVideoPlayer extends StandardGSYVideoPlayer implements SeekBar.OnSeekBarChangeListener {
    private static final int msgKey1 = 1;
    protected  int count = 0;
    private  Context contextFirst;
    public MySelfGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
        this.contextFirst = context;
    }

    public MySelfGSYVideoPlayer(Context context) {
        super(context);
        this.contextFirst = context;
    }

    public MySelfGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.contextFirst = context;
    }



    protected  TextView mSystemTime;
    protected LinearLayout mTimeandbarray;
    protected  LinearLayout controllerbottom;
    protected  ImageView playstart;
    protected  ImageView playbottom;
    protected  ImageView mBarrage;
    protected  ImageView mShare;
    protected  TextView mCurrentbottom;
    protected  TextView mTotalbottom;
    protected  ImageView mPan;
    protected  View mBatteryView;
    protected  ImageView newstart;
    protected  ImageView startPlay;
    protected  LinearLayout replay;
    protected  TextView replay_text;
    protected ProgressBar bottom_progressbar;
    protected  LinearLayout layout_bottom;
    protected  TextView batteryText;
    protected BatteryView batteryView;
    protected  LinearLayout mDanmu;
    protected  EditText edit_danmu;
    protected List<GSYVideoModel> mUriList = new ArrayList<>();
    protected boolean isDown = false;
    protected boolean isNormal = false;
    protected boolean isComplete = false;
    protected boolean isAdModel = false;
    protected View mJumpAd;
    protected TextView mADTime;
    protected boolean isFirstPrepared = false;
    public boolean isImageAd = false;

    protected ImageView mAdImageView;
    protected  WebView webView;
    protected  TextView mAdimage_skip;
    protected  FrameLayout mAdFrameLayout;
    protected  ImageView mAdClose;
    protected  ImageView mAd;
    public Bitmap bmp;                     //bitmap图片对象
    public int primaryWidth;               //原图片宽
    public int primaryHeight;              //原图片高
    public double scaleWidth, scaleHeight; //高宽比例
    public LinearLayout adLinearLayout;

    //片头视频广告
    protected String videoAdUrl;
    public void setVideoAdUrl(String videoAdUrl){
        this.videoAdUrl = videoAdUrl;
    }
    public String getVideoAdUrl(){
        return videoAdUrl;
    }
    protected  String pauseAdImageUrl;
    public void setPauseAdImageUrl(String pauseAdImageUrl){
        this.pauseAdImageUrl = pauseAdImageUrl;
    }
    public String getPauseAdImageUrl(){
        return  pauseAdImageUrl;
    }
    public ImageView getMadImageView(){
        return mAdImageView;
    }

    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<>();
    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> getUrls(){
        return urls;
    }




    /**
     * 继承后重写可替换为你需要的布局
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.myself;
    }
    public BatteryView getBatteryView(){
        return  batteryView;
    }
    public TextView getBatteryText(){
        return batteryText;
    }
    public TextView getTitleTextView() {
        return mTitleTextView;
    }
    private int batteryLevel;

    public FrameLayout surface_container;
    public FrameLayout getSurface(){
        return surface_container;
    }




    protected void init(final Context context) {
        super.init(context);
        // mWidgetContainer = (ViewGroup) findViewById(R.id.widget_container);
        mJumpAd = findViewById(R.id.jump_ad);
        mADTime = (TextView) findViewById(R.id.ad_time);
        surface_container = findViewById(R.id.surface_container);
        batteryView = findViewById(R.id.battery);
        layout_bottom = findViewById(R.id.all);
        mPan = findViewById(R.id.pan);
        newstart = findViewById(R.id.newstart);
        startPlay = findViewById(R.id.startPlay);
        mSystemTime = findViewById(R.id.systemtime);
        mTimeandbarray = findViewById(R.id.timeandbarray);
        controllerbottom = findViewById(R.id.controllerbottom);
        playstart = findViewById(R.id.playstart);
        playbottom = findViewById(R.id.playbottom);
        mBarrage = findViewById(R.id.barrage);
        mShare = findViewById(R.id.share);
        mCurrentbottom = findViewById(R.id.currentbottom);
        mTotalbottom = findViewById(R.id.totalbottom);
        replay = findViewById(R.id.replay);
        replay_text = findViewById(R.id.replay_text);
        bottom_progressbar = findViewById(R.id.bottom_progressbar);
      /*  mAdImageView = findViewById(R.id.adImage);
        mAdFrameLayout = findViewById(R.id.adFrameLayout);
        mAdClose = findViewById(R.id.ad_close);
        mAdClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdFrameLayout.setVisibility(GONE);
            }
        });
        mAd = findViewById(R.id.ad);*/
        mAdimage_skip = findViewById(R.id.adimage_skip);
        adLinearLayout = findViewById(R.id.adLinearLayout);
        mDanmu = findViewById(R.id.danmu);
        edit_danmu = findViewById(R.id.edit_danmu);
        edit_danmu.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            /*    //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    String text = edit_danmu.getText().toString();
                    if(text.length() > 0 ){//判断文本框是否有文字，如果有就去掉最后一位
                        String newText = text.substring(0, text.length() - 1);
                        edit_danmu.setText(newText);
                        edit_danmu.setSelection(newText.length());//设置焦点在最后
                    };
                }*/
                return false;
            }
        });

        new TimeThread().start();
        mDanmu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // System.out.println("点击弹幕");
            }
        });


        mBarrage.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(count % 2 == 0){
                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_nl);
                } else {
                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_hl);
                }
                count++;
            }
        });
        playstart.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                clickStartIcon();
            }
        });
        playbottom.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                clickStartIcon();
            }
        });
        newstart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // showWifiDialog();
                //startPlayLogic();

                playNextUrl(0);
                // startButtonLogic();
            }
        });

        startPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentState == CURRENT_STATE_PAUSE){
                    getGSYVideoManager().start();
                    setStateAndUi(CURRENT_STATE_PLAYING);

                }
            }
        });


    }

    public  ImageView getAdClose(){
        return  mAdClose;
    }

    public  ImageView getAd(){
        return  mAd;
    }


    public ImageView getPlaystart(){
        return playstart;
    }

    public TextView getCurrentTimeTextView(){
        return mCurrentTimeTextView;
    }

    public  TextView getTotalTimeTextView(){
        return mTotalTimeTextView;
    }


    public ImageView getLockScreen(){
        return mLockScreen;
    }


    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }



    GSYBaseVideoPlayer gsyBaseVideoPlayer;
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {

        gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);

        return gsyBaseVideoPlayer;
    }
    /**
     * 全屏的UI逻辑
     */
    private void initFullUI(MySelfGSYVideoPlayer mySelfGSYVideoPlayer) {

        if (mBottomProgressDrawable != null) {
            mySelfGSYVideoPlayer.setBottomProgressBarDrawable(mBottomProgressDrawable);
        }

        if (mBottomShowProgressDrawable != null && mBottomShowProgressThumbDrawable != null) {
            mySelfGSYVideoPlayer.setBottomShowProgressBarDrawable(mBottomShowProgressDrawable,
                    mBottomShowProgressThumbDrawable);
        }

        if (mVolumeProgressDrawable != null) {
            mySelfGSYVideoPlayer.setDialogVolumeProgressBar(mVolumeProgressDrawable);
        }

        if (mDialogProgressBarDrawable != null) {
            mySelfGSYVideoPlayer.setDialogProgressBar(mDialogProgressBarDrawable);
        }

        if (mDialogProgressHighLightColor >= 0 && mDialogProgressNormalColor >= 0) {
            mySelfGSYVideoPlayer.setDialogProgressColor(mDialogProgressHighLightColor, mDialogProgressNormalColor);
        }
    }

    public void seekTo(long position) {
        try {
            if (getGSYVideoManager() != null && position > 0) {
                System.out.println("seek2:"+position);
                getGSYVideoManager().seekTo(position);
            }
            if (getGSYVideoManager() != null && position <=  0) {
                getGSYVideoManager().seekTo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        System.out.println("开始拖动进度条");
        startProgressTimer();
        mHadSeekTouch = true;

       /* if (getGSYVideoManager() != null && mHadPlay) {
            try {
                //int progress = seekBar.getProgress();
                int time = seekBar.getProgress() * getDuration() / 100;
                System.out.println("time4:" + time);
                //  int time = progress  * getDuration() / 100;
                getGSYVideoManager().seekTo(time);
                mBottomProgressBar.setProgress(seekBar.getProgress());
                mProgressBar.setProgress(seekBar.getProgress());
                setTextAndProgress(seekBar.getProgress());
            } catch (Exception e) {
                Debuger.printfWarning(e.toString());
            }
        };*/

    }
    int errorPosition = 0;
    int errortime = 0;
    /***
     * 拖动进度条
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        System.out.println("停止拖动进度条");
        if (mVideoAllCallBack != null && isCurrentMediaListener()) {
            if (isIfCurrentIsFullscreen()) {
                Debuger.printfLog("onClickSeekbarFullscreen");
                mVideoAllCallBack.onClickSeekbarFullscreen(mOriginUrl, mTitle, this);
            } else {
                Debuger.printfLog("onClickSeekbar");
                mVideoAllCallBack.onClickSeekbar(mOriginUrl, mTitle, this);
            }
        }
        if (getGSYVideoManager() != null && mHadPlay) {
            try {
                //int progress = seekBar.getProgress();
                int time = seekBar.getProgress() * getDuration() / 100;
                errortime = seekBar.getProgress() * getDuration() / 100;
                System.out.println("time3:" + time);
                //  int time = progress  * getDuration() / 100;
                getGSYVideoManager().seekTo(time);
                mBottomProgressBar.setProgress(seekBar.getProgress());
                mProgressBar.setProgress(seekBar.getProgress());
                errorPosition = seekBar.getProgress();
            } catch (Exception e) {
                Debuger.printfWarning(e.toString());
            }
        }
        mHadSeekTouch = false;
        cancelProgressTimer();
        // resetProgressAndTime();
    }

    @Override
    public  void  onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            System.out.println("进度条改变");
        if (getGSYVideoManager() != null && mHadPlay) {
            try {
                //int progress = seekBar.getProgress();
                int time = seekBar.getProgress() * getDuration() / 100;

                mBottomProgressBar.setProgress(seekBar.getProgress());
                System.out.println("seekBarProgress():"+seekBar.getProgress());
                mProgressBar.setProgress(seekBar.getProgress());
               // setTextAndProgress(seekBar.getProgress());
                mCurrentTimeTextView.setText(CommonUtil.stringForTime(time));
                mCurrentbottom.setText(CommonUtil.stringForTime(time));

            } catch (Exception e) {
                Debuger.printfWarning(e.toString());
            }
        }
    }




    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    @Override
    public void onPrepared() {
        super.onPrepared();


    }
    @Override
    public int getFullId() {
        return GSYVideoManager.FULLSCREEN_ID;
    }
  /*  @Override
    public boolean dispatchKeyEvent( KeyEvent event) {
        super.dispatchKeyShortcutEvent(event);
        if ( event.getKeyCode() == KeyEvent.KEYCODE_DEL){//监听到删除按钮被按下
            System.out.println("删除");
            String text = edit_danmu.getText().toString();
            if(text.length() > 0 ){//判断文本框是否有文字，如果有就去掉最后一位
                String newText = text.substring(0, text.length() - 1);
                edit_danmu.setText(newText);
                edit_danmu.setSelection(newText.length());//设置焦点在最后
            };
        }
        return super.dispatchKeyShortcutEvent(event);
    }*/





    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    mSystemTime.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };

    protected void touchSurfaceDown(float x, float y) {
        mTouchingProgressBar = true;
        mDownX = x;
        mDownY = y;
        mMoveY = 0;
        mChangeVolume = false;
        mChangePosition = false;
        mShowVKey = false;
        mBrightness = false;
        mFirstTouch = true;
    }




    public boolean setUp(String url, boolean cacheWithPlay, File cachePath, String title){
        if (super.setUp(url, cacheWithPlay, cachePath, title)) {

            if (title != null && mTitleTextView != null) {
                mTitleTextView.setText(title);
                mTitleTextView.setVisibility(GONE);
            }

            if (mIfCurrentIsFullscreen) {
                System.out.println("全屏");
                if (mFullscreenButton != null)
                    mFullscreenButton.setImageResource(getShrinkImageRes());
                mFullscreenButton.setVisibility(GONE);
                mTitleTextView.setVisibility(VISIBLE);
                mLockScreen.setVisibility(VISIBLE);
                mShare.setVisibility(GONE);
                mBarrage.setVisibility(GONE);
                mTimeandbarray.setVisibility(VISIBLE);
                playstart.setVisibility(GONE);
                mCurrentTimeTextView.setVisibility(GONE);
                mTotalTimeTextView.setVisibility(GONE);
                mPan.setVisibility(GONE);
                bottom_progressbar.setVisibility(VISIBLE);
                controllerbottom.setVisibility(VISIBLE);
                if(isAdModel){
                    mADTime.setVisibility(VISIBLE);
                    mJumpAd.setVisibility(VISIBLE);
                }

            } else {
                System.out.println("非全屏");
                if (mFullscreenButton != null)
                    mFullscreenButton.setImageResource(getEnlargeImageRes());
                mTitleTextView.setVisibility(GONE);
                mLockScreen.setVisibility(GONE);
                mShare.setVisibility(GONE);
                mBarrage.setVisibility(GONE);
                controllerbottom.setVisibility(GONE);
                playstart.setVisibility(VISIBLE);
                mCurrentTimeTextView.setVisibility(VISIBLE);
                mTotalTimeTextView.setVisibility(VISIBLE);
                mTimeandbarray.setVisibility(GONE);
                mPan.setVisibility(GONE);
                bottom_progressbar.setVisibility(GONE);

            }

            return true;
        }

        return false;
    }




    public void resetProgressAndTime() {
        if (mProgressBar == null || mTotalTimeTextView == null || mCurrentTimeTextView == null) {
            return;
        }
        mProgressBar.setProgress(0);
        mProgressBar.setSecondaryProgress(0);
        mCurrentTimeTextView.setText(CommonUtil.stringForTime(0));
        mTotalTimeTextView.setText(CommonUtil.stringForTime(0));
        mCurrentbottom.setText(CommonUtil.stringForTime(0));
        mTotalbottom.setText("/"+CommonUtil.stringForTime(0));

        if (mBottomProgressBar != null) {
            mBottomProgressBar.setProgress(0);
            mBottomProgressBar.setSecondaryProgress(0);
        }

    }




    @Override
    protected void hideAllWidget() {
        if (isFirstPrepared && isAdModel) {
            return;
        }
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(controllerbottom, INVISIBLE);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(newstart, INVISIBLE);
        setViewShowState(replay_text, INVISIBLE);
        setViewShowState(mTimeandbarray,GONE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    /*@Override
    protected  void  lockTouchLogic(){
    }
*/
    public int getEnlargeImageRes() {
        if (mEnlargeImageRes == -1) {
            return R.drawable.vedio_full_screen_icon;
        }
        return mEnlargeImageRes;
    }
    /**
     * 处理锁屏屏幕触摸逻辑
     */
    protected void lockTouchLogic() {
        if (mLockCurScreen) {
            mLockScreen.setImageResource(R.drawable.vedio_lock_no_icon);
            mLockCurScreen = false;
        } else {
            mLockScreen.setImageResource(R.drawable.vedio_lock_icon);
            mLockCurScreen = true;
            hideAllWidget();
        }
    }
    /**
     * 设置右下角 显示切换到全屏 的按键资源
     * 必须在setUp之前设置
     * 不设置使用默认
     */
    public void setEnlargeImageRes(int mEnlargeImageRes) {
        this.mEnlargeImageRes = mEnlargeImageRes;
    }

    public int getShrinkImageRes() {
        if (mShrinkImageRes == -1) {
            return R.drawable.video_shrink;
        }
        return mShrinkImageRes;
    }

    /**
     * 设置右下角 显示退出全屏 的按键资源
     * 必须在setUp之前设置
     * 不设置使用默认
     */
    public void setShrinkImageRes(int mShrinkImageRes) {
        this.mShrinkImageRes = mShrinkImageRes;
    }

    /**
     * 亮度、进度、音频
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
       /* if(isAdModel){
            onClickUiToggle();
            return false;
        }*/
        int id = v.getId();
        float x = event.getX();
        float y = event.getY();

        if (mIfCurrentIsFullscreen && mLockCurScreen) {
            onClickUiToggle();
            startDismissControlViewTimer();
            return true;
        }

        if (id == R.id.fullscreen) {
            return false;
        }

        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    touchSurfaceDown(x, y);
                    cancelProgressTimer();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if ((mIfCurrentIsFullscreen && mIsTouchWigetFull)
                            || (mIsTouchWiget && !mIfCurrentIsFullscreen)) {
                        if (!mChangePosition && !mChangeVolume && !mBrightness) {
                            touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
                        }
                    }

                    touchSurfaceMove(deltaX, deltaY, y);
                    break;
                case MotionEvent.ACTION_UP:
                   // startDismissControlViewTimer();
                    touchSurfaceUp();
                    //   Debuger.printfLog(GSYVideoControlView.this.hashCode() + "------------------------------ surface_container ACTION_UP");
                    startProgressTimer();
                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (mHideKey && mShowVKey) {
                        return true;
                    }
                    break;
            }
            gestureDetector.onTouchEvent(event);
        } else if (id == R.id.progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    cancelProgressTimer();
                case MotionEvent.ACTION_MOVE:
                    startProgressTimer();
                   /* ViewParent vpdown = getParent();
                    while (vpdown != null) {
                        vpdown.requestDisallowInterceptTouchEvent(true);
                        vpdown = vpdown.getParent();
                    }*/

                    break;
                case MotionEvent.ACTION_UP:
                    startProgressTimer();
                    startDismissControlViewTimer();
                    //  Debuger.printfLog(GSYVideoControlView.this.hashCode() + "------------------------------ progress ACTION_UP");

                    break; /*  ViewParent vpup = getParent();
                    while (vpup != null) {
                        vpup.requestDisallowInterceptTouchEvent(false);
                        vpup = vpup.getParent();
                    }
                    mBrightnessData = -1f;*/
            }
        }

        return false;
    }
    /**
     * 广告期间不需要触摸
     */
    @Override
    protected void touchSurfaceUp() {
        if (mChangePosition && isAdModel) {
            return;
        }
        super.touchSurfaceUp();

    }
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        if (mChangePosition && isAdModel) {
            return;
        } else {
            super.touchSurfaceMove(deltaX, deltaY, y);
        }
    }

    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        if ((absDeltaX > mThreshold || absDeltaY > mThreshold)) {
            int screenWidth = CommonUtil.getScreenWidth(getContext());
            if (isAdModel && absDeltaX >= mThreshold && Math.abs(screenWidth - mDownX) > mSeekEndOffset) {
                //防止全屏虚拟按键
                mChangePosition = true;
                mDownPosition = getCurrentPositionWhenPlaying();
            } else {
                super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
            }
        }
    }


    protected void showAllWidget() {
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(controllerbottom, VISIBLE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(newstart, VISIBLE);
        setViewShowState(replay_text, VISIBLE);
        setViewShowState(mTimeandbarray,VISIBLE);
        //  mOrientationUtils.setEnable(true);
    }


   /* public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        return gsyBaseVideoPlayer;
    }
*/



    @Override
    protected void changeUiToNormal() {
        Debuger.printfLog("changeUiToNormal");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        updateStartImage();
       /* if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    @Override
    protected void changeUiToPreparingShow() {
        //   Debuger.printfLog("changeUiToPreparingShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);
        if (mLoadingProgressBar instanceof ENDownloadView) {
            ENDownloadView enDownloadView = (ENDownloadView) mLoadingProgressBar;
            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
        updateStartImage();
        upPlayImage();
    }



    @Override
    protected void changeUiToPlayingShow() {
        Debuger.printfLog("changeUiToPlayingShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);

        //setViewShowState(mStartButton,GONE);
        setViewShowState(mTimeandbarray,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(startPlay, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);

        System.out.println("getNetSpeed():" + getNetSpeed());

        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
        } else{
            setViewShowState(controllerbottom, GONE);
        }

       /* if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && !isAdModel) ? VISIBLE : GONE);
        setViewShowState(controllerbottom, (mIfCurrentIsFullscreen && !isAdModel) ? VISIBLE : GONE);
        setViewShowState(mAdImageView,GONE);


        updateStartImage();
        upPlayImage();
    }

    @Override
    protected void changeUiToPauseShow() {
        Debuger.printfLog("changeUiToPauseShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
      //  setViewShowState(mStartButton,VISIBLE);
        setViewShowState(mTimeandbarray,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(startPlay, VISIBLE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
       /* if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
        } else{
            setViewShowState(controllerbottom, GONE);
        }*/
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && !isAdModel) ? VISIBLE : GONE);
        setViewShowState(controllerbottom, (mIfCurrentIsFullscreen && !isAdModel) ? VISIBLE : GONE);
        setViewShowState(mAdImageView,VISIBLE);
        setViewShowState(mAdFrameLayout,VISIBLE);

        updateStartImage();
        updatePauseCover();
        upPlayImage();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        Debuger.printfLog("changeUiToPlayingBufferingShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
            setViewShowState(mTimeandbarray, VISIBLE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(mTimeandbarray, GONE);
        }

        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);
        if (mLoadingProgressBar instanceof ENDownloadView) {
            ENDownloadView enDownloadView = (ENDownloadView) mLoadingProgressBar;
            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
        upPlayImage();
    }

    @Override
    protected void changeUiToCompleteShow() {
        Debuger.printfLog("changeUiToCompleteShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, VISIBLE);

        setViewShowState(replay, VISIBLE);
        setViewShowState(replay_text, VISIBLE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        // updateStartImage();

        if(isDown){
           isComplete = true;
        }

        upPlayImage();
    }

    @Override
    protected void changeUiToError() {
        Debuger.printfLog("changeUiToError");
        String time = mCurrentTimeTextView.getText().toString();
        System.out.println("time:"+time);

        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, VISIBLE);
        int position = getCurrentPositionWhenPlaying();
        System.out.println("错误的位置:"+position);
        System.out.println("errorPosition:"+ errortime);
        if(isDown){
            System.out.println("isDown:"+ isDown);
            //setViewShowState(newstart,GONE);
            if(NetworkUtils.isConnected(contextFirst)){
                playNextUrl(errortime);
                isDown = false;
            } else{
                releaseVideos();
                return;
            }

        } else{
            System.out.println("!isDown:"+ isDown);
            setViewShowState(newstart,GONE);
            playNextUrl(errortime);
            isDown = true;
        }

        setViewShowState(replay, VISIBLE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();*/
        upPlayImage();
    }

    protected void changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear");

        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);

        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    protected void changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear");
        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);
/*        if (mLoadingProgressBar instanceof ENDownloadView) {
            ENDownloadView enDownloadView = (ENDownloadView) mLoadingProgressBar;
            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
        updateStartImage();*/
        upPlayImage();
    }

    //将其他组件隐藏起来
    protected void changeUiToClear() {
        Debuger.printfLog("changeUiToClear");
        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);

      /*  if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    protected void changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear");
        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, VISIBLE);
        setViewShowState(replay, VISIBLE);
        setViewShowState(replay_text, VISIBLE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
     /*   if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();*/
        upPlayImage();
    }


    protected void upPlayImage(){

        if (mCurrentState == CURRENT_STATE_PLAYING){
            playstart.setImageResource(R.drawable.vedio_stop_icon);
            playbottom.setImageResource(R.drawable.vedio_stop_icon);
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            playstart.setImageResource(R.drawable.vedio_play_icon);
            playbottom.setImageResource(R.drawable.vedio_play_icon);
        } else {
            playstart.setImageResource(R.drawable.vedio_play_icon);
            playbottom.setImageResource(R.drawable.vedio_play_icon);
        }

    }

    /**
     * 定义开始按键显示
     */
    protected void updateStartImage() {
        if (mStartButton instanceof ENPlayView) {
            ENPlayView enPlayView = (ENPlayView) mStartButton;
            enPlayView.setDuration(500);
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                enPlayView.play();
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                enPlayView.pause();
            } else {
                enPlayView.pause();
            }
        } else if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.video_click_pause_selector);
                // playbottom.setImageResource(R.drawable.vedio_stop_icon);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.video_click_error_selector);
                // playbottom.setImageResource(R.drawable.vedio_play_icon);
            } else {
                imageView.setImageResource(R.drawable.video_click_play_selector);
                // playbottom.setImageResource(R.drawable.vedio_play_icon);
            }
        }


    }



    /**
     * 点击触摸显示和隐藏逻辑
     */

    @Override
    protected void onClickUiToggle() {
        if (isAdModel) {
            System.out.println("广告视频跳转:"+isAdModel);
            System.out.println("videoAdUrl:"+getVideoAdUrl());
            Intent intent = new Intent(CommonUtil.getActivityContext(contextFirst), VideoAdWebViewActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("videoAdUrl",getVideoAdUrl());
            intent.putExtras(bundle);
            CommonUtil.getActivityContext(contextFirst).startActivity(intent);
            return;
        }

        if (mIfCurrentIsFullscreen && mLockCurScreen) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }

        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPrepareingClear();
                } else {
                    changeUiToPreparingShow();
                }
            }

            //视频正在播f放状态
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingShow();
                }
            }

        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPauseClear();
                } else {
                    changeUiToPauseShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {

            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToCompleteClear();
                } else {
                    changeUiToCompleteShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingBufferingClear();
                } else {
                    changeUiToPlayingBufferingShow();
                }
            }
        }
    }

    /*protected int  positionCurrent = 0;
    protected boolean taskOk = false;*/
    public void setTextAndProgress(int secProgress) {
      /*  if(positionCurrent == -1){
            taskOk = true;
           // return;
        } else{
            taskOk = false;
        }*/
        int positionCurrent = getCurrentPositionWhenPlaying();
    /*    if(taskOk){
            positionCurrent = 0;
            cancelProgressTimer();
        }*/
        System.out.println("positionText:"+positionCurrent);
        int duration = getDuration();
        System.out.println("durationText:"+duration);
        int progress = (int) (positionCurrent * 100 / (duration == 0 ? 1 : duration));
        System.out.println("progressText:"+progress);
        if(positionCurrent <= 0 ){
            setProgressAndTime(0, 0, positionCurrent, duration);
        }else {
            setProgressAndTime(progress, secProgress, positionCurrent, duration);
            //  setProgressAndTime(progress, secProgress, position, duration);
        }
    }

    public void  setProgressAndVideo(int position){

        int duration = getDuration();
        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
        if(position <= 0){
            seekTo(0);
            mProgressBar.setProgress(0);
            mBottomProgressBar.setProgress(0);
        } else {
            seekTo(position);
            mProgressBar.setProgress(progress);
            mBottomProgressBar.setProgress(progress);
        }
    }

    @Override
    public void onVideoPause() {
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            mPauseBeforePrepared = true;
        }
        try {
            if (getGSYVideoManager() != null &&
                    getGSYVideoManager().isPlaying()) {
                setStateAndUi(CURRENT_STATE_PAUSE);
           //     mCurrentPosition = getGSYVideoManager().getCurrentPosition();
                if (getGSYVideoManager() != null)
                    getGSYVideoManager().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作
     */
    @Override
    public void onVideoResume(boolean seek) {
        mPauseBeforePrepared = false;
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            try {
                if (mCurrentPosition >= 0 && getGSYVideoManager() != null) {
                    if (seek) {
                       // getGSYVideoManager().seekTo(mCurrentPosition);
                    }
                    getGSYVideoManager().start();
                    setStateAndUi(CURRENT_STATE_PLAYING);
                    if (mAudioManager != null && !mReleaseWhenLossAudio) {
                        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    }
                    mCurrentPosition = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {

        if (mGSYVideoProgressListener != null && (mCurrentState == CURRENT_STATE_PLAYING) ) {
            //\ startProgressTimer();
            mGSYVideoProgressListener.onProgress(progress, secProgress, currentTime, totalTime);
        }

        if (mGSYVideoProgressListener != null && (mCurrentState == CURRENT_STATE_PAUSE) ) {

            mGSYVideoProgressListener.onProgress(progress, secProgress, currentTime, totalTime);

        }

        if (mProgressBar == null || mTotalTimeTextView == null || mCurrentTimeTextView == null) {
            return;
        }

        if(mHadSeekTouch) {
            return;
        }

        if (!mTouchingProgressBar) {
            if (progress != 0) {
                mProgressBar.setProgress(progress);
            }
            if(progress <= 0 ){
                mProgressBar.setProgress(0);
            }

        }


        if (getGSYVideoManager().getBufferedPercentage() > 0) {
            secProgress = getGSYVideoManager().getBufferedPercentage();
        }

        if (secProgress > 94) secProgress = 100;
             setSecondaryProgress(secProgress);
        mTotalTimeTextView.setText(CommonUtil.stringForTime(totalTime));
        mTotalbottom.setText("/"+ CommonUtil.stringForTime(totalTime));
        // if (currentTime > 0)
        mCurrentTimeTextView.setText(CommonUtil.stringForTime(currentTime));
        mCurrentbottom.setText(CommonUtil.stringForTime(currentTime));
        if (mBottomProgressBar != null) {
            if (progress != 0) mBottomProgressBar.setProgress(progress);
            setSecondaryProgress(secProgress);
        }
        if (mADTime != null && currentTime > 0) {
            int totalSeconds = totalTime / 1000;
            int currentSeconds = currentTime / 1000;
            mADTime.setText("" + (totalSeconds - currentSeconds));
        }


    }


    public void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {

        if (oldF != null && oldF.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) oldF.getParent();
            vp.removeView(viewGroup);
        }
        mCurrentState = getGSYVideoManager().getLastState();
        if (gsyVideoPlayer != null) {
            cloneParams(gsyVideoPlayer, this);
        }
        getGSYVideoManager().setListener(getGSYVideoManager().lastListener());
        getGSYVideoManager().setLastListener(null);
        setStateAndUi(mCurrentState);
        addTextureView();
        mSaveChangeViewTIme = System.currentTimeMillis();
        if (mVideoAllCallBack != null) {
            Debuger.printfError("onQuitFullscreen");
            mVideoAllCallBack.onQuitFullscreen(mOriginUrl, mTitle, this);
        }
        mIfCurrentIsFullscreen = false;
        if (mHideKey) {
            showNavKey(mContext, mSystemUiVisibility);
        }showSupportActionBar(mContext, mActionBar, mStatusBar);
        if(getFullscreenButton() != null) {
            getFullscreenButton().setImageResource(getEnlargeImageRes());
        }
    }

    @Override
    public void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        final   MySelfGSYVideoPlayer sf = (MySelfGSYVideoPlayer) from;
        MySelfGSYVideoPlayer st = (MySelfGSYVideoPlayer) to;
        st.surface_container =sf.surface_container;
        st.mUriList = sf.mUriList;
        st.urls = sf.urls;
        st.isDown = sf.isDown;
        st.isAdModel = sf.isAdModel;
        st.videoAdUrl = sf.getVideoAdUrl();
        st.isFirstPrepared = sf.isFirstPrepared;
        st.pauseAdImageUrl = sf.getPauseAdImageUrl();
        if(sf.bmp != null){
            st.bmp = sf.bmp;
            st.getAdClose().setVisibility(View.VISIBLE);
            st.getAd().setVisibility(View.VISIBLE);
            //    st.bmp = sf.mAdImageView.getBackground();
            //原始大小
            primaryWidth = st.bmp.getWidth();
            primaryHeight = st.bmp.getHeight();
            //初始比例为1
            scaleWidth = scaleHeight = 1;

            //st.mAdImageView.setImageBitmap(bmp);
            // scale(2, 2);
            scaleWidth = scaleWidth * 1.5;  //缩放到原来的*倍
            scaleHeight = scaleHeight * 1.5;

            Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
            matrix.postScale((float)scaleWidth, (float)scaleHeight);    //设置高宽比例（三维矩阵）

            //缩放后的BitMap
            Bitmap newBmp = Bitmap.createBitmap(st.bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

            //重新设置BitMap
            st.mAdImageView.setImageBitmap(newBmp);
            //scale(0.8,0.8);
            displayAd();
            st.mAdImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommonUtil.getActivityContext(contextFirst), PauseImageAdWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("pauseImageAdUrl",getPauseAdImageUrl());
                    intent.putExtras(bundle);
                    CommonUtil.getActivityContext(contextFirst).startActivity(intent);
                }
            });
        }


        st.changeAdUIState();
    }


    public void displayAd(){

        //原始大小
        primaryWidth = bmp.getWidth();
        primaryHeight = bmp.getHeight();
        //初始比例为1
        scaleWidth = scaleHeight = 1;
        //st.mAdImageView.setImageBitmap(bmp);
        // scale(2, 2);
        scaleWidth = scaleWidth * 1.0;  //缩放到原来的*倍
        scaleHeight = scaleHeight * 1.0;

        Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
        matrix.postScale((float)scaleWidth, (float)scaleHeight);    //设置高宽比例（三维矩阵）

        //缩放后的BitMap
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

        //重新设置BitMap
        mAdImageView.setImageBitmap(newBmp);
    }
    /**
     * 缩放
     */
    private void scale(double  scale_width, double scale_height) {

        //这种方法，有点不好是：如果图片大小超出屏幕会报错。
        if((scale_width > 1 && scaleWidth * primaryWidth >= CommonUtil.getActivityContext(contextFirst).getWindowManager().getDefaultDisplay().getWidth())
                || (scale_width > 1 && scale_height * primaryHeight >= CommonUtil.getActivityContext(contextFirst).getWindowManager().getDefaultDisplay().getHeight())){
            //bt_bigger.setEnabled(false);
        }else {
            //bt_bigger.setEnabled(true);
        }



        scaleWidth = scaleWidth * scale_width;  //缩放到原来的*倍
        scaleHeight = scaleHeight * scale_height;

        Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
        matrix.postScale((float)scaleWidth, (float)scaleHeight);    //设置高宽比例（三维矩阵）

        //缩放后的BitMap
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

        //重新设置BitMap
        mAdImageView.setImageBitmap(newBmp);


    }

    /**
     * 根据是否广告url修改ui显示状态
     */
    public void changeAdUIState() {

        if (mJumpAd != null) {
            mJumpAd.setVisibility((isFirstPrepared && isAdModel) ? VISIBLE : GONE);
        }

        if(adLinearLayout != null){
            adLinearLayout.setVisibility((isFirstPrepared && isAdModel) ? VISIBLE : GONE);
        }

        if (mADTime != null) {
            mADTime.setVisibility((isFirstPrepared && isAdModel) ? VISIBLE : GONE);
        }

        /*if (mWidgetContainer != null) {
            mWidgetContainer.setVisibility((isFirstPrepared && isAdModel) ? GONE : VISIBLE);
        }*/
       /* if (layout_bottom != null) {
            int color = (isFirstPrepared && isAdModel) ? Color.TRANSPARENT : getContext().getResources().getColor(R.color.bottom_container_bg);
            layout_bottom.setBackgroundColor(color);
        }*/


        if(playstart != null){
            playstart.setVisibility((isFirstPrepared && isAdModel ) || isImageAd  || mIfCurrentIsFullscreen  ? GONE : VISIBLE);
        }

        if(controllerbottom!= null){
            controllerbottom.setVisibility((isFirstPrepared && isAdModel ) || isImageAd  || !mIfCurrentIsFullscreen ? GONE : VISIBLE);
        }

        if( mFullscreenButton!= null){
            mFullscreenButton.setVisibility(  mIfCurrentIsFullscreen ? GONE : VISIBLE);
        }

        if(mLockScreen!= null){
            mLockScreen.setVisibility((isFirstPrepared && isAdModel) || isImageAd || !mIfCurrentIsFullscreen  ? GONE : VISIBLE);
        }

        if (mCurrentTimeTextView != null) {
            mCurrentTimeTextView.setVisibility(((isFirstPrepared && isAdModel ) || isImageAd ) || (mIfCurrentIsFullscreen && isImageAd ) || mIfCurrentIsFullscreen ? GONE : VISIBLE);

        }

        if (mTotalTimeTextView != null) {
            mTotalTimeTextView.setVisibility(((isFirstPrepared && isAdModel)  || isImageAd  )|| (mIfCurrentIsFullscreen && isImageAd ) || mIfCurrentIsFullscreen ?  GONE : VISIBLE);
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility((isFirstPrepared && isAdModel) || isImageAd  ? INVISIBLE : VISIBLE);
            mProgressBar.setEnabled(!(isFirstPrepared && isAdModel));
        }
    }


    protected void loopSetProgressAndTime() {
        if (mProgressBar == null || mTotalTimeTextView == null || mCurrentTimeTextView == null) {
            return;
        }
        mProgressBar.setProgress(0);
        mProgressBar.setSecondaryProgress(0);
        mCurrentTimeTextView.setText(CommonUtil.stringForTime(0));
        mCurrentbottom.setText(CommonUtil.stringForTime(0));
        if (mBottomProgressBar != null)
            mBottomProgressBar.setProgress(0);
    }
    /**
     * 双击暂停/播放
     * 如果不需要，重载为空方法即可
     */
    @Override
    protected void touchDoubleUp() {
       /* if (isAdModel) {
            Intent intent = new Intent(CommonUtil.getAppCompActivity(contextFirst), PauseImageAdWebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("adUrl",getAdUrl());
            intent.putExtras(bundle);
            CommonUtil.getAppCompActivity(contextFirst).startActivity(intent);
            return;
        }*/
        if (mIfCurrentIsFullscreen && mLockCurScreen) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        super.clickStartIcon();
    }



    protected void clickStartIcon() {
        if (TextUtils.isEmpty(mUrl)) {
            Debuger.printfError("********" + getResources().getString(R.string.no_url));
            //Toast.makeText(getActivityContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if ( mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR ) {
            if (isShowNetConfirm()) {
                showWifiDialog();
                return;
            }
            startButtonLogic();
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            try {
                playstart.setImageResource(R.drawable.vedio_play_icon);
                playbottom.setImageResource(R.drawable.vedio_play_icon);
                newstart.setImageResource(R.drawable.vedio_play_icon);
                onVideoPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStateAndUi(CURRENT_STATE_PAUSE);
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                if (mIfCurrentIsFullscreen) {
                    Debuger.printfLog("onClickStopFullscreen");
                    mVideoAllCallBack.onClickStopFullscreen(mOriginUrl, mTitle, this);
                } else {
                    Debuger.printfLog("onClickStop");
                    mVideoAllCallBack.onClickStop(mOriginUrl, mTitle, this);
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                if (mIfCurrentIsFullscreen) {
                    Debuger.printfLog("onClickResumeFullscreen");
                    mVideoAllCallBack.onClickResumeFullscreen(mOriginUrl, mTitle, this);
                } else {
                    Debuger.printfLog("onClickResume");
                    mVideoAllCallBack.onClickResume(mOriginUrl, mTitle, this);
                }
            }

            if (!mHadPlay && !mStartAfterPrepared) {
                startAfterPrepared();
            }

            try {
                playstart.setImageResource(R.drawable.vedio_stop_icon);
                playbottom.setImageResource(R.drawable.vedio_stop_icon);
                newstart.setImageResource(R.drawable.vedio_stop_icon);
                getGSYVideoManager().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStateAndUi(CURRENT_STATE_PLAYING);
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            layout_bottom.setVisibility(GONE);
            startButtonLogic();
        }
    }

    public ArrayList<String> subString(String url){
        ArrayList<String> list = new ArrayList<>();
        String staticUrl = "";
        String httpUrl = "";
        if(url.startsWith("static")){
            staticUrl = url.substring(url.indexOf("/")+1,url.indexOf("http"));
        /*    String configRoot = contextFirst.getExternalFilesDir(null).getPath();
            staticUrl = configRoot + staticUrl;*/
            System.out.println("staticUrl:" +staticUrl);
            if(!staticUrl.equals("")){
                list.add(staticUrl);
                httpUrl = url.substring(url.indexOf("http"),url.length());
                if(!httpUrl.equals("")){
                    list.add(httpUrl);
                }
            }
        }
        if(url.startsWith("http")){
            list.add(url);
        }


        return list;
    }

   /* public String playUrl(String originUrl) {
        String url = originUrl;
        if (url.startsWith("http") && !url.contains("127.0.0.1") && url.contains(".m3u8")) {
            String endUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            endUrl = endUrl.substring(0,endUrl.lastIndexOf("?"));
            System.out.println("endUrl:" + endUrl);
            String tempUrl = Md5Utils.md5(url);
            System.out.println("tempUrl:" + tempUrl);
            String configRoot = contextFirst.getExternalFilesDir(null).getPath();
            System.out.println("configRoot:" + configRoot);
            String testUrl = "/storage/emulated/0/Android/data/vip.maogou.app/files/2/7/dbd63fba03e0e661eb93d556bc920f/27dbd63fba03e0e661eb93d556bc920f";
            String folder = testUrl;
           // String folder = configRoot + "/"+tempUrl;
            System.out.println("folder:" + folder);
            File file = new File(folder);
            if (file.exists()) {
                try {
                    url = folder + "/" + endUrl;
                    // url = folder;
                    System.out.println("downurl:" + url);
                    File downFile = new File(url);
                    if (downFile.exists()) {
                        System.out.println("下载文件地址:" + url);
                        System.out.println("新:" + Uri.parse(url));
                        return url;
                    } else {
                        System.out.println("下载文件不存在");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("不存在");
            }
        }
        return  originUrl;

    }*/


    public static class GSYADVideoModel extends GSYVideoModel {
        /**
         * 正常
         */
        public static int TYPE_NORMAL = 0;

        /**
         * 广告
         */
        public static int TYPE_AD = 1;

        /**
         * 本地
         */
        public static int TYPE_DOWN = 2;

        /**
         * 类型
         */
        private int mType = TYPE_NORMAL;

        /**
         * 是否跳过
         */
        private boolean isSkip;

        /**
         * @param url   播放url
         * @param title 标题
         * @param type  类型 广告还是正常类型
         */
        public GSYADVideoModel(String url, String title, int type) {
            this(url, title, type, false);
        }

        /**
         * @param url    播放url
         * @param title  标题
         * @param type   类型 广告还是正常类型
         * @param isSkip 是否跳过
         */
        public GSYADVideoModel(String url, String title, int type, boolean isSkip) {
            super(url, title);
            this.mType = type;
            this.isSkip = isSkip;
        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            this.mType = type;
        }

        public boolean isSkip() {
            return isSkip;
        }

        public void setSkip(boolean skip) {
            isSkip = skip;
        }

        @Override
        public String toString() {
            return "GSYADVideoModel{" +
                    "mType=" + mType +
                    ", isSkip=" + isSkip +
                    ", mUrl=" + mUrl +
                    '}';
        }
    }



    /**
     * 如果需要片头广告的，请用setAdUp
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @return
     */

    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position) {
        return setUp(url, cacheWithPlay, position, null);
    }

    /**
     * 如果需要片头广告的，请用setAdUp
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */

    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath) {
        return setUp(url, cacheWithPlay, position, cachePath, new HashMap<String, String>());
    }

    /**
     * 如果需要片头广告的，请用setAdUp
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @return
     */

    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData) {
        return setUp(url, cacheWithPlay, position, cachePath, mapHeadData, true);
    }


    /**
     * 如果需要片头广告的，请用setAdUp
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @param changeState   切换的时候释放surface
     * @return
     */
    protected boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData, boolean changeState) {
        System.out.println("positionUrl:" + position);
        GSYVideoModel gsyVideoModel = url.get(position);
        if (gsyVideoModel instanceof MySelfGSYVideoPlayer.GSYADVideoModel) {
            MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;
            System.out.println("url.size():" + url.size());
            if (gsyadVideoModel.isSkip() && position < (url.size() - 1)) {
                System.out.println("广告");
                return setUp(url, cacheWithPlay, position + 1, cachePath, mapHeadData, changeState);
            }


            isAdModel = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
            isDown = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
        }

        mUriList = url;
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);
        System.out.println("gsyVideoModel.getUrl():" + gsyVideoModel.getUrl());
        if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
            mTitleTextView.setText(gsyVideoModel.getTitle());
        }
        return set;
    }

    /******************对外接口*******************/

    /**
     * 带片头广告的，setAdUp
     *
     * @param url
     * @param cacheWithPlay
     * @param position
     * @return
     */
    public boolean setAdUp(ArrayList<GSYADVideoModel> url, boolean cacheWithPlay, int position) {
        return setUp((ArrayList<GSYVideoModel>) url.clone(), cacheWithPlay, position);
    }

    /**
     * 带片头广告的，setAdUp
     *
     * @param url
     * @param cacheWithPlay
     * @param position
     * @param cachePath
     * @return
     */
    public boolean setAdUp(ArrayList<GSYADVideoModel> url, boolean cacheWithPlay, int position, File cachePath) {
        return setUp((ArrayList<GSYVideoModel>) url.clone(), cacheWithPlay, position, cachePath);
    }

    /**
     * 带片头广告的，setAdUp
     *
     * @param url
     * @param cacheWithPlay
     * @param position
     * @param cachePath
     * @param mapHeadData
     * @return
     */
    public boolean setAdUp(ArrayList<GSYADVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData) {
        return setUp((ArrayList<GSYVideoModel>) url.clone(), cacheWithPlay, position, cachePath, mapHeadData);
    }
    /**
     * 播放下一集
     *
     * @return true表示还有下一集
     */
    public boolean playNextUrl(int position) {
        if (isDown){
            if (mPlayPosition < (mUriList.size() - 1)) {
                if(!isComplete){
                    mPlayPosition += 1;
                }

                System.out.println("mPlayPositionDown:" + mPlayPosition);
                System.out.println("错误的网络位置:" + position);
                GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
                MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;
                isDown = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
                isNormal = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_NORMAL);
                isAdModel = (gsyadVideoModel.getType() == MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_AD);
                System.out.println("是不是下载视频:" + isDown);
                mSaveChangeViewTIme = 0;
                setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
                if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                    mTitleTextView.setText(gsyVideoModel.getTitle());
                }
                System.out.println("当前播的本地视频位置:" + position);
                setSeekOnStart(position);
                startPlayLogic();
                return true;
            }

        } else if(!isDown) {
            if(mUriList.size() == 1 ){
                mPlayPosition = 0;
            } else{
                mPlayPosition -= 1;
            }

            System.out.println("mPlayPositionDown:" + mPlayPosition);
            System.out.println("错误的网络位置:" + position);
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;
            isDown = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
            isNormal = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_NORMAL);
            isAdModel = (gsyadVideoModel.getType() == MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_AD);
            System.out.println("是不是下载视频:" + isDown);
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            System.out.println("当前播的本地视频位置:" + position);
            setSeekOnStart(position);
            startPlayLogic();
            return true;
        }
    /*        System.out.println("startTime:" + time);
            VideoOptionModel videoOptionModel =
                    new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "seek-at-start", time);
            List<VideoOptionModel> list = new ArrayList<>();
            list.add(videoOptionModel);
            GSYVideoManager.instance().setOptionModelList(list);*//*
            System.out.println("当前播的位置:"+position);
            setSeekOnStart(position);
            startPlayLogic();
            return true;
        }*/

        return false;
    }



}

