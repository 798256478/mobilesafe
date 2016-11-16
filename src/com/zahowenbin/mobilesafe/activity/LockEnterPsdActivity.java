package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.R.interpolator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LockEnterPsdActivity extends Activity {
	private EditText et_lock_psd;
	private Button btn_submit;
	private Button btn_cancel;
	private String packageName;
	private ImageView iv_icon;
	private TextView tv_name;
	private Drawable icon;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_enter_psd);
		packageName = getIntent().getStringExtra("packageName");
		initUI();
		initData();
	}

	private void initData() {
		PackageManager packageManager = getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
			icon = applicationInfo.loadIcon(packageManager);
			name = applicationInfo.loadLabel(packageManager).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		iv_icon.setBackgroundDrawable(icon);
		tv_name.setText(name);
	}

	private void initUI() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		et_lock_psd = (EditText) findViewById(R.id.et_lock_psd);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lock_psd = SpUtil.getString(getApplicationContext(), ConstantView.LOCK_PSD, "");
				if(lock_psd.equals(et_lock_psd.getText().toString())){
					Intent intent = new Intent("android.intent.action.PASS");
					intent.putExtra("packageName", packageName);
					sendBroadcast(intent);
					finish();
				} else {
					ToastUtil.show(getApplicationContext(), "√‹¬Î¥ÌŒÛ");
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
}
