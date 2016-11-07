package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;
import com.zahowenbin.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SetUp2Activity extends Activity {
	private SettingItemView siv_bind;
	private static final String tag = "SetUp2Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up2);
		
		initUI();
		initData();
	}
	
	private void initData() {
		final String sim_bind = SpUtil.getString(this, ConstantView.SIM_BIND, "");
		siv_bind.setCheck(TextUtils.isEmpty(sim_bind)?false:true);
		siv_bind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_bind.isCheck();
				if(!isCheck){
					TelephonyManager manger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String simSerialNumber = manger.getSimSerialNumber();
					if(!TextUtils.isEmpty(simSerialNumber)){
						SpUtil.putString(getApplicationContext(), ConstantView.SIM_BIND, simSerialNumber);
						siv_bind.setCheck(true);
					} else {
						ToastUtil.show(getApplicationContext(), "–Ë“™≤Â»Îsimø®");
					}
					
				} else {
					SpUtil.remove(getApplicationContext(), ConstantView.SIM_BIND);
					siv_bind.setCheck(false);
				}
			}
		});
		
	}

	private void initUI() {
		siv_bind = (SettingItemView) findViewById(R.id.siv_bind);
		
	}

	public void nextPage(View view){
		Intent intent = new Intent(this, SetUp3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
	
	public void prevPage(View view){
		Intent intent = new Intent(this, SetUp1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.prev_in_anim, R.anim.prev_out_anim);
	}
}
