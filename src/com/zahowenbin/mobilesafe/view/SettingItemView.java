package com.zahowenbin.mobilesafe.view;

import com.zahowenbin.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.zahowenbin.mobilesafe";
	private TextView tv_title;
	private TextView tv_des;
	private CheckBox cb_box;
	private String mDestitle;
	private String mDeson;
	private String mDesoff;

	public SettingItemView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.setting_item_view, this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_des = (TextView)findViewById(R.id.tv_des);
		cb_box = (CheckBox)findViewById(R.id.cb_box);
		
		initAttrs(attrs);
		tv_title.setText(mDestitle);
	}
	
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
	}

	public boolean isCheck() {
		return cb_box.isChecked();
	}
	
	public void setCheck(boolean isCheck) {
		cb_box.setChecked(isCheck);
		tv_des.setText(isCheck?mDeson:mDesoff);
	}

}
