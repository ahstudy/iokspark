package com.iok.spark;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.iokokok.app.Constants;

import com.iokokok.util.NetHelper;
import com.iokokok.util.SharePreferenceUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;

public class Appstart extends Activity{
	private SharePreferenceUtil util;
	private Handler mHandler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.appstart);
		Intent service = new Intent(this, GetMsgService.class);
		startService(service);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		//util.setIsFirst(true);
		if (util.getisFirst()) {
			createShut();// 创建快捷方式
			//moveSound();
			new Handler().postDelayed(new Runnable(){
				public void run(){
					//开场动画
					//Intent intent = new Intent (Appstart.this,Viewpager.class);	
					//跳过开场动画
					Intent intent = new Intent (Appstart.this,Login.class);	
					startActivity(intent);			
					Appstart.this.finish();
				}
			}, 1000);
		}else {// 如果是首次运行
			new Handler().postDelayed(new Runnable(){
				public void run(){
					//开场动画
					Intent intent = new Intent (Appstart.this,Viewpager.class);	
					//跳过开场动画
					//Intent intent = new Intent (Appstart.this,Login.class);	
					startActivity(intent);			
					Appstart.this.finish();
				}
			}, 1000);
		}
   }
	
	/**
	 * 创建桌面快捷方式
	 */
	public void createShut() {
		// 创建添加快捷方式的Intent
		Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		String title = getResources().getString(R.string.app_name);
		// 加载快捷方式的图标
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				Appstart.this, R.drawable.icon);
		// 创建点击快捷方式后操作Intent,该处当点击创建的快捷方式后，再次启动该程序
		Intent myIntent = new Intent(Appstart.this,
				Appstart.class);
		// 设置快捷方式的标题
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 设置快捷方式的图标
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 设置快捷方式对应的Intent
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
		// 发送广播添加快捷方式
		sendBroadcast(addIntent);
		util.setIsFirst(false);
	}

	/**
	 * 复制原生资源文件“来消息声音”到应用目录下，
	 */
	public void moveSound() {
		InputStream is = getResources().openRawResource(R.raw.msg);
		File file = new File(getFilesDir(), "msg.mp3");
		try {
			OutputStream os = new FileOutputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			System.out.println("声音复制完毕"+getFilesDir()+"msg.mp3");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}