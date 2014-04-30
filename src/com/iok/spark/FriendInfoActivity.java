package com.iok.spark;

import java.io.ByteArrayInputStream;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendInfoActivity extends Activity {
	private TextView usernametxt,usernicktxt,nocardtxt,usersextxt;
	private ProgressDialog dialog ;
	private Button sendmessagebtn,addfriendbtn,delfriendbtn;
	private VCard usercard;
	private LinearLayout cardlayout;
	private ImageView headImg;
	private Bitmap head;
	private String username,usernick,jid;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_friend);
        jid=this.getIntent().getStringExtra("jid");
        username=jid.split("@")[0];
        String usertype=this.getIntent().getStringExtra("usertype");
        sendmessagebtn=(Button)findViewById(R.id.btnsend);
        addfriendbtn=(Button)findViewById(R.id.btnaddfriend);
        delfriendbtn=(Button)findViewById(R.id.btndelfriend);
        if (usertype.equals("friend")){
        	sendmessagebtn.setVisibility(sendmessagebtn.VISIBLE);
        	addfriendbtn.setVisibility(addfriendbtn.GONE);
        	delfriendbtn.setVisibility(delfriendbtn.VISIBLE);
        }else if(usertype.equals("check")){
        	sendmessagebtn.setVisibility(sendmessagebtn.GONE);
        	addfriendbtn.setVisibility(addfriendbtn.VISIBLE);
        	delfriendbtn.setVisibility(delfriendbtn.GONE);
        }else if(usertype.equals("invest")){
        	sendmessagebtn.setVisibility(sendmessagebtn.GONE);
        	addfriendbtn.setVisibility(addfriendbtn.GONE);
        	delfriendbtn.setVisibility(delfriendbtn.GONE);
        }
        usernametxt=(TextView)findViewById(R.id.username);
        usernicktxt=(TextView)findViewById(R.id.usernick);
        nocardtxt=(TextView)findViewById(R.id.txtnocard);
        usersextxt=(TextView)findViewById(R.id.usersex);
        headImg=(ImageView)findViewById(R.id.userhead);
        cardlayout=(LinearLayout)findViewById(R.id.cardlayout);
        handler.sendEmptyMessage(1);
        new Thread(new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					usernick=XmppTool.getUserNick(XmppTool.getConnection(), jid);
					usercard=XmppTool.getUserVCard(XmppTool.getConnection(), jid);
					if(usercard!=null&&usercard.getAvatar()!=null){
						ByteArrayInputStream bais = new ByteArrayInputStream(  
			                    usercard.getAvatar());  
			            head =BitmapFactory.decodeStream(bais, null,null);
					}
					handler.sendEmptyMessage(2);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }).start();
	}
	
	//添加好友
	public void addfriend(View v){
		//Presence response = new Presence(Presence.Type.subscribe);
        //response.setTo(jid);
        //XmppTool.getConnection().sendPacket(response);
        boolean isadd=XmppTool.addUser(XmppTool.getConnection().getRoster(), jid, jid.split("@")[0]);
        if (isadd){
        	Toast.makeText(this, "好友添加成功！", Toast.LENGTH_LONG).show();
        	this.finish();
        }else{
        	Toast.makeText(this, "好友添加失败！", Toast.LENGTH_LONG).show();
        }
	}
	//删除好友
	public void delfriend(View v){
		boolean isdel=XmppTool.removeUser(XmppTool.getConnection().getRoster(), jid);
		if (isdel){
        	Toast.makeText(this, "好友删除成功！", Toast.LENGTH_LONG).show();
        	this.finish();
        }else{
        	Toast.makeText(this, "好友删除失败！", Toast.LENGTH_LONG).show();
        }
	}
	//发送信息
	public void sendmessage(View v){
		Intent intent = new Intent (this,ChatActivity.class);
		//intent.putExtra("user",  (String)listdata.get(arg2).get("user"));
		intent.putExtra("TOUSERID",username);
		intent.putExtra("TOUSERNICK", usernick);
		startActivity(intent);	
	}
	
	//用户返回
	public void btn_back(View v){
		finish();
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(FriendInfoActivity.this);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("我行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力为你获取资料......"); 
				//设置ProgressDialog 标题图标 
				dialog.setIcon(R.drawable.icon); 
				//设置ProgressDialog 的进度条是否不明确 
				dialog.setIndeterminate(false); 
				//设置ProgressDialog 是否可以按退回按键取消 
				dialog.setCancelable(true); 
				//显示 
				dialog.show(); 
			}else if(msg.what==2){
				Log.i("test:","thread is end");
				Toast.makeText(FriendInfoActivity.this, "更新成功", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				usernametxt.setText(username);
				usernicktxt.setText(usernick);
				if (head!=null){
					headImg.setImageBitmap(head);
				}
				if (usercard==null){
					nocardtxt.setVisibility(nocardtxt.VISIBLE);
					cardlayout.setVisibility(cardlayout.GONE);
				}else{
					nocardtxt.setVisibility(nocardtxt.GONE);
					cardlayout.setVisibility(cardlayout.VISIBLE);
					usersextxt.setText(usercard.getField("sex"));
				}
				dialog.cancel();
				//FriendInfoActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(FriendInfoActivity.this, "头像更新失败", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				//FriendInfoActivity.this.finish();
			}
		}
   };
}
