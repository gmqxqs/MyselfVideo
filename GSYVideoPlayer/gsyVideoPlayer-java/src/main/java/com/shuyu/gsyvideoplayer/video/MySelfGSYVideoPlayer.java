package com.shuyu.gsyvideoplayer.video;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.BuildConfig;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.MyselfView.BatteryView;
import com.shuyu.gsyvideoplayer.R;

import com.shuyu.gsyvideoplayer.event.ScreenEvent;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.screening.DLNAManager;
import com.shuyu.gsyvideoplayer.screening.DLNAPlayer;
import com.shuyu.gsyvideoplayer.screening.bean.DeviceInfo;
import com.shuyu.gsyvideoplayer.screening.bean.MediaInfo;
import com.shuyu.gsyvideoplayer.screening.listener.DLNAControlCallback;
import com.shuyu.gsyvideoplayer.screening.listener.DLNADeviceConnectListener;
import com.shuyu.gsyvideoplayer.screening.listener.DLNARegistryListener;
import com.shuyu.gsyvideoplayer.screening.listener.DLNAStateCallback;
import com.shuyu.gsyvideoplayer.simplepermission.PermissionsManager;
import com.shuyu.gsyvideoplayer.simplepermission.PermissionsRequestCallback;
import com.shuyu.gsyvideoplayer.utils.BiliDanmukuParser;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.view.DanamakuAdapter;
import com.shuyu.gsyvideoplayer.view.LoadingDialog2;

import org.fourthline.cling.model.action.ActionInvocation;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import moe.codeest.enviews.ENPlayView;

public class MySelfGSYVideoPlayer extends StandardGSYVideoPlayer implements SeekBar.OnSeekBarChangeListener, DLNADeviceConnectListener {
    private static final int msgKey1 = 1;
    protected  int count = 0;
    private  Handler handler;
    private  Context contextFirst;
    private static   List<DanmuBean> danmuBeanList = new ArrayList<>();
    public List<DanmuBean> getDanmuBeanList() {
        return danmuBeanList;
    }
    public void setDanmuBeanList(List<DanmuBean> danmuBeanList) {
        this.danmuBeanList = danmuBeanList;
    }
    private static  String fileName;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    private  BaseDanmakuParser mParser;//解析器对象
    private  IDanmakuView mDanmakuView;//弹幕view
    private  DanmakuContext mDanmakuContext;
    private long mDanmakuStartSeekPosition = -1;
    private boolean mDanmaKuShow = false;
    private LoadingDialog2 mLoadingDialog;
    protected  TextView mSystemTime;
    protected LinearLayout mTimeandbarray;
    protected  LinearLayout controllerbottom;
    protected  ImageView playstart,playstart2;
    protected  ImageView playbottom;
    protected  ImageView mBarrage;
    protected  ImageView mShare;
    protected  ImageView mMore;
    protected  TextView mCurrentbottom;
    protected  TextView mTotalbottom;
    protected  ImageView mPan;
    protected  View mBatteryView;
    protected  ImageView newstart;
    protected  LinearLayout replay;
    protected  TextView replay_text;
    protected ProgressBar bottom_progressbar;
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
    private  String edtextContent ="";
    private boolean mfontColor1=false,mfontColor2=false,mfontColor3=false,mfontColor4=false,mfontColor5=false,mfontColor6=false,mfontColor7=false,mfontColor8=false,mfontSmallSize = false,mfontBigSize = false;
    private long mChooseColor = 16777215;
    private List<Boolean> fontColorList;

    private static final int CODE_REQUEST_PERMISSION = 1010;
    private static final int CODE_REQUEST_MEDIA = 1011;
    private int curItemType = MediaInfo.TYPE_VIDEO;
    private String mMediaPath;
    private DeviceInfo mDeviceInfo;
    private static DLNAPlayer mDLNAPlayer;
    private DLNARegistryListener mDLNARegistryListener;
    private DevicesAdapter mDevicesAdapter;
    private ListView mDeviceListView;


    public boolean isDestory() {
        return DLNAManager.getInstance().isDestory();
    }

    public void setDestory(boolean destory) {
        DLNAManager.getInstance().setDestory(destory);
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

    public DanmakuContext getDanmakuContext() {
        return mDanmakuContext;
    }

    public IDanmakuView getDanmakuView() {
        return mDanmakuView;
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

    int errorPosition = 0;

    public int getErrorPosition() {
        return errorPosition;
    }

    public void setErrorPosition(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    int errortime = 0;

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

    protected void init(final Context context) {
        super.init(context);

        handler = new Handler(context.getMainLooper());
        mDanmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
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
        playbottom = findViewById(R.id.playbottom);
        mBarrage = findViewById(R.id.barrage);
        mShare = findViewById(R.id.share);
        mMore = findViewById(R.id.more);
        mCurrentbottom = findViewById(R.id.currentbottom);
        mTotalbottom = findViewById(R.id.totalbottom);
        replay = findViewById(R.id.replay);
        replay_text = findViewById(R.id.replay_text);
        bottom_progressbar = findViewById(R.id.bottom_progressbar);
        mDanmu = findViewById(R.id.danmu);
        mDanmuImage = findViewById(R.id.danmuImage);
        mDanmuText = findViewById(R.id.danmuText);
        new TimeThread().start();
        mLoading = findViewById(R.id.loading2);
        mDanmu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击弹幕","点击弹幕");
                onVideoPause();
                showPopwindow();
                controllerbottom.setVisibility(GONE);

            }
        });
        mDanmuImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击弹幕","点击弹幕");
                onVideoPause();

                showPopwindow();

                controllerbottom.setVisibility(GONE);
            }
        });
        mDanmuText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击弹幕","点击弹幕");
                onVideoPause();

                showPopwindow();

                controllerbottom.setVisibility(GONE);
            }
        });
        mBarrage.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDanmaKuShow = !mDanmaKuShow;
                resolveDanmakuShow();
            }
        });
        mShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final CustomDialog confirmDialog = new CustomDialog(mContext, "请选择投屏设备", "", "开始投屏", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                },"放弃");

                final View view = View.inflate(mContext, R.layout.custom_dialog_layout, null);

                TextView dialog_confirm = view.findViewById(R.id.dialog_confirm);
                dialog_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  EditText editText = view.findViewById(R.id.telephone);

                    }
                });

                confirmDialog.show();*/
                BrowserActivity.forward(mContext);


            }
        });

        mMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DLNAManager.setIsDebugMode(BuildConfig.DEBUG);
                Activity activity = findActivity(mContext);
                if(activity != null){
                    PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(CODE_REQUEST_PERMISSION,
                            activity, new PermissionsRequestCallback() {
                                @Override
                                public void onGranted(int requestCode, String permission) {
                                    boolean hasPermission = PackageManager.PERMISSION_GRANTED ==
                                            (mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                    & mContext.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO));
                                    if (hasPermission) {
                                        DLNAManager.getInstance().init(mContext, new DLNAStateCallback() {
                                            @Override
                                            public void onConnected() {
                                                Log.e("投屏", "DLNAManager ,onConnected");
                                                initDlna();
                                            }

                                            @Override
                                            public void onDisconnected() {
                                                Log.e("投屏", "DLNAManager ,onDisconnected");
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onDenied(int requestCode, String permission) {

                                }

                                @Override
                                public void onDeniedForever(int requestCode, String permission) {

                                }

                                @Override
                                public void onFailure(int requestCode, String[] deniedPermissions) {

                                }

                                @Override
                                public void onSuccess(int requestCode) {

                                }
                            });
                }


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
                Log.e("点击newStart","点击newStart");
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
        initDanmaku(danmuBeanList);

        getBackButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击后退","点击后退2");
                if(mContext != null){
                    destroy();
                }

            }
        });



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

    public void destroy(){
        if (mDLNAPlayer != null && mContext != null) {
            mDLNAPlayer.destroy();
            DLNAManager.getInstance().unregisterListener(mDLNARegistryListener);
            DLNAManager.getInstance().destroy();
        }

    }

    public void startScreen(DeviceInfo deviceInfo){
        if(mDLNAPlayer != null){
            mDLNAPlayer.connect(deviceInfo);
        } else{
           Log.e("mDLNAPLayer","mDLNAPlayer为空");
        }

    }

   static List<DeviceInfo> deviceInfos =  new ArrayList<>();;

    public List<DeviceInfo> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    private void initDlna() {
        Log.e("投屏设备initDlna","投屏initDlna");
        mDLNAPlayer = new DLNAPlayer(mContext);
        mDLNAPlayer.setConnectListener(this);
        mDLNARegistryListener = new DLNARegistryListener() {
            @Override
            public void onDeviceChanged(List<DeviceInfo> deviceInfoList) {
                /*mDevicesAdapter.clear();
                mDevicesAdapter.addAll(deviceInfoList);
                mDevicesAdapter.notifyDataSetChanged();*/
             /*  for(DeviceInfo deviceInfo : deviceInfoList){
                   Log.e("投屏设备",deviceInfo.toString());
               }*/
               /* for(DeviceInfo deviceInfo : deviceInfoList){
                    Log.e("投屏设备添加数据",deviceInfo.toString());
                    deviceInfos.add(deviceInfo);

                }*/
              //  EventBus.getDefault().post(new ScreenEvent(deviceInfoList));
                for(DeviceInfo deviceInfo : deviceInfoList){
                    Log.e("投屏设备添加数据",deviceInfo.toString());
                    deviceInfos.add(deviceInfo);

                }
                mVideoAllCallBack.onCastScreen(mOriginUrl,deviceInfoList,MySelfGSYVideoPlayer.this);
            }
        };

        DLNAManager.getInstance().registerListener(mDLNARegistryListener);

    }



    public void initDanmaku(List<DanmuBean> list) {
        Log.e("初始化弹幕","初始化弹幕");
        if(list.size() == 0 || list == null){
            return;
        }
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        DanamakuAdapter  danamakuAdapter = new DanamakuAdapter(mDanmakuView);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), danamakuAdapter) // 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        Log.e("mDanmakuView",mDanmakuView+"");
        if (mDanmakuView != null) {


            //todo 这是为了demo效果，实际上需要去掉这个，外部传输文件进来
            Gson gson = new Gson();
            String str =  gson.toJson(list);
            Log.e("danmuBean",str);
            byte[] byteArray = str.getBytes();
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            mParser = createParser(bis);

            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void prepared() {
                    if (getDanmakuView() != null) {
                        Log.e("弹幕准备","弹幕准备");
                        getDanmakuView().start();
                        Log.e("getDanma",getDanmakuStartSeekPosition()+"");
                        if (getDanmakuStartSeekPosition() != -1) {
                            resolveDanmakuSeek(MySelfGSYVideoPlayer.this, getDanmakuStartSeekPosition());
                            setDanmakuStartSeekPosition(-1);
                        }
                        resolveDanmakuShow();
                    }
                }
            });
            //    mDanmakuView.enableDanmakuDrawingCache(true);

        }
    }


    /**
     弹幕的显示与关闭
     */
    private void resolveDanmakuShow() {
        post(new Runnable() {
            @Override
            public void run() {
                Log.e("mDanmaKuShow11",mDanmaKuShow+"");
                if (mDanmaKuShow) {
                    if (!getDanmakuView().isShown())
                        getDanmakuView().show();
                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_hl);
                } else {

                    if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }

                    mBarrage.setImageResource(R.drawable.veido_barrage_icon_nl);

                }
            }
        });

    }

    /**
     开始播放弹幕
     */
    private void onPrepareDanmaku(MySelfGSYVideoPlayer gsyVideoPlayer) {
        if (gsyVideoPlayer.getDanmakuView() != null && !gsyVideoPlayer.getDanmakuView().isPrepared() && gsyVideoPlayer.getParser() != null) {
            gsyVideoPlayer.getDanmakuView().prepare(gsyVideoPlayer.getParser(),
                    gsyVideoPlayer.getDanmakuContext());
        }
    }

    /**
     弹幕偏移
     */
    private void resolveDanmakuSeek(MySelfGSYVideoPlayer gsyVideoPlayer, long time) {
        if (mHadPlay && gsyVideoPlayer.getDanmakuView() != null && gsyVideoPlayer.getDanmakuView().isPrepared()) {
            gsyVideoPlayer.getDanmakuView().seekTo(time);
        }
    }

    /**
     创建解析器对象，解析输入流

     @param stream
     @returnd
     */
    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        // Log.e("dataSource",dataSource.toString());
        parser.load(dataSource);
        return parser;

    }

    /**
     释放弹幕控件
     */
    private void releaseDanmaku(MySelfGSYVideoPlayer danmakuVideoPlayer) {
        if (danmakuVideoPlayer != null && danmakuVideoPlayer.getDanmakuView() != null) {
            Debuger.printfError("release Danmaku!");
            danmakuVideoPlayer.getDanmakuView().release();
        }
    }

    /**
     模拟添加弹幕数据
     */
    private void addDanmaku(String content,int size,boolean islive) {
        //   mDanmakuContext.mDanmakuFactory.notifyDispSizeChanged(mDanmakuContext);
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);

        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        danmaku.text = content;
        Log.e("contentText",content);
        danmaku.padding = 5;
        danmaku.priority = 8;  // 可能会被各种过滤器过滤并隐藏显示，所以提高等级
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
        //     Log.e("弹幕全屏",mParser.getDisplayer().getDensity()+"");
        //    danmaku.textSize = size * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textSize = size * (mParser.getDisplayer().getDensity() - 0.6f);
        // danmaku.textSize = size * (2.75f - 0.6f);
        danmaku.textColor = (int)mChooseColor;
        mDanmakuView.addDanmaku(danmaku);
        DanmuBean danmuBean = new DanmuBean();
        danmuBean.setDisplayTime(getCurrentPositionWhenPlaying()/1000);
        danmuBean.setType(1);
        danmuBean.setFontColor(mChooseColor);
        danmuBean.setDanmuText(content);
        danmuBean.setFontSize(size);
        if (mVideoAllCallBack != null){
            mVideoAllCallBack.onClickSend(mOriginUrl, danmuBean, MySelfGSYVideoPlayer.this);
        }
    }
    public BaseDanmakuParser getParser() {

        return mParser;
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {

        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            MySelfGSYVideoPlayer gsyVideoPlayer = (MySelfGSYVideoPlayer) gsyBaseVideoPlayer;
            //对弹幕设置偏移记录
            gsyVideoPlayer.setDanmakuStartSeekPosition(getCurrentPositionWhenPlaying());
            gsyVideoPlayer.setDanmaKuShow(getDanmaKuShow());
            onPrepareDanmaku(gsyVideoPlayer);
        }
        return gsyBaseVideoPlayer;
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
        mHadSeekTouch = true;
    }

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
                int time = seekBar.getProgress() * getDuration() / 100;
                //  int time = progress  * getDuration() / 100;

                getGSYVideoManager().seekTo(time);
                mBottomProgressBar.setProgress(seekBar.getProgress());
                mProgressBar.setProgress(seekBar.getProgress());
                Log.e("投屏跳转",time+"");
                if(mDLNAPlayer != null){
                    mDLNAPlayer.seekTo(time+"", new DLNAControlCallback() {
                        @Override
                        public void onSuccess(@Nullable ActionInvocation invocation) {
                            Log.e("投屏跳转Success","投屏跳转Success");
                        }

                        @Override
                        public void onReceived(@Nullable ActionInvocation invocation, @Nullable Object... extra) {
                            Log.e("投屏跳转onReceived","投屏跳转onReceived");
                        }

                        @Override
                        public void onFailure(@Nullable ActionInvocation invocation, int errorCode, @Nullable String errorMsg) {
                            Log.e("投屏跳转onFailure","投屏跳转onFailure");
                        }
                    });
                }
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

    @Override
    public void onConnect(DeviceInfo deviceInfo, int errorCode) {
        if (errorCode == CONNECT_INFO_CONNECT_SUCCESS) {
            mDeviceInfo = deviceInfo;
            Toast.makeText(mContext, "连接设备成功", Toast.LENGTH_SHORT).show();
            startPlay();
        }
    }

    @Override
    public void onDisconnect(DeviceInfo deviceInfo, int type, int errorCode) {

    }


    /**
     * 开始投屏播放
     */
    private void startPlay() {
        Log.e("投屏开始播放","投屏开始播放");
        Log.e("mOrginUrl",mOriginUrl+"");
        String sourceUrl = mOriginUrl;
        final MediaInfo mediaInfo = new MediaInfo();
        if (!TextUtils.isEmpty(sourceUrl)) {
            mediaInfo.setMediaId(Base64.encodeToString(sourceUrl.getBytes(), Base64.NO_WRAP));
            mediaInfo.setUri(sourceUrl);
        }
        mediaInfo.setMediaType(curItemType);
        Log.e("投屏信息",mediaInfo.toString());
        mDLNAPlayer.setDataSource(mediaInfo);
        mDLNAPlayer.start("30000",new DLNAControlCallback() {
            @Override
            public void onSuccess(@Nullable ActionInvocation invocation) {
                Toast.makeText(mContext, "投屏成功", Toast.LENGTH_SHORT).show();
                setStateAndUi(CURRENT_STATE_PAUSE);
            }

            @Override
            public void onReceived(@Nullable ActionInvocation invocation, @Nullable Object... extra) {

            }

            @Override
            public void onFailure(@Nullable ActionInvocation invocation, int errorCode, @Nullable String errorMsg) {
                Toast.makeText(mContext, "投屏失败", Toast.LENGTH_SHORT).show();
            }
        });
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
        onPrepareDanmaku(this);


    }
    @Override
    public int getFullId() {
        return GSYVideoManager.FULLSCREEN_ID;
    }


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
                mShare.setVisibility(VISIBLE);
                mMore.setVisibility(VISIBLE);
                mBarrage.setVisibility(VISIBLE);
                mTimeandbarray.setVisibility(VISIBLE);
                playstart.setVisibility(GONE);
                mCurrentTimeTextView.setVisibility(GONE);
                mTotalTimeTextView.setVisibility(GONE);
                mPan.setVisibility(GONE);
                bottom_progressbar.setVisibility(VISIBLE);
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
                mMore.setVisibility(GONE);
                mBarrage.setVisibility(GONE);
                controllerbottom.setVisibility(GONE);
                playstart.setVisibility(VISIBLE);
                mCurrentTimeTextView.setVisibility(VISIBLE);
                mTotalTimeTextView.setVisibility(VISIBLE);
                mTimeandbarray.setVisibility(GONE);
                mPan.setVisibility(GONE);
                bottom_progressbar.setVisibility(GONE);
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




    /**
     * 触摸音量dialog，如需要自定义继承重写即可，记得重写dismissVolumeDialog
     */
    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getVolumeLayoutId(), null);
            if (localView.findViewById(getVolumeProgressId()) instanceof ProgressBar) {
                mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(getVolumeProgressId()));
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.setProgressDrawable(mVolumeProgressDrawable);
                }
            }
            mVolumeDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            mVolumeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            mVolumeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            mVolumeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mVolumeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.START;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mVolumeDialog.getWindow().setAttributes(localLayoutParams);

        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.setProgress(volumePercent);
        }
        if(mDLNAPlayer!=null){
            mDLNAPlayer.setVolume(volumePercent, new DLNAControlCallback() {
                @Override
                public void onSuccess(@Nullable ActionInvocation invocation) {
                    Log.e("投屏设置音量onSuccess","投屏设置音量onSuccess");
                }

                @Override
                public void onReceived(@Nullable ActionInvocation invocation, @Nullable Object... extra) {
                    Log.e("投屏设置音量onReceived","投屏设置音量onReceived");
                }

                @Override
                public void onFailure(@Nullable ActionInvocation invocation, int errorCode, @Nullable String errorMsg) {
                    Log.e("投屏设置音量onFailure","投屏设置音量onFailure");
                }
            });
        }
    }

    @Override
    protected void dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
            mVolumeDialog = null;
        }
    }


    /**
     * 触摸亮度dialog，如需要自定义继承重写即可，记得重写dismissBrightnessDialog
     */
    @Override
    protected void showBrightnessDialog(float percent) {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getBrightnessLayoutId(), null);
            if (localView.findViewById(getBrightnessTextId()) instanceof TextView) {
                mBrightnessDialogTv = (TextView) localView.findViewById(getBrightnessTextId());
            }
            mBrightnessDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            mBrightnessDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            mBrightnessDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            mBrightnessDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mBrightnessDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mBrightnessDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams localLayoutParams = mBrightnessDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.END;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mBrightnessDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (mBrightnessDialogTv != null)
            mBrightnessDialogTv.setText((int) (percent * 100) + "%");
    }


    @Override
    protected void dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
            mBrightnessDialog = null;
        }
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
        danmakuOnResume();
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
        danmakuOnPause();
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

    public void setTextAndProgress(int secProgress) {

        int positionCurrent = getCurrentPositionWhenPlaying();

        int duration = getDuration();

        int progress = (int) (positionCurrent * 100 / (duration == 0 ? 1 : duration));

        if(positionCurrent <= 0 ){
            setProgressAndTime(0, 0, positionCurrent, duration);
        }else {
            setProgressAndTime(progress, secProgress, positionCurrent, duration);

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
        danmakuOnPause();
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
        danmakuOnResume();

    }
    @Override
    public void onCompletion() {
        releaseDanmaku(this);
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
        int time = mProgressBar.getProgress() * getDuration() / 100;
        //如果已经初始化过的，直接seek到对于位置
        if (mHadPlay && getDanmakuView() != null && getDanmakuView().isPrepared()) {
            resolveDanmakuSeek(this, time);
        } else if (mHadPlay && getDanmakuView() != null && !getDanmakuView().isPrepared()) {
            //如果没有初始化过的，记录位置等待
            setDanmakuStartSeekPosition(time);
        }
    }

    protected void danmakuOnPause() {
        Log.e("myselfVideoPlayerlist","danmakuOnPause1");
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            Log.e("myselfVideoPlayerlist","danmakuOnPause2");
            mDanmakuView.pause();
        }
    }

    protected void danmakuOnResume() {
        Log.e("myselfVideoPlayerlist","danmakuOnResume1");
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            Log.e("myselfVideoPlayerlist","danmakuOnResume2");
            mDanmakuView.resume();
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
    }


    public void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {

        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            MySelfGSYVideoPlayer gsyDanmaVideoPlayer = (MySelfGSYVideoPlayer) gsyVideoPlayer;
            setDanmaKuShow(gsyDanmaVideoPlayer.getDanmaKuShow());
            if (gsyDanmaVideoPlayer.getDanmakuView() != null &&
                    gsyDanmaVideoPlayer.getDanmakuView().isPrepared()) {
                resolveDanmakuSeek(this, gsyDanmaVideoPlayer.getCurrentPositionWhenPlaying());
                resolveDanmakuShow();
                releaseDanmaku(gsyDanmaVideoPlayer);
            }
        }
    }
    int fullCount = 0;
    @Override
    public void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        Log.e("播放器横竖屏切换","横竖屏切换");
        MySelfGSYVideoPlayer sf = (MySelfGSYVideoPlayer) from;
        MySelfGSYVideoPlayer st = (MySelfGSYVideoPlayer) to;
        st.surface_container = sf.surface_container;
        st.mContext = sf.mContext;
        st.mUriList = sf.mUriList;
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
        super.clickStartIcon();
    }

    InputMethodManager imm = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
    int fontSize = 20;


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
        final LinearLayout fontDisplay = view.findViewById(R.id.fontDisplay);
        final EditText editText = view.findViewById(R.id.editText);
        final Button send = view.findViewById(R.id.send);

        if(edtextContent.length() != 0 || edtextContent != null){
            editText.setText(edtextContent);
        }
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
                    fontSize = 20;
                }
                if(mfontBigSize){
                    fontSize = 25;
                }
                if(editText.getText().toString().length() > 0){
                    addDanmaku(editText.getText().toString(),fontSize,true);
                }

                if(imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                fontDisplay.setVisibility(GONE);
                onVideoResume();
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置可获得焦点
                Log.e("获取焦点","获取焦点");
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
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                fontDisplay.startAnimation(mHiddenAction);
                fontDisplay.setVisibility(GONE);
                if(!hasFocus){
                    //    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (imm != null)
                        imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // hasFocus = !hasFocus;
                count++;
            }
        });

        fontImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("fontImage","fontImage");
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
                mChooseColor = 16777215;
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
                mChooseColor = 16711935;
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
                mChooseColor = 146114;
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
                mChooseColor = 16737996;
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
                mChooseColor = 1667233;
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
                mChooseColor = 255255;
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
                mChooseColor = 10458123;
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
                mChooseColor = 16711680;
            }
        });


        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                edtextContent = editText.getText().toString();
                controllerbottom.setVisibility(VISIBLE);

            }
        });

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
            setSeekOnStart(errorPosition);
            startButtonLogic();
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            try {
                playstart.setImageResource(R.drawable.vedio_play_icon);
                //    playstart2.setImageResource(R.drawable.video_play_pressed);
                playbottom.setImageResource(R.drawable.vedio_play_icon);
                newstart.setImageResource(R.drawable.vedio_play_icon);
                onVideoPause();
                danmakuOnPause();
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
            danmakuOnResume();
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
        Log.e("播放url开始:" , gsyVideoModel.getUrl());

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

  /*  public class BrowseRegistryListener extends DefaultRegistryListener {
        @Override
        public void deviceAdded(Registry registry, final Device device) {
            super.deviceAdded(registry, device);
            runOnUiThread(new Runnable() {
                public void run() {
                    mDevicesAdapter.add(device);
                }
            });
        }

        @Override
        public void deviceRemoved(Registry registry, final Device device) {
            super.deviceRemoved(registry, device);
            runOnUiThread(new Runnable() {
                public void run() {
                    mDevicesAdapter.remove(device);
                }
            });
        }
    }


    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }*/


}