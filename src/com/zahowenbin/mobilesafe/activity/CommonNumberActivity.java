package com.zahowenbin.mobilesafe.activity;

import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.QueryCommonNumDao;
import com.zahowenbin.mobilesafe.engine.QueryCommonNumDao.Child;
import com.zahowenbin.mobilesafe.engine.QueryCommonNumDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> group;
	private MyAdapter myAdapter;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			elv_common_number.setAdapter(myAdapter);
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initData();
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + myAdapter.getChild(groupPosition, childPosition).number));
				startActivity(intent);
				return false;
			}
		});
	}

	private void initData() {
		new Thread(){
			public void run() {
				group = QueryCommonNumDao.getGroup();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	
	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return group.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return group.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return group.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return group.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("           " + getGroup(groupPosition).name);
			textView.setPadding(5, 8, 5, 8);
			textView.setTextColor(new Color().RED);
			textView.setTextSize(16);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.commonnum_child_item, null);
			TextView tv_commonnum_child_name = (TextView) view.findViewById(R.id.tv_commonnum_child_name);
			TextView tv_commonnum_child_number = (TextView) view.findViewById(R.id.tv_commonnum_child_number);
			tv_commonnum_child_name.setText(getChild(groupPosition, childPosition).name);
			tv_commonnum_child_number.setText(getChild(groupPosition, childPosition).number);
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}
