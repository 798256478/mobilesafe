package com.zahowenbin.mobilesafe.receiver;

import com.zahowenbin.mobilesafe.service.AppWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("NewApi")
public class MyAppWidgetProvider extends AppWidgetProvider {
	/*
	 * 创建第一个widget时触发的事件 
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	/*
	 *	当widget发生变化时触发的事件（增加一个widget或者删除一个widget） 
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	/*
	 * 当widget的大小发生变化时触发的事件（改变widget的大小，创建一个新的widget时相当于将一个widget的大小从0变成指定大小，也会触发该事件）
	 */
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	/*
	 * 删除一个widget时触发该事件
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	/*
	 * 将该应用的所有widget全部删除时触发
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(new Intent(context, AppWidgetService.class));
	}
}
