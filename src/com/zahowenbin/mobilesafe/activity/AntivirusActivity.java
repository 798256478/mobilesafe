package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;
import com.zahowenbin.mobilesafe.engine.QueryAntivirusDao;
import com.zahowenbin.mobilesafe.utils.Md5Util;
import android.net.Uri;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AntivirusActivity extends Activity {
	protected static final int SCANNED = 100;
	protected static final int SCANER_OVER = 101;
	private TextView iv_scann1ing_name;
	private ProgressBar pb_scanning;
	private ImageView iv_rotate;
	private List<AppInfo> antivirusApp;
	private LinearLayout ll_scanning_put;
	private List<AppInfo> appInfoList;
	private int index = 0;
	
	Handler mHandler = new Handler(){
		@SuppressWarnings("deprecation")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNED:
				AppInfo appInfo = (AppInfo)msg.obj;
				iv_scann1ing_name.setText(appInfo.name);
				TextView textView = new TextView(getApplicationContext());
				textView.setTextSize(16);
				textView.setHeight(50);
				pb_scanning.setProgress(index);
				if(antivirusApp.contains(appInfo)){
					textView.setText("危险应用：   " + appInfo.name);
					textView.setTextColor(Color.RED);
				}else{
					textView.setText("安全应用：   " + appInfo.name);
				}
				ll_scanning_put.addView(textView, 0);
				break;
			case SCANER_OVER:
				iv_scann1ing_name.setText("查杀完成");
				iv_rotate.clearAnimation();
				if(antivirusApp.isEmpty()){
					new AlertDialog.Builder(AntivirusActivity.this)
									.setMessage("查杀完成，未发现可疑应用")
									.setNeutralButton("确定", null)
									.show();
				} else {
					for (AppInfo antivirusAppInfo : antivirusApp) {
						String packageName = antivirusAppInfo.packageName;
						Intent intent = new Intent("android.intent.action.DELETE");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setData(Uri.parse("package:" + packageName));
						startActivity(intent);
					}
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		initUI();
		initData();
	}

	private void initData() {
		new Thread(){
			@Override
			public void run() {
				List<String> antivirus = QueryAntivirusDao.getAntivirus();
				appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				pb_scanning.setMax(appInfoList.size());
				antivirusApp = new ArrayList<AppInfo>();
				try {
					for (AppInfo appInfo : appInfoList) {
						PackageInfo packageInfo = getPackageManager().getPackageInfo(appInfo.packageName, PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
						Signature[] signatures = packageInfo.signatures;
						String signature = signatures[0].toCharsString();
						String encoder_signature = Md5Util.encoder(signature);
						if(antivirus.contains(encoder_signature)){
							antivirusApp.add(appInfo);
						}
						try {
							sleep(50 + new Random().nextInt(100));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						index++;
						Message message = Message.obtain();
						message.what = SCANNED;
						message.obj = appInfo;
						mHandler.sendMessage(message);
					} 
					Message message = Message.obtain();
					message.what = SCANER_OVER;
					mHandler.sendMessage(message);
				}catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void initUI() {
		iv_scann1ing_name = (TextView) findViewById(R.id.iv_scanning_name);
		pb_scanning = (ProgressBar) findViewById(R.id.pb_scanning);
		iv_rotate = (ImageView) findViewById(R.id.iv_rotate);
		ll_scanning_put = (LinearLayout) findViewById(R.id.ll_scanning_put);
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_rotate.startAnimation(rotateAnimation);
		

	}
}
