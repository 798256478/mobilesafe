package com.zahowenbin.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.ProgressInfoProvider;
import com.zahowenbin.mobilesafe.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class AppWidgetService extends Service {
	
	private Timer timer;
	private InnerReceiver innerReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		startTimer();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.equals(Intent.ACTION_SCREEN_OFF)){
				if(timer != null){
					timer.cancel();
					timer = null;
				}
			} else {
				startTimer();
			}
		}
	}
	
	private void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				updateWidget();
			}
		}, 0, 3000);
	}

	protected void updateWidget() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		//��ȡ����С����ת����view����Ӧ�õİ����� С�����Ĳ����ļ���
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.provider_widget);
		remoteViews.setTextViewText(R.id.tv_process_count, "��ǰ���н���" + ProgressInfoProvider.getProgressTotal(getApplicationContext()));
		remoteViews.setTextViewText(R.id.tv_process_memory, "��ǰ�����ڴ�" + Formatter.formatFileSize(getApplicationContext(), ProgressInfoProvider.getAvailSpace(getApplicationContext())));
		
		//ͨ��������ͼ���͹㲥���ڹ㲥��������ɱ�����̣�ƥ�����action
		Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent pendingBroadcast = PendingIntent.getBroadcast(getApplicationContext(), 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingBroadcast);
		
		//���С��������Ӧ��
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_widget, pendingIntent);
		
		ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
		appWidgetManager.updateAppWidget(componentName, remoteViews);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(innerReceiver != null){
			unregisterReceiver(innerReceiver);
		}
		timer.cancel();
	}
}
