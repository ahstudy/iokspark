package com.iok.spark;

import java.io.ByteArrayInputStream;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.iokokok.app.Iokapplication;
import com.iokokok.util.XmppTool;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoXiaohei extends Activity {
	private String pUSERID="";
	private String pUSERNICK="";
	private String cursexstr;
	private Bitmap userhead=null;
	public static ImageView userimg=null;
	public ProgressDialog dialog ;
	public static TextView curnicktxt=null;
	public static TextView usersex=null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo); 
        Iokapplication iok=(Iokapplication) this.getApplicationContext();
        pUSERID = iok.getUSERID();//getIntent().getStringExtra("USERID");
        pUSERNICK=iok.getUSERNICK();
        TextView curusertxt=(TextView)findViewById(R.id.curusername);
        curnicktxt=(TextView)findViewById(R.id.curusernick);
        usersex=(TextView)findViewById(R.id.usersex);
        userimg=(ImageView)findViewById(R.id.myuserimg);
        curusertxt.setText(pUSERID);
        curnicktxt.setText(pUSERNICK);
        handler.sendEmptyMessage(1);
        Thread thread=new Thread(){
        	public void run(){
        		try {
        			//daysparkmess=XmppTool.getStateMessage();
        			VCard mycard=XmppTool.getUserVCard(XmppTool.getConnection(), pUSERID+"@"+XmppTool.SERVER_NAME);
        			if(mycard == null || mycard.getAvatar() == null)  
                    {  
                        userhead=null;//userimg.setImageResource(R.drawable.icon); 
                        if (mycard.getField("sex")!=null)
                        	cursexstr=mycard.getField("sex").toString();
                        handler.sendEmptyMessage(2);
                    }else{
                    	ByteArrayInputStream bais = new ByteArrayInputStream(  
                        		mycard.getAvatar());  
                        userhead =BitmapFactory.decodeStream(bais, null,null);
                        if (mycard.getField("sex")!=null)
                        	cursexstr=mycard.getField("sex").toString();
                        handler.sendEmptyMessage(2);
                       
                    }
                    
                    
        		} catch (XMPPException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			handler.sendEmptyMessage(2);
        		}
        	}
        };
        thread.start();
        
        
    }

	//�޸��û��ǳ�
		public void editnick(View v){
			Intent intent = new Intent (InfoXiaohei.this,EditUserNick.class);
			startActivity(intent);	
		}
		//�޸ĵ�������
		public void editsex(View v){
			Intent intent = new Intent (InfoXiaohei.this,EditSexActivity.class);
			String sex=usersex.getText().toString();
			intent.putExtra("sex", usersex.getText().toString());
			startActivity(intent);
		}
		

   public void btn_back(View v) {     //������ ���ذ�ť
      	this.finish();
      } 
   //�޸��û�ͷ��
   public void editimg(View v){
	   Intent intent = new Intent();
		intent.setClass(InfoXiaohei.this,EditHeadSel.class);
		startActivity(intent);
   }
   public void btn_back_send(View v) {     //������ ���ذ�ť
     	this.finish();
     } 
   public void head_xiaohei(View v) {     //ͷ��ť
	   Intent intent = new Intent();
		intent.setClass(InfoXiaohei.this,InfoXiaoheiHead.class);
		startActivity(intent);
    } 
    
   
   private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(InfoXiaohei.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ����ظ�����Ϣ......"); 
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
				if (userhead==null){
					userimg.setImageResource(R.drawable.usernohead);
				}else{
					userimg.setImageBitmap(userhead);
				}
				usersex.setText(cursexstr);
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
			}
		}
   };
}
