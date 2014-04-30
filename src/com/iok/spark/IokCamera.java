package com.iok.spark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class IokCamera extends Activity implements OnClickListener {
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private CameraCallback mCallback;
	private ImageButton mTakePicButton;
	private ImageButton mSwitchButton;
	private Button mflashButton;
	
	private boolean saved = true;
	private boolean isFrontCamera;

	public static final int MESSAGE_SVAE_SUCCESS = 0;
	public static final int MESSAGE_SVAE_FAILURE = 1;
	
	private  final int FLASH_MODE_AUTO = 0;
	private  final int FLASH_MODE_ON = 1;
	private final int FLASH_MODE_OFF = 2;
	private int mFlashMode = 0;
	private ProgressDialog progressDialog = null;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			saved = true;
			switch (msg.what) {
			case MESSAGE_SVAE_SUCCESS:
				String path=(String )msg.obj;
				progressDialog.cancel();
				//Toast.makeText(IokCamera.this, "保存成功"+path, Toast.LENGTH_SHORT)
				//		.show();
				Intent data=new Intent();  
	            data.putExtra("path", path); 
	            //请求代码可以自己设置，这里设置成20  
	            setResult(102, data);  
				IokCamera.this.finish();
				break;
			case MESSAGE_SVAE_FAILURE:
				Toast.makeText(IokCamera.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
				break;
				
			case 3:
				progressDialog = ProgressDialog.show(IokCamera.this, "请稍等...", "获取数据中...", true);
				break;
				
			}
		};
	};
	private SeekBar mZoomBar;
	private View mZoomLayout;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		initView();

	}

	private void initView() {

		mTakePicButton = (ImageButton) findViewById(R.id.camera_take_btn);
		mTakePicButton.setOnClickListener(this);
		mSwitchButton = (ImageButton) findViewById(R.id.camera_switch_btn);
		mSwitchButton.setOnClickListener(this);
		mflashButton = (Button) findViewById(R.id.flashMode);
		mflashButton.setOnClickListener(this);
		
		mZoomLayout = findViewById(R.id.zoomLayout);
		mZoomBar = (SeekBar) findViewById(R.id.seekBar1);
		mZoomBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mCallback.setZoom(progress);
			}
		});
		
		initSurfaceView();
	}

	//返回键事件
	public void find_back(View v){
		this.finish();
	}
	private void initSurfaceView() {
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		mHolder = mSurfaceView.getHolder();
		mCallback = new CameraCallback(this);
		mHolder.addCallback(mCallback);
		mSurfaceView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& !isFrontCamera) {//前置摄像头取消触摸自动聚焦功能
					View view = findViewById(R.id.RelativeLayout1);
					mCallback.autoFocus(view, event);
				}

				return true;
			}
		});
		
		//判断是否支持前置摄像头
		int cameras = mCallback.getNumberOfCameras();
		if (cameras <= 1) {
			mSwitchButton.setVisibility(View.GONE);
		}
		
		//是否支持闪关灯
		if (!mCallback.isSupportedFlashMode()) {
			mflashButton.setVisibility(View.GONE);
		}
		
		if (mCallback.isSupportedZoom()) {
			mZoomBar.setMax(mCallback.getMaxZoom());
		}else{
			mZoomBar.setVisibility(View.GONE);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_take_btn:
			if (saved) {
				saved = false;
				MediaPlayer music=MediaPlayer.create(IokCamera.this, R.raw.camera);
				music.start();
				mCallback.takePicture(mHandler);
				mHandler.sendEmptyMessage(3);
			}
			break;
		case R.id.camera_switch_btn:
			isFrontCamera = !isFrontCamera;
			if (isFrontCamera) {
				//mSwitchButton.setText("打开后摄像头");
				mflashButton.setVisibility(View.GONE);
				mZoomLayout.setVisibility(View.GONE);
			}else{
				//mSwitchButton.setText("打开前摄像头");
				mflashButton.setVisibility(View.VISIBLE);
				mZoomLayout.setVisibility(View.VISIBLE);
			}
			mCallback.switchCamera(mSurfaceView, isFrontCamera);
			break;
		case R.id.flashMode:
			mFlashMode = (mFlashMode+1)%3;
			switch (mFlashMode) {
			case FLASH_MODE_AUTO:
				mflashButton.setText("flash_auto");
				break;
			case FLASH_MODE_ON:
				mflashButton.setText("flash_on");
				break;
			case FLASH_MODE_OFF:
				mflashButton.setText("flash_off");
				break;

			default:
				break;
			}
			mCallback.SetFlashMode(mFlashMode);
			break;
		
		default:
			break;
		}

	}

}