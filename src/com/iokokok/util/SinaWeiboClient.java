package com.iokokok.util;

import java.text.SimpleDateFormat;
import com.iokokok.app.Constants;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.AccessTokenKeeper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SinaWeiboClient {
	/** 微博API接口类，提供登陆等功能  */
    private Weibo mWeibo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当sdk支持sso时有效 */
    private SsoHandler mSsoHandler;
    private Context ctx;
    private static String SINA_BASE_URL="https://api.weibo.com/2/";
	public SinaWeiboClient(Context context){
		this.ctx=context;
		mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
	}
	
	//获得授权窗口
	public void getSinaLogin(){
		mWeibo.anthorize(ctx, new AuthDialogListener());
	}
	
	/**
     * 微博认证授权回调类。
     * 1. SSO登陆时，需要在{@link #onActivityResult}中调用mSsoHandler.authorizeCallBack后，
     *    该回调才会被执行。
     * 2. 非SSO登陆时，当授权后，就会被执行。
     * 当授权成功后，请保存该access_token、expires_in等信息到SharedPreferences中。
     */
    class AuthDialogListener implements WeiboAuthListener {
        
        public void onComplete(Bundle values) {
            
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String  uid=values.getString("uid");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(mAccessToken.getExpiresTime()));
               // mText.setText("认证成功: \r\n access_token: " + token + "\r\n" + "expires_in: "
                //        + expires_in + "\r\n有效期：" + date);
                //System.out.println(date);
                AccessTokenKeeper.keepAccessToken(ctx, mAccessToken);
                //System.out.println(uid);
                //Toast.makeText(Login.this, "认证成功"+uid, Toast.LENGTH_SHORT).show();
            }
        }

        public void onError(WeiboDialogError e) {
            //Toast.makeText(getApplicationContext(), 
             //       "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
            //Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
        }

        public void onWeiboException(WeiboException e) {
            //Toast.makeText(getApplicationContext(), 
             //       "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    //获取用户信息
    public void getUserInfo(String Token,String uid,Handler handler){
    	String rlt = "";
        try {
            String url = SINA_BASE_URL+"users/show.json";
            Log.i("Weibo", "Weibo.showUser().url " + url);
            RequestParams params = new RequestParams();
    		params.put("uid", uid);
			params.put("access_token", Token);
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(url, params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);
				    
				}  
			});

            Log.i("Weibo", "Weibo.showUser().rlt " + rlt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
