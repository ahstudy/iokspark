package com.iokokok.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
//import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.IBBProviders;
//import org.jivesoftware.smackx.provider.BytestreamsProvider;
//import org.jivesoftware.smackx.provider.IBBProviders;
//import org.jivesoftware.smackx.provider.IBBProviders;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.iok.spark.R;
import com.iokokok.user.SearchMessage;
import com.iokokok.user.UserMessage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;


public class XmppTool {

	private static XMPPConnection con = null;
	public static String SERVER_HOST = "221.123.160.26";//你openfire服务器所在的ip
	public static  String SERVER_NAME = "iokserver2";//设置openfire时的服务器名
	
	private static void openConnection() {
		try {
			if (null == con || !con.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;//开启DEBUG模式
				//配置连接
				ConnectionConfiguration config = new ConnectionConfiguration(
						SERVER_HOST, 5222,
						SERVER_NAME);
				if (Build.VERSION.SDK_INT >= 14) {  
					config.setTruststoreType("AndroidCAStore"); //$NON-NLS-1$  
					config.setTruststorePassword(null);  
					config.setTruststorePath(null);  
			    } else {  
			    	config.setTruststoreType("BKS"); //$NON-NLS-1$  
			        String path = System.getProperty("javax.net.ssl.trustStore"); //$NON-NLS-1$  
			        if (path == null)  
			            path = System.getProperty("java.home") + File.separator //$NON-NLS-1$  
			                    + "etc" + File.separator + "security" //$NON-NLS-1$ //$NON-NLS-2$  
			                    + File.separator + "cacerts.bks"; //$NON-NLS-1$  
			        config.setTruststorePath(path);  
			    }  
			    // connConfig.setSASLAuthenticationEnabled(false);  
				config.setReconnectionAllowed(true);  
				config.setSecurityMode(SecurityMode.disabled); 
				config.setSendPresence(true);
				config.setSASLAuthenticationEnabled(true);
				
				config.setSendPresence(false);
				con = new XMPPConnection(config);
				con.connect();//连接到服务器
				//配置 各种Provider 如果不配置 则会无法解析数据
				configureConnection1(ProviderManager.getInstance());
			}
		}
		catch (XMPPException xe) 
		{
			xe.printStackTrace();
		}
		
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		return con;
	}
	
	public static String getDate() {
        Calendar c = Calendar.getInstance();						
        return (String) DateFormat.format("MM:dd hh:mm:ss",c);//sbBuffer.toString();
    }
	public static void closeConnection() {
		if (con!=null){
			con.disconnect();
			con = null;
		}
	}
    /** 
     * 注册用户 
     * @param connection 
     * @param regUserName 
     * @param regUserPwd 
     * @return 
     */  
    public static boolean createAccount(XMPPConnection connection,String regUserName,String regUserPwd)  
    {  
        try {  
        	AccountManager amgr = connection.getAccountManager();
        	amgr.createAccount(regUserName, regUserPwd);  
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
     
	public static String getUserNick(XMPPConnection connection,String user){
		String usernick=user;
		if (!user.contains("@"))
    		user=user+"@"+SERVER_NAME;
		VCard vcard = new VCard();  
        try {
			vcard.load(connection, user);
			usernick=vcard.getNickName();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
  
		return usernick;
		
	}
    /** 
     * 注册用户 
     * @param connection 
     * @param regUserName 
     * @param regUserPwd 
     * @return 
     */  
    public static boolean registerUser(XMPPConnection connection,String regUserName,String regUserPwd){
    	try {
	    	Registration registration = new Registration();
			registration.setType(IQ.Type.SET);
			registration.setTo(SERVER_NAME);
			registration.setUsername(regUserName.trim());
			registration.setPassword(regUserPwd.trim());
			PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()),new PacketTypeFilter(IQ.class));
			PacketCollector collector = connection.createPacketCollector(filter); 
			connection.sendPacket(registration); 
			Packet response = collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
			collector.cancel();
			if (response == null ||response.getError()!=null) {
	            return false;
	        }
			return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    /** 
     * 删除当前用户 
     * @param connection 
     * @return 
     */  
    public static boolean deleteAccount(XMPPConnection connection)  
    {  
        try {  
            connection.getAccountManager().deleteAccount();  
          
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
      
    /** 
     * 修改密码 
     * @param connection 
     * @return 
     */  
    public static boolean changePassword(XMPPConnection connection,String pwd)  
    {  
        try {  
            connection.getAccountManager().changePassword(pwd);  
          
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
	/** 
     * 返回所有组信息 <RosterGroup> 
     *  
     * @return List(RosterGroup) 
     */  
    public static List<RosterGroup> getGroups(Roster roster) {  
        List<RosterGroup> groupsList = new ArrayList<RosterGroup>();  
        Collection<RosterGroup> rosterGroup = roster.getGroups();  
        Iterator<RosterGroup> i = rosterGroup.iterator();  
        while (i.hasNext())  
            groupsList.add(i.next());  
        return groupsList;  
    }  
    
    /** 
     * 返回相应(groupName)组里的所有用户<RosterEntry> 
     *  
     * @return List(RosterEntry) 
     */  
    public static List<RosterEntry> getEntriesByGroup(Roster roster,  
            String groupName) {  
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();  
        RosterGroup rosterGroup = roster.getGroup(groupName);  
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        while (i.hasNext())  
            EntriesList.add(i.next());  
        return EntriesList;  
    }  
  
    /** 
     * 返回所有用户信息 <RosterEntry> 
     *  
     * @return List(RosterEntry) 
     */  
    public static List<RosterEntry> getAllEntries(Roster roster) {  
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();  
        Collection<RosterEntry> rosterEntry = roster.getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        while (i.hasNext())  
            EntriesList.add(i.next());  
        return EntriesList;  
    }  
    
    /** 
     * 获取用户的vcard信息 
     * @param connection 
     * @param user 
     * @return 
     * @throws XMPPException 
     */  
    public static VCard getUserVCard(XMPPConnection connection,String user) throws XMPPException  
    {  
    	if (!user.contains("@"))
    		user=user+"@"+SERVER_NAME;
        VCard vcard = new VCard();  
        vcard.load(connection, user);  
        //System.out.println(vcard.getField("sex"));
        //System.out.println(vcard.getField("DESC"));
        //System.out.println(vcard.getEmailHome());
        //System.out.println(vcard.getOrganization());
        //System.out.println(vcard.getNickName());
        //System.out.println(vcard.getPhoneWork("PHONE"));
        //System.out.println(vcard.getProperty("DESC"));
        //System.out.println(vcard.getAvatar());
        return vcard;  
    }  
  
    
    public static void setUserSex(XMPPConnection connection,String sex) throws XMPPException{
    	VCard vCard = new VCard();
        vCard.load(connection);
        vCard.setField("sex", sex);
        if (vCard.getAvatar()!=null){
 	       byte[] bytes=vCard.getAvatar();
 	       String encodedImage = StringUtils.encodeBase64(bytes);  
 	       vCard.setAvatar(bytes, encodedImage);  
 	       vCard.setEncodedImage(encodedImage);  
 	       vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"  
 	               + encodedImage + "</BINVAL>", true); 
        }
        vCard.save(connection);
    }
    public static void setUserVCard(XMPPConnection connection)
    		   throws XMPPException {
    	VCard vCard = new VCard();
        vCard.load(connection);
        vCard.setEmailHome("lulu@sina.com");
    	vCard.setOrganization("售后");
        vCard.setNickName("颜昌军t2");
    	vCard.setField("sex", "男");
        vCard.setPhoneWork("PHONE", "3443233");
    	vCard.setField("DESC", "描述信息。。。");
    	// vCard.setAvatar(vCard.getAvatar());
    	vCard.save(connection);
    	System.out.println("添加成功");
    }
    /** 
     * 获取用户头像信息 
      */ 
    public static Bitmap getUserImage(String user) {  
    	Bitmap ic = null;  
        try {  
            System.out.println("获取用户头像信息: "+user);  
            if (!user.contains("@"))
        		user=user+"@"+SERVER_NAME;
            VCard vcard = new VCard();  
            vcard.load(con, user);  
              
            if(vcard == null || vcard.getAvatar() == null)  
            {  
                return null;  
            }  
            ByteArrayInputStream bais = new ByteArrayInputStream(  
                    vcard.getAvatar());  
            ic =BitmapFactory.decodeStream(bais, null,null);
            //Bitmap image = ImageIO.read(bais.);  
      
              
            //ic = new Bitmap(image);  
            System.out.println("图片大小:"+ic.getHeight()+" "+ic.getWidth());  
          
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return ic;  
    }  
    
    /** 
     * 添加一个组 
     */  
    public static boolean addGroup(Roster roster,String groupName)  
    {  
        try {  
            roster.createGroup(groupName);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 删除一个组 
     */  
    public static boolean removeGroup(Roster roster,String groupName)  
    {  
        return false;  
    }  
      
    /** 
     * 添加一个好友  无分组 
     */  
    public static boolean addUser(Roster roster,String userName,String name)  
    {  
        try {  
            roster.createEntry(userName, name, new String[]{"我行网"});  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
    /** 
     * 添加一个好友到分组 
     * @param roster 
     * @param userName 
     * @param name 
     * @return 
     */  
    public static boolean addUser(Roster roster,String userName,String name,String groupName)  
    {  
        try {  
            roster.createEntry(userName, name,new String[]{ groupName});  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 删除一个好友 
     * @param roster 
     * @param userName 
     * @return 
     */  
    public static boolean removeUser(Roster roster,String userName)  
    {  
        try {  
              
            //if(userName.contains("@"))  
            //{  
            //    userName = userName.split("@")[0];  
            //}  
            RosterEntry entry = roster.getEntry(userName);  
            System.out.println("删除好友:"+userName);  
            System.out.println("User: "+(roster.getEntry(userName) == null));  
            roster.removeEntry(entry);  
              
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
          
    }
    /** 
    * 修改心情 
    * @param connection 
    * @param status 
    */  
   public static void changeStateMessage(XMPPConnection connection,String status)  
   {  
       Presence presence = new Presence(Presence.Type.available);
       presence.setStatus(status);  
       connection.sendPacket(presence);  
     
   }  
    
   /** 
    * 获取心情 
    * @param connection 
    * @param status 
    */  
   public static String getStateMessage()  
   {  
	   String curstate="";
	   try {
	       Presence presence = new Presence(Presence.Type.available);
	       curstate=presence.getStatus().toString();  
	       System.out.print(curstate);
	   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
		}
       return curstate;
       //connection.sendPacket(presence);  
     
   }  
   //修改用户昵称
   
   public static void changeNickName(XMPPConnection connection,String nickname) throws XMPPException{
	   VCard vcard = new VCard();  
       vcard.load(connection);
       vcard.setNickName(nickname);
       if (vcard.getAvatar()!=null){
	       byte[] bytes=vcard.getAvatar();
	       String encodedImage = StringUtils.encodeBase64(bytes);  
	       vcard.setAvatar(bytes, encodedImage);  
	       vcard.setEncodedImage(encodedImage);  
	       vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"  
	               + encodedImage + "</BINVAL>", true); 
       }
       vcard.save(connection);
   }
   //修改用户头像
   public static void changeImage(XMPPConnection connection,File f) throws XMPPException, IOException  
   {  
     
       VCard vcard = new VCard();  
       vcard.load(connection);  
         
           byte[] bytes;  
           
               bytes = getFileBytes(f);  
               String encodedImage = StringUtils.encodeBase64(bytes);  
               vcard.setAvatar(bytes, encodedImage);  
               vcard.setEncodedImage(encodedImage);  
               vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"  
                       + encodedImage + "</BINVAL>", true);  
                 
                 
               ByteArrayInputStream bais = new ByteArrayInputStream(  
                       vcard.getAvatar());  
              // Image image = ImageIO.read(bais);  
               //ImageIcon ic = new ImageIcon(image);  
                  
            
           
           vcard.save(connection);  
            
   }  
     
   private static byte[] getFileBytes(File file) throws IOException {  
       BufferedInputStream bis = null;  
       try {  
           bis = new BufferedInputStream(new FileInputStream(file));  
           int bytes = (int) file.length();  
           byte[] buffer = new byte[bytes];  
           int readBytes = bis.read(buffer);  
           if (readBytes != buffer.length) {  
               throw new IOException("Entire file not read");  
           }  
           return buffer;  
       } finally {  
           if (bis != null) {  
               bis.close();  
           }  
       }  
   } 
   
   
   //用户查询
   public static List<SearchMessage>  searchUsers(XMPPConnection connection,String userName) throws XMPPException  
   {  
       List<SearchMessage> results = new ArrayList<SearchMessage>();  
       System.out.println("查询开始..............."+connection.getHost()+connection.getServiceName());  
         
       UserSearchManager usm = new UserSearchManager(connection);  
         
         
       Form searchForm = usm.getSearchForm("search."+SERVER_NAME);  
       Form answerForm = searchForm.createAnswerForm();  
       answerForm.setAnswer("Username", true);  
       answerForm.setAnswer("Name", true);
       answerForm.setAnswer("search", userName);  
       ReportedData data = usm.getSearchResults(answerForm, "search."+SERVER_NAME); 
       Iterator<Row> it = data.getRows();  
       Row row = null;  
       
       Roster roster=XmppTool.getConnection().getRoster();
       List<RosterEntry> noroster=XmppTool.getAllEntries(roster);
       while(it.hasNext())  
       {  
           row = it.next(); 
           boolean isjid=false;
           String curUser=XmppTool.getConnection().getUser().split("@")[0];
           for (RosterEntry rosterEntry:noroster) {
        	   if(rosterEntry.getUser().equals(row.getValues("jid").next().toString())){
        		   Log.i("rosterjid",rosterEntry.getUser());
        		   isjid=true; 
	           }
           }
           if (isjid==false &&!row.getValues("username").next().toString().equals(curUser)){
        	   Bitmap userhead=XmppTool.getUserImage(row.getValues("jid").next().toString());
	    	   SearchMessage usermessage=new SearchMessage(row.getValues("username").next().toString(),
	    			   row.getValues("name").next().toString(),
	    			   row.getValues("jid").next().toString(),userhead);
	    	   Log.i("tjid",row.getValues("jid").next().toString());
	    	   results.add(usermessage);
           }
        }  
          
        return results;  
   }  
   
   /**
    * 判断openfire用户的状态
    *     strUrl : url格式 - http://my.openfire.com:9090/plugins/presence/status?jid=user1@my.openfire.com&type=xml
    *    返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
    *  说明   ：必须要求 openfire加载 presence 插件，同时设置任何人都可以访问
    */   
   public  static short IsUserOnLine(String user)
   {
       short            shOnLineState    = 0;    //-不存在-
       String strUrl="http://"+SERVER_HOST+":9090/plugins/presence/status?jid="+user+"&type=xml";
       try
       {
           URL             oUrl     = new URL(strUrl);
       URLConnection     oConn     = oUrl.openConnection();
       if(oConn!=null)
       {
           BufferedReader     oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
           if(null!=oIn)
           {
               String strFlag = oIn.readLine();
               oIn.close();
              
               if(strFlag.indexOf("type=\"unavailable\"")>=0)
               {
                   shOnLineState = 2;
               }
               if(strFlag.indexOf("type=\"error\"")>=0)
               {
                   shOnLineState = 0;
               }
               else if(strFlag.indexOf("priority")>=0 || strFlag.indexOf("id=\"")>=0)
               {
                   shOnLineState = 1;
               }
           }
       }
       }
       catch(Exception e)
       {           
       }
      
       return     shOnLineState;
   } 

  
   
   /**
	 * xmpp配置
	 */
	private static void configureConnection1(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",new GroupChatInvitation.Provider());
		// Service Discovery # Items //解析房间列表
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());
		// Service Discovery # Info //某一个房间的信息
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses","http://jabber.org/protocol/address",new MultipleAddressesProvider());
		pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());
		//pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",new BytestreamsProvider());
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.SessionExpiredError());
	}
   /**
	 * xmpp配置
	 * @param pm
	 * @author by_wsc
	 * @email wscnydx@gmail.com
	 * @date 日期：2013-4-15 时间：下午11:02:20
	 */
	private static void configureConnection(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (Exception e) {
			e.printStackTrace();
			// Logs.v(TAG,
			// "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items //解析房间列表
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info //某一个房间的信息
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());
		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());

		 pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
		 new IBBProviders.Open());
		
		 pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
		 new IBBProviders.Close());
		
		 pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
		 new IBBProviders.Data());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}
    
}
