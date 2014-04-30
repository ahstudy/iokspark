package com.iok.spark;

import com.iokokok.app.Constants;
import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SinaWeiboActivity extends Activity {
	public ProgressDialog dialog ;
	private Iokapplication myapplication;
	private EditText mytxt;
	private TextView mycount;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinaweibo);
        myapplication=(Iokapplication)this.getApplicationContext();
        mytxt=(EditText)findViewById(R.id.sina_txt);
        mycount=(TextView)findViewById(R.id.count_txt);
        mytxt.addTextChangedListener(new TextWatcher(){

			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				System.out.println("1:"+mytxt.getText().toString().length());
				mycount.setText("���֣�"+mytxt.getText().toString().length()+"/140");
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				System.out.println("2:"+mytxt.getText().toString().length());
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				System.out.println("3:"+mytxt.getText().toString().length());
			}
        	
        });
    }
	
	
	public void send_weibo(View v){
		String token = myapplication.getSinaToken();
		System.out.println(token);
		handler.sendEmptyMessage(1);
        try {
            String url = Constants.SINA_BASE_URL+"statuses/update.json";//���ı�
            Log.i("Weibo", "Weibo.showUser().url " + url);
            RequestParams params = new RequestParams();
			params.put("access_token", token);
			params.put("status", mytxt.getText().toString());
			//params.put("url", "http://img2.pp.cc/attachment/weibo/content/201309/27/623168_20130927162034.jpg");
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);
				    handler.sendEmptyMessage(2);
				} 
			});
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(3);
        }
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what==1)
			{
				
				dialog = new ProgressDialog(SinaWeiboActivity.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("����΢��"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("�������ڷ���΢��......"); 
				//����ProgressDialog ����ͼ�� 
				dialog.setIcon(R.drawable.icon); 
				//����ProgressDialog �Ľ������Ƿ���ȷ 
				dialog.setIndeterminate(false); 
				//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
				dialog.setCancelable(true); 
				//��ʾ 
				dialog.show(); 
			}
			else if(msg.what==2)
			{
				
				dialog.cancel();
				mytxt.setText("");
				Toast.makeText(SinaWeiboActivity.this, "���ͳɹ���",Toast.LENGTH_SHORT).show();
				//XmppTool.changeStateMessage(XmppTool.getConnection(), "��������");
								//String state=XmppTool.getStateMessage();
				//Intent intent = new Intent();
				//��ת���¹��ܽ���
				//intent.setClass(Login.this, Whatsnew.class);
				//messageDB.close();
				//ֱ����ת��������
				//intent.setClass(SinaWeiboActivity.this, MainWeixin.class);
				//SinaWeiboActivity.this.startActivity(intent);
				
			}
			else if (msg.what==3){
				dialog.cancel();
				Toast.makeText(SinaWeiboActivity.this, "����ʧ�ܣ�",Toast.LENGTH_SHORT).show();
			}
		};
	};
}
