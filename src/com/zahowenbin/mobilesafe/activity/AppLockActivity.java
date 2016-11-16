package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.dao.AppLockDao;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppLockActivity extends Activity {
	private List<AppInfo> lockAppList;
	private List<AppInfo> unLockAppList;
	private AppLockDao appLockDao;
	private ListView lv_unlock_list;
	private ListView lv_lock_list;
	private TextView tv_unlock_title;
	private TextView tv_lock_title;
	private Button btn_unlock;
	private Button btn_lock;
	private LinearLayout ll_unlock_list;
	private LinearLayout ll_lock_list;
	private MyAdapter lockAdapter;
	private MyAdapter unLockAdapter;
	
	Handler mHandler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			lockAdapter = new MyAdapter(true);
			lv_lock_list.setAdapter(lockAdapter);
			unLockAdapter = new MyAdapter(false);
			lv_unlock_list.setAdapter(unLockAdapter);
			tv_unlock_title.setText("未加锁程序" + unLockAppList.size());
			tv_lock_title.setText("已加锁程序" + lockAppList.size());
		};
	};
	private TranslateAnimation toLockAnimation;
	private TranslateAnimation toUnLockAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		appLockDao = AppLockDao.instance(this);
		initUI();
		initData();
	}

	private void initData() {
		new Thread(){

			@Override
			public void run() {
				List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				lockAppList = new ArrayList<AppInfo>();
				unLockAppList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfoList) {
					if(appLockDao.findAll().contains(appInfo.packageName)){
						lockAppList.add(appInfo);
						unLockAppList.remove(appInfo);
					} else {
						lockAppList.remove(appInfo);
						unLockAppList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		ll_lock_list = (LinearLayout) findViewById(R.id.ll_lock);
		ll_unlock_list = (LinearLayout) findViewById(R.id.ll_unlock);
		
		btn_lock = (Button) findViewById(R.id.btn_lock);
		btn_unlock = (Button) findViewById(R.id.btn_unlock);
		
		tv_lock_title = (TextView) findViewById(R.id.tv_lock_title);
		tv_unlock_title = (TextView) findViewById(R.id.tv_unlock_title);
		
		lv_lock_list = (ListView) findViewById(R.id.lv_lock_list);
		lv_unlock_list = (ListView) findViewById(R.id.lv_unlock_list);
		
		toLockAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		toLockAnimation.setDuration(400);
		toUnLockAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		toUnLockAnimation.setDuration(400);
		
		btn_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll_lock_list.setVisibility(View.VISIBLE);
				ll_unlock_list.setVisibility(View.GONE);
				btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
				if(lockAdapter != null){
					lockAdapter.notifyDataSetChanged();
					tv_lock_title.setText("已加锁程序" + lockAdapter.getCount());
				}
			}
		});
		
		btn_unlock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll_lock_list.setVisibility(View.GONE);
				ll_unlock_list.setVisibility(View.VISIBLE);
				btn_lock.setBackgroundResource(R.drawable.tab_right_default);
				btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				if(unLockAdapter != null){
					unLockAdapter.notifyDataSetChanged();
					tv_unlock_title.setText("未加锁程序" + unLockAdapter.getCount());
				}
			}
		});
	}
	
	class MyAdapter extends BaseAdapter{
		private Boolean flag = false;
		public MyAdapter(Boolean flag){
			this.flag = flag;
		}
		
		@Override
		public int getCount() {
			if(flag){
				return lockAppList.size();
			} else {
				return unLockAppList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if(flag){
				return lockAppList.get(position);
			} else {
				return unLockAppList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.app_lock_list_item, null);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
				viewHolder.name = (TextView) convertView.findViewById(R.id.tv_app_name);
				viewHolder.lock = (ImageView) convertView.findViewById(R.id.iv_lock_img);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final View tempView = convertView;
			viewHolder.icon.setBackgroundDrawable(getItem(position).drawable);
			viewHolder.name.setText(getItem(position).name);
			if(getItem(position).packageName.equals(getPackageName())){
				viewHolder.lock.setVisibility(View.GONE);
			}
			else{
				if(flag){
					viewHolder.lock.setBackgroundResource(R.drawable.lock);	
				} else {
					viewHolder.lock.setBackgroundResource(R.drawable.unlock);
				}	
			}
			
			viewHolder.lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(flag){
						toUnLockAnimation.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								appLockDao.delete(getItem(position).packageName);
								unLockAppList.add(getItem(position));
								lockAppList.remove(getItem(position));
								tv_lock_title.setText("已加锁程序" + getCount());
								if(lockAdapter != null){
									lockAdapter.notifyDataSetChanged();
								}
							}
						});
						tempView.startAnimation(toUnLockAnimation);
					} else{
						toLockAnimation.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								appLockDao.insert(getItem(position).packageName);
								lockAppList.add(getItem(position));
								unLockAppList.remove(getItem(position));
								tv_unlock_title.setText("未加锁程序" + getCount());
								if(unLockAdapter != null){
									unLockAdapter.notifyDataSetChanged();
								}
							}
						});
						tempView.startAnimation(toLockAnimation);
					}
				}
			});
			return convertView;
		}
		
	}
	
	class ViewHolder{
		ImageView icon;
		TextView name;
		ImageView lock;
	}
}
