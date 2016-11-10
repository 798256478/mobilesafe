package com.zahowenbin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.db.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoProvider {
	public static List<AppInfo> getAppInfoList(Context context){
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo appInfo = new AppInfo();
			appInfo.packageName = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.name = applicationInfo.loadLabel(packageManager).toString();
			appInfo.drawable = applicationInfo.loadIcon(packageManager);
			if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
				appInfo.isSystem = true;
			} else {
				appInfo.isSystem = false;
			}
			
			if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				appInfo.isSDCard = true;
			} else {
				appInfo.isSDCard = false;
			}
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
	
	
}
