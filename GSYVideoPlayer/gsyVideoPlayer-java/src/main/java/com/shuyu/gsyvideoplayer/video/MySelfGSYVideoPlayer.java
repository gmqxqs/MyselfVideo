package com.shuyu.gsyvideoplayer.video;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.MyselfView.BatteryView;
import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.AppContext;
import com.shuyu.gsyvideoplayer.utils.BiliDanmukuParser;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.FontColorEvent;
import com.shuyu.gsyvideoplayer.utils.InitDanmuEvent;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
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
import java.io.FileNotFoundException;
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
import moe.codeest.enviews.ENDownloadView;
import moe.codeest.enviews.ENPlayView;

public class MySelfGSYVideoPlayer extends StandardGSYVideoPlayer implements SeekBar.OnSeekBarChangeListener {
    private static final int msgKey1 = 1;
    protected  int count = 0;
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
    private  BaseDanmakuParser mParser;//解析器对象
    private  IDanmakuView mDanmakuView;//弹幕view
    private  DanmakuContext mDanmakuContext;
    private long mDanmakuStartSeekPosition = -1;
    private boolean mDanmaKuShow = false;
    private File mDumakuFile;
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
    protected ProgressBar bottom_progressbar;
    protected  LinearLayout layout_bottom;
    protected  TextView batteryText;
    protected BatteryView batteryView;
    protected  LinearLayout mDanmu;
    private boolean mSwitch = false;
    protected boolean isDown = false;
    protected boolean isNormal = false;
    protected boolean isComplete = false;
    private SeekBar mSeekBar;
    private GSYVideoManager mTmpManager;
    private boolean mTouch = true;
    private moe.codeest.enviews.ENDownloadView enDownloadView;
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
    private List<Boolean> fontColorList;
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
        // mWidgetContainer = (ViewGroup) findViewById(R.id.widget_container);
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
        mCurrentbottom = findViewById(R.id.currentbottom);
        mTotalbottom = findViewById(R.id.totalbottom);
        replay = findViewById(R.id.replay);
        replay_text = findViewById(R.id.replay_text);
        bottom_progressbar = findViewById(R.id.bottom_progressbar);
        mDanmu = findViewById(R.id.danmu);

   /*     edit_danmu.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    String text = edit_danmu.getText().toString();
                    if(text.length() > 0 ){//判断文本框是否有文字，如果有就去掉最后一位
                        String newText = text.substring(0, text.length() - 1);
                        edit_danmu.setText(newText);
                        edit_danmu.setSelection(newText.length());//设置焦点在最后
                    };
                }
                return false;
            }
        });*/

        new TimeThread().start();
        enDownloadView = findViewById(R.id.loading2);
        mDanmu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow();
            }
        });
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
        Log.e("播放弹幕11","播放弹幕");
        initDanmaku(danmuBeanList,fileName);
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
     * 向SD卡写入一个XML文件
     *
     * @param
     */
    public void savexml(List<DanmuBean> list,String name) {

        try {

            File file = new File(mContext.getExternalCacheDir(),
                    name + ".xml");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            Log.e("弹幕文件",file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startTag(null, "i");
            for (int i = 0; i < list.size(); i++) {
                serializer.startTag(null, "d");
                serializer.attribute(null, "p",list.get(i).getDisplayTime()+","+list.get(i).getType()+","+list.get(i).getFontSize()+","+list.get(i).getFontColor());
                // 写姓名
               /* serializer.startTag(null, "name");
                serializer.text("张三" + i);
                serializer.endTag(null, "name");
                // 写性别
                serializer.startTag(null, "gender");
                serializer.text("男" + i);
                serializer.endTag(null, "gender");
                // 写年龄
                serializer.startTag(null, "age");
                serializer.text("1" + i);
                serializer.endTag(null, "age");
                serializer.endTag(null, "person");*/
                serializer.text(list.get(i).getDanmuText());
                serializer.endTag(null, "d");
            }
            serializer.endTag(null, "i");
            serializer.endDocument();
            fos.close();
           // Toast.makeText(CommonUtil.getActivityContext(mContext), "写入成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
          //  Toast.makeText(CommonUtil.getActivityContext(mContext), "写入失败", Toast.LENGTH_SHORT).show();
        }

    }



    public void initDanmaku(List<DanmuBean> list,String name) {
        Log.e("初始化弹幕","初始化弹幕");
        if(list.size() == 0 || list == null){
            return;
        }
        if(name.length() == 0 || name == null){
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
            Log.e("mDumakuFile",mDumakuFile+"");
            if (mDumakuFile != null) {
                mParser = createParser(getIsStream(mDumakuFile));
            }

            //todo 这是为了demo效果，实际上需要去掉这个，外部传输文件进来


            savexml(list,name);
            File file = new File(mContext.getExternalCacheDir(),
                    "persons.xml");
            try {
                InputStream in = new FileInputStream(file);
                Log.e("mParser",in+"");
                mParser = createParser(in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }




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
                        if (getDanmakuStartSeekPosition() != -1) {
                            resolveDanmakuSeek(MySelfGSYVideoPlayer.this, getDanmakuStartSeekPosition());
                            setDanmakuStartSeekPosition(-1);
                        }
                        resolveDanmakuShow();
                    }
                }
            });
            mDanmakuView.enableDanmakuDrawingCache(true);

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

      /*  post(new Runnable() {
            @Override
            public void run() {
                if (mDanmaKuShow) {
                    if (!getDanmakuView().isShown())
                        getDanmakuView().show();

                } else {
                    if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }

                }
            }
        });*/
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
     @return
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
        Log.e("弹幕全屏",danmaku+"");
        Log.e("弹幕全屏",mDanmakuContext+"");
        Log.e("弹幕全屏",mDanmakuContext.mDanmakuFactory+"");
        Log.e("弹幕全屏",mDanmakuView+"");
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        danmaku.text = "这是一条弹幕 " + content;
        danmaku.padding = 5;
        danmaku.priority = 8;  // 可能会被各种过滤器过滤并隐藏显示，所以提高等级
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
   //     Log.e("弹幕全屏",mParser.getDisplayer().getDensity()+"");
    //    danmaku.textSize = size * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textSize = size * (mParser.getDisplayer().getDensity() - 0.6f);
       // danmaku.textSize = size * (2.75f - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }
    public BaseDanmakuParser getParser() {
        if (mParser == null) {
            if (mDumakuFile != null) {
                mParser = createParser(getIsStream(mDumakuFile));
            }
        }
        return mParser;
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
        //EventBus.getDefault().post(new InitDanmuEvent(danmuBeanList,fileName));
        return gsyBaseVideoPlayer;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(InitDanmuEvent e) {

     // initDanmaku(e.getList(),e.getFileName());
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

                System.out.println("time3:" + CommonUtil.stringForTime(time));
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
        // resetProgressAndTime();
    }

    @Override
    public  void  onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        System.out.println("进度条改变");
        if (getGSYVideoManager() != null && mHadPlay) {
            try {
                //int progress = seekBar.getProgress();
                int time = seekBar.getProgress() * getDuration() / 100;
                errortime = seekBar.getProgress() * getDuration() / 100;
                mBottomProgressBar.setProgress(seekBar.getProgress());
                System.out.println("seekBarProgress():"+seekBar.getProgress());
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
        onPrepareDanmaku(this);


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
                mBarrage.setVisibility(VISIBLE);
                mTimeandbarray.setVisibility(VISIBLE);
                playstart.setVisibility(GONE);
                mCurrentTimeTextView.setVisibility(GONE);
                mTotalTimeTextView.setVisibility(GONE);
                mPan.setVisibility(GONE);
                bottom_progressbar.setVisibility(VISIBLE);
                controllerbottom.setVisibility(VISIBLE);
                playstart2.setVisibility(VISIBLE);

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
        setViewShowState(mTopContainer, VISIBLE);
  //      setViewShowState(playstart2,VISIBLE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, GONE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, GONE);
       // setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        setViewShowState(mLockScreen, mIfCurrentIsFullscreen? VISIBLE : GONE);
        updateStartImage();
       /* if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    @Override
    protected void changeUiToPreparingShow() {
        Log.e("播放器","changeUiToPreparing");
        setViewShowState(mTopContainer, VISIBLE);
  //      setViewShowState(playstart2,VISIBLE);
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


    private  int mCurrentposition = 0;

    @Override
    protected void changeUiToPlayingShow() {
        Log.e("播放器","changeUiToPlayingShow");
        if(mSwitch){
            hideLoading();
        }
        mTouch = true;
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
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

       /* if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/

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

        setViewShowState(mTopContainer, VISIBLE);
  //      setViewShowState(playstart2,VISIBLE);
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
        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
            setViewShowState(getFullscreenButton(),GONE);
        } else{
            setViewShowState(controllerbottom, GONE);
            setViewShowState(getFullscreenButton(),VISIBLE);
        }
        upPlayImage();
    }


    protected void changeUiToChangeShow() {
        Log.e("播放器","changeUiToChangeShow");
        mTouch = false;


        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
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
    }
    protected void changeUiToChangeClear() {
      //  mTouch = true;
        Log.e("播放器","changeUiToChangeClear");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(playstart2,GONE);
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
    }



    @Override
    protected void changeUiToCompleteShow() {
        Log.e("播放器","changeUiToCompleteShow");
        setViewShowState(mTopContainer, VISIBLE);
  //      setViewShowState(playstart2,VISIBLE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, VISIBLE);
        setViewShowState(replay, VISIBLE);
        setViewShowState(replay_text, VISIBLE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
       // setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        // updateStartImage();
        if(mIfCurrentIsFullscreen){
            setViewShowState(controllerbottom, VISIBLE);
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
        Log.e("播放器","changeUiToError");
        String time = mCurrentTimeTextView.getText().toString();
        System.out.println("time:"+time);

        errortime =  CommonUtil.intForTime(time);
        setViewShowState(mTopContainer, GONE);
        setViewShowState(playstart2,GONE);
        setViewShowState(mBottomContainer, GONE);
        setViewShowState(controllerbottom, GONE);
        setViewShowState(mTimeandbarray,GONE);
        setViewShowState(newstart, VISIBLE);
      //  int position = getCurrentPositionWhenPlaying();
        Log.e("isDown:",isDown+"");
        if(isDown){
            Log.e("isDown1:",isDown+"");
            //setViewShowState(newstart,GONE);
            if(NetworkUtils.isConnected(contextFirst)){
                //playNextUrl(errortime);

                if(mUriList.size() > 0){
                    String url = mUriList.get(mSourcePosition).getUrl();
                    File file = new File(url);
                    if(file.exists()){
                        Log.e("文件存在","文件存在");
                        resolveStartChange();
                        isDown = false;
                    } else {
                        mSourcePosition++;
                        Log.e("文件不存在","文件不存在");
                        setUp(mUriList.get(mSourcePosition).getUrl(),true,mUriList.get(mSourcePosition).getTitle());
                        startPlayLogic();
                    }
                }

            } else{
               /* playNextUrl(errortime);
                isDown = false;*/
                releaseVideos();
                return;
            }

        } else{
            setViewShowState(newstart,GONE);
           // playNextUrl(errortime);
            if(mUriList.size() > 0 ){
                String url = mUriList.get(0).getUrl();
                File file = new File(url);
                if(file.exists()){

                    resolveStartChange();
                } else {

                    setUp(mUriList.get(mSourcePosition).getUrl(),true,mUriList.get(mSourcePosition).getTitle());
                    startPlayLogic();
                }

            }


        }

        setViewShowState(replay, VISIBLE);
        setViewShowState(replay_text, GONE);
        setViewShowState(mLoadingProgressBar, GONE);
        setViewShowState(mThumbImageViewLayout, GONE);
     //   setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLockScreen,(mIfCurrentIsFullscreen) ? VISIBLE : GONE);
        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();*/
        upPlayImage();
    }

    private void showLoading() {
      //  hideLoading();
        hideLoading();
        mLoadingDialog = new LoadingDialog2(mContext);
        mLoadingDialog.show();
        setViewShowState(enDownloadView,View.VISIBLE);
        if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
            enDownloadView.start();
        }
    }

    private void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        setViewShowState(enDownloadView,View.GONE);
    }

    protected void changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear");
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
        /*if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    protected void changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear");
        setViewShowState(mTopContainer, GONE);
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

      /*  if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }*/
        upPlayImage();
    }

    protected void changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear");
        setViewShowState(mTopContainer, GONE);
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
      //  danmakuOnPause();
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

    public void setDanmaKuStream(File is) {
        mDumakuFile = is;
        if (!getDanmakuView().isPrepared()) {
            onPrepareDanmaku((MySelfGSYVideoPlayer) getCurrentPlayer());
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

       /* if (oldF != null && oldF.getParent() != null) {
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
        }*/
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
        MySelfGSYVideoPlayer sf = (MySelfGSYVideoPlayer) from;
        MySelfGSYVideoPlayer st = (MySelfGSYVideoPlayer) to;
        st.surface_container = sf.surface_container;
        st.mUriList = sf.mUriList;
        st.urls = sf.urls;
        st.isDown = sf.isDown;
        st.mTouch = sf.mTouch;
        st.count = sf.count;
        st.mDanmaKuShow = sf.mDanmaKuShow;
        st.
        fullCount++;
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
        super.clickStartIcon();
    }

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

        // 这里检验popWindow里的button是否可以点击
         /*    Button first = (Button) view.findViewById(R.id.first);

        first.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });*/

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
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mfontSmallSize){
                    fontSize = 20;
                }
                if(mfontBigSize){
                    fontSize = 25;
                }

                addDanmaku(editText.getText().toString(),fontSize,true);
                Toast.makeText(mContext,"发送成功",Toast.LENGTH_SHORT);
                Log.e("弹幕全屏","发送成功");
            }
        });
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
                Log.e("editText","editText");
                fontDisplay.setVisibility(GONE);
                if(!hasFocus){
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
               // hasFocus = !hasFocus;
            }
        });

        fontImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","fontImage");
                InputMethodManager imm = (InputMethodManager)
                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                fontDisplay.setVisibility(VISIBLE);
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
            }
        });


        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
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
            System.out.println("temp:"+temp.length);
            for(int i = 0; i < temp.length; i++){
                System.out.println("temp:" + temp[i]);
            }
        }
        if(temp.length > 1){
            list.add(temp[0]);
            list.add(temp[1]);
        } else if (temp.length <= 1){
            list.add(temp[0]);
        }

        for(int i = 0; i < list.size(); i++){
            System.out.println("tempUrl:"+list.get(i));
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
        System.out.println("positionUrl:" + position);
        GSYVideoModel gsyVideoModel = url.get(position);
        if (gsyVideoModel instanceof MySelfGSYVideoPlayer.GSYADVideoModel) {
            MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;
            System.out.println("url.size():" + url.size());
            if (gsyadVideoModel.isSkip() && position < (url.size() - 1)) {
                System.out.println("广告");
                return setUp(url, cacheWithPlay, position + 1, cachePath, mapHeadData, changeState);
            }


            // isAdModel = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
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


    private void resolveStartChange() {
       // final String name = mUrlList.get(position).getName();
       // if (mSourcePosition != position) {
            if ((mCurrentState == GSYVideoPlayer.CURRENT_STATE_PLAYING
                    || mCurrentState == GSYVideoPlayer.CURRENT_STATE_PAUSE || mCurrentState == GSYVideoPlayer.CURRENT_STATE_ERROR)) {

                if (mSourcePosition < (mUriList.size() - 1)) {
                    if(!isComplete){
                        mSourcePosition += 1;
                    }
                }
                showLoading();
                Log.e("mSourcePosition",mSourcePosition+"");
                final String url = mUriList.get(mSourcePosition).getUrl();
                Log.e("播放url",url);
                cancelProgressTimer();
                hideAllWidget();
                if (mTitle != null && mTitleTextView != null) {
                    mTitleTextView.setText(mTitle);
                }
                /*mPreSourcePosition = mSourcePosition;
                isChanging = true;
                mTypeText = name;
                mSwitchSize.setText(name);
                mSourcePosition = position;*/
                //创建临时管理器执行加载播放
                mTmpManager = GSYVideoManager.tmpInstance(gsyMediaPlayerListener);
                mTmpManager.initContext(getContext().getApplicationContext());
                resolveChangeUrl(mCache, mCachePath, url);
                mTmpManager.prepare(mUrl, mMapHeadData, mLooping, mSpeed, mCache, mCachePath, null);
                //changeUiToPlayingBufferingShow();
                changeUiToChangeShow();
            }
        //}
        /*else {
            Toast.makeText(getContext(), "已经是 " + name, Toast.LENGTH_LONG).show();
        }*/
    }

    private void resolveChangeUrl(boolean cacheWithPlay, File cachePath, String url) {
        if (mTmpManager != null) {
            mCache = cacheWithPlay;
            mCachePath = cachePath;
            mOriginUrl = url;
            this.mUrl = url;
        }
    }
    private int mSourcePosition = 0;
    private void resolveChangedResult() {
       // isChanging = false;
        mTmpManager = null;
    /*    final String name = mUrlList.get(mSourcePosition).getName();
        final String url = mUrlList.get(mSourcePosition).getUrl();
        mTypeText = name;
        mSwitchSize.setText(name);*/
        final String url = mUriList.get(mSourcePosition).getUrl();
        resolveChangeUrl(mCache, mCachePath, url);
       // hideLoading();
        mSwitch = true;
    }
    private GSYMediaPlayerListener gsyMediaPlayerListener = new GSYMediaPlayerListener() {
        @Override
        public void onPrepared() {
            if (mTmpManager != null) {
                mTmpManager.start();
            //    Log.e("errorTime2222:",errortime+"");
                mTmpManager.seekTo(errortime);
               // mTouch = true;
            }
        }

        @Override
        public void onAutoCompletion() {
            Log.e("TAutoCompletion","AutoCompletion");
        }

        @Override
        public void onCompletion() {
            Log.e("TCompletion","AutoCompletion");
        }

        @Override
        public void onBufferingUpdate(int percent) {
            Log.e("TBufferingUpdate111","BufferingUpdate"+percent);
        }

        @Override
        public void onSeekComplete() {
            if (mTmpManager != null) {
                GSYVideoBaseManager manager = GSYVideoManager.instance();
                GSYVideoManager.changeManager(mTmpManager);
                mTmpManager.setLastListener(manager.lastListener());
                mTmpManager.setListener(manager.listener());
                manager.setDisplay(null);
              /*  Debuger.printfError("**** showDisplay onSeekComplete ***** " + mSurface);
                Debuger.printfError("**** showDisplay onSeekComplete isValid ***** " + mSurface.isValid());*/
                mTmpManager.setDisplay(mSurface);
                changeUiToChangeClear();
                resolveChangedResult();
                manager.releaseMediaPlayer();
              //  hideLoading();

            }
        }



        @Override
        public void onError(int what, int extra) {
         //   mSourcePosition = mPreSourcePosition;
            Log.e("failplay",what+"");
            if (mTmpManager != null) {
                mTmpManager.releaseMediaPlayer();
            }
            post(new Runnable() {
                @Override
                public void run() {
                    resolveChangedResult();
                    Toast.makeText(mContext, "change Fail", Toast.LENGTH_LONG).show();
                    hideLoading();
                }
            });
        }

        @Override
        public void onInfo(int what, int extra) {
            Log.e("TBufferingUpdate222",what+"-----"+extra);
        }

        @Override
        public void onVideoSizeChanged() {
            Log.e("onVideoSizeChanged","onVideoSizeChanged");
        }

        @Override
        public void onBackFullscreen() {
            Log.e("onBackFullscreen","onBackFullscreen");
        }

        @Override
        public void onVideoPause() {

        }

        @Override
        public void onVideoResume() {
            Log.e("onVideoResume","onVideoResume");
        }

        @Override
        public void onVideoResume(boolean seek) {
            Log.e("onVideoResume","onVideoResume"+seek);
        }
    };






    /**
     * 播放下一集
     *
     * @return true表示还有下一集
     */
    /*public boolean playNextUrl(int position) {
        if (isDown){
            System.out.println("离线mPlayPosition:"+mPlayPosition);
            System.out.println("离线mUriList:"+ mUriList.size());
            if (mPlayPosition < (mUriList.size() - 1)) {
                if(!isComplete){
                    mPlayPosition += 1;
                }

                System.out.println("mPlayPositionNormal:" + mPlayPosition );
                GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
                MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;

                mSaveChangeViewTIme = 0;
                setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
                if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                    mTitleTextView.setText(gsyVideoModel.getTitle());
                }

                System.out.println("当前播的位置:"+position);
                setSeekOnStart(position);
                startPlayLogic();
                return true;
            }

        } else if(!isDown) {
         *//*   if(mUriList.size() == 1 ){
                mPlayPosition = 0;
            } else{
                mPlayPosition -= 1;
            }*//*

            if (mPlayPosition < (mUriList.size() - 1)) {
                if(!isComplete){
                    mPlayPosition += 1;
                }
                Log.e("mPlayPositionDown",mPlayPosition+"");
                Log.e("错误的网络位置:" , position+"");
                GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
                MySelfGSYVideoPlayer.GSYADVideoModel gsyadVideoModel = (MySelfGSYVideoPlayer.GSYADVideoModel) gsyVideoModel;
                isDown = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_DOWN);
                isNormal = (gsyadVideoModel.getType() == GSYADVideoModel.TYPE_NORMAL);
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

        }
    *//*
            System.out.println("startTime:" + time);
            VideoOptionModel videoOptionModel =
                    new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "seek-at-start", time);
            List<VideoOptionModel> list = new ArrayList<>();
            list.add(videoOptionModel);
            GSYVideoManager.instance().setOptionModelList(list);*//**//*
            System.out.println("当前播的位置:"+position);
            setSeekOnStart(position);
            startPlayLogic();
            return true;
        }*//*

        return false;
    }*/

   /* @Override
    public boolean onSurfaceDestroyed(Surface surface) {
        Log.e("播放器","播放器清空");
        //清空释放
        setDisplay(null);
        //同一消息队列中去release
        //todo 需要处理为什么全屏时全屏的surface会被释放了
       // releaseSurface(surface);
        return true;
    }*/




}