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
	 * ������һ��widgetʱ�������¼� 
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	/*
	 *	��widget�����仯ʱ�������¼�������һ��widget����ɾ��һ��widget�� 
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	/*
	 * ��widget�Ĵ�С�����仯ʱ�������¼����ı�widget�Ĵ�С������һ���µ�widgetʱ�൱�ڽ�һ��widget�Ĵ�С��0���ָ����С��Ҳ�ᴥ�����¼���
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
	 * ɾ��һ��widgetʱ�������¼�
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	/*
	 * ����Ӧ�õ�����widgetȫ��ɾ��ʱ����
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(new Intent(context, AppWidgetService.class));
	}
}
