package com.iok.spark;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.iokokok.app.Constants;
import com.iokokok.app.Iokapplication;
import com.iokokok.user.ChatMessage;
import com.iokokok.util.MessageDB;
import com.iokokok.util.SharePreferenceUtil;
import com.iokokok.util.TimeRender;
import com.iokokok.util.XmppTool;
/**
 * 收取消息服务
 * 
 * @author way
 * 
 */
public class GetMsgService extends Service {
	private static final int MSG = 0x001;
	private static final int SUBSCRIBE=0x002;
	private static final int UNSUBSCRIBE=0x003;
	private Iokapplication application;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Context mContext = this;
	private SharePreferenceUtil util;
	private MessageDB messageDB;
	private  XMPPConnection mycon = null;
	// �յ��û������ؼ������Ĺ㲥������ʾ֪ͨ��
	private BroadcastReceiver backKeyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "�����̨����", 0).show();
			setMsgNotification();
		}
	};
	
	
	// ��������֪ͨ����Ϣ��handler
		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG:

					
					break;

				default:
					break;
				}
			}
		};

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

	@Override
	public void onCreate() {// 在onCreate方法里面注册广播接收�?
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("����������","go...........");
		messageDB = new MessageDB(this);
		//IntentFilter filter = new IntentFilter();
		//filter.addAction(Constants.BACKKEY_ACTION);
		//registerReceiver(backKeyReceiver, filter);
		//mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		//application = (Iokapplication) this.getApplicationContext();
		//application.setmNotificationManager(mNotificationManager);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i("����������","go...........");
		util = new SharePreferenceUtil(getApplicationContext(),
				Constants.SAVE_USER);
		mycon=XmppTool.getConnection();
		if (mycon != null) {
			
			ChatManager cm = XmppTool.getConnection().getChatManager();
			cm.addChatListener(new ChatManagerListener() {
      			public void chatCreated(Chat chat, boolean able) 
      			{
      				chat.addMessageListener(new MessageListener() {
						public void processMessage(Chat arg0,
								org.jivesoftware.smack.packet.Message arg1) {
							// TODO Auto-generated method stub
							String fromUserchat=arg1.getFrom().split("/")[0];
							String fromuser=fromUserchat.split("@")[0];
  							String mess=arg1.getBody();
  							String[] args = new String[] {fromuser,mess};
							Log.i("�����յ���Ϣ��",fromuser);
							if (!fromUserchat.equals(application.getChatuser())){
								ChatMessage entity=new ChatMessage(1,fromuser,mess,TimeRender.getDate(),0,0);
								messageDB.saveUserMsg(application.getUSERID(), entity);
								Message message = handler.obtainMessage();
								message.what = MSG;
								message.obj=args;
								handler.sendMessage(message);
							}
							
						}
      				});
      			}
      		});
		}
	}
	
	
	@Override
	// 在服务被摧毁时，做一些事�?
	public void onDestroy() {
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
		unregisterReceiver(backKeyReceiver);
		mNotificationManager.cancel(Constants.NOTIFY_ID);
		// 给服务器发�?下线消息
		if (mycon!=null) {
			XmppTool.closeConnection();
		}
	}

	/**
	 * 创建通知
	 */
	private void setMsgNotification() {
		int icon = R.drawable.notify;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置�?正在运行"栏目�?
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.notify_view);
		contentView.setTextViewText(R.id.notify_name, util.getName());
		contentView.setTextViewText(R.id.notify_msg, "手机QQ正在后台运行");
		//contentView.setTextViewText(R.id.notify_time, MyDate.getDate());
		// 指定个�?化视�?
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, MainWeixin.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);
	}
}
