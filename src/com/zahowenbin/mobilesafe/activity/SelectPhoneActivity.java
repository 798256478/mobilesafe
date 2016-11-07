package com.zahowenbin.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zahowenbin.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectPhoneActivity extends Activity {
	protected static final String tag = "SelectPhoneActivity";
	private List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
	private ListView lv_contact_list;
	private Handler mHandler = new Handler(){
		private Adapter mAdapter;

		public void handleMessage(android.os.Message msg) {
			mAdapter = new Adapter();
			lv_contact_list.setAdapter(mAdapter);
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_phone);
		
		initUI();
		initData();
	}
	
	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactList.size();
		}

		@Override
		public Map<String, String> getItem(int position) {
			// TODO Auto-generated method stub
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.contact_list_item, null);
			TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView)view.findViewById(R.id.tv_phone);
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			return view;
		}
		
	}

	private void initData() {
		new Thread(){
			public void run(){
				//获取内容解析对象
				ContentResolver contentResolver = getContentResolver();
				Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), 
						new String[]{"contact_id"}, 
						null, 
						null, 
						null);
				contactList.clear();
				while(cursor.moveToNext()){
					String id = cursor.getString(0);
					Log.i(tag, id);
					Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), 
							new String[]{"data1", "mimetype"}, 
							"raw_contact_id = ?", new String[]{id}, null);
					Map<String, String> hashMap = new HashMap<String, String>();
					while(indexCursor.moveToNext()){
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);
						if(type.equals("vnd.android.cursor.item/phone_v2")){
							if(!TextUtils.isEmpty(data))
							{
								hashMap.put("phone", data);
							} 
						} else if(type.equals("vnd.android.cursor.item/name")){
							if(!TextUtils.isEmpty(data))
							{
								hashMap.put("name", data);
							} 
						}
					}
					indexCursor.close();
					contactList.add(hashMap);
				}
				cursor.close();
				mHandler.sendEmptyMessage(0);
			}
		}.start();
		
	}

	private void initUI() {
		lv_contact_list = (ListView)findViewById(R.id.lv_contact_list);
		lv_contact_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv_phone = (TextView)view.findViewById(R.id.tv_phone);
				String phone = tv_phone.getText().toString().trim();
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
			}
		});
	}
}
