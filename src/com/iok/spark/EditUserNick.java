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
			Toast.makeText(this, "��������Ҫ�޸ĵ��ǳ�", Toast.LENGTH_LONG).show();
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
					//���ý�������񣬷��ΪԲ�Σ���ת�� 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//����ProgressDialog ���� 
					dialog.setTitle("������"); 
					//����ProgressDialog ��ʾ��Ϣ 
					dialog.setMessage("����Ŭ���޸��û��ǳ�......"); 
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
					InfoXiaohei.curnicktxt.setText(iok.getUSERNICK());
					dialog.cancel();
					EditUserNick.this.finish();
				}
			}
	   };
}
