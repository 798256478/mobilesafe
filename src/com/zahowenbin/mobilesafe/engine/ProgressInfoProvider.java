package com.zahowenbin.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.db.domain.ProgressInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class ProgressInfoProvider {
	public static int getProgressTotal(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		return runningAppProcesses.size();
	}
	
	public static long getAvailSpace(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);//给memoryInfo对象赋值
		return memoryInfo.availMem;
	}
	
	public static long getTotalSpace(Context context){
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader= new BufferedReader(fileReader);
			char[] charArray = bufferedReader.readLine().toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if(c >= '0' && c <= '9'){
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString()) * 1024;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static List<ProgressInfo> getProgressInfoList(Context context){
		PackageManager packageManager = context.getPackageManager();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		List<ProgressInfo> progressInfoList = new ArrayList<ProgressInfo>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			ProgressInfo progressInfo = new ProgressInfo();
			progressInfo.packageNmae = runningAppProcessInfo.processName;
			android.os.Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			android.os.Debug.MemoryInfo processInfo = processMemoryInfo[0];
			progressInfo.dirtySpace = processInfo.getTotalPrivateDirty() * 1024;
			try {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(runningAppProcessInfo.processName, 0);
				progressInfo.name = packageManager.getApplicationLabel(applicationInfo).toString();
				progressInfo.icon = packageManager.getApplicationIcon(applicationInfo);
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
					progressInfo.isSystem = true;
				} else {
					progressInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				progressInfo.name = runningAppProcessInfo.processName;
				progressInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
				progressInfo.isSystem = true;
			}
			progressInfoList.add(progressInfo);
		}
		return progressInfoList;
	}

	public static void cleanProgress(Context context, ProgressInfo progressInfo) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(progressInfo.packageNmae);
	}

	public static void cleanAllProcess(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
	}
}
