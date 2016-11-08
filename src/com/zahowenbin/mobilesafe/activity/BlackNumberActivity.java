package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.dao.BlackNumberDao;
import com.zahowenbin.mobilesafe.db.domain.BlackNumberInfo;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private Button btn_add_blacknumber;
	private ListView lv_blacknumber;
	private BlackNumberDao blackNumberDao;
	private List<BlackNumberInfo> mBlackNumberInfos;
	protected int mode = 1;
	private EditText et_blacknumber;
	private RadioGroup rg_intercept_mode;
	private Button btn_blacknumber_submit;
	private Button btn_blacknumber_cancel;
	private MyAdapter adapter;
	private AlertDialog alertDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(adapter == null){
				adapter = new MyAdapter();
				lv_blacknumber.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber_list);
		initUI();
		initData();
	}

	private void initData() {
		new Thread(){
			@Override
			public void run() {
				blackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
				mBlackNumberInfos = blackNumberDao.findAll();
				mHandler.sendEmptyMessage(0);
			}
		}.start();	
	}

	private void initUI() {
		btn_add_blacknumber = (Button) findViewById(R.id.btn_add_blacknumber);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		btn_add_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showAddDialog();
			}
		});

	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return mBlackNumberInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mBlackNumberInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			View view = View.inflate(getApplicationContext(), R.layout.blacknumber_list_item, null);
			TextView tv_intercept_phone = (TextView) view.findViewById(R.id.tv_intercept_phone);
			TextView tv_intercept_mode = (TextView) view.findViewById(R.id.tv_intercept_mode);
			ImageView iv_delete_blacknumber = (ImageView) view.findViewById(R.id.iv_delete_blacknumber);
			tv_intercept_phone.setText(mBlackNumberInfos.get(arg0).getPhone().toString());
			switch (Integer.parseInt(mBlackNumberInfos.get(arg0).getMode())) {
			case 1:
				tv_intercept_mode.setText("拦截短信");
				break;
			case 2:
				tv_intercept_mode.setText("拦截电话");
				break;
			case 3:
				tv_intercept_mode.setText("拦截所有");
				break;
			}
			iv_delete_blacknumber.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					blackNumberDao.delete(mBlackNumberInfos.get(arg0).getPhone());
					mBlackNumberInfos.remove(arg0);
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					ToastUtil.show(getApplicationContext(), "删除成功");
				}
			});
			return view;
		}
		
	}

	protected void showAddDialog() {
		Builder builder = new AlertDialog.Builder(this);
		alertDialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) view.findViewById(R.id.et_blacknumber);
		rg_intercept_mode = (RadioGroup) view.findViewById(R.id.rg_intercept_mode);
		btn_blacknumber_submit = (Button) view.findViewById(R.id.btn_balcknumber_submit);
		btn_blacknumber_cancel = (Button) view.findViewById(R.id.btn_balcknumber_cancel);
		rg_intercept_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_phone:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;
				}	
			}
		});
		btn_blacknumber_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!TextUtils.isEmpty(et_blacknumber.getText().toString())){
					blackNumberDao.insert(et_blacknumber.getText().toString(), mode + "");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.setPhone(et_blacknumber.getText().toString());
					blackNumberInfo.setMode(mode + "");
					mBlackNumberInfos.add(0, blackNumberInfo);
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					alertDialog.dismiss();
				}else {
					ToastUtil.show(getApplicationContext(), "请输入电话号码");
				}
			}
		});
		btn_blacknumber_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				alertDialog.dismiss();
			}
		});
		alertDialog.setView(view);
		alertDialog.show();
	}
	
}
