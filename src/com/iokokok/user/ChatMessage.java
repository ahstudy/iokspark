package com.iokokok.user;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.iok.spark.ChatMsgEntity;

public class ChatMessage {
	private int _id=0;
	private String name="";
	private String msg="";
	private String data="";
	private int isread=0;
	private int isdel=0;
	
	public ChatMessage(int _id,String name,String msg,String data,int isread,int isdel){
		this._id=_id;
		this.name=name;
		this.msg=msg;
		this.data=data;
		this.isread=isread;
		this.isdel=isdel;
	}
	
	public ChatMessage() {
		// TODO Auto-generated constructor stub
	}

	public int getid(){
		return _id;
	}
	
	public void setid(int _id){
		this._id=_id;
	}
	
	public String getname(){
		return name;
	}
	
	public void setname(String name){
		this.name=name;
	}
	
	public String getmsg(){
		return msg;
	}
	public void setmsg(String msg){
		this.msg=msg;
	}
	
	public int getisread(){
		return isread;
	}
	
	public void setisread(int isread){
		this.isread=isread;
	}
	
	public int getisdel(){
		return isdel;
	}
	
	public void setisdel(int isdel){
		this.isdel=isdel;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	@SuppressWarnings("null")
	public static List<ChatMessage> sort(List<ChatMessage> listMsg) {
		// TODO Auto-generated method stub
		int listcount=listMsg.size();
    	List<ChatMessage> returnlist = new ArrayList<ChatMessage>();
		for(int i=listcount-1;i>=0;i--){
			ChatMessage getlist=listMsg.get(i);
			if (getlist!=null)
				returnlist.add(getlist);
		}
		return returnlist;
	}
}
