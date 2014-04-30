package com.iokokok.util;

import java.util.ArrayList;
import java.util.List;









import com.iok.spark.ChatMsgEntity;
import com.iokokok.user.ChatMessage;
import com.iokokok.user.FriendMessage;
import com.iokokok.user.UserMessage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

public class MessageDB {
	private static SQLiteDatabase db=null;
	private String DB_NAME="iokokok";
	private String PRE="iok_";

	public MessageDB(Context context) {
		if (db==null)
			db = context.openOrCreateDatabase(DB_NAME,Context.MODE_PRIVATE, null);
	}

	
	public boolean istable(String tablename){
		Cursor c=db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + PRE + tablename +"'",null);
		try {
			if(c.moveToNext()){
	            int count = c.getInt(0);
	            if(count>0){
	                return true;
	            }
			}
		 } catch (Exception e) {
             // TODO: handle exception
			 return false;
		 }               
		return false;
	}
	
	public void createUserList(){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + PRE+ "userlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, userpass VARCHAR,userhead BLOB,lastdata VARCHAR)");
	}
	public void insertUserList(UserMessage user){
		db.execSQL("INSERT INTO " + PRE+ "userlist VALUES (NULL, ?, ?,?,?)",new Object[]{user.getUsername(),user.getUserPass(),user.getUserhead(),user.getLastData()});  
	}
	public UserMessage getUserMess(String username){
		UserMessage user=null;
		Cursor c=db.rawQuery("SELECT userhead FROM "+ PRE+"userlist WHERE username='"+username+"'",null);
		System.out.println( "SELECT userhead FROM "+ PRE+"userlist WHERE username='"+username+"'");
		try {
			if(c.moveToNext()){
				user.setLastData(c.getString(c.getColumnIndex("lastdata")));
				user.setUserName(c.getString(c.getColumnIndex("username")));
				user.setUserPass(c.getString(c.getColumnIndex("userpass")));
				user.setUserhead(c.getBlob(c.getColumnIndex("userhead")));
			}
		} catch (Exception e) {
            // TODO: handle exception
			c.close();
			 return user;
		 }         
		c.close();
		return user;
	}
	public void createUserTable(String tablename){
        //创建用户聊天记录表
       db.execSQL("CREATE TABLE IF NOT EXISTS "+ PRE+tablename+"_messagelist (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, date VARCHAR, msg text,isread integer,isdel integer)");
       //创建用户设置表
       db.execSQL("CREATE TABLE IF NOT EXISTS "+ PRE+tablename+"_setting (_id INTEGER PRIMARY KEY AUTOINCREMENT, key VARCHAR, value text)");  
       //创建用户好友列表
       db.execSQL("CREATE TABLE IF NOT EXISTS "+ PRE+tablename+"_friendlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, usernick VARCHAR, userhead text,usergroup VARCHAR,lastmsg text,lastdata text)");
       
	}
	
	public void saveUserSet(String tablename,String key,String value){
		//创建用户设置表
	    db.execSQL("CREATE TABLE IF NOT EXISTS "+ PRE+tablename+"_setting (_id INTEGER PRIMARY KEY AUTOINCREMENT, key VARCHAR, value text)");  
	    db.execSQL("INSERT INTO "+ PRE+tablename+"_setting VALUES (NULL, ?, ?)",new Object[]{key,value}); 
	}
	//根据键值获取用户的设置值
	public String getUserSet(String tablename,String key){
		String value="";
		Cursor c=db.rawQuery("SELECT value FROM "+ PRE+tablename+"_setting WHERE key='"+key+"'",null);
		try {
			if(c.moveToNext()){
				value=c.getString(c.getColumnIndex("value"));
			}
		} catch (Exception e) {
            // TODO: handle exception
			c.close();
			 return value;
		 }         
		c.close();
		return value;
	}
	public void saveUserFriend(String tablename,FriendMessage myfriend){
		 //创建用户好友列表
	     db.execSQL("CREATE TABLE IF NOT EXISTS "+ PRE+tablename+"_friendlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, usernick VARCHAR, userhead text,usergroup VARCHAR,lastmsg text,lastdata text)");
	     db.execSQL("INSERT INTO "+ PRE+tablename+"_friendlist VALUES (NULL, ?, ?,?,?, ?,?)",
	    		 new Object[]{myfriend.getUsername(),myfriend.getUsernick(),myfriend.getUserhead(),myfriend.getUsergroup(),myfriend.getLastmsg(),myfriend.getLastdata()}); 
	}
	public String getFriendNick(String tablename,String friendname){
		String nickstr="";
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_friendlist where username='"+friendname+"'", null);
		if (c.getCount()>0){
			try {
				while (c.moveToNext()) {
					nickstr=c.getString(c.getColumnIndex("usernick"));
				}
			} catch (Exception e) {
	            // TODO: handle exception
				 return nickstr;
			 }               
		}
		return nickstr;
	}
	
	public String getFriendHead(String tablename,String friendname){
		String headstr="";
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_friendlist where username='"+friendname+"'", null);
		if (c.getCount()>0){
			try {
				while (c.moveToNext()) {
					headstr=c.getString(c.getColumnIndex("userhead"));
				}
			} catch (Exception e) {
	            // TODO: handle exception
				 return headstr;
			 }               
		}
		return headstr;
	}
	public List<FriendMessage> getUserFriendList(String tablename){
		List<FriendMessage> mylist= new ArrayList<FriendMessage>();;
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_friendlist", null);
		if (c.getCount()>0){
			try {
				while (c.moveToNext()) {
					int _id=c.getInt(c.getColumnIndex("_id"));
					String username = c.getString(c.getColumnIndex("username"));
					String usernick=c.getString(c.getColumnIndex("usernick"));
					String userhead=c.getString(c.getColumnIndex("userhead"));
					String usergroup = c.getString(c.getColumnIndex("usergroup"));
					String lastmsg=c.getString(c.getColumnIndex("lastmsg"));
					String lastdata=c.getString(c.getColumnIndex("lastdata"));
					FriendMessage entity = new FriendMessage(_id,username,usernick,userhead,usergroup,lastmsg,lastdata,"friend");
					//ChatMsgEntity entity = new ChatMsgEntity(name, date, message, img,isComMsg);
					mylist.add(entity);
				}
			} catch (Exception e) {
	            // TODO: handle exception
				 return mylist;
			 }               
			
		}
		c.close();
		return mylist;
	}
	public void saveUserMsg(String tablename, ChatMessage entity) {
		 db.execSQL("CREATE TABLE IF NOT EXISTS "+PRE+tablename
				 +"_messagelist (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, date VARCHAR, msg text,isread integer,isdel integer)");
		boolean issave=true;
		 Cursor c=db.rawQuery("SELECT count(*) FROM "+PRE+tablename
				 +"_messagelist WHERE name='" +entity.getname()+"' AND date='" + entity.getData() + "' AND" +
				 		" msg='"+entity.getmsg()+"'",null);
			try {
				if(c.moveToNext()){
		            int count = c.getInt(0);
		            if(count>0){
		                issave=false;
		            }
				}
			 } catch (Exception e) {
	             // TODO: handle exception
				 issave=true;
			 }               
		if (issave){
			db.execSQL(
	 				"insert into "+PRE+tablename
					 +"_messagelist (_id,name,date,msg,isread,isdel) values(null,?,?,?,?,?)",
	 				new Object[] {entity.getname(), entity.getData(),entity.getmsg(),entity.getisread(), entity.getisdel()});

		}
		 		
	}
	
	public List<ChatMessage> getUserMsgList(String tablename,String username,int pagenum,int count){
		List<ChatMessage> mylist=new ArrayList<ChatMessage>();
		int begin=(pagenum-1) *count;
		int total=pagenum *count;
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_messagelist WHERE name ='"+username+"'   order by _id desc limit "+total+"", null);
		if (c.getCount()>0){
			try {
				while (c.moveToNext()) {
					int _id=c.getInt(c.getColumnIndex("_id"));
					String name = c.getString(c.getColumnIndex("name"));
					String date=c.getString(c.getColumnIndex("date"));
					String msg=c.getString(c.getColumnIndex("msg"));
					int isread = 1;
					int isdel=c.getInt(c.getColumnIndex("isdel"));
					ChatMessage entity = new ChatMessage(_id,name,msg,date,isread,isdel);
			    	this.setOffistrue(tablename, _id);
					mylist.add(entity);
				}
			} catch (Exception e) {
	            // TODO: handle exception
				 return mylist;
			 }               
			
		}
		c.close();
		return mylist;
	}
	//获取离线信息数目
	public int getUserOffCount(String tablename,String user){
		int result=0;
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_messagelist Where isread=0 and name='"+user+"'", null);
		result=c.getCount();
		c.close();
		return result;
		
	}
	
	public void setOffistrue(String tablename,int _id){
		db.execSQL("update "+ PRE+tablename+"_messagelist set isread=1 where _id="+_id);
	}
	//获取最后一条离线信息
	public ChatMessage getLastMsg(String tablename,String user){
		ChatMessage entity =null;
		Cursor c = db.rawQuery("SELECT * from "+ PRE+tablename+"_messagelist Where name='"+user+"'  ORDER BY _id desc Limit 1 ", null);
		if (c.getCount()>0){
			try {
				while (c.moveToNext()) {
					int _id=c.getInt(c.getColumnIndex("_id"));
					String name = c.getString(c.getColumnIndex("name"));
					String date=c.getString(c.getColumnIndex("date"));
					String msg=c.getString(c.getColumnIndex("msg"));
					int isread = c.getInt(c.getColumnIndex("isread"));
					int isdel=c.getInt(c.getColumnIndex("isdel"));
					entity = new ChatMessage(_id,name,msg,date,isread,isdel);
				}
			} catch (Exception e) {
	            // TODO: handle exception
				 return entity;
			 }               
			
		}
		c.close();
		return entity;
	}

	public static void close() {
		if (db != null)
			db.close();
	}
}
