package com.zahowenbin.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.zahowenbin.mobilesafe.R;
import android.util.Log;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheManagerActivity extends Activity {
	protected static final int CACHE_INFO = 100;
	protected static final int CACHE_TITLE = 101;
	protected static final int CACHE_CLEAR = 102;
	protected static final int SCAN_OVER = 103;
	private int index = 0;
	private int count = 0;
	private String name = "";
	private String tempPackageName = null;
	private int viewPosition = 0;

	private PackageManager mPackageManager;
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CACHE_INFO:
				
				final AppInfo appInfo = (AppInfo) msg.obj;
				
				final View view = View.inflate(getApplicationContext(), R.layout.cache_list_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
				ImageView iv_clear_cache = (ImageView) view.findViewById(R.id.iv_clear_cache);
				
				iv_icon.setBackgroundDrawable(appInfo.icon);
				tv_name.setText(appInfo.name);
				tv_cache_size.setText(Formatter.formatFileSize(getApplicationContext(), appInfo.cacheSize));
				
				ll_app_list.addView(view, viewPosition);
				
				iv_clear_cache.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						viewPosition = ll_app_list.indexOfChild(view);
						tempPackageName = appInfo.packageName;
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+ appInfo.packageName));
						startActivityForResult(intent, 0);
						ll_app_list.removeView(view);
					}
				});
				break;
			case CACHE_TITLE:
				tv_cache_serch_title.setText(name);
				break;
			case CACHE_CLEAR:
				ll_app_list.removeAllViews();
				break;
			case SCAN_OVER:
				tv_cache_serch_title.setText("扫描完成");
				if(count == 0){
					new AlertDialog.Builder(CacheManagerActivity.this)
								.setMessage("无可清理缓存的应用").setNegativeButton("确定", null).show();
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_manager);
		initUI();
		initData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		viewPosition = 0;
		getCacheSize(tempPackageName);
	}

	private void initUI() {
		ll_app_list = (LinearLayout) findViewById(R.id.ll_app_list);
		pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
		tv_cache_serch_title = (TextView) findViewById(R.id.tv_cache_serch_title);
		btn_clear_cache = (Button) findViewById(R.id.btn_clear_cache);
		
		btn_clear_cache.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearAllCache();
			}
		});
	}
	
	private IPackageDataObserver.Stub mIPackageDataObserver = new IPackageDataObserver.Stub(){

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			Message message = Message.obtain();
			message.what = CACHE_CLEAR;
			mHandler.sendMessage(message);
		}
		
	};

	protected void clearAllCache() {
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
			method.invoke(mPackageManager, Long.MAX_VALUE, mIPackageDataObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		mPackageManager = getPackageManager();
		new Thread(){
			public void run() {
				List<ApplicationInfo> installedApplications = mPackageManager.getInstalledApplications(0);
				pb_progress.setMax(installedApplications.size());
				for (ApplicationInfo applicationInfo : installedApplications) {
					try {
						name = mPackageManager.getApplicationInfo(applicationInfo.packageName, 0).loadLabel(mPackageManager).toString();
					} catch (NameNotFoundException e1) {
						e1.printStackTrace();
					}
					
					getCacheSize(applicationInfo.packageName);				
					Message message = Message.obtain();
					message.what = CACHE_TITLE;
					mHandler.sendMessage(message);
					try {
						Thread.sleep(100 + new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index++;
					pb_progress.setProgress(index);
				}
				Message message = Message.obtain();
				message.what = SCAN_OVER;
				mHandler.sendMessage(message);
			};
		}.start();
	}

	private IPackageStatsObserver.Stub mIPackageStatsObserver = new IPackageStatsObserver.Stub() {
		
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;
			if(cacheSize > 4096*3){
				count++;
				AppInfo appInfo = new AppInfo();
				appInfo.packageName = pStats.packageName;
				appInfo.cacheSize = cacheSize;
				try {
					appInfo.name = mPackageManager.getApplicationInfo(pStats.packageName, 0).loadLabel(mPackageManager).toString();
					appInfo.icon = mPackageManager.getApplicationInfo(pStats.packageName, 0).loadIcon(mPackageManager);
				} catch (Exception e) {
					appInfo.name = pStats.packageName;
					appInfo.icon = getResources().getDrawable(R.drawable.ic_launcher);
					e.printStackTrace();
				}
				Message message = Message.obtain();
				message.what = CACHE_INFO;
				message.obj = appInfo;
				mHandler.sendMessage(message);
			}
		}
	};

	private LinearLayout ll_app_list;

	private ProgressBar pb_progress;
	private TextView tv_cache_serch_title;
	private Button btn_clear_cache;
	
	class AppInfo{
		String packageName;
		String name;
		Drawable icon;
		long cacheSize;
	}

	private void getCacheSize(String packageName) {
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			method.invoke(mPackageManager, packageName, mIPackageStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
