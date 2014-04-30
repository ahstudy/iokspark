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
			Toast.makeText(this, "��������Ҫ�޸ĵ�����", Toast.LENGTH_LONG).show();
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
					//���ý�������񣬷��ΪԲ�Σ���ת�� 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//����ProgressDialog ���� 
					dialog.setTitle("������"); 
					//����ProgressDialog ��ʾ��Ϣ 
					dialog.setMessage("����Ŭ���޸��û�����......"); 
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
					//InfoXiaohei.dayspark.setText(iok.getUSERNICK());
					dialog.cancel();
					EditSparkActivity.this.finish();
				}
			}
	   };
}
