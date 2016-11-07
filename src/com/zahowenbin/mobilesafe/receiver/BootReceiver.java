package com.zahowenbin.mobilesafe.receiver;

import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;


public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String oldSimNumber = SpUtil.getString(context, ConstantView.SIM_BIND, "");
		TelephonyManager telephoneManger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String nowSimNumber = telephoneManger.getSimSerialNumber();
		Boolean setUpOver = SpUtil.getBoolean(context, ConstantView.SETUPOVER, false);
		if(setUpOver && !oldSimNumber.equals(nowSimNumber)){
			String safePhone = SpUtil.getString(context, ConstantView.BIND_PHONE, "");
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(safePhone, null, "sim change!!!", null, null);
		}
	}
}
