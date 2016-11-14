package com.zahowenbin.mobilesafe.receiver;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.service.LocationService;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Boolean setUpOver = SpUtil.getBoolean(context, ConstantView.SETUPOVER, false);
		if(setUpOver){
			//获取短信内容，一条短信太长可能会被截为多个短信，所以获取的是个对象数组
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				//获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//获取短信对象的基本信息
				//String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				if(messageBody.contains("#*alarm*#")){
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}
				if(messageBody.contains("#*location*#")){
					context.startService(new Intent(context, LocationService.class));
				}
			}
		}
	}

}
