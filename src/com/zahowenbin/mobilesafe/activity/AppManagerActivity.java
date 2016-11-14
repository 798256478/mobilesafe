package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
	private TextView tv_memory;
	private TextView tv_sd_memory;
	private List<AppInfo> mAppInfoList;
	private ListView lv_applist;
	private List<AppInfo> mSystemAppList;
	private List<AppInfo> mConstumAppList;
	private TextView tv_app_float_title;
	private AppInfo mAppinfo;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapter myAdapter = new MyAdapter();
			lv_applist.setAdapter(myAdapter);
			if(tv_app_float_title!= null && mConstumAppList != null){
				tv_app_float_title.setText("用户应用（" + mConstumAppList.size() +"）");	
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		initUI();
		initData();
	}

	private void initData() {
		lv_applist.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mConstumAppList != null && mSystemAppList != null){
					if(firstVisibleItem < mConstumAppList.size() + 1){
						tv_app_float_title.setText("用户应用（" + mConstumAppList.size() +"）" );
					} else {
						tv_app_float_title.setText("系统应用（" + mSystemAppList.size() +"）" );
					}
				}		
			}
		});
		lv_applist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position != 0 && position != mConstumAppList.size() +1){
					if(position < mConstumAppList.size()+1){
						mAppinfo = mConstumAppList.get(position - 1);
					} else {
						mAppinfo = mSystemAppList.get(position - mConstumAppList.size() - 2);
					}
					showPopUpWindow(view);
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void showPopUpWindow(View view) {
		View popUpView = View.inflate(this, R.layout.popup_window_app_action, null);
		ImageView btn_boot = (ImageView) popUpView.findViewById(R.id.iv_boot);
		ImageView btn_uninstall = (ImageView) popUpView.findViewById(R.id.iv_uninstall);
		ImageView btn_share = (ImageView) popUpView.findViewById(R.id.iv_share);
		final PopupWindow popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		ImageView iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
		popupWindow.showAsDropDown(view, iv_app_icon.getWidth() + 20, -view.getHeight() + 10, Gravity.CENTER);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 1, 1);
		scaleAnimation.setDuration(500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);
		popUpView.setAnimation(animationSet);
		btn_boot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PackageManager packageManager = getPackageManager();
				Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppinfo.getPackageName());
				if(launchIntentForPackage != null){
					startActivity(launchIntentForPackage);
				} else {
					ToastUtil.show(getApplicationContext(), "此程序无法启动");
				}
				popupWindow.dismiss();
			}
		});
		btn_uninstall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAppinfo.isSystem){
					ToastUtil.show(getApplicationContext(), "此应用不能卸载");
				}else{
					Intent intent = new Intent("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"+mAppinfo.getPackageName()));
					startActivity(intent);
				}
				popupWindow.dismiss();
			}
		});
		btn_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名是" + mAppinfo.name);
				intent.setType("text/plain");
				startActivity(intent);
				popupWindow.dismiss();
			}
		});
	}

	private void initUI() {
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		tv_app_float_title = (TextView) findViewById(R.id.tv_app_float_title);
		String path = Environment.getDataDirectory().getAbsolutePath();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String memory = Formatter.formatFileSize(this, getAvailSpace(path));//格式化区域块大小(将数字转化为MB,GB之类的)
		String sdMemory = Formatter.formatFileSize(this, getAvailSpace(sdPath));
		tv_memory.setText("当前磁盘可用空间 " + memory);
		tv_sd_memory.setText("当前SD卡可用空间" + sdMemory);
	}
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}
		
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mConstumAppList.size() +1){
				return 0;
			} else {
				return 1;
			}
		}
		
		@Override
		public int getCount() {
			return mAppInfoList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if(position > 0 && position < mConstumAppList.size()+1){
				return mConstumAppList.get(position - 1);
			} else if(position > mConstumAppList.size()+1){
				return mSystemAppList.get(position - mConstumAppList.size() - 2);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(getItemViewType(position) == 0){
				ViewTitleHolder viewTitleHolder = null;
				if(convertView == null){
					viewTitleHolder = new ViewTitleHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.applist_title_item, null);
					viewTitleHolder.tv_app_type_title = (TextView) convertView.findViewById(R.id.tv_app_type_title);
					convertView.setTag(viewTitleHolder);
				} else {
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				if(position == 0){
					viewTitleHolder.tv_app_type_title.setText("用户应用（" + mConstumAppList.size() + "）");
				} else {
					viewTitleHolder.tv_app_type_title.setText("系统应用（" + mSystemAppList.size() + "）");
				}
			}else {
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
				
				viewHolderer.iv_app_icon.setBackgroundDrawable(getItem(position).drawable);
				viewHolderer.tv_app_package_name.setText(getItem(position).packageName);
				viewHolderer.tv_app_location.setText(getItem(position).isSDCard?"SD卡应用":"手机应用");
			}
			return convertView;
		}
		
	}
	
	class ViewHolderer{
		ImageView iv_app_icon;
		TextView tv_app_package_name;
		TextView tv_app_location;
	}
	
	class ViewTitleHolder{
		TextView tv_app_type_title;
	}
	
	private long getAvailSpace(String path){
		//获取可用磁盘类的对象
		StatFs statFs = new StatFs(path);
		@SuppressWarnings("deprecation")
		long availAbleBlocks = statFs.getAvailableBlocks(); //可用区块的数量
		@SuppressWarnings("deprecation")
		long blockCount = statFs.getBlockSize();  //每个区块的大小
		//statFs.getBlockCount();区块的总量
		
		return availAbleBlocks * blockCount;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData();
		
	}

	private void getData() {
		new Thread(){
			@Override
			public void run() {
				super.run();
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mSystemAppList = new ArrayList<AppInfo>();
				mConstumAppList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if(appInfo.isSystem){
						mSystemAppList.add(appInfo);
					} else {
						mConstumAppList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	
}
