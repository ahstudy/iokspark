package com.iok.spark;

import org.jivesoftware.smack.XMPPException;

import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.util.PhpHttpUtil;
import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditPassActivity extends Activity {
	public Iokapplication iok=null;
	public ProgressDialog dialog ;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editpass);
        //pUSERID = iok.getUSERID();//getIntent().getStringExtra("USERID");
        iok=(Iokapplication) this.getApplicationContext();
        //String pUSERNICK=iok.getUSERNICK();
        //EditText usertxt=(EditText)findViewById(R.id.userspark_edit);
        //usertxt.setText(pUSERNICK);
    }

	
	public void edit_pass(View v){
		EditText usertxt=(EditText)findViewById(R.id.userpass_edit);
		EditText usertxt1=(EditText)findViewById(R.id.userpass_edit1);
		final String newpass= usertxt.getText().toString();
		final String newpass1= usertxt1.getText().toString();
		if(newpass.equals("")){
			Toast.makeText(this, "��������Ҫ�޸ĵ�����", Toast.LENGTH_LONG).show();
		}else if (!newpass.equals(newpass1)){
			Toast.makeText(this, "����������������һ��", Toast.LENGTH_LONG).show();
		}else{
			RequestParams params=new RequestParams();
			params.put("username", iok.getUSERID());
			params.put("newpass",newpass);
			String url=PhpHttpUtil.SERVICE_HOST+"index.php/User/editpass/";
    		AsyncHttpClient client = new AsyncHttpClient();
    		client.post(url, params, new AsyncHttpResponseHandler(){
    			@Override  
    			public void onSuccess(String response) {
    				
    			}
    		});
			Thread thread=new Thread(){
				public void run(){
					handler.sendEmptyMessage(1);
					try {
						XmppTool.changePassword(XmppTool.getConnection(),newpass);
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
					dialog = new ProgressDialog(EditPassActivity.this);
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
					EditPassActivity.this.finish();
			    	XmppTool.closeConnection();
			    	//MessageDB.close();
			    	//System.exit(0);
			    	MainWeixin.instance.finish();//�ر�Main ���Activity
				}
			}
	   };
}
