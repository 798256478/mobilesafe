package com.zahowenbin.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;
import com.zahowenbin.mobilesafe.utils.StreamUtil;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SplashActivity extends Activity {

    protected static final String tag = "MainActivity";
	protected static final int ENTER_HOME = 100;
	protected static final int UPDATE_VERSION = 101;
	protected static final int URL_ERROR = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;
    private TextView tv_version_name;
    private int mLocalVersion;
    private String mVersionDes;
    private String mDownloadUrl;
    
    
    private Handler mhandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case UPDATE_VERSION:
				showUpdateDialog();
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(getApplicationContext(), "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(getApplicationContext(), "读取异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(getApplicationContext(), "解析异常");
				enterHome();
				break;
			}
    	};
    };
	private RelativeLayout rl_root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除app头的第一种方式
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        
        
        if(!hasShortcut()){
        	initShortCut();
        }
        initUI();
        initData();
        initAnimation();
        initDB();
    }



	private void initShortCut() {
		//1,给intent维护图标,名称
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//维护图标（教程上的方法有错，这是正确的）
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, 
				ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
		//名称
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士快捷方式");
		//2,点击快捷方式后跳转到的activity
		//2.1维护开启的意图对象
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		//3,发送广播
		sendBroadcast(intent);
	}

	private void initDB() {
		initAddressDB("address.db");
		initAddressDB("commonnum.db");
	}
	
	public boolean hasShortcut() {
		  String url = "";
		  url = "content://" + getAuthorityFromPermission("com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
		  ContentResolver resolver = getContentResolver();
		  Cursor cursor = resolver.query(Uri.parse(url), new String[] { "title", "iconResource" }, "title=?", new String[] { getString(R.string.app_name).trim() }, null);
		  if (cursor != null && cursor.moveToFirst()) {
		   cursor.close();
		   return true;
		  }
		  return false;
	}
	/*
	 * 因为不同的手机厂商可能对手机系统进行了修改使用原生的
	 * “content://com.android.launcher.settings/favorites?notify=true"
	 * 或者
	 * "content://com.android.launcher2.settings/favorites?notify=true"
	 * 并不能准确判断 需要通过权限去获取当前手机provider.authority
	 */
	private String getAuthorityFromPermission(String permission) {
		if (permission == null)
		return null;
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packs != null) {
		   for (PackageInfo pack : packs) {
			   ProviderInfo[] providers = pack.providers;
			   if (providers != null) {
				   for (ProviderInfo provider : providers) {
					   if (permission.equals(provider.readPermission))
						   return provider.authority;
					   if (permission.equals(provider.writePermission))
						   return provider.authority;
				   }
			   }
		   }
		}
		return null;
	}

	private void initAddressDB(String DBName) {
		File fileDir = getFilesDir();
		File file = new File(fileDir, DBName);
		if(file.exists()){
			return;
		}
		InputStream is = null;
		FileOutputStream  fos = null;
		try {
			is = getAssets().open(DBName);
			fos = new FileOutputStream(file);
			int temp = -1;
			byte[] array = new byte[1024];
			while ((temp = is.read(array)) != -1 ) {
				fos.write(array, 0, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(is != null && fos != null){
				try {
					is.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}

	protected void showUpdateDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("升级提醒");
		builder.setMessage(mVersionDes);
		builder.setPositiveButton("马上升级", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadApk();
				
			}
		});
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whcih) {
				enterHome();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	protected void downloadApk() {
		//判断SD卡是否可用
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mobilesafe.apk";
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {//下载成功
					Log.i(tag, "下载成功");
					File file = responseInfo.result;//获取下载文件
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException exception, String s) {//下载失败
					Log.i(tag, "下载失败");
					Log.i(tag, exception+"");
					Log.i(tag, s);
				}
				
				@Override
				public void onStart() {//开始下载
					Log.i(tag, "开始下载");
					super.onStart();
				}
				
				@Override
				public void onLoading(long total, long current,boolean isUploading) {//下载过程中
					Log.i(tag, "total = " + total); //下载总量
					Log.i(tag, "current = " + current);//当前下载量
					Log.i(tag, "isUploading = " + isUploading);//是否正在下载
					super.onLoading(total, current, isUploading);
				}
			});
		}
		
	}

	protected void installApk(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
	}
	
	//开启一个activity后，返回调用的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
     * 初始化数据的方法
     */
    private void initData() {

        tv_version_name.setText("版本名称 " + getVersionName());
        mLocalVersion = getVersionCode();//將變量設為成員變量快捷鍵 Ctrl+shift+f
        
        if(SpUtil.getBoolean(this, ConstantView.OPEN_UPDATE, false)){
        	checkVersion();
        } else {
        	mhandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
        
    }

    private void checkVersion() {
        //需要連接服務器，耗費時間，所以要開一條線程
        new Thread(){
          public void run(){
        	  Message msg = Message.obtain();
        	  long startTime = System.currentTimeMillis();
              try {
                  URL url = new URL("http://192.168.1.106/update.json");
                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                  connection.setConnectTimeout(2000);//設置連接超時
                  connection.setReadTimeout(2000);//設置讀取超時
                  //connection.setRequestMethod("GET");設置訪問方式，一般默認為GET，所以可以不寫
                  if(connection.getResponseCode() == 200){ //返回200，代表讀取成功
                      InputStream is = connection.getInputStream();//獲取讀取流
                      String json = StreamUtil.streamToString(is);
                      Log.i(tag, json);
                      JSONObject jsonObject = new JSONObject(json);
                      String versionCode = jsonObject.getString("versionCode");
                      String versionName = jsonObject.getString("versionName");
                      mVersionDes = jsonObject.getString("versionDes");
                      mDownloadUrl = jsonObject.getString("downloadUrl");
                      Log.i(tag, versionCode);
                      Log.i(tag, versionName);
                      Log.i(tag, mVersionDes);
                      Log.i(tag, mDownloadUrl);
                      if(mLocalVersion < Integer.parseInt(versionCode)){
                    	  Log.i(tag, "update");
                    	  msg.what = UPDATE_VERSION;
                      } else {
                    	  Log.i(tag, "enter_home");
                    	  msg.what = ENTER_HOME;
                      }
                  } else{
                      Log.i("err", connection.getResponseCode()+"");
                  }
              } catch (MalformedURLException e) {
            	  msg.what = URL_ERROR;
                  e.printStackTrace();
              } catch (IOException e) {
            	  msg.what = IO_ERROR;
                  e.printStackTrace();
              } catch (JSONException e) {
				msg.what = JSON_ERROR;
				e.printStackTrace();
			} finally {
				long endTime = System.currentTimeMillis();
				if((endTime - startTime) < 4000){
					try {
						Thread.sleep(4000-(endTime - startTime));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mhandler.sendMessage(msg);
			}
          }
        }.start();
    }

    /**
     * 获取版本号
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * 获取版本名称
     * @return 应用版本名称 返回Null代表异常
     */
    private String getVersionName() {
        //获取包管理对象packageManager
        PackageManager pm = this.getPackageManager();
        try {
            //从包管理对象中获取制定包名的基本信息（版本名称，版本号等基本信息） 传0代表获取基本信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //返回版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  初始化UI方法
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }
}
