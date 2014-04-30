package com.iokokok.user;

public class UserMessage {
	private String username;
	private String userpass;
	private String lastdata;
	private String usersex;
	private byte[] userhead;
	
	
	public UserMessage(String username,String userpass,byte[] userhead,String lastdata){
		this.username=username;
		this.userpass=userpass;
		this.setUserhead(userhead);
		this.lastdata=lastdata;
	}
	
	public void setLastData(String lastdata){
		this.lastdata=lastdata;
	}
	
	public String getLastData(){
		return lastdata;
	}
	public void setUserName(String username){
		this.username=username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUserPass(String userpass){
		this.userpass=userpass;
	}
	
	public String getUserPass(){
		return userpass;
	}

	public byte[] getUserhead() {
		return userhead;
	}

	public void setUserhead(byte[] userhead) {
		this.userhead = userhead;
	}

	public String getUsersex() {
		return usersex;
	}

	public void setUsersex(String usersex) {
		this.usersex = usersex;
	}

}
