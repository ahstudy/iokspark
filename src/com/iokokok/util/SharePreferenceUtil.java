package com.iokokok.util;


import com.iokokok.app.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}



	public void setUserName(String username) {
		editor.putString("username", username);
		editor.commit();
	}

	public String getUserName() {
		return sp.getString("username", "");
	}
	
	public void setPasswd(String passwd) {
		editor.putString("userpass", passwd);
		editor.commit();
	}

	public String getPasswd() {
		return sp.getString("userpass", "");
	}

	// 用户的id，即QQ�?
	public void setId(String id) {
		editor.putString("id", id);
		editor.commit();
	}

	public String getId() {
		return sp.getString("id", "");
	}

	// 用户的昵�?
	public String getName() {
		return sp.getString("name", "");
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	// 用户的邮�?
	public String getEmail() {
		return sp.getString("email", "");
	}

	public void setEmail(String email) {
		editor.putString("email", email);
		editor.commit();
	}

	// 用户自己的头�?
	public Integer getImg() {
		return sp.getInt("img", 0);
	}

	public void setImg(int i) {
		editor.putInt("img", i);
		editor.commit();
	}

	// ip
	public void setIp(String ip) {
		editor.putString("ip", ip);
		editor.commit();
	}

	public String getIp() {
		return sp.getString("ip", Constants.SERVER_IP);
	}

	// 端口
	public void setPort(int port) {
		editor.putInt("port", port);
		editor.commit();
	}

	public int getPort() {
		return sp.getInt("port", Constants.SERVER_PORT);
	}

	// 是否在后台运行标�?
	public void setIsStart(boolean isStart) {
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsStart() {
		return sp.getBoolean("isStart", false);
	}

	// 是否第一次运行本应用
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}
}
