package com.zahowenbin.mobilesafe.activity;


import com.zahowenbin.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AtoolsActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		initUI();
	}

	private void initUI() {
		TextView tv_query_phone = (TextView) findViewById(R.id.tv_query_phone);
		tv_query_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), QueryPhoneActivity.class));
			}
		});
		
	}
}
