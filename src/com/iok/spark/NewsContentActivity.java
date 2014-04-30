package com.iok.spark;

import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsContentActivity extends Activity {
	private String curUrl="";
	public ProgressDialog dialog ;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newscontent);
        curUrl = getIntent().getStringExtra("url");
        //newsTitle = getIntent().getStringExtra("title");
        //TextView newstitle=(TextView)findViewById(R.id.newstitle);
        //newstitle.setText(newsTitle);
        WebView newsweb=(WebView)findViewById(R.id.newsweb);
        newsweb.getSettings().setJavaScriptEnabled(true); 
        handler.sendEmptyMessage(1);
        newsweb.loadUrl(curUrl);
        newsweb.setWebViewClient(new HelloWebViewClient ()); 
        //usertxt.setText(pUSERNICK);
    }

	
	
	 private Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg)
			{
				if (msg.what==1){
					dialog = new ProgressDialog(NewsContentActivity.this);
					//���ý�������񣬷��ΪԲ�Σ���ת�� 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//����ProgressDialog ���� 
					dialog.setTitle("������"); 
					//����ProgressDialog ��ʾ��Ϣ 
					dialog.setMessage("����Ŭ����������......"); 
					//����ProgressDialog ����ͼ�� 
					dialog.setIcon(R.drawable.icon); 
					//����ProgressDialog �Ľ������Ƿ���ȷ 
					dialog.setIndeterminate(false); 
					//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
					dialog.setCancelable(true); 
					//��ʾ 
					dialog.show(); 
				}else if(msg.what==2){
					dialog.cancel();
					
				}
			}
	   };
	   
	   private class HelloWebViewClient extends WebViewClient { 
	        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	            view.loadUrl(url); 
	            return true; 
	        } 
	        @Override
            public void onPageFinished(WebView view, String url)
            {
	        	handler.sendEmptyMessage(2);
                    //��ʼ
                super.onPageFinished(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {

                    //����
                    super.onPageStarted(view, url, favicon);

            }
	    } 
}
