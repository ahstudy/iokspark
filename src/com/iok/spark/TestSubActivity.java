package com.iok.spark;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.http.SyncHttpClient;
import com.iokokok.user.ChatMessage;
import com.iokokok.util.Msg;
import com.iokokok.util.PhpHttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TestSubActivity extends Activity {
	public ProgressDialog dialog ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.testsub);
	}
	//≤‚ ‘…œ¥´Õº∆¨
	public void testuploadpic(View v){
	}
	
	
	//≤‚ ‘∑˛ŒÒ∆˜
	public void testservice(View v){
		RequestParams params = new RequestParams();
		
		final android.os.Message message = new android.os.Message();// handle
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(20000);
		client.get("http://221.123.160.26/ioksparkService/", null, new AsyncHttpResponseHandler(){
			@Override  
			public void onSuccess(String response) {  
			    System.out.println(response);  
			    //handler.sendEmptyMessage(2);
			    try {
			    	JSONObject jObj = new JSONObject(response);
			    	JSONArray chatlogs=jObj.getJSONArray("result");
			    	System.out.println(chatlogs.length());
			    	for (int i=0;i<chatlogs.length();i++){
			    		JSONObject obj=chatlogs.getJSONObject(i);
			    		JSONObject mycontent=new JSONObject(obj.getString("content"));
			    		System.out.println(mycontent.getString("filePath"));
			    	}
		        } catch (JSONException e) {
		            Log.e("JSON Parser", "Error parsing data " + e.toString());
		        }
			}  
		});
		
	}
	
}
