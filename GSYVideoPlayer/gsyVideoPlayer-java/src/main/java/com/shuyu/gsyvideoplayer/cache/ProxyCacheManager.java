package com.shuyu.gsyvideoplayer.cache;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.FileUtils;
import com.shuyu.gsyvideoplayer.utils.HttpConnect;
import com.shuyu.gsyvideoplayer.utils.Md5Utils;
import com.shuyu.gsyvideoplayer.utils.StorageUtils;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 代理缓存管理器
 Created by guoshuyu on 2018/5/18.
 */

public class ProxyCacheManager implements ICacheManager, CacheListener {

    //视频代理
    protected HttpProxyCacheServer proxy;

    protected File mCacheDir;

    protected boolean mCacheFile;

    private static ProxyCacheManager proxyCacheManager;

    private ICacheManager.ICacheAvailableListener cacheAvailableListener;

    protected ProxyCacheUserAgentHeadersInjector userAgentHeadersInjector = new ProxyCacheUserAgentHeadersInjector();

    /**
     单例管理器
     */
    public static synchronized ProxyCacheManager instance() {
        if (proxyCacheManager == null) {
            proxyCacheManager = new ProxyCacheManager();
        }
        return proxyCacheManager;
    }


    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if (cacheAvailableListener != null) {
            cacheAvailableListener.onCacheAvailable(cacheFile, url, percentsAvailable);
        }
    }



    @Override
    public void doCacheLogic(Context context, IMediaPlayer mediaPlayer, String originUrl, Map<String, String> header, File cachePath) {
        String url = originUrl;
     //   checkURlBTS(url);
        userAgentHeadersInjector.mMapHeadData.clear();
        if (header != null) {
            userAgentHeadersInjector.mMapHeadData.putAll(header);
        }

        if(url.startsWith("http") && !url.contains("127.0.0.1") && url.contains(".m3u8")){
            String endUrl = url.substring(url.lastIndexOf("/")+1,url.length());
            System.out.println("endUrl:" + endUrl);
            String tempUrl = Md5Utils.md5(url);
            System.out.println("tempUrl:" + tempUrl);
            String configRoot = context.getExternalFilesDir(null).getPath();
            System.out.println("configRoot:" + configRoot);
            String testUrl = "/storage/emulated/0/Android/data/com.example.gsyvideoplayer/files";
            String folder = testUrl + "/" + tempUrl;
         //   String folder = configRoot + "/"+tempUrl;
            System.out.println("folder:" +folder);
            File file = new File(folder);
            if(file.exists()){
                 try{
                     url = folder + "/"+ endUrl;
                    // url = folder;
                     System.out.println("downurl:"+url);
                     File downFile = new File(url);
                     if(downFile.exists()){
                         System.out.println("下载文件地址:" + url);
                         System.out.println("新:"+ Uri.parse(url));
                         try{
                             mediaPlayer.setDataSource(context, Uri.parse(url),header);
                         }catch (IOException e){
                             System.out.println("失效");
                         }

                         return;
                     } else{
                         System.out.println("下载文件不存在");
                     }


                }catch (Exception e){
                    e.printStackTrace();
                }
          //      return;
            } else{
                System.out.println("不存在");
            }
        }


        if (url.startsWith("http") && !url.contains("127.0.0.1") && !url.contains(".m3u8")) {
            HttpProxyCacheServer proxy = getProxy(context.getApplicationContext(), cachePath);
            if (proxy != null) {
                //此处转换了url，然后再赋值给mUrl。
                url = proxy.getProxyUrl(url);
                mCacheFile = (!url.startsWith("http"));
                //注册上缓冲监听
                if (!mCacheFile) {
                    proxy.registerCacheListener(this, originUrl);
                }
            }
        }   else if ((!url.startsWith("http") && !url.startsWith("rtmp")
                && !url.startsWith("rtsp") && !url.contains(".m3u8"))) {
            mCacheFile = true;
        }

        try {
            mediaPlayer.setDataSource(context, Uri.parse(url), header);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }






    @Override
    public void clearCache(Context context, File cachePath, String url) {
        if (TextUtils.isEmpty(url)) {
            String path = StorageUtils.getIndividualCacheDirectory
                    (context.getApplicationContext()).getAbsolutePath();
            FileUtils.deleteFiles(new File(path));
        } else {
            Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
            String name = md5FileNameGenerator.generate(url);
            if (cachePath != null) {
                String tmpPath = cachePath.getAbsolutePath() + File.separator + name + ".download";
                String path = cachePath.getAbsolutePath() + File.separator + name;
                CommonUtil.deleteFile(tmpPath);
                CommonUtil.deleteFile(path);
            } else {
                String pathTmp = StorageUtils.getIndividualCacheDirectory
                        (context.getApplicationContext()).getAbsolutePath()
                        + File.separator + name + ".download";
                String path = StorageUtils.getIndividualCacheDirectory
                        (context.getApplicationContext()).getAbsolutePath()
                        + File.separator + name;
                CommonUtil.deleteFile(pathTmp);
                CommonUtil.deleteFile(path);
            }
        }
    }

    @Override
    public void release() {
        if (proxy != null) {
            try {
                proxy.unregisterCacheListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean cachePreview(Context context, File cacheDir, String url) {
        HttpProxyCacheServer proxy = getProxy(context.getApplicationContext(), cacheDir);
        if (proxy != null) {
            //此处转换了url，然后再赋值给mUrl。
            url = proxy.getProxyUrl(url);
        }
        return (!url.startsWith("http"));
    }

    @Override
    public boolean hadCached() {
        return mCacheFile;
    }


    @Override
    public void setCacheAvailableListener(ICacheAvailableListener cacheAvailableListener) {
        this.cacheAvailableListener = cacheAvailableListener;
    }

    /**
     创建缓存代理服务,带文件目录的.
     */
    public HttpProxyCacheServer newProxy(Context context, File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context);
        builder.cacheDirectory(file);
        builder.headerInjector(userAgentHeadersInjector);
        mCacheDir = file;
        return builder.build();
    }

    public void setProxy(HttpProxyCacheServer proxy) {
        this.proxy = proxy;
    }

    /**
     创建缓存代理服务
     */
    public HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context.getApplicationContext())
                .headerInjector(userAgentHeadersInjector).build();
    }


    /**
     获取缓存代理服务
     */
    public static HttpProxyCacheServer getProxy(Context context) {
        HttpProxyCacheServer proxy = ProxyCacheManager.instance().proxy;
        return proxy == null ? (ProxyCacheManager.instance().proxy =
                ProxyCacheManager.instance().newProxy(context)) : proxy;
    }


    /**
     获取缓存代理服务,带文件目录的
     */
    public static HttpProxyCacheServer getProxy(Context context, File file) {

        //如果为空，返回默认的
        if (file == null) {
            return getProxy(context);
        }

        //如果已经有缓存文件路径，那么判断缓存文件路径是否一致
        if (ProxyCacheManager.instance().mCacheDir != null
                && !ProxyCacheManager.instance().mCacheDir.getAbsolutePath().equals(file.getAbsolutePath())) {
            //不一致先关了旧的
            HttpProxyCacheServer proxy = ProxyCacheManager.instance().proxy;

            if (proxy != null) {
                proxy.shutdown();
            }
            //开启新的
            return (ProxyCacheManager.instance().proxy =
                    ProxyCacheManager.instance().newProxy(context, file));
        } else {
            //还没有缓存文件的或者一致的，返回原来
            HttpProxyCacheServer proxy = ProxyCacheManager.instance().proxy;

            return proxy == null ? (ProxyCacheManager.instance().proxy =
                    ProxyCacheManager.instance().newProxy(context, file)) : proxy;
        }
    }

}
