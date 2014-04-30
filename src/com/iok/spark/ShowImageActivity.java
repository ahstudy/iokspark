package com.iok.spark;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.util.ZoomBitmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

public class ShowImageActivity extends Activity {
	private String picUrl="";
	private String smallpicUrl="";
	public ProgressDialog dialog ;
	static final String mimeType = "text/html";  
	static final String encoding = "utf-8";  
	private Uri picUri =null;
	private ImageViewTouch picView=null;
	private WebView newsweb=null;
	private Bitmap myBitmap=null;
	private Bitmap smallBitmap=null;
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showimage);
        if (myBitmap!=null)
        	destoryBimap();
		myBitmap = (Bitmap) getIntent().getParcelableExtra("bundle");
		if (myBitmap==null){
			picUrl = getIntent().getStringExtra("picurl");
			picUri=Uri.parse(picUrl);
			if (myBitmap!=null)
				destoryBimap();
			myBitmap= ZoomBitmap.getimage(picUrl);
			
		}
		System.out.println("file size:"+myBitmap.getByteCount());
		System.out.println("Image:width"+myBitmap.getWidth()+";height:"+myBitmap.getHeight());
		//myBitmap=ZoomBitmap.comp(myBitmap);
		myBitmap=ZoomBitmap.rotateBitmap(myBitmap,90);
		
        picView=(ImageViewTouch)findViewById(R.id.showimage);
        picView.setDisplayType( DisplayType.FIT_IF_BIGGER );
        picView.setImageBitmap(myBitmap);

    }

	public void upload_pic(View v){
		handler.sendEmptyMessage(1);
		smallBitmap=ZoomBitmap.PicZoom(myBitmap, 120, 120);
		try {
			smallpicUrl=ZoomBitmap.saveMyBitmap(smallBitmap,"small");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			picUrl=ZoomBitmap.saveMyBitmap(myBitmap,"big");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sendMulFilePhp(smallpicUrl,picUrl);
		
	}
	public void find_back(View v){
		Intent data=new Intent();  
        data.putExtra("picUrl", ""); 
        data.putExtra("smallpicUrl", "");
        //请求代码可以自己设置，这里设置成20  
        setResult(103, data);  
		ShowImageActivity.this.finish();
	}
	
	public void leftrotate(View v){
		myBitmap=ZoomBitmap.rotateBitmap(myBitmap,90);
		picView.setImageBitmap(myBitmap);
	}
	public void rightrotate(View v){
		myBitmap=ZoomBitmap.rotateBitmap(myBitmap,-90);
		picView.setImageBitmap(myBitmap);
	}
	//发送多文件到服务器
	private void sendMulFilePhp(final String smallPath,final String path){
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				String actionUrl = "http://221.123.160.26/upload.php";
				Map<String, String> params = new HashMap<String, String>();
				params.put("title", "strParamValue");
				Map<String, File> files = new HashMap<String, File>();
				String[] patharr=path.split("/");
				String bigname=patharr[patharr.length-1];
				patharr=smallPath.split("/");
				String smallname=patharr[patharr.length-1];
				files.put(bigname, new File(path));
				files.put(smallname, new File(smallPath));
				try {
						com.iokokok.util.HttpFileUpTool.post(actionUrl, params, files);
					} catch (IOException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(2);
						//Log.i(TAG, e.toString());
					}
				handler.sendEmptyMessage(2);
			}
			
		}).start();
		
//			Log.i(TAG, "code:"+code);
	}
	
	//发送文件到PHP服务器
	
		public void sendFilePhp(String path){
			RequestParams params = new RequestParams();

			final android.os.Message message = new android.os.Message();// handle
			try {
				params.put("uploadpath", "pic");
				params.put("uploadfile", new File(path));
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(20000);
				client.post("http://221.123.160.26", params, new AsyncHttpResponseHandler(){
					@Override  
					public void onSuccess(String response) {  
					    System.out.println(response);  
					    handler.sendEmptyMessage(2);
						
					}  
				});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} // Upload a File
			
		}
	 private Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg)
			{
				if (msg.what==1){
					dialog = new ProgressDialog(ShowImageActivity.this);
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
					Intent data=new Intent();  
		            data.putExtra("picUrl", picUrl); 
		            data.putExtra("smallpicUrl", smallpicUrl);
		            //请求代码可以自己设置，这里设置成20  
		            setResult(102, data);  
					ShowImageActivity.this.finish();
					
				}
			}
	   };
	   

	   
	   /**
		 * 销毁图片文件
		 */
		private void destoryBimap() {
			if (myBitmap != null && !myBitmap.isRecycled()) {
				myBitmap.recycle();
				myBitmap = null;
			}
		}
		
		@Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        	Intent data=new Intent();  
	            data.putExtra("picUrl", ""); 
	            data.putExtra("smallpicUrl", "");
	            //请求代码可以自己设置，这里设置成20  
	            setResult(103, data);  
				ShowImageActivity.this.finish();
	  
	        }  
	          
	        return false;  
	          
	    }  
}
