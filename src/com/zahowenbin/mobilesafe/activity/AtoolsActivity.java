package com.zahowenbin.mobilesafe.activity;


import java.io.File;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.SmsBackUp;
import com.zahowenbin.mobilesafe.engine.SmsBackUp.CallBack;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Path;
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
		progressDialog.show();
		new Thread(){
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "smsBack.xml";
				SmsBackUp.backUp(getApplicationContext(), path, new CallBack() {
					
					@Override
					public void setProgress(int index) {
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
