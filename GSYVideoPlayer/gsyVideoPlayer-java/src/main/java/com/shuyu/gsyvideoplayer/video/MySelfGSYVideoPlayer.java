package com.shuyu.gsyvideoplayer.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.Adapter.MyDanmuAdapter;
import com.shuyu.gsyvideoplayer.Adapter.MyDanmuModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.MyselfView.BatteryView;
import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.danmu.DanmuView;
import com.shuyu.gsyvideoplayer.listener.DanmuCallBack;
import com.shuyu.gsyvideoplayer.listener.InitDanmuEvent;
import com.shuyu.gsyvideoplayer.listener.ProgressEvent;
import com.shuyu.gsyvideoplayer.listener.SendDanmuEvent;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.BiliDanmukuParser;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.view.DanamakuAdapter;
import com.shuyu.gsyvideoplayer.view.LoadingDialog2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlSerializer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import moe.codeest.enviews.ENPlayView;

public class MySelfGSYVideoPlayer extends StandardGSYVideoPlayer implements SeekBar.OnSeekBarChangeListener {
    private static final int msgKey1 = 1;
    protected  int count = 0;
    private  Context contextFirst;
    private static boolean mStopTrack,mStartTrack;
    private  static List<DanmuBean> danmuBeanList = new ArrayList<>();
    public void setDanmuBeanList(List<DanmuBean> danmuBeanList) {
        this.danmuBeanList = danmuBeanList;
    }
    private static  List<DanmuBean> danmuBeanListAll = new ArrayList<>();
    public void  setDanmuBeanListAll(List<DanmuBean> danmuBeanList) {
        danmuCount = 0;
        this.danmuBeanList = danmuBeanList;
        for(DanmuBean danmuBean : danmuBeanList){
           danmuBeanListAll.add(danmuBean);
        }


        for(int i = 0; i < danmuBeanList.size(); i++){
            for(int j = i+1; j < danmuBeanList.size(); j++){
                if(danmuBeanList.get(j).getDisplayTime() == danmuBeanList.get(i).getDisplayTime()){
                    danmuBeanList.get(j).setDisplayTime(danmuBeanList.get(j).getDisplayTime()+1);
                }
            }

        }

        mStartTrack = false;
        requestDanmu++;


    }
    static int danmuCount = 0;
    static int danmuCount2 = 0;
    private static  String fileName;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
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

  // 弹幕
    private DanmuView danmu;

    public DanmuView getDanmu() {
        return danmu;
    }

    private MyDanmuAdapter adapter;
    private long mDanmakuStartSeekPosition = -1;
    private static boolean mDanmaKuShow = false;
    private LoadingDialog2 mLoadingDialog;
    protected  TextView mSystemTime;
    protected LinearLayout mTimeandbarray;
    protected  LinearLayout controllerbottom;
    protected  ImageView playstart,playstart2;
    protected  ImageView playbottom;
    protected  ImageView mBarrage;
    protected  ImageView mShare;
    protected  TextView mCurrentbottom;
    protected  TextView mTotalbottom;
    protected  ImageView mPan;
    protected  View mBatteryView;
    protected  ImageView newstart;
    protected  LinearLayout replay;
    protected  TextView replay_text;

    protected  LinearLayout layout_bottom;
    protected  TextView batteryText;
    protected BatteryView batteryView;
    protected  LinearLayout mDanmu;
    private ImageView mDanmuImage;
    private TextView mDanmuText;
    private boolean mSwitch = false;
    protected boolean isDown = false;
    protected boolean isNormal = false;
    protected boolean isComplete = false;
    private SeekBar mSeekBar;
    private GSYVideoManager mTmpManager;
    private boolean mTouch = true;
    private static boolean mError = false;
    private ProgressBar mLoading;
    protected List<GSYVideoModel> mUriList = new ArrayList<>();
    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<>();
    public ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> getUrls(){
        return urls;
    }
    private String title;
    private boolean cacheWithPlay;
    private Bitmap bmp;
    private int primaryWidth;               //原图片宽
    private int primaryHeight;              //原图片高
    private double scaleWidth, scaleHeight; //高宽比例

    private boolean mfontColor1=false,mfontColor2=false,mfontColor3=false,mfontColor4=false,mfontColor5=false,mfontColor6=false,mfontColor7=false,mfontColor8=false,mfontSmallSize = false,mfontBigSize = false;
    private long mChooseColor = 1;
    private List<Boolean> fontColorList;


    protected int timeCycle;
    //弹幕回调
    protected DanmuCallBack danmuCallBack;

    private static  int requestDanmu = 0;
    private static   boolean isRequest = true;
    private  boolean isFirst = true;
    private static   boolean isFirstInitDanmu = true;
    public void setTimeCycle(int timeCycle) {
        this.timeCycle = timeCycle;
    }


    public void setDanmuCallBack(DanmuCallBack danmuCallBack) {
        this.danmuCallBack = danmuCallBack;
    }



    public static boolean ismError() {
        return mError;
    }

    public static void setmError(boolean mError) {
        MySelfGSYVideoPlayer.mError = mError;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCacheWithPlay() {
        return cacheWithPlay;
    }

    public void setCacheWithPlay(boolean cacheWithPlay) {
        this.cacheWithPlay = cacheWithPlay;
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
        EventBus.getDefault().register(this);
        //mDanmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        adapter = new MyDanmuAdapter(context);
        surface_container = findViewById(R.id.surface_container);
        batteryView = findViewById(R.id.battery);
        layout_bottom = findViewById(R.id.all);
        mPan = findViewById(R.id.pan);
        newstart = findViewById(R.id.newstart);
        mSystemTime = findViewById(R.id.systemtime);
        mTimeandbarray = findViewById(R.id.timeandbarray);
        controllerbottom = findViewById(R.id.controllerbottom);
        playstart = findViewById(R.id.playstart);
        playstart2 = findViewById(R.id.playstart2);
        playstart2 = findViewById(R.id.playstart2);
        playbottom = findViewById(R.id.playbottom);
        mBarrage = findViewById(R.id.barrage);
        mShare = findViewById(R.id.share);
        mCurrentbottom = findViewById(R.id.currentbottom);
        mTotalbottom = findViewById(R.id.totalbottom);
        replay = findViewById(R.id.replay);
        replay_text = findViewById(R.id.replay_text);
     //   bottom_progressbar = findViewById(R.id.bottom_progressbar);
        mDanmu = findViewById(R.id.danmu);
        mDanmuImage = findViewById(R.id.danmuImage);
        mDanmuText = findViewById(R.id.danmuText);
        new TimeThread().start();
        mLoading = findViewById(R.id.loading2);
        mDanmu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onVideoPause();
                showPopwindow();
                controllerbottom.setVisibility(GONE);

            }
        });
        mDanmuImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onVideoPause();

                showPopwindow();

                controllerbottom.setVisibility(GONE);
            }
        });
        mDanmuText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onVideoPause();

                showPopwindow();

                controllerbottom.setVisibility(GONE);
            }
        });
        danmu = findViewById(R.id.danmaku_view);
        danmu.setAdapter(adapter);
        danmu.setGravity(1);
        if (mDanmaKuShow) {
                  /*  if (!getDanmakuView().isShown())
                        getDanmakuView().show();*/
            if(danmu.getVisibility() != View.VISIBLE){
                danmu.setVisibility(VISIBLE);
            }

            mBarrage.setImageResource(R.drawable.veido_barrage_icon_hl);

        } else {
            if(danmu.getVisibility() != View.INVISIBLE){
                danmu.setVisibility(INVISIBLE);
            }
                  /*  if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }*/
            // danmu.setVisibility(INVISIBLE);
            mBarrage.setImageResource(R.drawable.veido_barrage_icon_nl);

        }

        mBarrage.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDanmaKuShow = !mDanmaKuShow;
                resolveDanmakuShow();
            }
        });
        playstart.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                clickStartIcon();
            }
        });
        playstart2.setOnClickListener(new OnClickListener(){
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

                if (mNetChanged) {
                    mNetChanged = false;
                    netWorkErrorLogic();
                    if (mVideoAllCallBack != null) {
                        mVideoAllCallBack.onPlayError(mOriginUrl, mTitle, this);
                    }
                    return;
                }
                isComplete = false;
                mSourcePosition = 0;
                setUp(urls, cacheWithPlay, mCachePath, mMapHeadData, title);
                startPlayLogic();
                //  isDown = true;
            }
        });
        //创建BitMap对象，用于显示图片
        bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.video_play_pressed);
        //原始大小
        primaryWidth = bmp.getWidth() ;
        primaryHeight = bmp.getHeight();
        //初始比例为1
        scaleWidth = scaleHeight = 1;
        Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
        matrix.postScale((float)(scaleWidth * 1.2), (float)(scaleHeight * 1.2));    //设置高宽比例（三维矩阵）
        //扩大后的BitMap
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);
        playstart2.setImageBitmap(newBmp);

        Log.e("DanmuAdapter",adapter.toString()+"1111");

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



    /**
     * 播放的视频的位置发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressEvent (ProgressEvent e) {

        if(danmuCallBack!=null){
            danmuCallBack.onSetDanmu(e.getMinute());
        }
        isRequest = false;

       // initDanmaku(danmuBeanListAll);
    }

    /**
     * 播放的视频的位置发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendDanmuEvent (SendDanmuEvent e) {

        if(danmuCallBack!=null){
        //    Log.e("初始化弹幕发送",e.getDanmuBean().toString());
            danmuCallBack.onClickSend(e.getDanmuBean());
        }

    }

    /**
     * 播放的视频的位置发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitDanmuEvent (InitDanmuEvent e) {


       //initDanmaku(e.getDanmuBeanList());

    }

    public void releaseDanmuCallBack(){
        if(danmuCallBack != null){
            danmuCallBack = null;
        }
        if(danmuBeanListAll.size() > 0){
            danmuBeanListAll.clear();
        }
    }




    private InputStream getIsStream(File file) {
        try {
            InputStream instream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            StringBuilder sb1 = new StringBuilder();
            sb1.append("<i>");
            //分行读取
            while ((line = buffreader.readLine()) != null) {
                sb1.append(line);
            }
            sb1.append("</i>");
            Log.e("3333333", sb1.toString());
            instream.close();
            return new ByteArrayInputStream(sb1.toString().getBytes());
        } catch (java.io.FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        return null;
    }

    /**
     弹幕的显示与关闭
     */
    private void resolveDanmakuShow() {
        post(new Runnable() {
            @Override
            public void run() {

                if (mDanmaKuShow) {
                  /*  if (!getDanmakuView().isShown())
                        getDanmakuView().show();*/
                    if(danmu.getVisibility() != View.VISIBLE){
                        danmu.setVisibility(VISIBLE);
                    }

                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_hl);

                } else {
                    if(danmu.getVisibility() != View.INVISIBLE){
                        danmu.setVisibility(INVISIBLE);
                    }

                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_nl);

                }
            }
        });

    }

    public long getDanmakuStartSeekPosition() {
        return mDanmakuStartSeekPosition;
    }

    public void setDanmakuStartSeekPosition(long danmakuStartSeekPosition) {
        this.mDanmakuStartSeekPosition = danmakuStartSeekPosition;
    }

    public void setDanmaKuShow(boolean danmaKuShow) {
        this.mDanmaKuShow = danmaKuShow;
    }

    public boolean getDanmaKuShow() {
        return mDanmaKuShow;
    }


    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {

        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);

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

        startProgressTimer();

    }
    int errorPosition = 0;

    public int getErrorPosition() {
        return errorPosition;
    }

    public void setErrorPosition(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    int errortime = 0;
    int time = 0;
    /***
     * 拖动进度条
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
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
                time = seekBar.getProgress() * getDuration() / 100;
                //  int time = progress  * getDuration() / 100;
                getGSYVideoManager().seekTo(time);
                mBottomProgressBar.setProgress(seekBar.getProgress());
                mProgressBar.setProgress(seekBar.getProgress());

            } catch (Exception e) {
                Debuger.printfWarning(e.toString());
            }
        }
        mHadSeekTouch = false;
        cancelProgressTimer();
        if(timeCycle > 0 && time < 0){
            danmuBeanListAll.clear();
            danmuBeanList.clear();
            requestDanmu = 0;
            //mDanmakuView.removeAllDanmakus(true);
            EventBus.getDefault().post(new ProgressEvent(0));
            //  danmakuOnPause();
        }
        if(timeCycle > 0 && time >= 0){
            requestDanmu = time / (timeCycle * 1000);
            if(requestDanmu >= 0){
                danmuBeanListAll.clear();
                danmuBeanList.clear();
                // mDanmakuView.removeAllDanmakus(true);
                EventBus.getDefault().post(new ProgressEvent(requestDanmu));
                // danmakuOnPause();
            }
        }
        mStopTrack = true;
        danmuCount2 = 0;
        danmu.removeAllViews();
    }

    protected void touchSurfaceUp() {
        if (mChangePosition) {
            int duration = getDuration();
            int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
            if (mBottomProgressBar != null)
                mBottomProgressBar.setProgress(progress);
        }

        mTouchingProgressBar = false;
        dismissProgressDialog();
        dismissVolumeDialog();
        dismissBrightnessDialog();
        if (mChangePosition && getGSYVideoManager() != null && (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE)) {
            try {

                getGSYVideoManager().seekTo(mSeekTimePosition);

                if(timeCycle > 0 && mSeekTimePosition < 0){
                    danmuBeanListAll.clear();
                    danmuBeanList.clear();
                    requestDanmu = 0;
                    //mDanmakuView.removeAllDanmakus(true);
                    EventBus.getDefault().post(new ProgressEvent(0));
                  //  danmakuOnPause();
                }
                if(timeCycle > 0 && mSeekTimePosition >= 0){
                    requestDanmu = mSeekTimePosition / (timeCycle * 1000);
                    if(requestDanmu >= 0){
                        danmuBeanListAll.clear();
                        danmuBeanList.clear();
                       // mDanmakuView.removeAllDanmakus(true);
                        EventBus.getDefault().post(new ProgressEvent(requestDanmu));
                       // danmakuOnPause();
                    }
                }
                mStopTrack = true;
                danmuCount2 = 0;
                danmu.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int duration = getDuration();
            int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
            if (mProgressBar != null) {
                mProgressBar.setProgress(progress);
            }
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekPosition");
                mVideoAllCallBack.onTouchScreenSeekPosition(mOriginUrl, mTitle, this);
            }
        } else if (mBrightness) {
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekLight");
                mVideoAllCallBack.onTouchScreenSeekLight(mOriginUrl, mTitle, this);
            }
        } else if (mChangeVolume) {
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekVolume");
                mVideoAllCallBack.onTouchScreenSeekVolume(mOriginUrl, mTitle, this);
            }
        }
    }

    @Override
    public  void  onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        if (getGSYVideoManager() != null && mHadPlay) {
            try {
                //int progress = seekBar.getProgress();
                int time = seekBar.getProgress() * getDuration() / 100;
                errortime = seekBar.getProgress() * getDuration() / 100;
                mBottomProgressBar.setProgress(seekBar.getProgress());

                mProgressBar.setProgress(seekBar.getProgress());
                // setTextAndProgress(seekBar.getProgress());
                mCurrentTimeTextView.setText(CommonUtil.stringForTime(time));
                mCurrentbottom.setText(CommonUtil.stringForTime(time));
                errorPosition = seekBar.getProgress();
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
                    if(msg != null){
                        msg.what = msgKey1;
                        if(mHandler != null){
                            mHandler.sendMessage(msg);
                        }
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    @Override
    public void onPrepared() {
        super.onPrepared();
      /*  onPrepareDanmaku(this);
        isFirst = false;*/

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

                if (mFullscreenButton != null)
                    mFullscreenButton.setImageResource(getShrinkImageRes());
                mFullscreenButton.setVisibility(GONE);
                mTitleTextView.setVisibility(VISIBLE);
                mLockScreen.setVisibility(VISIBLE);
                mShare.setVisibility(GONE);
                mBarrage.setVisibility(VISIBLE);
                mTimeandbarray.setVisibility(VISIBLE);
                playstart.setVisibility(GONE);
                mCurrentTimeTextView.setVisibility(GONE);
                mTotalTimeTextView.setVisibility(GONE);
                mPan.setVisibility(GONE);
             //   bottom_progressbar.setVisibility(GONE);
                controllerbottom.setVisibility(VISIBLE);
                if(mCurrentState == CURRENT_STATE_PAUSE){
                    playstart2.setVisibility(VISIBLE);
                }

            } else {
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
             //   bottom_progressbar.setVisibility(GONE);
                playstart2.setVisibility(GONE);
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
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(controllerbottom, INVISIBLE);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(newstart, INVISIBLE);
        setViewShowState(replay_text, INVISIBLE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(playstart2,GONE);
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
            if(mTouch){
                //  playstart2.setVisibility(VISIBLE);

                gestureDetector.onTouchEvent(event);
            }

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


    protected void showAllWidget() {
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(controllerbottom, VISIBLE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,VISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(newstart, VISIBLE);
        setViewShowState(replay_text, VISIBLE);
        setViewShowState(mTimeandbarray,VISIBLE);
        setViewShowState(playstart2,VISIBLE);
        //  mOrientationUtils.setEnable(true);
    }


   /* public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        return gsyBaseVideoPlayer;
    }
*/



    @Override
    protected void changeUiToNormal() {
        Log.e("播放器","changeUiToNormal");

        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, GONE);

        setViewShowState(mLockScreen, mIfCurrentIsFullscreen? VISIBLE : GONE);
        updateStartImage();

        upPlayImage();
    }


    @Override
    protected void changeUiToPreparingShow() {
        Log.e("播放器","changeUiToPreparing");
        setViewShowState(mTopContainer,(mIfCurrentIsFullscreen) ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);
        updateStartImage();
        upPlayImage();
        danmu.pause();
    //    danmakuOnPause();
    }



    private  int mCurrentposition = 0;

    @Override
    protected void changeUiToPlayingShow() {
        Log.e("播放器","changeUiToPlayingShow");


        setmError(true);
        mTouch = true;
        layout_bottom.setVisibility(VISIBLE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mFullscreenButton,(mIfCurrentIsFullscreen) ? GONE : VISIBLE);
        setViewShowState(mTimeandbarray,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
            setViewShowState(getFullscreenButton(),GONE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(getFullscreenButton(),VISIBLE);
        }


        updateStartImage();
        upPlayImage();
        danmu.resume();
        //danmakuOnResume();
    }

    @Override
    protected void changeUiToPauseShow() {
        Log.e("播放器","changeUiToPauseShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mTimeandbarray,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
            setViewShowState(getFullscreenButton(),GONE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(getFullscreenButton(),VISIBLE);
        }
        updateStartImage();
        updatePauseCover();
        upPlayImage();
        danmu.pause();
        //danmakuOnPause();
    }


    @Override
    protected void changeUiToPlayingBufferingShow() {
        Log.e("播放器","changeUiToPlayingBufferingShow");
        setViewShowState(mTopContainer,(mIfCurrentIsFullscreen) ? INVISIBLE : VISIBLE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(controllerbottom, INVISIBLE);
        setViewShowState(newstart, INVISIBLE);
        setViewShowState(replay_text, INVISIBLE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(playstart2,GONE);
        if(mIfCurrentIsFullscreen){
            setViewShowState(getFullscreenButton(),GONE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(getFullscreenButton(),VISIBLE);
        }
        upPlayImage();
        danmu.pause();
        //danmakuOnPause();
    }



    @Override
    protected void changeUiToCompleteShow() {
        Log.e("播放器","changeUiToCompleteShow");
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
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        if(mIfCurrentIsFullscreen){
            // setViewShowState(controllerbottom, VISIBLE);
            setViewShowState(getFullscreenButton(),GONE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(getFullscreenButton(),VISIBLE);
        }
        isComplete = true;
        upPlayImage();
    }

    @Override
    protected void changeUiToError() {
        Log.e("mNetSateError",mNetSate);
        if(mNetSate.equals("NONE")){
            Log.e("mNetSate",mNetSate);
            setStateAndUi(CURRENT_STATE_AUTO_COMPLETE);
            //netWorkErrorLogic();
            if (mVideoAllCallBack != null) {
                mVideoAllCallBack.onPlayError(mOriginUrl, mTitle, this);
            }
            return;
        }
        Log.e("播放器","changeUiToError");
        hideAllWidget();
        String time = mCurrentTimeTextView.getText().toString();
        errorPosition = CommonUtil.intForTime(time);
        layout_bottom.setVisibility(INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mTopContainer,(mIfCurrentIsFullscreen) ? INVISIBLE : VISIBLE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        if(mUriList.size() > 0 ){
            if(mSourcePosition < mUriList.size() -1){
                mSourcePosition++;
            }
            mError = false;
            setUp(mUriList.get(mSourcePosition).getUrl(),true,"");
            createNetWorkState();
            listenerNetWorkState();
            Log.e("播放器播放错误",errorPosition+"");
            setSeekOnStart(errorPosition);
            startButtonLogic();
        }


        upPlayImage();
        danmu.removeAllViews();
    }




    protected void changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);

        upPlayImage();
    }

    protected void changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);

        upPlayImage();
    }

    //将其他组件隐藏起来
    protected void changeUiToClear() {
        Debuger.printfLog("changeUiToClear");
        setViewShowState(mTopContainer, GONE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen, GONE);


        upPlayImage();
    }

    protected void changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
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
     * 增对列表优化，在播放前的时候才进行setup
     */
    @Override
    protected void prepareVideo() {
     /*   if (mSetUpLazy) {
            super.setUp(mOriginUrl,
                    mCache,
                    mCachePath,
                    mMapHeadData,
                    mTitle);
        }*/
        super.prepareVideo();
        if(timeCycle > 0){
            EventBus.getDefault().post(new ProgressEvent(0));
            isFirst = false;
        }
    }

    /**
     * 点击触摸显示和隐藏逻辑
     */

    @Override
    protected void onClickUiToggle() {
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
            Log.e("点击","播放点击");
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingShow();
                }
            }

        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            Log.e("点击","暂停点击");
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

        int duration = getDuration();

        int progress = (int) (positionCurrent * 100 / (duration == 0 ? 1 : duration));

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
                if (getGSYVideoManager() != null){
                    getGSYVideoManager().pause();
                    danmu.pause();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //danmakuOnPause();
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
       // danmakuOnResume();

    }
    @Override
    public void onCompletion() {
        //releaseDanmaku(this);
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
        int time = mProgressBar.getProgress() * getDuration() / 100;
        //如果已经初始化过的，直接seek到对于位置

       /* if (mHadPlay && getDanmakuView() != null && getDanmakuView().isPrepared()) {
            resolveDanmakuSeek(this, time);
          //  setDanmakuStartSeekPosition(time);
        } else if (mHadPlay && getDanmakuView() != null && !getDanmakuView().isPrepared()) {
            //如果没有初始化过的，记录位置等待
            Log.e("弹幕初始化等待time",time+"");
            setDanmakuStartSeekPosition(time);
        }*/
    }

/*    protected void danmakuOnPause() {
        Log.e("myselfVideoPlayerlist","danmakuOnPause1");
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            Log.e("myselfVideoPlayerlist","danmakuOnPause2");
            mDanmakuView.pause();
        }
    }*/

   /* protected void danmakuOnResume() {
        Log.e("myselfVideoPlayerlist","danmakuOnResume1");
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            Log.e("myselfVideoPlayerlist","danmakuOnResume2");
            mDanmakuView.resume();
        }
    }

    public void setDanmaKuStream(File is) {
        mDumakuFile = is;
        if (!getDanmakuView().isPrepared()) {
            onPrepareDanmaku((MySelfGSYVideoPlayer) getCurrentPlayer());
        }
    }*/

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

        if(timeCycle > 0){
            Log.e("初始化弹幕isRequest22",requestDanmu+"");
            if(currentTime > requestDanmu * timeCycle * 1000){
                isRequest = true;
            }
            if(currentTime >= requestDanmu * timeCycle * 1000){
                Log.e("初始化弹幕isRequest",requestDanmu+"");
            }
         //   Log.e("当前视频isRequest",isRequest+"");
            if(isRequest){

                EventBus.getDefault().post(new ProgressEvent(requestDanmu));
                mStopTrack = false;

                mStartTrack = false;
            }


            if(mStopTrack){
                if(danmuCount2 == 0){
                    for(int i = 0; i < danmuBeanList.size(); i++){
                        if(danmuBeanList.get(i).getDisplayTime() > currentTime){
                            danmuCount2 = i;
                            mStartTrack = true;
                            break;
                        }
                    }
                }

                if(danmuCount2 < danmuBeanList.size()){
                    if(currentTime >= danmuBeanList.get(danmuCount2).getDisplayTime() && mStartTrack){
                        MyDanmuModel danmuEntity = new MyDanmuModel();
                        danmuEntity.setContent(danmuBeanList.get(danmuCount2).getDanmuText());
                        int num = 1 + (int)(Math.random() * (4-1+1));
                        danmuEntity.setType(num);
                        danmu.setSpeed(2);
                        danmu.addDanmu(danmuEntity,danmuBeanList.get(danmuCount2).getFontSize(),danmuBeanList.get(danmuCount2).getFontColor());
                        Log.e("添加弹幕1",danmuEntity.getContent()+",time:"+danmuBeanList.get(danmuCount2).getDisplayTime()+",danmuCount:"+danmuCount2+",mStopTrack:"+mStopTrack+",mStartTrack:"+mStartTrack);
                        danmuCount2++;
                    }
                }

            } else {
                if(danmuCount < danmuBeanList.size()){
                    if(currentTime >= danmuBeanList.get(danmuCount).getDisplayTime()){
                        MyDanmuModel danmuEntity = new MyDanmuModel();
                        danmuEntity.setContent(danmuBeanList.get(danmuCount).getDanmuText());
                        int num = 1 + (int)(Math.random() * (4-1+1));
                        danmuEntity.setType(num);
                        danmu.setSpeed(2);
                        danmu.addDanmu(danmuEntity,danmuBeanList.get(danmuCount).getFontSize(),danmuBeanList.get(danmuCount).getFontColor());
                        Log.e("添加弹幕2",danmuEntity.getContent()+",time:"+danmuBeanList.get(danmuCount).getDisplayTime());
                        danmuCount++;
                    }
                }
            }

        }








    }


    public void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {

        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            MySelfGSYVideoPlayer gsyDanmaVideoPlayer = (MySelfGSYVideoPlayer) gsyVideoPlayer;
            setDanmaKuShow(gsyDanmaVideoPlayer.getDanmaKuShow());
        }
    }
    int fullCount = 0;
    @Override
    public void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        Log.e("播放器横竖屏切换","横竖屏切换");
        MySelfGSYVideoPlayer sf = (MySelfGSYVideoPlayer) from;
        MySelfGSYVideoPlayer st = (MySelfGSYVideoPlayer) to;
        st.danmuBeanList = sf.danmuBeanList;
  //      st.mDanmakuView = sf.mDanmakuView;
   //     st.danmuCallBack = sf.danmuCallBack;
        st.danmuCount = sf.danmuCount;
        st.danmuCount2 = sf.danmuCount2;
        st.mDanmaKuShow = sf.mDanmaKuShow;
        st.mStopTrack = sf.mStopTrack;
        st.mStartTrack = sf.mStartTrack;
        st.surface_container = sf.surface_container;
        st.mUriList = sf.mUriList;
        st.timeCycle = sf.timeCycle;
        st.isFirst = sf.isFirst;
        st.isRequest = sf.isRequest;
        st.requestDanmu = sf.requestDanmu;
        st.urls = sf.urls;
        st.mTouch = sf.mTouch;
        st.mError = sf.mError;
        st.errortime = sf.errortime;
        st.errorPosition = sf.errorPosition;
        st.mCurrentState = sf.mCurrentState;
        Log.e("播放器st",st.errorPosition+"");
        st.mCurrentbottom.setText(sf.mCurrentbottom.getText());
        st.mTotalbottom.setText(sf.mTotalbottom.getText());
        st.mCurrentTimeTextView.setText(sf.mCurrentTimeTextView.getText());
        st.mTotalTimeTextView.setText(sf.mTotalTimeTextView.getText());
        if(fullCount % 2 != 0){
            Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
            matrix.postScale((float)(scaleWidth * 1.5), (float)(scaleHeight * 1.5));    //设置高宽比例（三维矩阵）

            //缩放后的BitMap
            Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

            //重新设置BitMap
            st.playstart2.setImageBitmap(newBmp);
            if(mCurrentState != CURRENT_STATE_PAUSE){
                st.playstart2.setVisibility(GONE);
            }
        }
        super.cloneParams(from, to);
        //  addDanmaku(true);


   /*     bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.video_play_pressed);
        sf.playstart2.setImageBitmap(bmp);*/
        //  st.playstart2 = sf.playstart2;
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

        if (mIfCurrentIsFullscreen && mLockCurScreen) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mIfCurrentIsFullscreen && !mLockCurScreen) {
            setViewShowState(mLockScreen, VISIBLE);
            //  return;
        }
       // super.clickStartIcon();
        clickStartIcon();
    }

    InputMethodManager imm = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
    int fontSize = 18;


    /**
     * 显示popupWindow
     */
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindowlayout, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 设置允许在外点击消失
        window.setOutsideTouchable(false);


        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);

        //软键盘不会挡着popupwindow
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(this.findViewById(R.id.danmu),
                Gravity.BOTTOM, 0, 0);




        final ImageView fontColor1 = view.findViewById(R.id.fontColor1);
        final ImageView fontColor2 = view.findViewById(R.id.fontColor2);
        final ImageView fontColor3 = view.findViewById(R.id.fontColor3);
        final ImageView fontColor4 = view.findViewById(R.id.fontColor4);
        final ImageView fontColor5 = view.findViewById(R.id.fontColor5);
        final ImageView fontColor6 = view.findViewById(R.id.fontColor6);
        final ImageView fontColor7 = view.findViewById(R.id.fontColor7);
        final ImageView fontColor8 = view.findViewById(R.id.fontColor8);
        final ImageView fontSmallSize = view.findViewById(R.id.fontSmallSize);
        final ImageView fontBigSize = view.findViewById(R.id.fontBigSize);
        final ImageView fontImage = view.findViewById(R.id.fontImage);
        final LinearLayout fontImageLinearLayout = view.findViewById(R.id.fontImageLinearLayout);
        final LinearLayout fontDisplay = view.findViewById(R.id.fontDisplay);
        final EditText editText = view.findViewById(R.id.editText);
        final Button send = view.findViewById(R.id.send);

      /*  if(edtextContent.length() != 0 || edtextContent != null){
            editText.setText(edtextContent);
        }*/
        final TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
        final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mfontSmallSize){
                    fontSize = 18;
                }
                if(mfontBigSize){
                    fontSize = 20;
                }
                if(editText.getText().toString().length() > 0){
                    //addDanmaku(editText.getText().toString(),fontSize,true);
                    DanmuBean danmuBean = new DanmuBean();
                    danmuBean.setFontSize(fontSize);
                    String text = editText.getText().toString();
                    /*if(text.indexOf(",") > 0){
                        int i = text.indexOf(",");
                        text.re
                    }*/
                    text = text.replace("，","");
                    text = text.replace(",","");
                    text = text.replace(" ","");
                    danmuBean.setDanmuText(text);
                    danmuBean.setType(1);
                    danmuBean.setFontColor(mChooseColor);
                    danmuBean.setDisplayTime(getCurrentPositionWhenPlaying());
                    EventBus.getDefault().post(new SendDanmuEvent(danmuBean));
                    MyDanmuModel model = new MyDanmuModel();
                    model.setContent(editText.getText().toString());
                    model.setType(1);
                  /*  model.setGoodNum(0);
                    model.setGood(true);*/
                    danmu.setSpeed(2);
                  //  adapter.setTextSize(20);
                    danmu.addDanmu(model,fontSize,mChooseColor);
                    editText.setText("");
                }

                if(imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                fontDisplay.setVisibility(GONE);
                onVideoResume();
                danmu.resume();
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置可获得焦点

                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                //请求获得焦点
                //    editText.requestFocus();

                //调用系统输入法
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);

            }
        },200);
        editText.requestFocus();
        //点击软键盘外部，收起软键盘
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontDisplay.setVisibility(GONE);
                onVideoPause();
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                fontDisplay.startAnimation(mHiddenAction);
                fontDisplay.setVisibility(GONE);
                if(!hasFocus){
                    //    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                   /* if (imm != null)
                        imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);*/
                }
                // hasFocus = !hasFocus;
                count++;
            }
        });

     /*   fontImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                count++;

                InputMethodManager imm = (InputMethodManager)
                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(count % 2 != 0){
                    fontDisplay.startAnimation(mShowAction);
                    fontDisplay.setVisibility(VISIBLE);
                } else{
                    //  fontDisplay.startAnimation(mHiddenAction);
                    fontDisplay.setVisibility(GONE);
                }


            }
        });*/
        fontImageLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;

                InputMethodManager imm = (InputMethodManager)
                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(count % 2 != 0){
                    fontDisplay.startAnimation(mShowAction);
                    fontDisplay.setVisibility(VISIBLE);
                } else{
                    //  fontDisplay.startAnimation(mHiddenAction);
                    fontDisplay.setVisibility(GONE);
                }
            }
        });
        fontSmallSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontSmallSize.setBackgroundResource(R.drawable.fong_size_mini_hl);
                fontBigSize.setBackgroundResource(R.drawable.fong_size_big_nl);
                mfontBigSize =false;
                mfontSmallSize = true;
            }
        });
        fontBigSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontBigSize.setBackgroundResource(R.drawable.fong_size_bi_hl);
                fontSmallSize.setBackgroundResource(R.drawable.fong_size_mini);
                mfontBigSize = true;
                mfontSmallSize = false;
            }
        });
        fontColor1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1pressed);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = true;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 1;
            }
        });


        fontColor2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2pressed);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = true;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 2;
            }
        });

        fontColor3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3pressed);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = true;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 3;
            }
        });

        fontColor4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4pressed);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = true;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 4;
            }
        });
        fontColor5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5pressed);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = true;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 5;
            }
        });
        fontColor6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6pressed);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = true;
                mfontColor7 = false;
                mfontColor8 = false;
                mChooseColor = 6;
            }
        });

        fontColor7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7pressed);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8normal);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = true;
                mfontColor8 = false;
                mChooseColor = 7;
            }
        });

        fontColor8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontColor1.setBackgroundResource(R.drawable.video_fontcolor1normal);
                fontColor2.setBackgroundResource(R.drawable.video_fontcolor2normal);
                fontColor3.setBackgroundResource(R.drawable.video_fontcolor3normal);
                fontColor4.setBackgroundResource(R.drawable.video_fontcolor4normal);
                fontColor5.setBackgroundResource(R.drawable.video_fontcolor5normal);
                fontColor6.setBackgroundResource(R.drawable.video_fontcolor6normal);
                fontColor7.setBackgroundResource(R.drawable.video_fontcolor7normal);
                fontColor8.setBackgroundResource(R.drawable.video_fontcolor8pressed);
                mfontColor1 = false;
                mfontColor2 = false;
                mfontColor3 = false;
                mfontColor4 = false;
                mfontColor5 = false;
                mfontColor6 = false;
                mfontColor7 = false;
                mfontColor8 = true;
                mChooseColor = 8;
            }
        });


        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //edtextContent = editText.getText().toString();
                controllerbottom.setVisibility(VISIBLE);

            }
        });

    }

 /*   public void startAfterPrepared(){
        super.startAfterPrepared();
        Log.e("初始化弹幕2","初始化弹幕2");
        //initDanmaku(danmuBeanListAll);
    }*/

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
            setSeekOnStart(errorPosition);
            startButtonLogic();
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            try {
                playstart.setImageResource(R.drawable.vedio_play_icon);
                //    playstart2.setImageResource(R.drawable.video_play_pressed);
                playbottom.setImageResource(R.drawable.vedio_play_icon);
                newstart.setImageResource(R.drawable.vedio_play_icon);
                onVideoPause();
                //danmakuOnPause();
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
                //   playstart2.setImageResource(R.drawable.video_pause_pressed);
                playbottom.setImageResource(R.drawable.vedio_stop_icon);
                newstart.setImageResource(R.drawable.vedio_stop_icon);
                getGSYVideoManager().start();

            } catch (Exception e) {
                e.printStackTrace();
            }
            setStateAndUi(CURRENT_STATE_PLAYING);
            //danmakuOnResume();
            danmu.resume();
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            layout_bottom.setVisibility(GONE);
            startButtonLogic();
        }


    }

    public ArrayList<String> subString(String url){
        ArrayList<String> list = new ArrayList<>();
        String[] temp = url.split("\\*\\*\\*");
        if(temp != null){
            if(temp.length > 1){
                list.add(temp[0]);
                list.add(temp[1]);
            } else if (temp.length <= 1){
                list.add(temp[0]);
            }

        }



        return list;
    }




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

        GSYVideoModel gsyVideoModel = url.get(position);
        if (gsyVideoModel instanceof MySelfGSYVideoPlayer.GSYADVideoModel) {
            MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;

            if (gsyadVideoModel.isSkip() && position < (url.size() - 1)) {

                return setUp(url, cacheWithPlay, position + 1, cachePath, mapHeadData, changeState);
            }


            // isAdModel = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
            isDown = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
        }

        mUriList = url;
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);

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

    public boolean setUp(ArrayList<GSYADVideoModel> url, boolean cacheWithPlay, File cachePath, Map<String, String> mapHeadData, String title){
        mCache = cacheWithPlay;
        mCachePath = cachePath;


        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) < CHANGE_DELAY_TIME)
            return false;
        mCurrentState = CURRENT_STATE_NORMAL;

        this.mTitle = title;
        setStateAndUi(CURRENT_STATE_NORMAL);
        setAdUp(url,mCache, 0);
        return true;
    }

    /**
     * 在点击播放的时候才进行真正setup
     */
    public boolean setUpLazy(ArrayList<GSYADVideoModel> url, boolean cacheWithPlay, File cachePath, Map<String, String> mapHeadData, String title) {
        // mOriginUrl = url;
        mCache = cacheWithPlay;
        mCachePath = cachePath;
        mSetUpLazy = true;
        mTitle = title;
        mMapHeadData = mapHeadData;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) < CHANGE_DELAY_TIME)
            return false;
        mUrl = "waiting";
        mCurrentState = CURRENT_STATE_NORMAL;
        setAdUp(url,mCache, 0);
        return true;
    }
    @Override
    public boolean setUp(String url, boolean cacheWithPlay, String title) {
        ArrayList<MySelfGSYVideoPlayer.GSYADVideoModel> urls = new ArrayList<>();
        ArrayList<String> listUrl = new ArrayList<String>();
        listUrl = subString(url);
        urls = getUrls();

        if(listUrl.size() >= 2){
            urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel(listUrl.get(0),
                    title, MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_DOWN));
            urls.add(new MySelfGSYVideoPlayer.GSYADVideoModel(listUrl.get(1),
                    title, MySelfGSYVideoPlayer.GSYADVideoModel.TYPE_NORMAL));
            return setUp(urls, cacheWithPlay, mCachePath, mMapHeadData, title);
        }
        return super.setUp(url, cacheWithPlay, ((File) null), title);
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



    private int mSourcePosition = 0;


}