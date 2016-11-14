package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.activity.AppManagerActivity.MyAdapter;
import com.zahowenbin.mobilesafe.activity.AppManagerActivity.ViewHolderer;
import com.zahowenbin.mobilesafe.activity.AppManagerActivity.ViewTitleHolder;
import com.zahowenbin.mobilesafe.db.domain.AppInfo;
import com.zahowenbin.mobilesafe.db.domain.ProgressInfo;
import com.zahowenbin.mobilesafe.engine.AppInfoProvider;
import com.zahowenbin.mobilesafe.engine.ProgressInfoProvider;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ProcessManagerActivity extends Activity {
	private int mProgressTotal;
	private String mAvailSpace;
	private TextView tv_progress_count;
	private TextView tv_ram_memory;
	private String mTotalSpace;
	protected List<ProgressInfo> mProgressInfoList;
	protected ArrayList<ProgressInfo> mSystemProgressList;
	protected ArrayList<ProgressInfo> mConstumProgressList;
	private TextView tv_progress_float_title;
	private ListView lv_progresslist;
	private MyAdapter myAdapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			lv_progresslist.setAdapter(myAdapter);
			if(tv_progress_float_title!= null && mConstumProgressList != null){
				tv_progress_float_title.setText("用户应用（" + mConstumProgressList.size() +"）");	
			}
		};
	};
	private long availSpace;
	private long totalSpace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		initData();
		initUI();
	}

	private void initUI() {
		tv_progress_count = (TextView) findViewById(R.id.tv_process_count);
		tv_ram_memory = (TextView) findViewById(R.id.tv_ram_memory);
		tv_progress_count.setText("当前运行进程：" + mProgressTotal);
		tv_ram_memory.setText("内存可用/总数：" + mAvailSpace + "/"+ mTotalSpace);
		lv_progresslist = (ListView) findViewById(R.id.lv_processlist);
		tv_progress_float_title = (TextView) findViewById(R.id.tv_process_float_title);
		Button btn_select_all = (Button) findViewById(R.id.btn_select_all);
		Button btn_select_inverse = (Button) findViewById(R.id.btn_select_inverse);
		Button btn_clean = (Button) findViewById(R.id.btn_clean);
		Button btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_select_all.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (ProgressInfo customProgressInfo : mConstumProgressList) {
					if(!customProgressInfo.packageNmae.equals(getPackageName())){
						customProgressInfo.isCheck = true;
					}
				}
				for (ProgressInfo systemProgressInfo : mSystemProgressList) {
					systemProgressInfo.isCheck = true;
				}
				if(myAdapter != null){					
					myAdapter.notifyDataSetChanged();
				}
			}
		});
		btn_select_inverse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (ProgressInfo customProgressInfo : mConstumProgressList) {
					if(!customProgressInfo.packageNmae.equals(getPackageName())){
						customProgressInfo.isCheck = !customProgressInfo.isCheck ;
					}
				}
				for (ProgressInfo systemProgressInfo : mSystemProgressList) {
					systemProgressInfo.isCheck = !systemProgressInfo.isCheck;
				}
				if(myAdapter != null){					
					myAdapter.notifyDataSetChanged();
				}
			}
		});
		btn_clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<ProgressInfo> tempList = new ArrayList<ProgressInfo>();
				long cleanSpace = 0;
				for (ProgressInfo customProgressInfo : mConstumProgressList) {
					if(customProgressInfo.isCheck){
						tempList.add(customProgressInfo);
					}
				}
				for (ProgressInfo systemProgressInfo : mSystemProgressList) {
					if(systemProgressInfo.isCheck){
						tempList.add(systemProgressInfo);
					}
				}
				for (ProgressInfo progressInfo : tempList) {
					if(mConstumProgressList.contains(progressInfo)){
						mConstumProgressList.remove(progressInfo);
						cleanSpace += progressInfo.dirtySpace;
					}
					if(mSystemProgressList.contains(progressInfo)){
						mSystemProgressList.remove(progressInfo);
						cleanSpace += progressInfo.dirtySpace;
					}
					ProgressInfoProvider.cleanProgress(getApplicationContext(), progressInfo);
				}
				if(myAdapter != null){					
					myAdapter.notifyDataSetChanged();
				}
				tv_progress_count.setText("当前运行内存：" + (mProgressTotal - tempList.size()));
				tv_ram_memory.setText("内存可用/总数：" + 
				Formatter.formatFileSize(getApplicationContext(), availSpace += cleanSpace) + "/"+ mTotalSpace);
				ToastUtil.show(getApplicationContext(), "清除"+ tempList.size() +"个进程,释放"+ Formatter.formatFileSize(getApplicationContext(), cleanSpace) +"空间");
			}
		});
		btn_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),ProcessSettingActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		lv_progresslist.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mConstumProgressList != null && mSystemProgressList != null){
					if(firstVisibleItem < mConstumProgressList.size() + 1){
						tv_progress_float_title.setText("用户应用（" + mConstumProgressList.size() +"）" );
					} else {
						tv_progress_float_title.setText("系统应用（" + mSystemProgressList.size() +"）" );
					}
				}		
			}
		});
		lv_progresslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position != 0 && position != mConstumProgressList.size() +1){
					CheckBox cb_progress = (CheckBox) view.findViewById(R.id.cb_progress);
					if(position < mConstumProgressList.size()+1){
						if(!mConstumProgressList.get(position - 1).getPackageNmae().equals(getPackageName())){
							Boolean isCheck = !mConstumProgressList.get(position - 1).isCheck;
							cb_progress.setChecked(isCheck);
							mConstumProgressList.get(position - 1).isCheck = isCheck;
						}
					} else {
						Boolean isCheck = !mSystemProgressList.get(position - mConstumProgressList.size() - 2).isCheck;
						cb_progress.setChecked(isCheck);
						mSystemProgressList.get(position - mConstumProgressList.size() - 2).isCheck = isCheck;
					}
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(myAdapter != null){
			myAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initData() {
		mProgressTotal = ProgressInfoProvider.getProgressTotal(this);
		availSpace = ProgressInfoProvider.getAvailSpace(this);
		mAvailSpace = Formatter.formatFileSize(this, availSpace);
		totalSpace = ProgressInfoProvider.getTotalSpace(this);
		mTotalSpace = Formatter.formatFileSize(this, totalSpace);
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				mProgressInfoList = ProgressInfoProvider.getProgressInfoList(getApplicationContext());
				mSystemProgressList = new ArrayList<ProgressInfo>();
				mConstumProgressList = new ArrayList<ProgressInfo>();
				for (ProgressInfo progressInfo : mProgressInfoList) {
					if(progressInfo.isSystem){
						mSystemProgressList.add(progressInfo);
					} else {
						mConstumProgressList.add(progressInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}
	class MyAdapter extends BaseAdapter{
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount() + 1;
		}
		
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mConstumProgressList.size() +1){
				return 0;
			} else {
				return 1;
			}
		}
		
		@Override
		public int getCount() {
			Boolean show_system = SpUtil.getBoolean(getApplicationContext(), ConstantView.SHOW_SYSTEM, false);
			if(show_system){
				return mConstumProgressList.size() + mSystemProgressList.size() + 2;
			} else {
				return mConstumProgressList.size() + 1;
			}
		}

		@Override
		public ProgressInfo getItem(int position) {
			// TODO Auto-generated method stub
			if(position > 0 && position < mConstumProgressList.size()+1){
				return mConstumProgressList.get(position - 1);
			} else if(position > mConstumProgressList.size()+1){
				return mSystemProgressList.get(position - mConstumProgressList.size() - 2);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(getItemViewType(position) == 0){
				ViewTitleHolder viewTitleHolder = null;
				if(convertView == null){
					viewTitleHolder = new ViewTitleHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.applist_title_item, null);
					viewTitleHolder.tv_progress_type_title = (TextView) convertView.findViewById(R.id.tv_app_type_title);
					convertView.setTag(viewTitleHolder);
				} else {
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				if(position == 0){
					viewTitleHolder.tv_progress_type_title.setText("用户进程（" + mConstumProgressList.size() + "）");
				} else {
					viewTitleHolder.tv_progress_type_title.setText("系统进程（" + mSystemProgressList.size() + "）");
				}
			}else {
				ViewHolderer viewHolderer = null;
				if(convertView == null){
					viewHolderer = new ViewHolderer();
					convertView = View.inflate(getApplicationContext(), R.layout.progresslist_item, null);
					viewHolderer.iv_progress_icon = (ImageView) convertView.findViewById(R.id.iv_progress_icon);
					viewHolderer.tv_progress_name = (TextView) convertView.findViewById(R.id.tv_progress_name);
					viewHolderer.tv_progress_dirty_space = (TextView) convertView.findViewById(R.id.tv_progress_dirty_space);
					viewHolderer.cb_progress = (CheckBox) convertView.findViewById(R.id.cb_progress);
					convertView.setTag(viewHolderer);
				} else {
					viewHolderer = (ViewHolderer) convertView.getTag();
				}
				
				if(getItem(position).packageNmae.equals(getPackageName())){
					viewHolderer.cb_progress.setVisibility(View.GONE);
				}
				
				viewHolderer.iv_progress_icon.setBackgroundDrawable(getItem(position).icon);
				viewHolderer.tv_progress_name.setText(getItem(position).name);
				viewHolderer.cb_progress.setChecked(getItem(position).isCheck);
				viewHolderer.tv_progress_dirty_space.setText("占用内存" + Formatter.formatFileSize(getApplicationContext(), getItem(position).dirtySpace));
			}
			return convertView;
		}
		
	}
	
	class ViewHolderer{
		ImageView iv_progress_icon;
		TextView tv_progress_name;
		TextView tv_progress_dirty_space;
		CheckBox cb_progress;
	}
	
	class ViewTitleHolder{
		TextView tv_progress_type_title;
	}
	
}
