package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class BaseCacheManagerActivity extends TabActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_cache_manager);
		
		TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("ÊÖ»ú»º´æ");
		TabSpec tab2 = getTabHost().newTabSpec("sd_clear_cache").setIndicator("SD¿¨»º´æ");
		
		tab1.setContent(new Intent(this, CacheManagerActivity.class));
		tab2.setContent(new Intent(this, SDCacheManagerActivity.class));
		
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}
}
