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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	protected boolean mOnLoad = false;
	private int count;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(adapter == null){
				adapter = new MyAdapter();
				lv_blacknumber.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			mOnLoad = false;
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
				mBlackNumberInfos = blackNumberDao.find(0);
				count = blackNumberDao.getCount();
				mHandler.sendEmptyMessage(0);
			}
		}.start();	
	}

	private void initUI() {
		btn_add_blacknumber = (Button) findViewById(R.id.btn_add_blacknumber);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		btn_add_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				showAddDialog(null, null);
			}
		});
		lv_blacknumber.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = mBlackNumberInfos.get(position).getPhone();
				String mode2 = mBlackNumberInfos.get(position).getMode();
				showAddDialog(phone, mode2);
			}
			
		});
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			/*
			 * 滑动状态改变时触发 
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				/*
				 * 快速滑动时
				 */
				case OnScrollListener.SCROLL_STATE_FLING:
					
					break;
				/*
				 * 滑动状态停止时
				 */
				case OnScrollListener.SCROLL_STATE_IDLE:
					if(mBlackNumberInfos != null){
						if(lv_blacknumber.getLastVisiblePosition() >= mBlackNumberInfos.size() - 1 && !mOnLoad){
							if(count > mBlackNumberInfos.size()){
								new Thread(){
									@Override
									public void run() {
										blackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
										List<BlackNumberInfo> getMore = blackNumberDao.find(mBlackNumberInfos.size());
										mBlackNumberInfos.addAll(getMore);
										mOnLoad = true;
										mHandler.sendEmptyMessage(0);
									}
								}.start();
							}
						}
					}
					break;
				/*
				 * 手拖动屏幕慢慢滑动时 
				 */
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					
					break;
				}
				
			}
			
			/*
			 * 滑动过程中触发 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return mBlackNumberInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackNumberInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		/*
		 * listView优化方法
		 * 
		 * 1.复用converView
		 * 2.对findViewById次数的优化，使用ViewHolder
		 * 3.将ViewHolder定义成静态，不会去创建更多对象
		 * 4.listView如果有多个条目的时候，采用分页加载
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.blacknumber_list_item, null);
				viewHolder.tv_intercept_phone = (TextView) convertView.findViewById(R.id.tv_intercept_phone);
				viewHolder.tv_intercept_mode = (TextView) convertView.findViewById(R.id.tv_intercept_mode);
				viewHolder.iv_delete_blacknumber = (ImageView) convertView.findViewById(R.id.iv_delete_blacknumber);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tv_intercept_phone.setText(mBlackNumberInfos.get(position).getPhone().toString());
			switch (Integer.parseInt(mBlackNumberInfos.get(position).getMode())) {
			case 1:
				viewHolder.tv_intercept_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_intercept_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_intercept_mode.setText("拦截所有");
				break;
			}
			viewHolder.iv_delete_blacknumber.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View view) {
					blackNumberDao.delete(mBlackNumberInfos.get(position).getPhone());
					mBlackNumberInfos.remove(position);
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					ToastUtil.show(getApplicationContext(), "删除成功");
				}
			});
			return convertView;
		}
	}
	
	static class ViewHolder {
		TextView tv_intercept_phone;
		TextView tv_intercept_mode;
		ImageView iv_delete_blacknumber;
	}

	protected void showAddDialog(String phone, String mode2) {
		Builder builder = new AlertDialog.Builder(this);
		alertDialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) view.findViewById(R.id.et_blacknumber);
		rg_intercept_mode = (RadioGroup) view.findViewById(R.id.rg_intercept_mode);
		btn_blacknumber_submit = (Button) view.findViewById(R.id.btn_balcknumber_submit);
		btn_blacknumber_cancel = (Button) view.findViewById(R.id.btn_balcknumber_cancel);
		et_blacknumber.setText(phone);
		if( mode2 !=null ){
			switch (Integer.parseInt(mode2)) {
			case 1:
				rg_intercept_mode.check(R.id.rb_sms);
				break;
			case 2:
				rg_intercept_mode.check(R.id.rb_phone);
				break;
			case 3:
				rg_intercept_mode.check(R.id.rb_all);
				break;
			}
		}
		rg_intercept_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
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
			public void onClick(View view) {
				if(!TextUtils.isEmpty(et_blacknumber.getText().toString())){
					if(blackNumberDao.findOne(et_blacknumber.getText().toString())){
						blackNumberDao.update(et_blacknumber.getText().toString(), mode + "");
						for (BlackNumberInfo mBlackNumberInfo : mBlackNumberInfos) {
							if(mBlackNumberInfo.getPhone().equals(et_blacknumber.getText().toString())){
								mBlackNumberInfo.setMode(mode+"");
							}
						}
						ToastUtil.show(getApplicationContext(), "修改成功");
					} else {
						blackNumberDao.insert(et_blacknumber.getText().toString(), mode + "");
						BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
						blackNumberInfo.setPhone(et_blacknumber.getText().toString());
						blackNumberInfo.setMode(mode + "");
						mBlackNumberInfos.add(0, blackNumberInfo);
						ToastUtil.show(getApplicationContext(), "添加成功");
					}
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
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		alertDialog.setView(view);
		alertDialog.show();
	}
	
}
