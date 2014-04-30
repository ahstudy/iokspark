package com.iok.spark;
import java.io.IOException;
import java.net.MalformedURLException;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.json.JSONException;
import org.json.JSONObject;

import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.util.NetHelper;
import com.iokokok.util.PhpHttpUtil;
import com.iokokok.util.XmppTool;
import com.weibo.sdk.android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Register extends Activity {
	
	private  EditText userTxt;
	private  EditText nickTxt;
	private EditText pwdTxt1;
	private EditText pwdTxt2;
	private final int ERROR_CONN = 4001;
	private final int ERROR_REGISTER = 4002;
	private final int ERROR_REGISTER_REPEATUSER = 4003;
	private final int SUCCESS = 201;
	private String username;
	private String usernick;
	private String pwd1;
	public ProgressDialog dialog ;
	private String uid,name,imageurl,token,type;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        userTxt=(EditText)findViewById(R.id.register_user_edit);
        nickTxt=(EditText)findViewById(R.id.register_user_nick);
        pwdTxt1=(EditText)findViewById(R.id.register_passwd_one);
        pwdTxt2=(EditText)findViewById(R.id.register_passwd_two);
        type=getIntent().getStringExtra("type");
        if (type.equals("sina")){
	        uid=getIntent().getStringExtra("uid");
	        name=getIntent().getStringExtra("name");
	        imageurl=getIntent().getStringExtra("profile_image_url");
	        token=getIntent().getStringExtra("token");
	        if (!name.equals("")||name!=null){
	        	nickTxt.setText(name);
	        }
        }
    }
	
	//ע�ᰴť
	public void registerNewUser(View v){
		username=userTxt.getText().toString();
		usernick=nickTxt.getText().toString();
		pwd1=pwdTxt1.getText().toString();
		String pwd2=pwdTxt2.getText().toString();
		if (username.equals("")||usernick.equals("")||pwd1.equals("")||pwd2.equals("")){
			Toast.makeText(this, "�����������д��", Toast.LENGTH_LONG).show();;
		}else if(!pwd1.equals(pwd2)){
			Toast.makeText(this, "��������д���û�����һ�£�", Toast.LENGTH_LONG).show();;
		}else{
			if (!type.equals("sina")||name!=null){
				//������Ȩ
				RequestParams params = new RequestParams();
        		params.put("uid", uid);
        		params.put("token", token);
        		params.put("name", name);
        		params.put("username", username);
        		params.put("userpass", pwd1);
        		params.put("imageurl", imageurl);
        		String url="http://spark.iokokok.com/ioksparkService/index.php/Sina/saveuser/";
        		AsyncHttpClient client = new AsyncHttpClient();
        		client.post(url, params, new AsyncHttpResponseHandler(){
        			@Override  
        			public void onSuccess(String response) {  
        			    System.out.println(response);
        			}  
        		});
	        }
			new Thread(){
				public void run() {
					mHandler.sendEmptyMessage(1);
					try {
					Registration registration = new Registration();
					registration.setType(IQ.Type.SET);
					registration.setTo(XmppTool.SERVER_NAME);
					registration.setUsername(username.trim());
					registration.setPassword(pwd1.trim());
					registration.addAttribute("name", usernick);
					//registration.addAttribute("email", email);
					PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()),new PacketTypeFilter(IQ.class));
					PacketCollector collector = XmppTool.getConnection().createPacketCollector(filter); 
					XmppTool.getConnection().sendPacket(registration); 
					Packet response = collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
					collector.cancel();
			        if (response == null) {
			            throw new XMPPException("No response from server on status set.");
			        }
			        if (response.getError() != null) {
			            throw new XMPPException(response.getError());
			        }
					} catch (XMPPException e) {
						if(e.getXMPPError() != null && e.getXMPPError().getCode() == 409){
							mHandler.sendEmptyMessage(ERROR_REGISTER_REPEATUSER);
						}else{
							
							mHandler.sendEmptyMessage(ERROR_REGISTER);
						}
						e.printStackTrace();
						return;
					}
					
					mHandler.sendEmptyMessage(SUCCESS);
					
				};
			}.start();
		}
	}
	//���ذ�ť
	public void register_back(View v){
		this.finish();
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dialog = new ProgressDialog(Register.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��ע���û�......"); 
				//����ProgressDialog ����ͼ�� 
				dialog.setIcon(R.drawable.icon); 
				//����ProgressDialog �Ľ������Ƿ���ȷ 
				dialog.setIndeterminate(false); 
				//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
				dialog.setCancelable(true); 
				//��ʾ 
				dialog.show(); 
				break;
			case 2:
				dialog.cancel();
				break;
			case ERROR_CONN:
				XmppTool.closeConnection();
				Toast.makeText(Register.this, "�����������ʧ�ܣ����Ժ����ԣ�", Toast.LENGTH_LONG).show();
				break;
				
			case ERROR_REGISTER:
				XmppTool.closeConnection();
				Toast.makeText(Register.this, "ע��ʧ�ܣ�������", Toast.LENGTH_LONG).show();
				break;
			case ERROR_REGISTER_REPEATUSER:
				XmppTool.closeConnection();
				Toast.makeText(Register.this, "�˺��Ѵ��ڣ�", Toast.LENGTH_LONG).show();
				break;
			case SUCCESS:
				Toast.makeText(Register.this, "ע��ɹ���", Toast.LENGTH_LONG).show();
				Thread t=new Thread(new Runnable() {				
					public void run() {
						try {
							XmppTool.getConnection().login(username, pwd1);
							NetHelper.writeStringPreferences(Register.this, "CURUSERID", username);
							XmppTool.changeNickName(XmppTool.getConnection(), usernick);
							//״̬
							Presence presence = new Presence(Presence.Type.available);
							XmppTool.getConnection().sendPacket(presence);
							
							Intent intent = new Intent();
							//��ת���¹��ܽ���
							//intent.setClass(Login.this, Whatsnew.class);
							
							//ֱ����ת��������
							intent.setClass(Register.this, LoadingActivity.class);
//							intent.setClass(FormLogin.this, FormClient.class);
							//intent.putExtra("USERID", USERID);
							Iokapplication iok=(Iokapplication) Register.this.getApplicationContext();
							iok.setUSERID(username);
							iok.setUSERNICK(usernick);
							Register.this.finish();
							Register.this.startActivity(intent);
							mHandler.sendEmptyMessage(2);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							XmppTool.closeConnection();
							mHandler.sendEmptyMessage(2);
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(2);
					}
				});
				t.start();
				SystemClock.sleep(1000);
				//finish();
				break;

			}
		};
	};
}
