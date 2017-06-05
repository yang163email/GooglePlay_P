package com.yan.googleplay.manager;

import android.os.Environment;

import com.yan.googleplay.bean.DetailBean;
import com.yan.googleplay.bean.HomeBean;
import com.yan.googleplay.util.CommonUtils;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 楠GG on 2017/6/3.
 */

public class DownloadManager {
    private static final String TAG = "DownloadManager";
    private static DownloadManager mDownloadManager;

    public static final int STATE_UNDOWNLOAD = 0;
    public static final int STATE_WAITING = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_ERROR = 4;
    public static final int STATE_SUCCESS = 5;
    public static final int STATE_INSTALLED = 6;

    Map<String, DownloadListener> listeners = new HashMap<>();

    private DownloadManager() {
    }

    public static synchronized DownloadManager getInstance() {
        if(mDownloadManager == null) {
            mDownloadManager = new DownloadManager();
        }
        return mDownloadManager;
    }

    private Map<String, DownloadTask> mTasks = new HashMap<>();

    public void download(HomeBean.AppItem appItem, DownloadListener listener) {
        //下载应用
        int currentState = STATE_WAITING;
        //将等待中的任务传递过去
        listener.onStateChange(appItem.getPackageName(), currentState);
        DownloadTask downloadTask = new DownloadTask(appItem.getPackageName(),
                appItem.getDownloadUrl(), currentState, listener);
        //添加到集合中
        mTasks.put(appItem.getPackageName(), downloadTask);
        //执行任务
        ThreadManager.getDownloadPool().execute(downloadTask);
    }

    public void download(DetailBean detailBean, DownloadListener listener) {
        //下载应用
        int currentState = STATE_WAITING;
        //将等待中的任务传递过去
        listener.onStateChange(detailBean.packageName, currentState);
        DownloadTask downloadTask = new DownloadTask(detailBean.packageName,
                detailBean.downloadUrl, currentState, listener);
        //添加到集合中
        mTasks.put(detailBean.packageName, downloadTask);
        //执行任务
        ThreadManager.getDownloadPool().execute(downloadTask);
    }

    public int getState(String packageName, int fileSize) {
        //判断是否安装
        boolean installed = CommonUtils.isInstalled(UiUtil.getContext(), packageName);
        if(installed) {
            return STATE_INSTALLED;
        }
        //判断listener中是否有状态
        DownloadListener downloadListener = listeners.get(packageName);
        if(downloadListener != null) {
            return downloadListener.setState();
        }else {
            //目录中是否存在
            File file = getDownloadFile(packageName);
            if(file.exists()) {
                //存在
                if(file.length() == fileSize) {
                    //下载完成
                    return STATE_SUCCESS;
                }else {
                    return STATE_PAUSE;
                }
            }else {
                //不存在
                return STATE_UNDOWNLOAD;
            }
        }
    }

    /**暂停操作*/
    public void pause(String packageName) {
        DownloadTask downloadTask = mTasks.get(packageName);
        if(downloadTask != null) {
            //暂停
            downloadTask.isPause = true;
        }
    }

    /**取消下载
     * @param packageName*/
    public void cancel(String packageName) {
        DownloadTask downloadTask = mTasks.get(packageName);
        if(downloadTask != null) {
//            UiUtil.removeTask(downloadTask);
            //使用ThreadManager中的方法移除
            ThreadManager.getDownloadPool().remove(downloadTask);
        }
    }

    private class DownloadTask implements Runnable {

        private DownloadListener downloadListener;
        private int currentState;
        private String pkgName;
        private String downloadUrl;
        private boolean isPause;

        public DownloadTask(String packageName, String downloadUrl, int currentState, DownloadListener listener) {
            this.pkgName = packageName;
            this.downloadUrl = downloadUrl;
            this.currentState = currentState;
            this.downloadListener = listener;
        }

        @Override
        public void run() {
            long progress = getDownloadFile(pkgName).length();

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(Constant.DOWNLOAD + downloadUrl + "&range=" + progress).build();
            try {
                FileOutputStream fos = new FileOutputStream(getDownloadFile(pkgName), true);
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()) {
                    ResponseBody body = response.body();
                    //拿到字节流
                    InputStream is = body.byteStream();
                    //写文件
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                        currentState = STATE_DOWNLOADING;
                        downloadListener.onStateChange(pkgName, currentState);
                        //记录进度
                        progress += len;
                        downloadListener.onProgressChange(progress);
                        if(isPause) {
                            break;
                        }
                    }

                    if(isPause) {
                        currentState = STATE_PAUSE;
                    }else {
                        currentState = STATE_SUCCESS;
                    }
                    downloadListener.onStateChange(pkgName, currentState);
                }
            } catch (IOException e) {
                e.printStackTrace();
                currentState = STATE_ERROR;
                downloadListener.onStateChange(pkgName, currentState);
                //出错后，删除下载的文件
                getDownloadFile(pkgName).delete();
            }
        }
    }

    /**获取保存apk的文件名*/
    public File getDownloadFile(String pkgName) {
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sdcard
            dir = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/com.yan.googleplay/apk");
        }else {
            //rom
            dir = new File(UiUtil.getContext().getCacheDir(), "apk");
        }
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File apkFile = new File(dir, pkgName);
        return apkFile;
    }

    public interface DownloadListener {
        /**状态改变*/
        void onStateChange(String packageName, int state);
        /**设置状态*/
        int setState();
        /**进度改变*/
        void onProgressChange(long progress);
    }

    public void addDownloadListener(String packageName, DownloadListener listener) {
        if(!listeners.containsKey(packageName)) {
            //避免重复添加
            listeners.put(packageName, listener);
        }
    }
}
