package com.zahowenbin.mobilesafe.global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
				
				String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "mobilesafe.log";
				File file = new File(path);
				try {
					PrintWriter printWriter = new PrintWriter(file);
					ex.printStackTrace(printWriter);
					printWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
	}
}
