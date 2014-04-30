package com.iokokok.util;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.iok.spark.Login;
import com.iok.spark.MainWeixin;
import com.iok.spark.R;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;

public class PhpHttpUtil {
	public static String SERVICE_HOST="http://221.123.160.26/ioksparkService/";
	private static String UPLOAD_URL=SERVICE_HOST+"index.php/Index/upload";
	private static ProgressDialog dialog;
	private static Context ctx;
	private static String resultRes;
	private boolean isDown;
	public PhpHttpUtil(Context ctx){
		this.ctx=ctx;
	}
	/*上传文件到服务器
	 *@param path   上传文件路径
	 *@param type   上传文件类型，支持pic,video,amr
	 *@param username 上传文件用户名
	 */
	public  void uploadFile(String path,String username,String type){
		handler.sendEmptyMessage(1);
		isDown=false;
		RequestParams params = new RequestParams();
		
		try {
			params.put("uploadtype", type);
			params.put("username", username);
			params.put("uploadfile", new File(path));
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(UPLOAD_URL, params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);
				    resultRes=response;
				    android.os.Message message = new android.os.Message();
				    message.what=2;
				    message.obj=response;
				    isDown=true;
				    handler.sendMessage(message);
				}  
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(3);
		} // Upload a File
	}
	
	//获取返回的结果
	public String  getResult(){
		return resultRes;
	}
	private static Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what==1)
			{
				
				dialog = new ProgressDialog(ctx);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("行聊提示"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("行聊正在努力加载中......"); 
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
				
			}
			else if (msg.what==3){
				dialog.cancel();
			}
		};
	};
	
}