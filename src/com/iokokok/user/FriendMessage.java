package com.iokokok.user;

public class FriendMessage {
	private int _id;
	private String usertype;
	private String username;
	private String usernick;
	private String userhead;
	private String usergroup;
	private String lastmsg;
	private String lastdata;

	public FriendMessage(int _id2, String username2, String usernick2,
			String userhead2, String usergroup2, String lastmsg2,
			String lastdata2,String usertype) {
		// TODO Auto-generated constructor stub
		this._id=_id2;
		this.username=username2;
		this.usernick=usernick2;
		this.userhead=userhead2;
		this.usergroup=usergroup2;
		this.lastmsg=lastmsg2;
		this.lastdata=lastdata2;
		this.usertype=usertype;
	}

	public FriendMessage() {
		// TODO Auto-generated constructor stub
	}

	
	public String getUserType(){
		return usertype;
	}
	
	public void setUserType(String usertype){
		this.usertype=usertype;
	}
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsernick() {
		return usernick;
	}

	public void setUsernick(String usernick) {
		this.usernick = usernick;
	}

	public String getUserhead() {
		return userhead;
	}

	public void setUserhead(String userhead) {
		this.userhead = userhead;
	}

	public String getUsergroup() {
		return usergroup;
	}

	public void setUsergroup(String usergroup) {
		this.usergroup = usergroup;
	}

	public String getLastmsg() {
		return lastmsg;
	}

	public void setLastmsg(String lastmsg) {
		this.lastmsg = lastmsg;
	}

	public String getLastdata() {
		return lastdata;
	}

	public void setLastdata(String lastdata) {
		this.lastdata = lastdata;
	}
}
