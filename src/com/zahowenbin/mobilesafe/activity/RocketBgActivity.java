package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class RocketBgActivity extends Activity {
	private ImageView iv_rocket_bg_top;
	private AlphaAnimation alphaAnimation;
	private ImageView iv_rocket_bg_bottom;
	private Handler mHandler =  new Handler(){
		public void handleMessage(android.os.Message msg) {
			finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rocket_bg);
		iv_rocket_bg_top = (ImageView) findViewById(R.id.iv_rocket_bg_top);
		iv_rocket_bg_bottom = (ImageView) findViewById(R.id.iv_rocket_bg_bottom);
		alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		iv_rocket_bg_bottom.startAnimation(alphaAnimation);
		alphaAnimation.setDuration(1000);
		iv_rocket_bg_top.startAnimation(alphaAnimation);

		
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
}
