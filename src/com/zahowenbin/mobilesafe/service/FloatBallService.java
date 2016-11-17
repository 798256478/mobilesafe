package com.zahowenbin.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.activity.RocketBgActivity;
import com.zahowenbin.mobilesafe.engine.ProgressInfoProvider;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FloatBallService extends Service {
	
	protected static final int UPDATE_LOCATION = 100;
	protected static final int UPDATE_SEND = 101;
	protected static final int UPDATE_FLOAT = 102;
	private WindowManager mWindowManager;
	private int mScreenHeight;
	private int mScreenwidth;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mFloatBallView;
	private TextView iv_float_ball;
	private AnimationDrawable mAnimationDrawable;
	private WindowManager.LayoutParams params;
	private Timer timer;
	private InnerReceiver innerReceiver;
	private LayoutParams layoutParams;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_LOCATION:
				startTimer();
				params.x = SpUtil.getInt(getApplicationContext(), ConstantView.FLOAT_BALL_lOCATIONX, 0);
		        params.y = SpUtil.getInt(getApplicationContext(), ConstantView.FLOAT_BALL_lOCATIONY, 0);
		        iv_float_ball.setBackgroundResource(R.drawable.float_ball_default_animation_bg);
		        mAnimationDrawable = (AnimationDrawable) iv_float_ball.getBackground();
		        mAnimationDrawable.start();
				iv_float_ball.setText((int)(((double)ProgressInfoProvider.getAvailSpace(getApplicationContext())/(double)ProgressInfoProvider.getTotalSpace(getApplicationContext()))*100)+"%");
				break;
			case UPDATE_SEND:
				iv_float_ball.setText("");
				params.y = (Integer) msg.obj;
				break;
			case UPDATE_FLOAT:
				if(iv_float_ball != null)
					iv_float_ball.setText((int)(((double)ProgressInfoProvider.getAvailSpace(getApplicationContext())/(double)ProgressInfoProvider.getTotalSpace(getApplicationContext()))*100)+"%");
				break;
			}
			mWindowManager.updateViewLayout(mFloatBallView, params);
		};
	};


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
		mScreenwidth = mWindowManager.getDefaultDisplay().getWidth();
		showFloatBall();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
		startTimer();
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
				cancelTimer();
			} else {
				startTimer();
			}
		}
	}
	
	private void cancelTimer() {
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
	private void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Message message = new Message();
				message.what = UPDATE_FLOAT;
				mHandler.sendMessage(message);
			}
		}, 0, 3000);
	}

	private void showFloatBall() {
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;不允许获取触摸
		//指定吐司的位置
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.x = SpUtil.getInt(this, ConstantView.FLOAT_BALL_lOCATIONX, 0);
        params.y = SpUtil.getInt(this, ConstantView.FLOAT_BALL_lOCATIONY, 0);
        mFloatBallView = View.inflate(this, R.layout.toast_float_ball, null);

        iv_float_ball = (TextView) mFloatBallView.findViewById(R.id.iv_float_ball);
        mAnimationDrawable = (AnimationDrawable) iv_float_ball.getBackground();
        mAnimationDrawable.start();

        mWindowManager.addView(mFloatBallView, params);	
        iv_float_ball.setOnTouchListener(new OnTouchListener() {

			private int mStartX;
			private int mStartY;
			private int mMoveX;
			private int mMoveY;


			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					cancelTimer();
					mWindowManager.updateViewLayout(mFloatBallView, params);
					iv_float_ball.setBackgroundResource(R.drawable.float_ball_animation_bg);
					layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					iv_float_ball.setLayoutParams(layoutParams);
					iv_float_ball.setText("");
					 /*
			         * 使动态背景图片动起来
			         */
					mAnimationDrawable = (AnimationDrawable) iv_float_ball.getBackground();
			        mAnimationDrawable.start();
					mWindowManager.updateViewLayout(mFloatBallView, params);
					mStartX =(int) event.getRawX();
					mStartY =(int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					mMoveX =(int) event.getRawX();
					mMoveY =(int) event.getRawY();
					
					int desX = mMoveX-mStartX;
					int desY = mMoveY-mStartY;
					
					params.x = params.x + desX;
					params.y = params.y + desY;
					
					if(params.x<0){
						params.x = 0;
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.x > mScreenwidth - iv_float_ball.getWidth()){
						params.x = mScreenwidth - iv_float_ball.getWidth();
					}
					if(params.y > mScreenHeight - 22 - iv_float_ball.getHeight()){
						params.y = mScreenHeight - 22 - iv_float_ball.getHeight();
					}
					mWindowManager.updateViewLayout(mFloatBallView, params);
					mStartX =(int) event.getRawX();
					mStartY =(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					if((params.x + iv_float_ball.getWidth() ) > (mScreenwidth/3) && (params.x + iv_float_ball.getWidth() )< (mScreenwidth/3)*2 && params.y > mScreenHeight - 50 - iv_float_ball.getHeight()){
						Intent clearIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
						sendBroadcast(clearIntent);
						Intent intent = new Intent(getApplicationContext(), RocketBgActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						sendRocket();
					}else {
						startTimer();
						iv_float_ball.setBackgroundResource(R.drawable.float_ball_default_animation_bg);
						mAnimationDrawable = (AnimationDrawable) iv_float_ball.getBackground();
				        mAnimationDrawable.start();
						iv_float_ball.setText((int)(((double)ProgressInfoProvider.getAvailSpace(getApplicationContext())/(double)ProgressInfoProvider.getTotalSpace(getApplicationContext()))*100)+"%");
						if(params.x>mScreenwidth/2){
							params.x = mScreenwidth - 35;
						} else if(params.x<mScreenwidth/2){
							params.x = 0;
						}
						mWindowManager.updateViewLayout(mFloatBallView, params);
						SpUtil.putInt(getApplicationContext(), ConstantView.FLOAT_BALL_lOCATIONX, params.x);
						SpUtil.putInt(getApplicationContext(), ConstantView.FLOAT_BALL_lOCATIONY, params.y);
					}
					break;
				}
				return true;
			}
		});
        
	}

	protected void sendRocket() {
		new Thread(){
			private Message msg;

			@Override
			public void run() {
				int height = mScreenHeight - 22 - iv_float_ball.getHeight();
				for (int i = 0; i < 11; i++) {
					int left = height - (height / 10) * i;
					msg = Message.obtain();
					msg.what = UPDATE_SEND;
					msg.obj = left;
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg = Message.obtain();
				msg.what = UPDATE_LOCATION;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		if(mWindowManager != null && mFloatBallView != null){
			mWindowManager.removeView(mFloatBallView);
		}
		if(innerReceiver != null){			
			unregisterReceiver(innerReceiver);
		}
		if(timer != null){
			timer.cancel();
		}
		super.onDestroy();
	}
}
