package com.shuyu.gsyvideoplayer.screening;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import com.shuyu.gsyvideoplayer.nginxserver.nginx.NginxHelper;
import com.shuyu.gsyvideoplayer.org.nanohttpd.webserver.SimpleWebServer;
import com.shuyu.gsyvideoplayer.screening.listener.DLNARegistryListener;
import com.shuyu.gsyvideoplayer.screening.listener.DLNAStateCallback;
import com.shuyu.gsyvideoplayer.screening.log.AndroidLoggingHandler;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;


import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Description： DLNA管理器
 * <BR/>
 * Creator：yankebin
 * <BR/>
 * CreatedAt：2019-07-09
 */
public final class DLNAManager {
    private static final String TAG = "DLNAManager";
    private static final String LOCAL_HTTP_SERVER_PORT = "9578";
    private static boolean isDebugMode = false;
    private Context mContext;
    private AndroidUpnpService mUpnpService;
    private ServiceConnection mServiceConnection;
    private DLNAStateCallback mStateCallback;
    private RegistryListener mRegistryListener;
    private static List<DLNARegistryListener> registryListenerList = new ArrayList<>();
    private Handler mHandler;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isDestory = true;

    public boolean isDestory() {
        return isDestory;
    }

    public void setDestory(boolean destory) {
        isDestory = destory;
    }
    private DLNAManager() {
        AndroidLoggingHandler.injectJavaLogger();
        mHandler = new Handler(Looper.getMainLooper());
        Log.e("投屏initDlna","投屏初始化registry");
        registryListenerList = new ArrayList<>();
        mRegistryListener = new RegistryListener() {
            @Override
            public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.remoteDeviceDiscoveryStarted(registry, device);
                        }
                    }
                });
            }
            @Override
            public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.remoteDeviceDiscoveryFailed(registry, device, ex);
                        }
                    }
                });
            }
            @Override
            public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.remoteDeviceAdded(registry, device);
                        }
                    }
                });
            }
            @Override
            public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.remoteDeviceUpdated(registry, device);
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.remoteDeviceRemoved(registry, device);
                        }
                    }
                });
            }

            @Override
            public void localDeviceAdded(final Registry registry, final LocalDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.localDeviceAdded(registry, device);
                        }
                    }
                });
            }

            @Override
            public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.localDeviceRemoved(registry, device);
                        }
                    }
                });
            }

            @Override
            public void beforeShutdown(final Registry registry) {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.beforeShutdown(registry);
                        }
                    }
                });
            }

            @Override
            public void afterShutdown() {
                mHandler.post(() -> {
                    synchronized (DLNAManager.class) {
                        for (DLNARegistryListener listener : registryListenerList) {
                            listener.afterShutdown();
                        }
                    }
                });
            }
        };

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (null != intent && TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                    final NetworkInfo networkInfo = getNetworkInfo(context);
                    if (null == networkInfo) {
                        return;
                    }
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        initLocalMediaServer();
                    }
                }
            }
        };
    }

    private static class DLNAManagerCreator {
        private static DLNAManager manager = new DLNAManager();
    }

    public static DLNAManager getInstance() {

        return DLNAManagerCreator.manager;
       // return  new DLNAManager();
    }

    public void init(@NonNull Context context) {
        init(context, null);
    }

    public void init(@NonNull Context context, @Nullable DLNAStateCallback stateCallback) {
        Log.e("投屏initDlna","init");
        setDestory(true);
        if(registryListenerList == null){
            registryListenerList = new ArrayList<>();
            mHandler = new Handler(Looper.getMainLooper());
            mRegistryListener = new RegistryListener() {
                @Override
                public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
                    Log.e("投屏initDlna","remoteDeviceDiscoveryStarted");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.remoteDeviceDiscoveryStarted(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
                    Log.e("投屏initDlna","remoteDeviceDiscoveryFailed");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.remoteDeviceDiscoveryFailed(registry, device, ex);
                            }
                        }
                    });
                }

                @Override
                public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
                    Log.e("投屏initDlna","remoteDeviceAdded");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.remoteDeviceAdded(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
                    Log.e("投屏initDlna","remoteDeviceUpdated");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.remoteDeviceUpdated(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
                    Log.e("投屏initDlna","remoteDeviceRemoved");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.remoteDeviceRemoved(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void localDeviceAdded(final Registry registry, final LocalDevice device) {
                    Log.e("投屏initDlna","localDeviceAdded");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.localDeviceAdded(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
                    Log.e("投屏initDlna","localDeviceRemoved");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.localDeviceRemoved(registry, device);
                            }
                        }
                    });
                }

                @Override
                public void beforeShutdown(final Registry registry) {
                    Log.e("投屏initDlna","beforeShutdown");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.beforeShutdown(registry);
                            }
                        }
                    });
                }

                @Override
                public void afterShutdown() {
                    Log.e("投屏initDlna","afterShutdown");
                    mHandler.post(() -> {
                        synchronized (DLNAManager.class) {
                            for (DLNARegistryListener listener : registryListenerList) {
                                listener.afterShutdown();
                            }
                        }
                    });
                }
            };
        }


        if (null != mContext) {
            logW("ReInit DLNAManager");
            return;
        }
        if (context instanceof ContextThemeWrapper || context instanceof android.view.ContextThemeWrapper) {
            mContext = context.getApplicationContext();
        } else {
            mContext = context;
        }
        mStateCallback = stateCallback;
        //rtmp流媒体服务器
        NginxHelper.installNginxServer(mContext);
        initLocalMediaServer();
        initConnection();
        registerBroadcastReceiver();
    }

    private void initConnection() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mUpnpService = (AndroidUpnpService) service;
                //添加对UPNP服务的监听局域网内有哪些支持DLNA的设备
                mUpnpService.getRegistry().addListener(mRegistryListener);
                //开始发现设备
                mUpnpService.getControlPoint().search();
                if (null != mStateCallback) {
                    mStateCallback.onConnected();
                }
                logD("onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mUpnpService = null;
                if (null != mStateCallback) {
                    mStateCallback.onDisconnected();
                }
                logD("onServiceDisconnected");
            }
        };

        mContext.bindService(new Intent(mContext, DLNABrowserService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 本地视频和图片也可以直接投屏，根目录为sd卡根目录
     */
    private void initLocalMediaServer() {
        checkConfig();
        try {
            //rtmp流媒体服务器
            NginxHelper.stopNginxServer();
            NginxHelper.startNginxServer();
            //本地普通多媒体服务器
            final PipedOutputStream pipedOutputStream = new PipedOutputStream();
            System.setIn(new PipedInputStream(pipedOutputStream));
            new Thread(() -> {
                final String localIpAddress = getLocalIpStr(mContext);
                final String localMediaRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String[] args = {
                        "--host",
                        localIpAddress,/*局域网ip地址*/
                        "--port",
                        LOCAL_HTTP_SERVER_PORT,/*局域网端口*/
                        "--dir",
                        localMediaRootPath/*下载视频根目录*/
                };
                SimpleWebServer.startServer(args);
                logD("initLocalLinkService success,localIpAddress : " + localIpAddress +
                        ",localVideoRootPath : " + localMediaRootPath);
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            logE("initLocalLinkService failure", e);
        }
    }

    private void registerBroadcastReceiver() {
        checkConfig();
        mContext.registerReceiver(mBroadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterBroadcastReceiver() {
        checkConfig();
        if(mBroadcastReceiver != null){
            mContext.unregisterReceiver(mBroadcastReceiver);
        }

    }

    public void registerListener(DLNARegistryListener listener) {
        checkConfig();
        checkPrepared();
        Log.e("投屏initDlna",listener.toString());
        if (null == listener) {
            return;
        }

        Log.e("投屏initDlna","registryListenerList不为空");
        registryListenerList.add(listener);
        listener.onDeviceChanged(mUpnpService.getRegistry().getDevices());
    }

    public void unregisterListener(DLNARegistryListener listener) {
        checkConfig();
        checkPrepared();
        if (null == listener) {
            return;
        }
        mUpnpService.getRegistry().removeListener(listener);
        registryListenerList.remove(listener);
    }

    public void startBrowser() {
        checkConfig();
        checkPrepared();
        mUpnpService.getRegistry().addListener(mRegistryListener);
        mUpnpService.getControlPoint().search();
    }

    public void stopBrowser() {
        checkConfig();
        checkPrepared();
        mUpnpService.getRegistry().removeListener(mRegistryListener);
    }

    public void destroy() {
        Log.e("DLNAManager","mDLNAManager销毁");
        checkConfig();
        registryListenerList.clear();
        unregisterBroadcastReceiver();
        SimpleWebServer.stopServer();
        stopBrowser();
        if (null != mUpnpService) {
            mUpnpService.getRegistry().removeListener(mRegistryListener);
            mUpnpService.getRegistry().shutdown();
        }
        if (null != mServiceConnection) {
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        registryListenerList = null;
        mRegistryListener = null;
        mBroadcastReceiver = null;
        mStateCallback = null;
        mContext = null;
    }

    private void checkConfig() {
        if (null == mContext) {
            throw new IllegalStateException("Must call init(Context context) at first");
        }
    }

    private void checkPrepared() {
        if (null == mUpnpService) {
            throw new IllegalStateException("Invalid AndroidUpnpService");
        }
    }

    //------------------------------------------------------静态方法-----------------------------------------------

    /**
     * 获取ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpStr(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null == wifiInfo) {
            return "";
        }
        return intToIpAddress(wifiInfo.getIpAddress());
    }

    /**
     * int类型的ip转换成标准ip地址
     *
     * @param ip
     * @return
     */
    public static String intToIpAddress(int ip) {
        return (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
    }

    public static NetworkInfo getNetworkInfo(@NonNull Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return null == connectivityManager ? null : connectivityManager.getActiveNetworkInfo();
    }

    static String tryTransformLocalMediaAddressToLocalHttpServerAddress(@NonNull Context context,
                                                                        String sourceUrl) {
        logD("tryTransformLocalMediaAddressToLocalHttpServerAddress ,sourceUrl : " + sourceUrl);
        if (TextUtils.isEmpty(sourceUrl)) {
            return sourceUrl;
        }

        if (!isLocalMediaAddress(sourceUrl)) {
            return sourceUrl;
        }

        String newSourceUrl = getLocalHttpServerAddress(context) +
                sourceUrl.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
        logD("tryTransformLocalMediaAddressToLocalHttpServerAddress ,newSourceUrl : " + newSourceUrl);

        try {
            final String[] urlSplits = newSourceUrl.split("/");
            final String originFileName = urlSplits[urlSplits.length - 1];
            String fileName = originFileName;
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileName.replaceAll("\\+", "%20");
            newSourceUrl = newSourceUrl.replace(originFileName, fileName);
            logD("tryTransformLocalMediaAddressToLocalHttpServerAddress ,encodeNewSourceUrl : " + newSourceUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return newSourceUrl;
    }

    private static boolean isLocalMediaAddress(String sourceUrl) {
        return !TextUtils.isEmpty(sourceUrl)
                && !sourceUrl.startsWith("http://")
                && !sourceUrl.startsWith("https://")
                && sourceUrl.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    /**
     * 获取本地http服务器地址
     *
     * @param context
     * @return
     */
    public static String getLocalHttpServerAddress(Context context) {
        return "http://" + getLocalIpStr(context) + ":" + LOCAL_HTTP_SERVER_PORT;
    }

    public static void setIsDebugMode(boolean isDebugMode) {
        DLNAManager.isDebugMode = isDebugMode;
    }


    static void logV(String content) {
        logV(TAG, content);
    }

    public static void logV(String tag, String content) {
        if (!isDebugMode) {
            return;
        }
        Log.v(tag, content);
    }

    static void logD(String content) {
        logD(TAG, content);
    }

    public static void logD(String tag, String content) {
        if (!isDebugMode) {
            return;
        }
        Log.d(tag, content);
    }


    static void logI(String content) {
        logI(TAG, content);
    }

    public static void logI(String tag, String content) {
        if (!isDebugMode) {
            return;
        }
        Log.i(tag, content);
    }


    static void logW(String content) {
        logW(TAG, content);
    }

    public static void logW(String tag, String content) {
        if (!isDebugMode) {
            return;
        }
        Log.w(tag, content);
    }


    static void logE(String content) {
        logE(TAG, content);
    }


    public static void logE(String tag, String content) {
        logE(tag, content, null);
    }


    static void logE(String content, Throwable throwable) {
        logE(TAG, content, throwable);
    }

    public static void logE(String tag, String content, Throwable throwable) {
        if (!isDebugMode) {
            return;
        }
        if (null != throwable) {
            Log.e(tag, content, throwable);
        } else {
            Log.e(tag, content);
        }
    }
}
