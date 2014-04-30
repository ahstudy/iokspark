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
				  
				//����ϵͳ�Դ��Ĳ�����  
				 Intent intent = new Intent(Intent.ACTION_VIEW);  
				 intent.setDataAndType(videouri, "video/mp4");  
				 startActivity(intent);  
			}
        	
        });
	}
	
	
	public void upload_video(View v){
		//�����view���ϴ����ȵĵ���
		View upView = getLayoutInflater().inflate(R.layout.filebrowser_uploading, null);
		mPgBar = (ProgressBar)upView.findViewById(R.id.pb_filebrowser_uploading);
		mTvProgress = (TextView)upView.findViewById(R.id.tv_filebrowser_uploading);
		new AlertDialog.Builder(ShowVideoActivity.this).setTitle("�ϴ�����").setView(upView).create().show();
		//AsyncTask��ʵ��
		mTask = new MyUploadTask();
		mTask.execute(videopath, "http://221.123.160.26");
	}
	//�����ļ���PHP������
	
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
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ���ϴ���ͷ��......"); 
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
				Toast.makeText(ShowVideoActivity.this, "ͷ����³ɹ�", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				ShowVideoActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(ShowVideoActivity.this, "ͷ�����ʧ��", Toast.LENGTH_LONG).show();
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
			//���ս������ʾ
			mTvProgress.setText(result);	
		}

		@Override
		protected void onPreExecute() {
			//��ʼǰ��׼������
			mTvProgress.setText("loading...");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//��ʾ����
			mPgBar.setProgress(values[0]);
			mTvProgress.setText("loading..." + values[0] + "%");
		}

		@Override
		protected String doInBackground(String... params) {
			//����params[0]��params[1]��execute�������������
			String filePath = params[0];
			String uploadUrl = params[1];
			//���漴�ֻ����ϴ��ļ��Ĵ���
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

				//��ȡ�ļ��ܴ�С
				FileInputStream fis = new FileInputStream(filePath);
				long total = fis.available();
				byte[] buffer = new byte[8192]; // 8k
				int count = 0;
				int length = 0;
				while ((count = fis.read(buffer)) != -1) {
					dos.write(buffer, 0, count);
					//��ȡ���ȣ�����publishProgress()
					length += count;
					publishProgress((int) ((length / (float) total) * 100));
					//�����ǲ���ʱΪ����ʾ����,����500���룬����Ӧȥ��
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
				return "�ϴ��ɹ�";
		}catch (Exception e) {
			e.printStackTrace();
			return "�ϴ�ʧ��";
		}	
	}
   }
}
