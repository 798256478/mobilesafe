package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetUp4Activity extends Activity {
	private CheckBox cb_open_security;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up4);
		initUI();
	}
	
	private void initUI() {
		cb_open_security = (CheckBox)findViewById(R.id.cb_open_security);
		Boolean open_security = SpUtil.getBoolean(this, ConstantView.OPEN_SECURITY, false);
		cb_open_security.setChecked(open_security);
		cb_open_security.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cb_open_security.setChecked(isChecked);
				SpUtil.putBoolean(getApplicationContext(), ConstantView.OPEN_SECURITY, isChecked);
			}
		});
	}

	public void nextPage(View view){
		Boolean open_security = SpUtil.getBoolean(this, ConstantView.OPEN_SECURITY, false);
		if(open_security){
			Intent intent = new Intent(this, SetUpOverActivity.class);
			startActivity(intent);
			finish();
			SpUtil.putBoolean(this, ConstantView.SETUPOVER, true);
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "请开启安全防护");
		}
	}
	
	public void prevPage(View view){
		Intent intent = new Intent(this, SetUp3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.prev_in_anim, R.anim.prev_out_anim);
	}
}
