package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class RocketBgActivity extends Activity {
	private Handler mHandler =  new Handler(){
		public void handleMessage(android.os.Message msg) {
			finish();
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rocket_bg);
		ImageView iv_rocket_bg_top = (ImageView) findViewById(R.id.iv_rocket_bg_top);
		ImageView iv_rocket_bg_bottom = (ImageView) findViewById(R.id.iv_rocket_bg_bottom);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		iv_rocket_bg_bottom.startAnimation(alphaAnimation);
		iv_rocket_bg_top.startAnimation(alphaAnimation);
		
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
}
