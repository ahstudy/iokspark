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
	/** ΢��API�ӿ��࣬�ṩ��½�ȹ���  */
    private Weibo mWeibo;
    
    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
    private Oauth2AccessToken mAccessToken;

    /** ע�⣺SsoHandler ����sdk֧��ssoʱ��Ч */
    private SsoHandler mSsoHandler;
    private Context ctx;
    private static String SINA_BASE_URL="https://api.weibo.com/2/";
	public SinaWeiboClient(Context context){
		this.ctx=context;
		mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
	}
	
	//�����Ȩ����
	public void getSinaLogin(){
		mWeibo.anthorize(ctx, new AuthDialogListener());
	}
	
	/**
     * ΢����֤��Ȩ�ص��ࡣ
     * 1. SSO��½ʱ����Ҫ��{@link #onActivityResult}�е���mSsoHandler.authorizeCallBack��
     *    �ûص��Żᱻִ�С�
     * 2. ��SSO��½ʱ������Ȩ�󣬾ͻᱻִ�С�
     * ����Ȩ�ɹ����뱣���access_token��expires_in����Ϣ��SharedPreferences�С�
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
               // mText.setText("��֤�ɹ�: \r\n access_token: " + token + "\r\n" + "expires_in: "
                //        + expires_in + "\r\n��Ч�ڣ�" + date);
                //System.out.println(date);
                AccessTokenKeeper.keepAccessToken(ctx, mAccessToken);
                //System.out.println(uid);
                //Toast.makeText(Login.this, "��֤�ɹ�"+uid, Toast.LENGTH_SHORT).show();
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
    
    //��ȡ�û���Ϣ
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
