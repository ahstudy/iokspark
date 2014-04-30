package com.iok.spark;


import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.XMPPException;

import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditHeadSel extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	private File sdcardTempFile;
	//private AlertDialog dialog;
	public ProgressDialog dialog ;
	private int crop = 80;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editheadsel);
		//dialog=new MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.exit_layout2);
		sdcardTempFile = new File("/mnt/sdcard/", "tmp_pic_" + SystemClock.currentThreadTimeMillis() + ".png");
		layout.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void selpic(View v) {  
		Intent intent = new Intent("android.intent.action.PICK");
		intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
		intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", crop);// 输出图片大小
		intent.putExtra("outputY", crop);
		startActivityForResult(intent, 100);    	
      }  
	public void selcam(View v) {  
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", crop);// 输出图片大小
		intent.putExtra("outputY", crop);
		startActivityForResult(intent, 101);
      }  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			
			Bitmap bmp = BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());
			System.out.println(sdcardTempFile.getAbsolutePath());
			InfoXiaohei.userimg.setImageBitmap(bmp);
			Thread thread=new Thread(){
	        	public void run(){
	        		handler.sendEmptyMessage(1);
					try {
						XmppTool.changeImage(XmppTool.getConnection(), sdcardTempFile);
						sdcardTempFile.delete();
						handler.sendEmptyMessage(2);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(3);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(3);
					}
	        	}
			};
			thread.start();
			//imageView.setImageBitmap(bmp);
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(EditHeadSel.this);
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
				Toast.makeText(EditHeadSel.this, "头像更新成功", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				EditHeadSel.this.finish();
			}else if(msg.what==3){
				Toast.makeText(EditHeadSel.this, "头像更新失败", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				EditHeadSel.this.finish();
			}
		}
   };
}

