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
		/** �Ƿ����ð�ȫ��֤ */
		config.setSASLAuthenticationEnabled(false);
		/** �Ƿ����õ��� */
        //config.setDebuggerEnabled(true);
        /** ����connection���� */
		XMPPConnection connection = new XMPPConnection(config);
		try {
			/** �������� */
			connection.connect();
			/** ��¼*/
			connection.login(a, p);
			/** ������д�̣߳������뵽��������*/
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

