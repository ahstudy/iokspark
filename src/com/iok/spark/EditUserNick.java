package com.iok.spark;

import org.jivesoftware.smack.XMPPException;

import com.iokokok.app.Iokapplication;
import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditUserNick extends Activity {
	public Iokapplication iok=null;
	public ProgressDialog dialog ;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnick);
        //pUSERID = iok.getUSERID();//getIntent().getStringExtra("USERID");
        iok=(Iokapplication) this.getApplicationContext();
        String pUSERNICK=iok.getUSERNICK();
        EditText usertxt=(EditText)findViewById(R.id.usernick_edit);
        usertxt.setText(pUSERNICK);
    }

	
	public void edit_nick(View v){
		EditText usertxt=(EditText)findViewById(R.id.usernick_edit);
		final String newNick= usertxt.getText().toString();
		if(newNick.equals("")){
			Toast.makeText(this, "请输入你要修改的昵称", Toast.LENGTH_LONG).show();
		}else{
			Thread thread=new Thread(){
				public void run(){
					handler.sendEmptyMessage(1);
					try {
						XmppTool.changeNickName(XmppTool.getConnection(),newNick);
						iok.setUSERNICK(newNick);
						handler.sendEmptyMessage(2);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(2);
					}
				}
			};
			thread.start();
			
		}
	}
	
	
	 private Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg)
			{
				if (msg.what==1){
					dialog = new ProgressDialog(EditUserNick.this);
					//设置进度条风格，风格为圆形，旋转的 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//设置ProgressDialog 标题 
					dialog.setTitle("我行聊"); 
					//设置ProgressDialog 提示信息 
					dialog.setMessage("正在努力修改用户昵称......"); 
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
					InfoXiaohei.curnicktxt.setText(iok.getUSERNICK());
					dialog.cancel();
					EditUserNick.this.finish();
				}
			}
	   };
}
