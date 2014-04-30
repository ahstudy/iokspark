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
			createShut();// ������ݷ�ʽ
			//moveSound();
			new Handler().postDelayed(new Runnable(){
				public void run(){
					//��������
					//Intent intent = new Intent (Appstart.this,Viewpager.class);	
					//������������
					Intent intent = new Intent (Appstart.this,Login.class);	
					startActivity(intent);			
					Appstart.this.finish();
				}
			}, 1000);
		}else {// ������״�����
			new Handler().postDelayed(new Runnable(){
				public void run(){
					//��������
					Intent intent = new Intent (Appstart.this,Viewpager.class);	
					//������������
					//Intent intent = new Intent (Appstart.this,Login.class);	
					startActivity(intent);			
					Appstart.this.finish();
				}
			}, 1000);
		}
   }
	
	/**
	 * ���������ݷ�ʽ
	 */
	public void createShut() {
		// ������ӿ�ݷ�ʽ��Intent
		Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		String title = getResources().getString(R.string.app_name);
		// ���ؿ�ݷ�ʽ��ͼ��
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				Appstart.this, R.drawable.icon);
		// ���������ݷ�ʽ�����Intent,�ô�����������Ŀ�ݷ�ʽ���ٴ������ó���
		Intent myIntent = new Intent(Appstart.this,
				Appstart.class);
		// ���ÿ�ݷ�ʽ�ı���
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// ���ÿ�ݷ�ʽ��ͼ��
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// ���ÿ�ݷ�ʽ��Ӧ��Intent
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
		// ���͹㲥��ӿ�ݷ�ʽ
		sendBroadcast(addIntent);
		util.setIsFirst(false);
	}

	/**
	 * ����ԭ����Դ�ļ�������Ϣ��������Ӧ��Ŀ¼�£�
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
			System.out.println("�����������"+getFilesDir()+"msg.mp3");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}