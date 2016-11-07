package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetUpOverActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Boolean setUpOver = SpUtil.getBoolean(this, ConstantView.SETUPOVER, false);
		if(setUpOver){
			setContentView(R.layout.activity_set_up_over);
		} else {
			Intent intent = new Intent(this, SetUp1Activity.class);
			startActivity(intent);
			finish();
		}
		
		initUI();
	}

	private void initUI() {
		TextView tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		tv_phone_number.setText(SpUtil.getString(this, ConstantView.BIND_PHONE, ""));
		TextView tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SetUp1Activity.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
}
