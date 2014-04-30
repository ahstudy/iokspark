package com.iokokok.app;

public class Constants {
	public static final String SERVER_IP = "221.123.160.26";
	public static final int SERVER_PORT = 5222;
	public static  String SERVER_NAME = "iokserver2";
	public static final int REGISTER_FAIL = 0;
	public static final String ACTION = "com.iok.message";
	public static final String MSGKEY = "message";
	public static final String IP_PORT = "ipPort";
	public static final String SAVE_USER = "saveUser";
	public static final String BACKKEY_ACTION="com.iok.backKey";
	public static final int NOTIFY_ID = 0x911;
	public static final String DBNAME = "iokspark";
	
	 // 应用的key 请到官方申请正式的appkey替换APP_KEY
    public static final String APP_KEY      = "3253137034";

    // 替换为开发者REDIRECT_URL
    public static final String REDIRECT_URL = "http://www.iokokok.com";
    public static final String SINA_BASE_URL="https://api.weibo.com/2/";
    // 新支持scope：支持传入多个scope权限，用逗号分隔
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    public static final String CLIENT_ID         = "client_id";
    public static final String RESPONSE_TYPE     = "response_type";
    public static final String USER_REDIRECT_URL = "redirect_uri";
    public static final String DISPLAY           = "display";
    public static final String USER_SCOPE        = "scope";
    public static final String PACKAGE_NAME      = "packagename";
    public static final String KEY_HASH          = "key_hash";
}
