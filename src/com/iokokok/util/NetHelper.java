package com.iokokok.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class NetHelper {
	private static SharedPreferences mSharedPreferences; 
    private static SharedPreferences.Editor mEditor; 
	public static String httpStringGet(String url) throws Exception {
		return httpStringGet(url, "utf-8");
	}
	
	public static String getHtml(String url) throws Exception{
		HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==200){
			InputStream inStream=conn.getInputStream();
			String result=inStream.toString();
			return result;
		}
		return null;
	}
	/**
	 * 读取数据
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inputStream) throws Exception
	{
		byte[] buffer=new byte[1024];
		int len=-1;
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		
		while((len=inputStream.read(buffer))!=-1)
		{
			byteArrayOutputStream.write(buffer,0,len);
		}
		
		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 获取网上图片
	 * @throws Exception
	 */
	public static byte[] GetImage(String path) throws Exception
	{
		URL url=new URL(path);
		HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(6*1000);  //设置链接超时时间6s
		conn.setRequestMethod("GET");

		if(conn.getResponseCode()==200)
		{
			InputStream inputStream=conn.getInputStream();
			return readStream(inputStream);
		}
		return null;
	}
	
	/**
	 * 获取网址的html
	 * @throws Exception
	 */
	public static byte[] GetHtml(String urlpath) throws Exception
	{
		//String urlpath="http://www.sohu.com/";
		URL url=new URL(urlpath);
		HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(6*1000);  //设置链接超时时间6s

		conn.setRequestMethod("GET");

		if(conn.getResponseCode()==200)
		{
			InputStream inputStream=conn.getInputStream();
			byte[] data=readStream(inputStream);
			return data;
		}
		return null;
	}
	/**
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImage(String url,String filename) {
		try {
			return Drawable.createFromStream(
					(InputStream) new URL(url).getContent(), filename);
		} catch (MalformedURLException e) {
			Log.e("exception", e.getMessage());
		} catch (IOException e) {
			Log.e("exception", e.getMessage());
		}
		return null;
	}

	public static String httpStringGet(String url, String enc) throws Exception {
		// This method for HttpConnection
		String page = "";
		BufferedReader bufferedReader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					"android");
			
			 HttpParams httpParams = client.getParams();
			 HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			 HttpConnectionParams.setSoTimeout(httpParams, 5000);
			 
			HttpGet request = new HttpGet();
			request.setHeader("Content-Type", "text/plain; charset=utf-8");
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), enc));

			StringBuffer stringBuffer = new StringBuffer("");
			String line = "";

			String NL = System.getProperty("line.separator");
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + NL);
			}
			bufferedReader.close();
			page = stringBuffer.toString();
			Log.i("page", page);
			System.out.println(page + "page");
			return page;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					Log.d("BBB", e.toString());
				}
			}
		}
	}

	public static int checkNetWorkStatus(Context context) {
		int result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		result = 0;
		if (netinfo != null && netinfo.isConnected()) {
			if(netinfo.getType() == ConnectivityManager.TYPE_WIFI){
				result=1;
			}else if(netinfo.getType() == ConnectivityManager.TYPE_MOBILE){
				result=2;
			}
			Log.i("NetStatus", "The net was connected");
		} else {
			result = 0;
			Log.i("NetStatus", "The net was bad!");
		}
		return result;
	}
	
	public static String  getMyLoc(Context c){
		LocationManager locationManager =
	            (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
	    boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    String getaddress="";
	    if (!gpsEnabled) {
	        // Build an alert dialog here that requests that the user enable
	        // the location services, then when the user clicks the "OK" button,
	    	Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    c.startActivity(settingsIntent);
	    }else{
	    	Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_FINE);
		    criteria.setCostAllowed(false);
		    String providerName = locationManager.getBestProvider(criteria, true);
		    Location mylocation=locationManager.getLastKnownLocation(providerName);
		    double mylat=mylocation.getLatitude();
		    double mylong=mylocation.getLongitude();
		    String locationstr=String.valueOf(mylat)+","+String.valueOf(mylong);
		    String getjsonurl="http://maps.google.com/maps/api/geocode/json?latlng="+locationstr+"&language=zh-CN&sensor=false";
		    
		    try {
		    	String getjson=httpStringGet(getjsonurl).toString();
				JSONObject myjsonObject = new JSONObject(getjson);
				if (myjsonObject.getString("status").equals("OK")){
					JSONArray resultData=myjsonObject.getJSONArray("results");
					JSONObject oj = resultData.getJSONObject(0);
					Log.i("formatted_address", oj.getString("formatted_address"));
					Toast.makeText(c, oj.getString("formatted_address"), 100000).show();
					getaddress=oj.getString("formatted_address");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return getaddress;
	    
	}
	//往共享文件中写某一项
	public static void writeStringPreferences(Context context,String key,String value){
		mSharedPreferences = context.getSharedPreferences("iok", 0);  
		mEditor = mSharedPreferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}
	public static String readStringPreferences(Context context,String key,String delstr){
		mSharedPreferences = context.getSharedPreferences("iok", 0);
		delstr=mSharedPreferences.getString(key, delstr);
		return delstr;
	}
	public static void writebooleanPreferences(Context context,String key,boolean value){
		mSharedPreferences = context.getSharedPreferences("iok", 0);  
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}
	public static boolean readbooleanPreferences(Context context,String key,boolean delbool){
		mSharedPreferences = context.getSharedPreferences("iok", 0);
		delbool=mSharedPreferences.getBoolean(key, delbool);
		return delbool;
	}
	public static void writeIntPreferences(Context context,String key,int value){
		mSharedPreferences = context.getSharedPreferences("iok", 0);  
		mEditor = mSharedPreferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}
	public static int readIntPreferences(Context context,String key,int delint){
		mSharedPreferences = context.getSharedPreferences("iok", 0);
		delint=mSharedPreferences.getInt(key, delint);
		return delint;
	}
	
	/**
	 * 判断手机网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	
	/**
     * 到Url去下載圖片回傳BITMAP回來
     * @param imgUrl
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imgUrl) {
                URL url;
                Bitmap bitmap = null;
                try {
                        url = new URL(imgUrl);
                        InputStream is = url.openConnection().getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bitmap = BitmapFactory.decodeStream(bis);
                        bis.close();
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return bitmap;
        }
	
}
