package com.iokokok.user;

import android.graphics.Bitmap;

public class SearchMessage{
	private String username;
	private String usernick;
	private String jid;
	private Bitmap head;
	public SearchMessage(String username,String usernick,String jid,Bitmap head){
		this.setUsername(username);
		this.setUsernick(usernick);
		this.setJid(jid);
		this.setHead(head);
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
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public Bitmap getHead() {
		return head;
	}
	public void setHead(Bitmap head) {
		this.head = head;
	}
	
	
}
