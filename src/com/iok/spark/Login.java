package com.iok.spark;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iokokok.app.Constants;
import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.util.DialogHelper;
import com.iokokok.util.MessageDB;
import com.iokokok.util.NetHelper;
import com.iokokok.util.PhpHttpUtil;
import com.iokokok.util.XmppTool;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.AccessTokenKeeper;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText mUser; // �ʺű༭��
	private EditText mPassword; // ����༭��
	public ProgressDialog dialog ; 
	private Iokapplication myapplication;
	private MessageDB messageDB;
	private UpdateManager updateMan;
	private ProgressDialog updateProgressDialog;
	private String USERID ="";
	/** ΢��API�ӿ��࣬�ṩ��½�ȹ���  */
    private Weibo mWeibo;
    
    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
    private Oauth2AccessToken mAccessToken;

    /** ע�⣺SsoHandler ����sdk֧��ssoʱ��Ч */
    private SsoHandler mSsoHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        updateMan = new UpdateManager(this, appUpdateCb);
		updateMan.checkUpdate();
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        messageDB=new MessageDB(this);
        mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        myapplication=(Iokapplication)Login.this.getApplicationContext();
        String curUserid=NetHelper.readStringPreferences(this, "CURUSERID", "");
        messageDB.createUserList();
        
        if (!curUserid.equals(""))
        	mUser.setText(curUserid);
        
    }

    
    public void sinaLogin(View v){
    	Intent intent=new Intent(Login.this,NewsContentActivity.class );
    	intent.putExtra("url", "http://spark.iokokok.com/ioksparkService/index.php/Sina/");
    	this.startActivity(intent);
    	//mWeibo.anthorize(Login.this, new AuthDialogListener());
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
            
            final String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            final String  uid=values.getString("uid");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(mAccessToken.getExpiresTime()));
               // mText.setText("��֤�ɹ�: \r\n access_token: " + token + "\r\n" + "expires_in: "
                //        + expires_in + "\r\n��Ч�ڣ�" + date);
                AccessTokenKeeper.keepAccessToken(Login.this, mAccessToken);
                myapplication.setSinaToken(token);
              //����û��Ƿ��Ѿ�������
            	RequestParams params = new RequestParams();
        		params.put("uid", uid);
        		params.put("token", token);
        		String url=PhpHttpUtil.SERVICE_HOST+"index.php/Sina/checkuser/";
        		AsyncHttpClient client = new AsyncHttpClient();
        		client.post(url, params, new AsyncHttpResponseHandler(){
        			@Override  
        			public void onSuccess(String response) {  
        			    System.out.println(response);
        			    if (response!=null){
        				    try {
        						JSONObject result=new JSONObject(response);
        						int state=result.getInt("state");
        						if (state==1){
        							JSONArray user=result.getJSONArray("result");
        							JSONObject obj=user.getJSONObject(0);
        							login(obj.getString("username"),obj.getString("userpass"));
        						}else{
        							//JSONObject user=new JSONObject(result.getString("result"));
        							try {
										showUser(uid,token);
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (WeiboException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
        						}
        					} catch (JSONException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        			    }
        			}  
        		});
                Toast.makeText(Login.this, "��֤�ɹ�"+uid, Toast.LENGTH_SHORT).show();
            }
        }

        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(), 
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
            //Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
        }

        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(), 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    
    public String showUser(final String uid,final String token)
            throws MalformedURLException, IOException, WeiboException {
        String rlt = "";
        try {
            String url = Constants.SINA_BASE_URL+"users/show.json";
            Log.i("Weibo", "Weibo.showUser().url " + url);
            RequestParams params = new RequestParams();
    		params.put("uid", uid);
			params.put("access_token", token);
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(url, params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);
				    if (response!=null){
					    try {
							JSONObject result=new JSONObject(response);
							String name=result.getString("name");
							String profile_image_url=result.getString("profile_image_url");
							Intent intent =new Intent(Login.this,Register.class);
							intent.putExtra("type", "sina");
							intent.putExtra("uid", uid);
							intent.putExtra("name", name);
							intent.putExtra("profile_image_url", profile_image_url);
							intent.putExtra("token", token);
							Login.this.startActivity(intent);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				}  
			});

            Log.i("Weibo", "Weibo.showUser().rlt " + rlt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rlt;
    }
    @Override
	protected void onResume() {// ��onResume�����������ж������Ƿ���ã�����������,�����ڴ���������֮�󷵻ص�ǰActivityʱ����������������������
		super.onResume();
		if (isNetworkAvailable()) {
			Log.i("Spark:","Service is Start!");
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		} else {
			toast(this);
		}
	}
    public void login_mainweixin(View v) {
    	
      //ȡ��������û�������
		USERID = this.mUser.getText().toString();
		final String PWD = this.mPassword.getText().toString();
		if (USERID.equals("")||PWD.equals("")){
			Toast.makeText(this, "����д�û��������룡", Toast.LENGTH_LONG).show();
		}else{
			NetHelper.writeStringPreferences(this, "CURUSERID", USERID);
			Thread t=new Thread(new Runnable() {				
				public void run() {
					handler.sendEmptyMessage(1);
					try {
						//����
						XmppTool.getConnection().login(USERID, PWD);
						
						
						String mynickname=XmppTool.getUserNick(XmppTool.getConnection(), USERID+"@"+XmppTool.SERVER_NAME);
						Bitmap userhead=XmppTool.getUserImage(USERID+"@"+XmppTool.SERVER_NAME);
						messageDB.createUserTable(USERID);
						if (userhead!=null){
							byte[] bytes = null; 
							final ByteArrayOutputStream os = new ByteArrayOutputStream();
							// ��Bitmapѹ����PNG���룬����Ϊ100%�洢
							userhead.compress(Bitmap.CompressFormat.PNG, 100, os);//����PNG���кܶೣ����ʽ����jpeg�ȡ�
							bytes= os.toByteArray();
				            String encodedImage = StringUtils.encodeBase64(bytes);
							//UserMessage user=new UserMessage(USERID,PWD,bytes,TimeRender.getDate());
							//messageDB.insertUserList(user);
							messageDB.saveUserSet(USERID, "head", encodedImage);
						}
						myapplication.setUSERID(USERID);
						myapplication.setUSERPASS(PWD);
						myapplication.setUSERNICK(mynickname);
						myapplication.setMyDB(messageDB);
						//XmppTool.searchUsers(XmppTool.getConnection(), "");
						handler.sendEmptyMessage(2);
						//Login.this.finish();
					}
					catch (XMPPException e) 
					{
						XmppTool.closeConnection();
						
						handler.sendEmptyMessage(3);
					}					
				}
			});
			t.start();
		}
		
        
    	
      }  
    
    public void login(final String username,final String password){
    	NetHelper.writeStringPreferences(this, "CURUSERID", username);
		Thread t=new Thread(new Runnable() {				
			public void run() {
				handler.sendEmptyMessage(1);
				try {
					//����
					XmppTool.getConnection().login(username, password);
					
					
					String mynickname=XmppTool.getUserNick(XmppTool.getConnection(), username+"@"+XmppTool.SERVER_NAME);
					Bitmap userhead=XmppTool.getUserImage(username+"@"+XmppTool.SERVER_NAME);
					messageDB.createUserTable(username);
					if (userhead!=null){
						byte[] bytes = null; 
						final ByteArrayOutputStream os = new ByteArrayOutputStream();
						// ��Bitmapѹ����PNG���룬����Ϊ100%�洢
						userhead.compress(Bitmap.CompressFormat.PNG, 100, os);//����PNG���кܶೣ����ʽ����jpeg�ȡ�
						bytes= os.toByteArray();
			            String encodedImage = StringUtils.encodeBase64(bytes);
						//UserMessage user=new UserMessage(USERID,PWD,bytes,TimeRender.getDate());
						//messageDB.insertUserList(user);
						messageDB.saveUserSet(username, "head", encodedImage);
					}
					myapplication.setUSERID(username);
					myapplication.setUSERPASS(password);
					myapplication.setUSERNICK(mynickname);
					myapplication.setMyDB(messageDB);
					//XmppTool.searchUsers(XmppTool.getConnection(), "");
					handler.sendEmptyMessage(2);
					//Login.this.finish();
				}
				catch (XMPPException e) 
				{
					XmppTool.closeConnection();
					
					handler.sendEmptyMessage(3);
				}					
			}
		});
		t.start();
    }
    public void login_back(View v) {     //������ ���ذ�ť
      	this.finish();
      }  
    public void register(View v){   //ע�����û�
    	Intent intent = new Intent(Login.this,Register.class ); 
    	intent.putExtra("type", "go");
    	startActivity(intent);
    }
  
    private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what==1)
			{
				
				dialog = new ProgressDialog(Login.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("�����ĵ�¼"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����������Ŭ����¼��......"); 
				//����ProgressDialog ����ͼ�� 
				dialog.setIcon(R.drawable.icon); 
				//����ProgressDialog �Ľ������Ƿ���ȷ 
				dialog.setIndeterminate(false); 
				//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
				dialog.setCancelable(true); 
				//��ʾ 
				dialog.show(); 
			}
			else if(msg.what==2)
			{
				
				dialog.cancel();
				//XmppTool.changeStateMessage(XmppTool.getConnection(), "��������");
								//String state=XmppTool.getStateMessage();
				Intent intent = new Intent();
				//��ת���¹��ܽ���
				//intent.setClass(Login.this, Whatsnew.class);
				//messageDB.close();
				//ֱ����ת��������
				intent.setClass(Login.this, MainWeixin.class);
				Login.this.startActivity(intent);
				
			}
			else if (msg.what==3){
				dialog.cancel();
				Toast.makeText(Login.this, "��¼ʧ�ܣ�",Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	/**
	 * �ж��ֻ������Ƿ����
	 * 
	 * @param context
	 * @return
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
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

	private void toast(Context context) {
		new AlertDialog.Builder(context)
				.setTitle("��ܰ��ʾ")
				.setMessage("�ף�������������δ��Ŷ")
				.setPositiveButton("ǰ����",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
							}
						}).setNegativeButton("ȡ��", null).create().show();
	}
	
	
	 // �Զ����»ص�����
	UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() 
	{

		public void downloadProgressChanged(int progress) {
			if (updateProgressDialog != null
					&& updateProgressDialog.isShowing()) {
				updateProgressDialog.setProgress(progress);
			}

		}

		public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
			if (updateProgressDialog != null
					&& updateProgressDialog.isShowing()) {
				updateProgressDialog.dismiss();
			}
			if (sucess) {
				updateMan.update();
			} else {
				DialogHelper.Confirm(Login.this,
						R.string.dialog_error_title,
						R.string.dialog_downfailed_msg,
						R.string.dialog_downfailed_btnnext,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								updateMan.downloadPackage();

							}
						}, R.string.dialog_downfailed_btnnext, null);
			}
		}

		public void downloadCanceled() 
		{
			// TODO Auto-generated method stub

		}

		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo,CharSequence updatemess1,CharSequence updatemess2,CharSequence updatemess3) {
			if (hasUpdate) {
				String messStr=getText(R.string.dialog_update_msg).toString()
						+updateInfo+"\n�������ݣ�\n1."+updatemess1
						+"\n2."+updatemess2
						+"\n3."+updatemess3+
						getText(R.string.dialog_update_msg2).toString();
				DialogHelper.Confirm(Login.this,
						getText(R.string.dialog_update_title),
						messStr,
								getText(R.string.dialog_update_btnupdate),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								updateProgressDialog = new ProgressDialog(
										Login.this);
								updateProgressDialog
										.setMessage(getText(R.string.dialog_downloading_msg));
								updateProgressDialog.setIndeterminate(false);
								updateProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								updateProgressDialog.setMax(100);
								updateProgressDialog.setProgress(0);
								updateProgressDialog.show();

								updateMan.downloadPackage();
							}
						},getText( R.string.dialog_update_btnnext), null);
			}

		}
	};
}
