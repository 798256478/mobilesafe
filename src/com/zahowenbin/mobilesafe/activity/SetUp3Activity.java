package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetUp3Activity extends Activity {
	private EditText et_selected_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up3);
		
		initUI();
	}
	
	private void initUI() {
		Button select_phone =(Button)findViewById(R.id.select_phone);
		et_selected_phone = (EditText)findViewById(R.id.et_selected_phone);
		String bind_phone = SpUtil.getString(this, ConstantView.BIND_PHONE, "");
		et_selected_phone.setText(bind_phone);
		select_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SelectPhoneActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			String phone = data.getStringExtra("phone");
			et_selected_phone.setText(phone);
			SpUtil.putString(this, ConstantView.BIND_PHONE, phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void nextPage(View view){
		String selected_phone = et_selected_phone.getText().toString();
		if(!TextUtils.isEmpty(selected_phone)){
			Intent intent = new Intent(this, SetUp4Activity.class);
			startActivity(intent);
			finish();
			SpUtil.putString(this, ConstantView.BIND_PHONE, selected_phone);
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "请先绑定联系人");
		}
		
	}
	
	public void prevPage(View view){
		Intent intent = new Intent(this, SetUp2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.prev_in_anim, R.anim.prev_out_anim);
	}
}
