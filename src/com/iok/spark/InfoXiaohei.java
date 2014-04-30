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

	//修改用户昵称
		public void editnick(View v){
			Intent intent = new Intent (InfoXiaohei.this,EditUserNick.class);
			startActivity(intent);	
		}
		//修改当天心情
		public void editsex(View v){
			Intent intent = new Intent (InfoXiaohei.this,EditSexActivity.class);
			String sex=usersex.getText().toString();
			intent.putExtra("sex", usersex.getText().toString());
			startActivity(intent);
		}
		

   public void btn_back(View v) {     //标题栏 返回按钮
      	this.finish();
      } 
   //修改用户头像
   public void editimg(View v){
	   Intent intent = new Intent();
		intent.setClass(InfoXiaohei.this,EditHeadSel.class);
		startActivity(intent);
   }
   public void btn_back_send(View v) {     //标题栏 返回按钮
     	this.finish();
     } 
   public void head_xiaohei(View v) {     //头像按钮
	   Intent intent = new Intent();
		intent.setClass(InfoXiaohei.this,InfoXiaoheiHead.class);
		startActivity(intent);
    } 
    
   
   private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(InfoXiaohei.this);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("我行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力为你加载个人信息......"); 
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
