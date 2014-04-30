package com.iokokok.util;





import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;

public class ClientConServer {
	private static int PORT=5222;
	private Context context;
	public ClientConServer(Context context){
		this.context=context;

	}
	
	public boolean login(String a,String p){
		ConnectionConfiguration config = new ConnectionConfiguration("221.123.160.28", PORT);
		/** 是否启用安全验证 */
		config.setSASLAuthenticationEnabled(false);
		/** 是否启用调试 */
        //config.setDebuggerEnabled(true);
        /** 创建connection链接 */
		XMPPConnection connection = new XMPPConnection(config);
		try {
			/** 建立连接 */
			connection.connect();
			/** 登录*/
			connection.login(a, p);
			/** 开启读写线程，并加入到管理类中*/
			//ClientSendThread cst=new ClientSendThread(connection);
			//cst.start();
			//ManageClientThread.addClientSendThread(a, cst);
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return false;
	 }
}

