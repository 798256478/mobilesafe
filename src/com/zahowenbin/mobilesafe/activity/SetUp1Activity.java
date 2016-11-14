package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUp1Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up1);
	}

	public void nextPage(View view) {
		Intent intent = new Intent(this, SetUp2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
	
}
