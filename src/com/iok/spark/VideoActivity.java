package com.iok.spark;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoActivity extends Activity implements SurfaceHolder.Callback
{
private static final String TAG = "TAG-AndroidCameraActivity";
	
    public static final int MEDIA_TYPE_IMAGE = 1; 
    public static final int MEDIA_TYPE_VIDEO = 2; 
	
    private Camera mCamera; 
    private CameraPreview mPreview; 
    
    
    private ImageView captureButton;
    private ImageView captureplayButton;
    private MediaRecorder mMediaRecorder; 
    
    private boolean isRecording = false; 
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private boolean bool;
    private TextView tv01,tv02,tv03,tv04,tv05;
    public ProgressDialog dialog ;
    private File file;
    private Iokapplication myapplication;
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        tv01 = (TextView) findViewById(R.id.mediarecorder2_TextView01);
        tv02 = (TextView) findViewById(R.id.mediarecorder2_TextView02);
        tv03 = (TextView) findViewById(R.id.mediarecorder2_TextView03);
        tv04 = (TextView) findViewById(R.id.mediarecorder2_TextView04);
        tv05 = (TextView) findViewById(R.id.mediarecorder2_TextView05); 
        myapplication=(Iokapplication)this.getApplicationContext();
        // ����Cameraʵ�� 
        mCamera = getCameraInstance(); 
        mCamera.setDisplayOrientation(90);
        // ����Preview view��������Ϊactivity�е�����
        mPreview = new CameraPreview(this, mCamera); 
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview); 
        preview.addView(mPreview); 
        // ΪCapture��ť����listener,������Ƶ
	 	captureButton = (ImageView) findViewById(R.id.video_normal); 
	 	captureButton.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View v) {
 				// ��ʼ����Ƶcamera
 				if (prepareVideoRecorder()) {
					mMediaRecorder.start();
					tv01.setVisibility(View.VISIBLE);
	                tv02.setVisibility(View.VISIBLE);
	                tv03.setVisibility(View.VISIBLE);
	                tv04.setVisibility(View.VISIBLE);
	                tv05.setVisibility(View.VISIBLE);
	                bool=true;
	                handler.postDelayed(task, 1000);
						// ֪ͨ�û�¼���ѿ�ʼ 
						//captureButton.setText("ֹͣ¼��");
	                captureButton.setVisibility(captureButton.GONE);
	                captureplayButton.setVisibility(captureplayButton.VISIBLE);
	                
	                isRecording = true;
				} else {
						// ׼��δ����ɣ��ͷ�camera
					releaseMediaRecorder();
				}
 			}
 		});
	 // ΪCapture��ť����listener,������Ƶ
	 	captureplayButton = (ImageView) findViewById(R.id.video_play); 
	 	captureplayButton.setOnClickListener(new View.OnClickListener() {

	  			public void onClick(View v) {
	  		
	  				stopVideo();
	  			}
	  		});
	}
	
    @Override 
    protected void onPause() { 
        super.onPause(); 
        // �������ʹ��MediaRecorder��������Ҫ�ͷ�����
        releaseMediaRecorder(); 
        // ����ͣ�¼��������ͷ�����ͷ
        releaseCamera();
    } 
	public void stopVideo(){
		if (isRecording) {
			// ֹͣ¼���ͷ�camera
			mMediaRecorder.stop(); 
			releaseMediaRecorder(); 
			mCamera.lock(); 
			tv01.setVisibility(View.GONE);
            tv02.setVisibility(View.GONE);
            tv03.setVisibility(View.GONE);
            tv04.setVisibility(View.GONE);
            tv05.setVisibility(View.GONE);
            bool=false;
			//captureButton.setText("��ʼ¼��");
            captureButton.setVisibility(captureButton.VISIBLE);
            captureplayButton.setVisibility(captureplayButton.GONE);
			isRecording = false;
			System.out.println(file.length());
			if (file.length()>10485760){
				Toast.makeText(this, "¼���ļ�����10M��̫���ˣ�С����ʾѹ���ܴ󣡣�", Toast.LENGTH_LONG).show();
			}else{
				sendFilePhp(file.getPath().toString());
			}
				
		}
	}
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			// ���recorder����
			mMediaRecorder.reset(); 
			// �ͷ�recorder����
			mMediaRecorder.release(); 

			mMediaRecorder = null;
			// Ϊ����ʹ����������ͷ
			mCamera.lock(); 
		}
	}

	private void releaseCamera() {
		if (mCamera!= null) {
			// Ϊ����Ӧ���ͷ�����ͷ
			mCamera.release(); 
			mCamera = null;
		}
	}

	
	//���ؼ��¼�
		public void find_back(View v){
			this.finish();
		}
	private boolean prepareVideoRecorder() {

		mMediaRecorder = new MediaRecorder();

		// ��1��:������������ͷָ��MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// ��2��:ָ��Դ
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setOrientationHint(90);
		//int version = android.provider.Settings.System.getInt(this
		//	     .getContentResolver(),
		//	     android.provider.Settings.System.SYS_PROP_SETTING_VERSION,
		//	     3);
		//System.out.println("API level:"+version);
		//if(version>8){
			// ��3��:ָ��CamcorderProfile(��ҪAPI Level 8���ϰ汾)
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
		//}else{
		//	mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
	        // ��3��:���������ʽ�ͱ����ʽ(��Ե���API Level 8�汾)
	        //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
	        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
	        //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	        //mMediaRecorder.setVideoSize(320, 120);// ������Ƶ��С
		//}
	        File directory;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				directory = new File(Environment
						.getExternalStorageDirectory(), "video");
			} else {
				directory = new File(this.getApplicationContext().getCacheDir(), "video");
			}
			if (!directory.exists()) {
				directory.mkdir();
			}
	        file = new File(directory, System.currentTimeMillis()
					+ ".3gp");
		// ��4��:ָ������ļ�
		mMediaRecorder.setOutputFile(file.getPath().toString());
		System.out.println(file.getPath().toString());

		// ��5��:ָ��Ԥ�����
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// ��6��:������������׼��MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}
	
    private Camera getCameraInstance(){ 
        Camera c = null; 

        try { 
            // ��ȡCameraʵ��
            c = Camera.open(); 
        } catch (Exception e){ 
            // ����ͷ�����ã�����ռ�û򲻴��ڣ�
        } 
        // �������򷵻�null
        return c; 
    }
    
	private File getOutputMediaFile(int type) {
		System.out.println("dd"+type);
		return null;
	    // ......����ý���ļ�����xxx.mp4......
	}
	
	private boolean checkCameraHardware(Context context) { 
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){ 
            // ����ͷ���� 
            return true; 
        } else { 
            //����ͷ������ 
            return false; 
        } 
    }

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	public void sendFilePhp(String path){
		RequestParams params = new RequestParams();
		videohandler.sendEmptyMessage(1);
		try {
			//params.put("uploadpath", "video");
			params.put("uploadtype", "video");
			params.put("username", myapplication.getUSERID());
			params.put("uploadfile", new File(path));
			AsyncHttpClient client = new AsyncHttpClient();
			client.post("http://spark.iokokok.com/ioksparkService/index.php/Index/upload", params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);
				    Message msg=new Message();
				    if (response!=null){
					    try {
							JSONObject result=new JSONObject(response);
							int state=result.getInt("state");
							if (state==1){
    							JSONArray user=result.getJSONArray("result");
    							JSONObject obj=user.getJSONObject(0);
    							String filepath="http://spark.iokokok.com/ioksparkService/"+obj.getString("savepath")+obj.getString("savename");
    							msg.what=2;
    							msg.obj=filepath;
							}else{
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    videohandler.sendMessage(msg);
				    }
				}  
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			videohandler.sendEmptyMessage(3);
		} // Upload a File
		
	}
	private Handler videohandler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(VideoActivity.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ���ϴ���Ƶ......"); 
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
				Toast.makeText(VideoActivity.this, "��Ƶ�ϴ��ɹ�", Toast.LENGTH_LONG).show();
				Intent data=new Intent();  
	            data.putExtra("videopath", file.getPath().toString()); 
	            data.putExtra("videourl",(String)msg.obj);
	            data.putExtra("videotime", second); 
	            //�����������Լ����ã��������ó�20  
	            setResult(104, data);  
				dialog.cancel();
				VideoActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(VideoActivity.this, "��Ƶ�ϴ�ʧ��", Toast.LENGTH_LONG).show();
				//VideoActivity.this.finish();
			}
		}
	};
    /*��ʱ�����ã�ʵ�ּ�ʱ*/    
    private Handler handler = new Handler();
    private Runnable task = new Runnable()
    {
        public void run()
        {
            if (bool)
            {
                handler.postDelayed(this, 1000);
                second++;
                if (second < 60)
                {
                    tv05.setText(format(second));
                } else if (second < 3600)
                {
                    minute = second / 60;
                    second = second % 60;
                    tv03.setText(format(minute));
                    tv05.setText(format(second));
                } else
                {
                    hour = second / 3600;
                    minute = (second % 3600) / 60;
                    second = (second % 3600) % 60;
                    tv01.setText(format(hour));
                    tv03.setText(format(minute));
                    tv05.setText(format(second));
                }
            }
        }
    };
    
    /* ��ʽ��ʱ��*/    
    public String format(int i)
    {
        String s = i + "";
        if (s.length() == 1)
        {
            s = "0" + s;
        }
        return s;
    }
}