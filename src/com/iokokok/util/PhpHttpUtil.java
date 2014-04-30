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
	/*�ϴ��ļ���������
	 *@param path   �ϴ��ļ�·��
	 *@param type   �ϴ��ļ����ͣ�֧��pic,video,amr
	 *@param username �ϴ��ļ��û���
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
	
	//��ȡ���صĽ��
	public String  getResult(){
		return resultRes;
	}
	private static Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what==1)
			{
				
				dialog = new ProgressDialog(ctx);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������ʾ"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("��������Ŭ��������......"); 
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
				
			}
			else if (msg.what==3){
				dialog.cancel();
			}
		};
	};
	
}