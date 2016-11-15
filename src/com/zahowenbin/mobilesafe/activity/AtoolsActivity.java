package com.zahowenbin.mobilesafe.activity;


import java.io.File;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.SmsBackUp;
import com.zahowenbin.mobilesafe.engine.SmsBackUp.CallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AtoolsActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		initQueryAddress();
		initBackupSms();
		initCommonNumber();
		initAppLock();
	}

	private void initAppLock() {
		TextView tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
		tv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
			}
		});
	}

	private void initCommonNumber() {
		TextView tv_common_number = (TextView) findViewById(R.id.tv_common_number);
		tv_common_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
			}
		});
		
	}

	private void initBackupSms() {
		TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showBackUpDialog();
				
			}
		});
	}

	protected void showBackUpDialog() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("正在备份");
		
		new Thread(){
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "smsBack.xml";
				SmsBackUp.backUp(getApplicationContext(), path, new CallBack() {
					
					@Override
					public void setProgress(int index) {
						progressDialog.show();
						progressDialog.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
					}
				});
				progressDialog.dismiss();
			};
		}.start();
	}

	private void initQueryAddress() {
		TextView tv_query_phone = (TextView) findViewById(R.id.tv_query_phone);
		tv_query_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), QueryPhoneActivity.class));
			}
		});
		
	}
}
