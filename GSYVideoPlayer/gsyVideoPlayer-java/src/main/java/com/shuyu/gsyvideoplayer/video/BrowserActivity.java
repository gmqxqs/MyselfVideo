package com.shuyu.gsyvideoplayer.video;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.shuyu.gsyvideoplayer.BuildConfig;
import com.shuyu.gsyvideoplayer.R;
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
import org.fourthline.cling.model.action.ActionInvocation;
import java.util.List;
public class BrowserActivity extends AppCompatActivity implements DLNADeviceConnectListener {
    private static final String TAG = BrowserActivity.class.getSimpleName();
    private static final int CODE_REQUEST_PERMISSION = 1010;
    private static final int CODE_REQUEST_MEDIA = 1011;
    private int curItemType = MediaInfo.TYPE_UNKNOWN;
    private String mMediaPath;
    private DeviceInfo mDeviceInfo;
    private DLNAPlayer mDLNAPlayer;
    private DLNARegistryListener mDLNARegistryListener;
    private DevicesAdapter mDevicesAdapter;
    private ListView mDeviceListView;
    public static void forward(Context context) {
        context.startActivity(new Intent(context, BrowserActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLNAManager.setIsDebugMode(BuildConfig.DEBUG);
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(CODE_REQUEST_PERMISSION,
                this, new PermissionsRequestCallback() {
                    @Override
                    public void onGranted(int requestCode, String permission) {
                        boolean hasPermission = PackageManager.PERMISSION_GRANTED ==
                                (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        & checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO));
                        if (hasPermission) {
                            DLNAManager.getInstance().init(BrowserActivity.this, new DLNAStateCallback() {
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
        setContentView(R.layout.activity_brower);
        mDeviceListView = findViewById(R.id.deviceListView);
        final View emptyView = findViewById(R.id.layoutDeviceEmpty);
        mDeviceListView.setEmptyView(emptyView);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curItemType = MediaInfo.TYPE_VIDEO;
                DeviceInfo deviceInfo = mDevicesAdapter.getItem(position);
                if (null == deviceInfo) {
                    return;
                }
                mDLNAPlayer.connect(deviceInfo);
            }
        });
        mDevicesAdapter = new DevicesAdapter(BrowserActivity.this);
        mDeviceListView.setAdapter(mDevicesAdapter);
    }


    @Override
    public void onConnect(DeviceInfo deviceInfo, int errorCode) {
        if (errorCode == CONNECT_INFO_CONNECT_SUCCESS) {
            mDeviceInfo = deviceInfo;
            Toast.makeText(this, "连接设备成功", Toast.LENGTH_SHORT).show();
            seekTo("50");
            startPlay();
        }
    }

    @Override
    public void onDisconnect(DeviceInfo deviceInfo, int type, int errorCode) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_select_image){
            selectImage();
        }
        if(id == R.id.action_select_audio){
            selectAudio();
        }
        if(id == R.id.action_select_video){
            selectVideo();
        }
        if(id == R.id.action_screening_phone){
            screeningPhone();
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectVideo() {
        curItemType = MediaInfo.TYPE_VIDEO;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CODE_REQUEST_MEDIA);
    }

    private void selectAudio() {
        curItemType = MediaInfo.TYPE_AUDIO;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CODE_REQUEST_MEDIA);
    }

    private void selectImage() {
        curItemType = MediaInfo.TYPE_IMAGE;
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CODE_REQUEST_MEDIA);
    }

    private void screeningPhone() {
        curItemType = MediaInfo.TYPE_MIRROR;
        if (null != mDLNAPlayer && null != mDeviceInfo) {
            mDLNAPlayer.connect(mDeviceInfo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("投屏requestCode",requestCode+"");
        Log.e("投屏resultCode",resultCode+"");
        Log.e("投屏data",data+"");
        if (requestCode == CODE_REQUEST_MEDIA) {
            if (resultCode != RESULT_OK && data == null) {
                return;
            }
            Uri uri = data.getData();
            Log.e("投屏uri",uri+"");
            String path;
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                path = Util.getRealPathFromUriAboveApi19(this, uri);
                Log.e("投屏path1",path+"");
            } else {
                path = uri.getPath();
                Log.e("投屏path2",path+"");
            }
            Log.e("投屏path",path+"");
            mMediaPath = path;
            Log.d(TAG, path);
            if (null != mDLNAPlayer && null != mDeviceInfo) {
                mDLNAPlayer.connect(mDeviceInfo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mDLNAPlayer != null) {
            mDLNAPlayer.destroy();
        }
        DLNAManager.getInstance().unregisterListener(mDLNARegistryListener);
        DLNAManager.getInstance().destroy();
        super.onDestroy();
    }

    private void initDlna() {
        Log.e("投屏initDlna","投屏initDlna");
        mDLNAPlayer = new DLNAPlayer(this);
        mDLNAPlayer.setConnectListener(this);
        mDLNARegistryListener = new DLNARegistryListener() {
            @Override
            public void onDeviceChanged(List<DeviceInfo> deviceInfoList) {
                Log.e("投屏设备act",deviceInfoList.size()+"");
                mDevicesAdapter.clear();
                mDevicesAdapter.addAll(deviceInfoList);
                mDevicesAdapter.notifyDataSetChanged();

            }
        };
        DLNAManager.getInstance().registerListener(mDLNARegistryListener);
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        Log.e("投屏开始播放","投屏开始播放");
      //  String sourceUrl = mMediaPath;
        String sourceUrl = "https://youku.com-ok-pptv.com/20190901/6570_497d32b7/index.m3u8";
        Log.e("mOrginUrl",sourceUrl+"");
        final MediaInfo mediaInfo = new MediaInfo();
        if (!TextUtils.isEmpty(sourceUrl)) {
            mediaInfo.setMediaId(Base64.encodeToString(sourceUrl.getBytes(), Base64.NO_WRAP));
            mediaInfo.setUri(sourceUrl);
        }
        mediaInfo.setMediaType(curItemType);
        Log.e("投屏信息",mediaInfo.toString());
        mDLNAPlayer.setDataSource(mediaInfo);
        mDLNAPlayer.start("",new DLNAControlCallback() {
            @Override
            public void onSuccess(@Nullable ActionInvocation invocation) {
                Log.e("checkErrorBeforeExecute","投屏成功");
                Toast.makeText(BrowserActivity.this, "投屏成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onReceived(@Nullable ActionInvocation invocation, @Nullable Object... extra) {

            }

            @Override
            public void onFailure(@Nullable ActionInvocation invocation, int errorCode, @Nullable String errorMsg) {
                Toast.makeText(BrowserActivity.this, "投屏失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  seekTo(String time){
        Log.e("checkErrorBeforeExecute","开始跳转");
        mDLNAPlayer.seekTo(time, new DLNAControlCallback() {
            @Override
            public void onSuccess(@Nullable ActionInvocation invocation) {
                Log.e("跳转成功","跳转成功");
            }

            @Override
            public void onReceived(@Nullable ActionInvocation invocation, @Nullable Object... extra) {

            }

            @Override
            public void onFailure(@Nullable ActionInvocation invocation, int errorCode, @Nullable String errorMsg) {
                Log.e("跳转失败","跳转失败");
            }
        });
    }
}
