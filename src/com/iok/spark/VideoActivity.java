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
        // 创建Camera实例 
        mCamera = getCameraInstance(); 
        mCamera.setDisplayOrientation(90);
        // 创建Preview view并将其设为activity中的内容
        mPreview = new CameraPreview(this, mCamera); 
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview); 
        preview.addView(mPreview); 
        // 为Capture按钮加入listener,拍摄视频
	 	captureButton = (ImageView) findViewById(R.id.video_normal); 
	 	captureButton.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View v) {
 				// 初始化视频camera
 				if (prepareVideoRecorder()) {
					mMediaRecorder.start();
					tv01.setVisibility(View.VISIBLE);
	                tv02.setVisibility(View.VISIBLE);
	                tv03.setVisibility(View.VISIBLE);
	                tv04.setVisibility(View.VISIBLE);
	                tv05.setVisibility(View.VISIBLE);
	                bool=true;
	                handler.postDelayed(task, 1000);
						// 通知用户录像已开始 
						//captureButton.setText("停止录像");
	                captureButton.setVisibility(captureButton.GONE);
	                captureplayButton.setVisibility(captureplayButton.VISIBLE);
	                
	                isRecording = true;
				} else {
						// 准备未能完成，释放camera
					releaseMediaRecorder();
				}
 			}
 		});
	 // 为Capture按钮加入listener,拍摄视频
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
        // 如果正在使用MediaRecorder，首先需要释放它。
        releaseMediaRecorder(); 
        // 在暂停事件中立即释放摄像头
        releaseCamera();
    } 
	public void stopVideo(){
		if (isRecording) {
			// 停止录像，释放camera
			mMediaRecorder.stop(); 
			releaseMediaRecorder(); 
			mCamera.lock(); 
			tv01.setVisibility(View.GONE);
            tv02.setVisibility(View.GONE);
            tv03.setVisibility(View.GONE);
            tv04.setVisibility(View.GONE);
            tv05.setVisibility(View.GONE);
            bool=false;
			//captureButton.setText("开始录像");
            captureButton.setVisibility(captureButton.VISIBLE);
            captureplayButton.setVisibility(captureplayButton.GONE);
			isRecording = false;
			System.out.println(file.length());
			if (file.length()>10485760){
				Toast.makeText(this, "录像文件超过10M，太大了，小伙伴表示压力很大！！", Toast.LENGTH_LONG).show();
			}else{
				sendFilePhp(file.getPath().toString());
			}
				
		}
	}
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			// 清除recorder配置
			mMediaRecorder.reset(); 
			// 释放recorder对象
			mMediaRecorder.release(); 

			mMediaRecorder = null;
			// 为后续使用锁定摄像头
			mCamera.lock(); 
		}
	}

	private void releaseCamera() {
		if (mCamera!= null) {
			// 为其它应用释放摄像头
			mCamera.release(); 
			mCamera = null;
		}
	}

	
	//返回键事件
		public void find_back(View v){
			this.finish();
		}
	private boolean prepareVideoRecorder() {

		mMediaRecorder = new MediaRecorder();

		// 第1步:解锁并将摄像头指向MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// 第2步:指定源
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setOrientationHint(90);
		//int version = android.provider.Settings.System.getInt(this
		//	     .getContentResolver(),
		//	     android.provider.Settings.System.SYS_PROP_SETTING_VERSION,
		//	     3);
		//System.out.println("API level:"+version);
		//if(version>8){
			// 第3步:指定CamcorderProfile(需要API Level 8以上版本)
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
		//}else{
		//	mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
	        // 第3步:设置输出格式和编码格式(针对低于API Level 8版本)
	        //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
	        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
	        //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	        //mMediaRecorder.setVideoSize(320, 120);// 设置视频大小
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
		// 第4步:指定输出文件
		mMediaRecorder.setOutputFile(file.getPath().toString());
		System.out.println(file.getPath().toString());

		// 第5步:指定预览输出
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// 第6步:根据以上配置准备MediaRecorder
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
            // 获取Camera实例
            c = Camera.open(); 
        } catch (Exception e){ 
            // 摄像头不可用（正被占用或不存在）
        } 
        // 不可用则返回null
        return c; 
    }
    
	private File getOutputMediaFile(int type) {
		System.out.println("dd"+type);
		return null;
	    // ......生成媒体文件，如xxx.mp4......
	}
	
	private boolean checkCameraHardware(Context context) { 
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){ 
            // 摄像头存在 
            return true; 
        } else { 
            //摄像头不存在 
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
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("我行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力为你上传视频......"); 
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
				Toast.makeText(VideoActivity.this, "视频上传成功", Toast.LENGTH_LONG).show();
				Intent data=new Intent();  
	            data.putExtra("videopath", file.getPath().toString()); 
	            data.putExtra("videourl",(String)msg.obj);
	            data.putExtra("videotime", second); 
	            //请求代码可以自己设置，这里设置成20  
	            setResult(104, data);  
				dialog.cancel();
				VideoActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(VideoActivity.this, "视频上传失败", Toast.LENGTH_LONG).show();
				//VideoActivity.this.finish();
			}
		}
	};
    /*定时器设置，实现计时*/    
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
    
    /* 格式化时间*/    
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