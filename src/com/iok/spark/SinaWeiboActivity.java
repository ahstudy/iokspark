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
				mycount.setText("文字："+mytxt.getText().toString().length()+"/140");
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
            String url = Constants.SINA_BASE_URL+"statuses/update.json";//发文本
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
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("行聊微博"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("行聊正在发送微博......"); 
				//设置ProgressDialog 标题图标 
				dialog.setIcon(R.drawable.icon); 
				//设置ProgressDialog 的进度条是否不明确 
				dialog.setIndeterminate(false); 
				//设置ProgressDialog 是否可以按退回按键取消 
				dialog.setCancelable(true); 
				//显示 
				dialog.show(); 
			}
			else if(msg.what==2)
			{
				
				dialog.cancel();
				mytxt.setText("");
				Toast.makeText(SinaWeiboActivity.this, "发送成功！",Toast.LENGTH_SHORT).show();
				//XmppTool.changeStateMessage(XmppTool.getConnection(), "换个心情");
								//String state=XmppTool.getStateMessage();
				//Intent intent = new Intent();
				//跳转到新功能介绍
				//intent.setClass(Login.this, Whatsnew.class);
				//messageDB.close();
				//直接跳转到主界面
				//intent.setClass(SinaWeiboActivity.this, MainWeixin.class);
				//SinaWeiboActivity.this.startActivity(intent);
				
			}
			else if (msg.what==3){
				dialog.cancel();
				Toast.makeText(SinaWeiboActivity.this, "发送失败！",Toast.LENGTH_SHORT).show();
			}
		};
	};
}
