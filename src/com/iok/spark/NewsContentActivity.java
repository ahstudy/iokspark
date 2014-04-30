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
					//设置进度条风格，风格为圆形，旋转的 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//设置ProgressDialog 标题 
					dialog.setTitle("我行聊"); 
					//设置ProgressDialog 提示信息 
					dialog.setMessage("正在努力加载内容......"); 
					//设置ProgressDialog 标题图标 
					dialog.setIcon(R.drawable.icon); 
					//设置ProgressDialog 的进度条是否不明确 
					dialog.setIndeterminate(false); 
					//设置ProgressDialog 是否可以按退回按键取消 
					dialog.setCancelable(true); 
					//显示 
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
                    //开始
                super.onPageFinished(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {

                    //结束
                    super.onPageStarted(view, url, favicon);

            }
	    } 
}
