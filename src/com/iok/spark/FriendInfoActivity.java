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
	
	//��Ӻ���
	public void addfriend(View v){
		//Presence response = new Presence(Presence.Type.subscribe);
        //response.setTo(jid);
        //XmppTool.getConnection().sendPacket(response);
        boolean isadd=XmppTool.addUser(XmppTool.getConnection().getRoster(), jid, jid.split("@")[0]);
        if (isadd){
        	Toast.makeText(this, "������ӳɹ���", Toast.LENGTH_LONG).show();
        	this.finish();
        }else{
        	Toast.makeText(this, "�������ʧ�ܣ�", Toast.LENGTH_LONG).show();
        }
	}
	//ɾ������
	public void delfriend(View v){
		boolean isdel=XmppTool.removeUser(XmppTool.getConnection().getRoster(), jid);
		if (isdel){
        	Toast.makeText(this, "����ɾ���ɹ���", Toast.LENGTH_LONG).show();
        	this.finish();
        }else{
        	Toast.makeText(this, "����ɾ��ʧ�ܣ�", Toast.LENGTH_LONG).show();
        }
	}
	//������Ϣ
	public void sendmessage(View v){
		Intent intent = new Intent (this,ChatActivity.class);
		//intent.putExtra("user",  (String)listdata.get(arg2).get("user"));
		intent.putExtra("TOUSERID",username);
		intent.putExtra("TOUSERNICK", usernick);
		startActivity(intent);	
	}
	
	//�û�����
	public void btn_back(View v){
		finish();
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(FriendInfoActivity.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ���ȡ����......"); 
				//����ProgressDialog ����ͼ�� 
				dialog.setIcon(R.drawable.icon); 
				//����ProgressDialog �Ľ������Ƿ���ȷ 
				dialog.setIndeterminate(false); 
				//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
				dialog.setCancelable(true); 
				//��ʾ 
				dialog.show(); 
			}else if(msg.what==2){
				Log.i("test:","thread is end");
				Toast.makeText(FriendInfoActivity.this, "���³ɹ�", Toast.LENGTH_LONG).show();
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
				Toast.makeText(FriendInfoActivity.this, "ͷ�����ʧ��", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				//FriendInfoActivity.this.finish();
			}
		}
   };
}
