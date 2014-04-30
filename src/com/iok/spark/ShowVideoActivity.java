package com.iok.spark;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.user.ChatMessage;
import com.iokokok.util.Msg;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowVideoActivity extends Activity {
	Uri videouri;
	public ProgressDialog dialog ;
	private String videopath;
	private ProgressBar mPgBar;
	private TextView mTvProgress;
	private MyUploadTask mTask;
	@Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        setContentView(R.layout.showvideo);
        String showpath=getIntent().getStringExtra("showpath");
        videopath=getIntent().getStringExtra("videopath");
        Uri uri=Uri.parse(showpath);
        videouri=Uri.parse(videopath);
        ImageViewTouch showImage=(ImageViewTouch)findViewById(R.id.ShowVideo);
        showImage.setImageURI(uri);
        showImage.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				  
				//调用系统自带的播放器  
				 Intent intent = new Intent(Intent.ACTION_VIEW);  
				 intent.setDataAndType(videouri, "video/mp4");  
				 startActivity(intent);  
			}
        	
        });
	}
	
	
	public void upload_video(View v){
		//这里的view是上传进度的弹框
		View upView = getLayoutInflater().inflate(R.layout.filebrowser_uploading, null);
		mPgBar = (ProgressBar)upView.findViewById(R.id.pb_filebrowser_uploading);
		mTvProgress = (TextView)upView.findViewById(R.id.tv_filebrowser_uploading);
		new AlertDialog.Builder(ShowVideoActivity.this).setTitle("上传进度").setView(upView).create().show();
		//AsyncTask的实例
		mTask = new MyUploadTask();
		mTask.execute(videopath, "http://221.123.160.26");
	}
	//发送文件到PHP服务器
	
		public void sendFilePhp(String path){
			RequestParams params = new RequestParams();
			handler.sendEmptyMessage(1);
			try {
				params.put("uploadpath", "video");
				params.put("uploadfile", new File(path));
				AsyncHttpClient client = new AsyncHttpClient();
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
				
				handler.sendEmptyMessage(2);
			} // Upload a File
			
		}
		
	public void play(View v){
		Intent intent = new Intent(Intent.ACTION_VIEW);  
		 intent.setDataAndType(videouri, "video/3gp");  
		 startActivity(intent); 
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(ShowVideoActivity.this);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("我行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力为你上传新头像......"); 
				//设置ProgressDialog 标题图标 
				dialog.setIcon(R.drawable.icon); 
				//设置ProgressDialog 的进度条是否不明确 
				dialog.setIndeterminate(false); 
				//设置ProgressDialog 是否可以按退回按键取消 
				dialog.setCancelable(true); 
				//显示 
				dialog.show(); 
			}else if(msg.what==2){
				Log.i("test:","thread is end");
				Toast.makeText(ShowVideoActivity.this, "头像更新成功", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				ShowVideoActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(ShowVideoActivity.this, "头像更新失败", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				ShowVideoActivity.this.finish();
			}
		}
   };
   class MyUploadTask extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPostExecute(String result) {
			//最终结果的显示
			mTvProgress.setText(result);	
		}

		@Override
		protected void onPreExecute() {
			//开始前的准备工作
			mTvProgress.setText("loading...");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//显示进度
			mPgBar.setProgress(values[0]);
			mTvProgress.setText("loading..." + values[0] + "%");
		}

		@Override
		protected String doInBackground(String... params) {
			//这里params[0]和params[1]是execute传入的两个参数
			String filePath = params[0];
			String uploadUrl = params[1];
			//下面即手机端上传文件的代码
			String end = "\r\n";
			String twoHyphens = "--";
			String boundary = "******";
			try {
				URL url = new URL(uploadUrl);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setConnectTimeout(6*1000);
				httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
				httpURLConnection.setRequestProperty("Charset", "UTF-8");
				httpURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				DataOutputStream dos = new DataOutputStream(httpURLConnection
						.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + end);
				dos
						.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
								+ filePath.substring(filePath.lastIndexOf("/") + 1)
								+ "\"" + end);
				dos.writeBytes(end);

				//获取文件总大小
				FileInputStream fis = new FileInputStream(filePath);
				long total = fis.available();
				byte[] buffer = new byte[8192]; // 8k
				int count = 0;
				int length = 0;
				while ((count = fis.read(buffer)) != -1) {
					dos.write(buffer, 0, count);
					//获取进度，调用publishProgress()
					length += count;
					publishProgress((int) ((length / (float) total) * 100));
					//这里是测试时为了演示进度,休眠500毫秒，正常应去掉
					Thread.sleep(500);
				}		
				fis.close();
				dos.writeBytes(end);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
				dos.flush();

				InputStream is = httpURLConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				@SuppressWarnings("unused")
				String result = br.readLine();
				dos.close();
				is.close();
				return "上传成功";
		}catch (Exception e) {
			e.printStackTrace();
			return "上传失败";
		}	
	}
   }
}
