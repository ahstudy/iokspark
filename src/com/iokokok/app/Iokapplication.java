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
     * 引发异常：在一些不规范的代码中经常看到Activity或者是Service当中定义许多静态成员属性。这样做可能会造成许多莫名其妙的 null 
     * pointer异常。 
     */  
  
    /** 
     * 异常分析：Java虚拟机的垃圾回收机制会主动回收没有被引用的对象或属性。在内存不足时，虚拟机会主动回收处于后台的Activity或 
     * Service所占用的内存。当应用再次去调用静态属性或对象的时候，就会造成null pointer异常 
     */  
  
    /** 
     * 解决异常：Application在整个应用中，只要进程存在，Application的静态成员变量就不会被回收，不会造成null pointer异常 
     */  
    private String USERID;  
    private String USERNICK;
    private MessageDB mydb;
    private XmppTool client;
    private UserMessage user;
    private boolean isClientStart;// 客户端连接是否启动
	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;// 后台运行的消息
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
    	//client = new XmppTool();// 从配置文件中读ip和地址
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
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static Iokapplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(Iokapplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(Iokapplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
        }

        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                //Toast.makeText(Iokapplication.getInstance().getApplicationContext(), 
                //       "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
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
