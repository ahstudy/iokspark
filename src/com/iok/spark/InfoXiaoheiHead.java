package com.iok.spark;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.ImageView;

public class InfoXiaoheiHead extends Activity{
	
	private String picurl;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.info_xiaohei_head);
		picurl=getIntent().getStringExtra("picurl");
		ImageView pic=(ImageView)findViewById(R.id.userpic);
		Uri uri=null;
		if (picurl.indexOf("http://")>=0){
			//uri = Uri.parse(picurl);
			setViewImage(pic,picurl);
		}else{
			uri = Uri.fromFile(new File(picurl ));
		}
		pic.setImageURI(uri);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);   //全屏显示
		//Toast.makeText(getApplicationContext(), "孩子！好好背诵！", Toast.LENGTH_LONG).show();
		//overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);

   }
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void setViewImage(ImageView v, String value) {
        new ImageDownloadTask().execute(value, v);
    }
    private class ImageDownloadTask extends AsyncTask<Object, Object, Bitmap>{
    		private ImageView imageView = null;
    		@Override
    		protected Bitmap doInBackground(Object... params) {
    			// TODO Auto-generated method stub
    			Bitmap bmp = null;
    			imageView = (ImageView) params[1];
    			try {
    				bmp = BitmapFactory.decodeStream(new URL((String)params[0]).openStream());
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return bmp;
    		}
    		
    		protected void onPostExecute(Bitmap result){
    			imageView.setImageBitmap(result);
    		}
    	}
}