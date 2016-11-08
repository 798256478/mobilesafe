package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initUI();
		initData();
	}

	private void initData() {
		mTitleStrs = new String[]{
				"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"
		};
		
		mDrawableIds = new int[]{
				R.drawable.home_safe,R.drawable.home_callmsgsafe,
				R.drawable.home_apps,R.drawable.home_taskmanager,
				R.drawable.home_netmanager,R.drawable.home_trojan,
				R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
		};
		
		gv_home.setAdapter(new GridAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					showDialog();
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
					break;
				case 7:
					Intent intent2 = new Intent(getApplicationContext(), AtoolsActivity.class);
					startActivity(intent2);
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	protected void showDialog() {
		String sp = SpUtil.getString(this, ConstantView.MOBILE_SAFE_PSD, "");
		if(TextUtils.isEmpty(sp)){
			showSetPsdDialog();
		} else {
			showConfirmPsdDialog();
		}
	}

	private void showSetPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		dialog.setView(view);
		dialog.show();
		Button bt_submit = (Button)view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
				String confirmPsd = et_confirm_psd.getText().toString();
				String setPsd = et_set_psd.getText().toString();
				if(!TextUtils.isEmpty(confirmPsd) && !TextUtils.isEmpty(setPsd)){
					if(setPsd.equals(confirmPsd)){
						SpUtil.putString(getApplicationContext(), ConstantView.MOBILE_SAFE_PSD, setPsd);
						Intent intent = new Intent(getApplicationContext(), SetUpOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
					} else {
						ToastUtil.show(getApplicationContext(), "两次密码输入不一致");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请完整输入");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void showConfirmPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		dialog.setView(view);
		dialog.show();
		Button bt_submit = (Button)view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				String confirmPsd = et_confirm_psd.getText().toString();
				if(!TextUtils.isEmpty(confirmPsd)){
					String sp = SpUtil.getString(getApplicationContext(), ConstantView.MOBILE_SAFE_PSD, "");
					if(confirmPsd.equals(sp)){
						Intent intent = new Intent(getApplicationContext(), SetUpOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
					} else {
						ToastUtil.show(getApplicationContext(), "密码错误");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请完整输入");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void initUI() {
		gv_home = (GridView)findViewById(R.id.gv_home);
		
		
	}
	
	class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			ImageView iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			tv_title.setText(mTitleStrs[position]);
			return view;
		}
		
		
		
	}
}
