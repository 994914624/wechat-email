package com.android.system.need;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;


/**
 * Created by yang on 2017/4/27.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXMPush();
        //启动 MyService
//        Intent intent = new Intent(this,MyService.class);
//        startService(intent);
    }


    /**
     * 小米推送 required
     */
    // user your appid the key.
    private static final String APP_ID = "2882303761517571790";
    // user your appid the key.
    private static final String APP_KEY = "5681757176790";
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep cn.sensorsdata.demo
    public static final String TAG = "com.android.system.need";

    //public XiaomiPushActivity.DemoHandler handler=null;

    /**
     * 小米推送初始化
     */
    private void initXMPush(){
        MiPushClient.registerPush(this, APP_ID, APP_KEY);//xiaomi初始化 APP_ID、APP_KEY
        xiaomiLog();//xiaomiLog日志
        //handler用于转发接受到的消息
       // handler=new XiaomiPushActivity.DemoHandler(this);
       // XiaomiPushBroadcastReceiver.setHandle(handler);

    }

    /**
     * 小米 log
     */
    private void xiaomiLog(){

        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }


}
