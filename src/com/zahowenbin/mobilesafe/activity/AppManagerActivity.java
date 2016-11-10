package com.zahowenbin.mobilesafe.activity;

import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
	private TextView tv_memory;
	private TextView tv_sd_memory;
	private List<AppInfo> mAppInfoList;
	private ListView lv_applist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		initUI();
		initData();
	}

	private void initData() {
		mAppInfoList = AppInfoProvider.getAppInfoList(this);
		MyAdapter myAdapter = new MyAdapter();
		lv_applist.setAdapter(myAdapter);
	}

	private void initUI() {
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		String path = Environment.getDataDirectory().getAbsolutePath();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String memory = Formatter.formatFileSize(this, getAvailSpace(path));//格式化区域块大小(将数字转化为MB,GB之类的)
		String sdMemory = Formatter.formatFileSize(this, getAvailSpace(sdPath));
		tv_memory.setText("当前磁盘可用空间 " + memory);
		tv_sd_memory.setText("当前SD卡可用空间" + sdMemory);
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mAppInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mAppInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolderer viewHolderer = null;
			if(convertView == null){
				viewHolderer = new ViewHolderer();
				convertView = View.inflate(getApplicationContext(), R.layout.applist_item, null);
				viewHolderer.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
				viewHolderer.tv_app_package_name = (TextView) convertView.findViewById(R.id.tv_app_package_name);
				viewHolderer.tv_app_location = (TextView) convertView.findViewById(R.id.tv_app_location);
				convertView.setTag(viewHolderer);
			} else {
				viewHolderer = (ViewHolderer) convertView.getTag();
			}
			
			viewHolderer.iv_app_icon.setBackgroundDrawable(mAppInfoList.get(position).drawable);
			viewHolderer.tv_app_package_name.setText(mAppInfoList.get(position).packageName);
			viewHolderer.tv_app_location.setText(mAppInfoList.get(position).isSDCard?"SD卡应用":"手机应用");
			
			return convertView;
		}
		
	}
	
	class ViewHolderer{
		private ImageView iv_app_icon;
		private TextView tv_app_package_name;
		private TextView tv_app_location;
	}
	
	private long getAvailSpace(String path){
		//获取可用磁盘类的对象
		StatFs statFs = new StatFs(path);
		long availAbleBlocks = statFs.getAvailableBlocks(); //可用区块的数量
		long blockCount = statFs.getBlockSize();  //每个区块的大小
		//statFs.getBlockCount();区块的总量
		
		return availAbleBlocks * blockCount;
	}
	
	
}
