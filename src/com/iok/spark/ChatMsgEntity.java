
package com.iok.spark;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

public class ChatMsgEntity {
    private static final String TAG = ChatMsgEntity.class.getSimpleName();
    private int _id;
    private String userid;
    private String usernick;
    private String date;
    private String from;
    private String msg;
    private String msgtype;
    private String messtype;
    private int head;
    private Bitmap userImg;
    private boolean isComMeg = true;
    private String msgstate;
    
    
    public String getMsgState(){
    	return msgstate;
    }
    
    public void setMsgState(String msgstate){
    	this.msgstate=msgstate;
    }
    public String getMessType(){
    	return messtype;
    }
    
    public void setMessType(String messtype){
    	this.messtype=messtype;
    }
    public String getType(){
    	return msgtype;
    }
    
    public void setType(String msgtype){
    	this.msgtype=msgtype;
    }
    public String getFrom(){
    	return from;
    }
    
    public void setFrom(String from){
    	this.from=from;
    }
    public String getName() {
        return userid;
    }

    public void setName(String name) {
        this.userid = name;
    }
    
    public String getUserNick(){
    	return usernick;
    }
    
    public void setUserNick(String usernick){
    	this.usernick=usernick;
    }
    public int getHead(){
    	return head;
    }
    
    public void setHead(int head){
    	this.head=head;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return msg;
    }

    public void setText(String text) {
        this.msg = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	this.isComMeg = isComMsg;
    }
    
    public int getid(){
    	return _id;
    }
    
    public void setid(int _id){
    	this._id=_id;
    }
    public ChatMsgEntity(int _id,String name, String date, String from,String text,String usernick,String msgtype,int head,Bitmap userImg, boolean isComMsg,String messtype,String msgstate) {
        super();
        this._id=_id;
        this.userid = name;
        this.date = date;
        this.from=from;
        this.msg = text;
        this.usernick=usernick;
        this.msgtype=msgtype;
        this.head=head;
        this.userImg=userImg;
        this.isComMeg = isComMsg;
        this.messtype=messtype;
        this.msgstate=msgstate;
    }

	public Bitmap getBitmap() {
		
		// TODO Auto-generated method stub
		return userImg;
	}
	
	public void setBitmap(Bitmap userImg){
		this.userImg=userImg;
	}

	@SuppressWarnings("null")
	public static List<ChatMsgEntity> sort(List<ChatMsgEntity> listMsg) {
		// TODO Auto-generated method stub
		int listcount=listMsg.size();
    	List<ChatMsgEntity> returnlist = new ArrayList<ChatMsgEntity>();
		for(int i=listcount-1;i>=0;i--){
			ChatMsgEntity getlist=listMsg.get(i);
			if (getlist!=null)
				Log.i("getlist:",getlist.getText());
				returnlist.add(getlist);
		}
		return returnlist;
	}

}
