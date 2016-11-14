package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.service.LockScreenService;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.ServiceUtil;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_show_system;
	private CheckBox cb_lock_screen_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		initShowSystem();
		initLockScreenClean();
	}

	private void initShowSystem() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		Boolean show_system = SpUtil.getBoolean(this, ConstantView.SHOW_SYSTEM, false);
		if(show_system){
			cb_show_system.setChecked(show_system);
			cb_show_system.setText("显示系统进程");
		} else {
			cb_show_system.setChecked(show_system);
			cb_show_system.setText("隐藏系统进程");
		}
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SpUtil.putBoolean(getApplicationContext(), ConstantView.SHOW_SYSTEM, true);
					cb_show_system.setText("显示系统进程");
				} else {
					SpUtil.putBoolean(getApplicationContext(), ConstantView.SHOW_SYSTEM, false);
					cb_show_system.setText("隐藏系统进程");
				}
			}
		});
	}

	private void initLockScreenClean() {
		cb_lock_screen_clean = (CheckBox) findViewById(R.id.cb_lock_screen_clean);
		Boolean lock_screen_clean = ServiceUtil.isRunning(this, "com.zahowenbin.mobilesafe.service.LockScreenService");
		if(lock_screen_clean){
			cb_lock_screen_clean.setChecked(lock_screen_clean);
			cb_lock_screen_clean.setText("锁屏清理已开启");
		} else {
			cb_lock_screen_clean.setChecked(lock_screen_clean);
			cb_lock_screen_clean.setText("锁屏清理已关闭");
		}
		cb_lock_screen_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startService(new Intent(getApplicationContext(), LockScreenService.class));
					cb_lock_screen_clean.setText("锁屏清理已开启");
				} else {
					stopService(new Intent(getApplicationContext(), LockScreenService.class));
					cb_lock_screen_clean.setText("锁屏清理已关闭");
				}
			}
		});
		
	}
}
