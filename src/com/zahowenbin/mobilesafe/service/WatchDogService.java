package com.zahowenbin.mobilesafe.service;

import java.util.List;

import com.zahowenbin.mobilesafe.R.id;
import com.zahowenbin.mobilesafe.activity.LockEnterPsdActivity;
import com.zahowenbin.mobilesafe.db.dao.AppLockDao;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;
import com.zahowenbin.mobilesafe.service.AppWidgetService.InnerReceiver;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class WatchDogService extends Service {
	private Boolean isWatch = true;
	private AppLockDao appLockDao;
	private List<String> appLockList;
	private String skipPackageName = "";
	private InnerReceiver innerReceiver;
	private MyContentObserver myContentObserver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		appLockDao = AppLockDao.instance(this);
		IntentFilter intentFilter = new IntentFilter("android.intent.action.PASS");
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
		watch();
		myContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, myContentObserver);
	}
	
	class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			new Thread(){
				public void run() {
					appLockList = appLockDao.findAll();
				};
			}.start();
		}
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			skipPackageName = intent.getStringExtra("packageName");
		}
	}
	
	private void watch() {
		new Thread(){
			@Override
			public void run() {
				appLockList = appLockDao.findAll();
				while(isWatch){
					ActivityManager	activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					String packageName = runningTaskInfo.topActivity.getPackageName(); 
					if(appLockList.contains(packageName) && !packageName.equals(skipPackageName)) {
						Intent intent = new Intent(getApplicationContext(), LockEnterPsdActivity.class);
						intent.putExtra("packageName", packageName);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		isWatch = false;
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		super.onDestroy();
	}

}
