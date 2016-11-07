package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.QueryAddressDao;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryPhoneActivity extends Activity {
	protected String mPhoneNumber;
	private EditText et_phone_number;
	protected String mAddress;
	private TextView tv_query_result;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_phone);
		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		Button bt_query_btn = (Button) findViewById(R.id.bt_query_btn);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		
		bt_query_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mPhoneNumber = et_phone_number.getText().toString();
				if(TextUtils.isEmpty(mPhoneNumber)){
					Animation shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					et_phone_number.startAnimation(shakeAnimation);
					//手机振动
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//long[]{不振动，振动....}
					vibrator.vibrate(new long[]{50, 500}, -1);
				} else {
					query(mPhoneNumber);
				}
			}
		});
		
		et_phone_number.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				mPhoneNumber = et_phone_number.getText().toString();
				query(mPhoneNumber);
			}
		});
	}

	protected void query(final String mPhoneNumber) {
		new Thread(){
			public void run(){
				mAddress = QueryAddressDao.getAddress(mPhoneNumber);
				mHandler.sendEmptyMessage(0);
			}
		}.start();
		
	}
}
