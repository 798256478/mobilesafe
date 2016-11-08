package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.service.AddressService;
import com.zahowenbin.mobilesafe.service.FloatBallService;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.ServiceUtil;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.view.SettingClickView;
import com.zahowenbin.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingClickView scv_toast_style;
	private String[] mToastStyles;
	private int mToastStyleIndex;
	private SettingClickView scv_toast_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initUpdate();
		initAddress();
		initToastStyle();
		initToastLocation();
		initFloatBall();
	}

	private void initFloatBall() {
		final SettingItemView siv_float_ball = (SettingItemView) findViewById(R.id.siv_float_ball);
		Boolean isCheck = ServiceUtil.isRunning(this, "com.zahowenbin.mobilesafe.service.FloatBallService");
		siv_float_ball.setCheck(isCheck);
		siv_float_ball.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Boolean isCheck = SpUtil.getBoolean(getApplicationContext(), ConstantView.FLOAT_BALL, false);
				siv_float_ball.setCheck(!isCheck);
				if(!isCheck){
					startService(new Intent(getApplicationContext(), FloatBallService.class));
				} else {
					stopService(new Intent(getApplicationContext(), FloatBallService.class));
				}
			}
		});
	}

	private void initToastLocation() {
		scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_loaction);
		scv_toast_location.setTitle("点击设置吐司位置");
		scv_toast_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
			}
		});
		
	}

	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("号码归属地显示样式选择");
		mToastStyles = new String[]{"透明","蓝色","灰色","绿色","橙色"};
		mToastStyleIndex = SpUtil.getInt(this, ConstantView.TOAST_VIEW, 0);
		scv_toast_style.setDes(mToastStyles[mToastStyleIndex]);
		scv_toast_style.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showToastDialog();
				
			}
		});
	}

	protected void showToastDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择吐司样式");
		mToastStyleIndex = SpUtil.getInt(this, ConstantView.TOAST_VIEW, 0);
		builder.setSingleChoiceItems(mToastStyles, mToastStyleIndex, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				SpUtil.putInt(getApplicationContext(), ConstantView.TOAST_VIEW, arg1);
				scv_toast_style.setDes(mToastStyles[arg1]);
				arg0.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				
			}
		});
		builder.show();
	}

	private void initAddress() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		boolean isCheck = ServiceUtil.isRunning(this, "com.zahowenbin.mobilesafe.service.AddressService");
		siv_address.setCheck(isCheck);
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_address.isCheck();
				if(!isCheck){
					startService(new Intent(getApplicationContext(),AddressService.class));
				} else {
					stopService(new Intent(getApplicationContext(),AddressService.class));
				}
				siv_address.setCheck(!isCheck);
			}
		});
	}

	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView)findViewById(R.id.siv_update);
		Boolean open_update = SpUtil.getBoolean(this, ConstantView.OPEN_UPDATE, false);
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_update.isCheck();
				siv_update.setCheck(!isCheck);
				SpUtil.putBoolean(getApplicationContext(), ConstantView.OPEN_UPDATE, !isCheck);
			}
		});
	}
	
}
