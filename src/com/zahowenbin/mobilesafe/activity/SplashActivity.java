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
				ToastUtil.show(getApplicationContext(), "url�쳣");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(getApplicationContext(), "��ȡ�쳣");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(getApplicationContext(), "�����쳣");
				enterHome();
				break;
			}
    	};
    };
	private RelativeLayout rl_root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ȥ��appͷ�ĵ�һ�ַ�ʽ
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
		//1,��intentά��ͼ��,����
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//ά��ͼ�꣨�̳��ϵķ����д�������ȷ�ģ�
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, 
				ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
		//����
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "��ȫ��ʿ��ݷ�ʽ");
		//2,�����ݷ�ʽ����ת����activity
		//2.1ά����������ͼ����
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		//3,���͹㲥
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
	 * ��Ϊ��ͬ���ֻ����̿��ܶ��ֻ�ϵͳ�������޸�ʹ��ԭ����
	 * ��content://com.android.launcher.settings/favorites?notify=true"
	 * ����
	 * "content://com.android.launcher2.settings/favorites?notify=true"
	 * ������׼ȷ�ж� ��Ҫͨ��Ȩ��ȥ��ȡ��ǰ�ֻ�provider.authority
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
		builder.setTitle("��������");
		builder.setMessage(mVersionDes);
		builder.setPositiveButton("��������", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadApk();
				
			}
		});
		builder.setNegativeButton("�Ժ���˵", new DialogInterface.OnClickListener() {
			
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
		//�ж�SD���Ƿ����
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mobilesafe.apk";
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {//���سɹ�
					Log.i(tag, "���سɹ�");
					File file = responseInfo.result;//��ȡ�����ļ�
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException exception, String s) {//����ʧ��
					Log.i(tag, "����ʧ��");
					Log.i(tag, exception+"");
					Log.i(tag, s);
				}
				
				@Override
				public void onStart() {//��ʼ����
					Log.i(tag, "��ʼ����");
					super.onStart();
				}
				
				@Override
				public void onLoading(long total, long current,boolean isUploading) {//���ع�����
					Log.i(tag, "total = " + total); //��������
					Log.i(tag, "current = " + current);//��ǰ������
					Log.i(tag, "isUploading = " + isUploading);//�Ƿ���������
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
	
	//����һ��activity�󣬷��ص��õķ���
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
     * ��ʼ�����ݵķ���
     */
    private void initData() {

        tv_version_name.setText("�汾���� " + getVersionName());
        mLocalVersion = getVersionCode();//��׃���O��ɆT׃������I Ctrl+shift+f
        
        if(SpUtil.getBoolean(this, ConstantView.OPEN_UPDATE, false)){
        	checkVersion();
        } else {
        	mhandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
        
    }

    private void checkVersion() {
        //��Ҫ�B�ӷ����������M�r�g������Ҫ�_һ�l����
        new Thread(){
          public void run(){
        	  Message msg = Message.obtain();
        	  long startTime = System.currentTimeMillis();
              try {
                  URL url = new URL("http://192.168.1.106/update.json");
                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                  connection.setConnectTimeout(2000);//�O���B�ӳ��r
                  connection.setReadTimeout(2000);//�O���xȡ���r
                  //connection.setRequestMethod("GET");�O���L����ʽ��һ��Ĭ�J��GET�����Կ��Բ���
                  if(connection.getResponseCode() == 200){ //����200�������xȡ�ɹ�
                      InputStream is = connection.getInputStream();//�@ȡ�xȡ��
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
     * ��ȡ�汾��
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
     * ��ȡ�汾����
     * @return Ӧ�ð汾���� ����Null�����쳣
     */
    private String getVersionName() {
        //��ȡ���������packageManager
        PackageManager pm = this.getPackageManager();
        try {
            //�Ӱ���������л�ȡ�ƶ������Ļ�����Ϣ���汾���ƣ��汾�ŵȻ�����Ϣ�� ��0�����ȡ������Ϣ
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //���ذ汾����
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  ��ʼ��UI����
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }
}
