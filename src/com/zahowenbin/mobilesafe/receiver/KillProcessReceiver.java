package com.zahowenbin.mobilesafe.receiver;

import com.zahowenbin.mobilesafe.engine.ProgressInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProgressInfoProvider.cleanAllProcess(context);
	}

}
