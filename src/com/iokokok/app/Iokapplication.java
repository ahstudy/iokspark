package com.iokokok.app;

import java.util.LinkedList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.iok.spark.ChatMsgEntity;
import com.iok.spark.ChatMsgViewAdapter;
import com.iokokok.user.UserMessage;
import com.iokokok.util.MessageDB;
import com.iokokok.util.SharePreferenceUtil;
import com.iokokok.util.XmppTool;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

public class Iokapplication extends Application {
	/** 
     * �����쳣����һЩ���淶�Ĵ����о�������Activity������Service���ж�����ྲ̬��Ա���ԡ����������ܻ�������Ī������� null 
     * pointer�쳣�� 
     */  
  
    /** 
     * �쳣������Java��������������ջ��ƻ���������û�б����õĶ�������ԡ����ڴ治��ʱ����������������մ��ں�̨��Activity�� 
     * Service��ռ�õ��ڴ档��Ӧ���ٴ�ȥ���þ�̬���Ի�����ʱ�򣬾ͻ����null pointer�쳣 
     */  
  
    /** 
     * ����쳣��Application������Ӧ���У�ֻҪ���̴��ڣ�Application�ľ�̬��Ա�����Ͳ��ᱻ���գ��������null pointer�쳣 
     */  
    private String USERID;  
    private String USERNICK;
    private MessageDB mydb;
    private XmppTool client;
    private UserMessage user;
    private boolean isClientStart;// �ͻ��������Ƿ�����
	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;// ��̨���е���Ϣ
	private LinkedList<ChatMsgEntity> mRecentList;
	private ChatMsgViewAdapter mRecentAdapter;
	private int recentNum = 0;
	private String userchat;
	private static Iokapplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    public String mySinaToken;
    public String curNet;
    private String USERPASS,longitude,latitude;
    public static final String strKey = "FAfb3033417c3b9fcd98f647369c3613";
  
    @Override  
    public void onCreate() {  
        // TODO Auto-generated method stub  
    	SharePreferenceUtil util = new SharePreferenceUtil(this,
				Constants.SAVE_USER);
    	//client = new XmppTool();// �������ļ��ж�ip�͵�ַ
    	//client.getConnection();
    	mRecentList = new LinkedList<ChatMsgEntity>();
		//mRecentAdapter = new ChatMsgViewAdapter(getApplicationContext(),mRecentList);
    	mInstance = this;
		initEngineManager(this);
        super.onCreate();  
    }  
    
    
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(Iokapplication.getInstance().getApplicationContext(), 
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static Iokapplication getInstance() {
		return mInstance;
	}
	
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    static class MyGeneralListener implements MKGeneralListener {
        
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(Iokapplication.getInstance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(Iokapplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
        }

        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                //Toast.makeText(Iokapplication.getInstance().getApplicationContext(), 
                //       "���� DemoApplication.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG).show();
                Iokapplication.getInstance().m_bKeyRight = false;
            }
        }
    }
    
    public String getSinaToken(){
    	return mySinaToken;
    }
    
    public void setSinaToken(String token){
    	mySinaToken=token;
    }
    public XmppTool getClient() {
		return client;
	}

    public String getChatuser(){
    	return userchat;
    }
    
    public void setChatuser(String userchat){
    	this.userchat=userchat;
    }
	public boolean isClientStart() {
		return isClientStart;
	}

	public void setClientStart(boolean isClientStart) {
		this.isClientStart = isClientStart;
	}

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public LinkedList<ChatMsgEntity> getmRecentList() {
		return mRecentList;
	}

	public void setmRecentList(LinkedList<ChatMsgEntity> mRecentList) {
		this.mRecentList = mRecentList;
	}

	//public RecentChatAdapter getmRecentAdapter() {
	//	return mRecentAdapter;
	//}

	//public void setmRecentAdapter(RecentChatAdapter mRecentAdapter) {
	//	this.mRecentAdapter = mRecentAdapter;
	//}

	public int getRecentNum() {
		return recentNum;
	}

	public void setRecentNum(int recentNum) {
		this.recentNum = recentNum;
	}
	
    public String getUSERNICK(){
    	return USERNICK;
    }
    public void setUSERNICK(String USERNICK){
    	this.USERNICK=USERNICK;
    }
    public String getUSERID() {  
        return USERID;  
    }  
  
    public void setUSERID(String USERID) {  
        this.USERID = USERID;  
    }  
    
    public String getUSERPASS() {  
        return USERPASS;  
    }  
  
    public void setUSERPASS(String USERPASS) {  
        this.USERPASS = USERPASS;  
    } 
    public void setMyDB(MessageDB mydb){
    	this.mydb=mydb;
    }
    public MessageDB getMyDB(){
    	return mydb;
    }

    public String getCurNet(){
    	return curNet;
    }
    
    public void setCurNet(String curNet){
    	this.curNet=curNet;
    }
	public UserMessage getUser() {
		return user;
	}


	public void setUser(UserMessage user) {
		this.user = user;
	}

	public void setLongitude(String longitude){
		this.longitude=longitude;
	}
	
	public String getLongitude(){
		return longitude;
	}
	
	public void setLatitude(String latitude){
		this.latitude=latitude;
	}
	
	public String getLatitude(){
		return latitude;
	}
	public ChatMsgViewAdapter getmRecentAdapter() {
		return mRecentAdapter;
	}


	public void setmRecentAdapter(ChatMsgViewAdapter mRecentAdapter) {
		this.mRecentAdapter = mRecentAdapter;
	}
    

}
