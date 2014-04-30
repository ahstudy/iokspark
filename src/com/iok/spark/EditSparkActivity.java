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

public class EditSparkActivity extends Activity {
	public Iokapplication iok=null;
	public ProgressDialog dialog ;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editspark);
        //pUSERID = iok.getUSERID();//getIntent().getStringExtra("USERID");
        iok=(Iokapplication) this.getApplicationContext();
        //String pUSERNICK=iok.getUSERNICK();
        //EditText usertxt=(EditText)findViewById(R.id.userspark_edit);
        //usertxt.setText(pUSERNICK);
    }

	
	public void edit_spark(View v){
		EditText usertxt=(EditText)findViewById(R.id.userspark_edit);
		final String newSpark= usertxt.getText().toString();
		if(newSpark.equals("")){
			Toast.makeText(this, "请输入你要修改的心情", Toast.LENGTH_LONG).show();
		}else{
			Thread thread=new Thread(){
				public void run(){
					handler.sendEmptyMessage(1);
					try {
						XmppTool.changeStateMessage(XmppTool.getConnection(),newSpark);
						//iok.setUSERNICK(newSpark);
						handler.sendEmptyMessage(2);
					} catch (Exception e) {
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
					dialog = new ProgressDialog(EditSparkActivity.this);
					//设置进度条风格，风格为圆形，旋转的 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//设置ProgressDialog 标题 
					dialog.setTitle("我行聊"); 
					//设置ProgressDialog 提示信息 
					dialog.setMessage("正在努力修改用户心情......"); 
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
					//InfoXiaohei.dayspark.setText(iok.getUSERNICK());
					dialog.cancel();
					EditSparkActivity.this.finish();
				}
			}
	   };
}
