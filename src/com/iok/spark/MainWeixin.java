package com.iok.spark;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.iok.spark.BaiduMapActivity.MyLocationListenner;
import com.iokokok.adapter.FriendsViewAdapter;
import com.iokokok.app.Constants;
import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.user.ChatMessage;
import com.iokokok.user.FriendMessage;
import com.iokokok.util.BMapUtil;
import com.iokokok.util.MessageDB;
import com.iokokok.util.Msg;
import com.iokokok.util.PhpHttpUtil;
import com.iokokok.util.TimeRender;
import com.iokokok.util.XmppTool;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.AccessTokenKeeper;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MainWeixin extends Activity {
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	private String myaddress ="";
	public MyLocationListenner myListener = new MyLocationListenner();
	
	public static MainWeixin instance = null;
	 
	private ViewPager mTabPager;	
	private TextView LoadTxt;
	private ImageView mTabImg,LoadImg;// 动画图片
	private ImageView mTab1,mTab2,mTab3,mTab4;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;//单个水平动画位移
	private int two;
	private int three;
	private LinearLayout mClose;
    private LinearLayout mCloseBtn;
    private View layout;	
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	private List<Map<String, Object>> listdata;
	private List<Map<String, Object>> newslistdata;
	List<Map<String, String>> groupData ;
    List<List<Map<String, Object>>> childData ;
	private ListView listView;
	private ListView newslistView;
	private ExpandableListView exlist;
	private SimpleAdapter listAdapter;
	private SimpleAdapter newslistAdapter;
	private HashMap<String, Object> listmap;
	private HashMap<String, Object> newslistmap;
	public static String pUSERID;//当前用户
	public static String pUSERNICK;//当前用户昵称
	public ProgressDialog dialog ; 
	private Roster roster ;
	private Iokapplication myapplication;
    private MessageDB messageDB;
    /** 微博API接口类，提供登陆等功能  */
    private Weibo mWeibo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
	//private Button mRightBtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_weixin);
        myapplication=(Iokapplication)this.getApplicationContext();
        pUSERID = myapplication.getUSERID();//getIntent().getStringExtra("USERID");
        pUSERNICK=myapplication.getUSERNICK();
        messageDB=myapplication.getMyDB();
         //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        instance = this;

      //定位初始化
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
        //Intent service = new Intent(this, GetMsgService.class);
		//startService(service);
		//XmppTool.getAllEntries(XmppTool.getConnection().getRoster());
		/*
        mRightBtn = (Button) findViewById(R.id.right_btn);
        mRightBtn.setOnClickListener(new Button.OnClickListener()
		{	@Override
			public void onClick(View v)
			{	showPopupWindow (MainWeixin.this,mRightBtn);
			}
		  });*/
		LoadTxt=(TextView)findViewById(R.id.txtloading);
		LoadImg=(ImageView)findViewById(R.id.imgloading);
        mTabPager = (ViewPager)findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mTab1 = (ImageView) findViewById(R.id.img_weixin);
        mTab2 = (ImageView) findViewById(R.id.img_address);
        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_settings);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth/4; //设置水平动画平移大小
        two = one*2;
        three = one*3;
        //Log.i("info", "获取的屏幕分辨率为" + one + two + three + "X" + displayHeight);
        IntentFilter filters = new IntentFilter(); 
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateReceiver mReceiver=new mNetworkStateReceiver();
        registerReceiver(mReceiver, filters);
        //InitImageView();//使用动画
      //将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.main_tab_weixin, null);
        View view2 = mLi.inflate(R.layout.main_tab_address, null);
        View view3 = mLi.inflate(R.layout.main_tab_friends, null);
        View view4 = mLi.inflate(R.layout.main_tab_settings, null);
        
      //每个页面的view数据
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
      //填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			//@Override
			//public CharSequence getPageTitle(int position) {
				//return titles.get(position);
			//}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));	
				if (position==0){
					LoadNews();
				}
				return views.get(position);
			}
		};
		mTabPager.setAdapter(mPagerAdapter);
		
		//LoadNews();
		/*
		//添加用户 添加好友或删除好友的监听
		PacketFilter filter = new PacketTypeFilter(Presence.class);
		PacketListener mymesListener = new PacketListener() {

			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				Log.i("PacketListener","PacketListener is loading......");
				Presence presence = (Presence) packet;
		        String from = presence.getFrom();    
                if (presence.getType().equals(Presence.Type.subscribe)) {      
                	android.os.Message msg = new android.os.Message();      
                    msg.what = 4;  
                    Bundle data = new Bundle();      
                    data.putString("from", from);      
                    //Log.i(ContactManager.class.getCanonicalName(), "from:"+from);      
                    msg.setData(data);      
                    handler.sendMessage(msg);      
                } else if (presence.getType().equals(Presence.Type.unsubscribe)) {      
                	android.os.Message msg = new android.os.Message();      
                    msg.what = 5;         
                    Bundle data = new Bundle();      
                    data.putString("from", from);      
                    msg.setData(data);      
                    handler.sendMessage(msg);      
                }      
                //Log.i(ContactManager.class.getCanonicalName(), "type:" + presence.getType());      
			}     
            
        }; 
        XmppTool.getConnection().addPacketListener(mymesListener, filter);
        /*/
        //getUserOffinvest();
        getOfflinMessage(pUSERID);
        
        
		mTabPager.setCurrentItem(0);
		
    }
    protected void onDestory(){
    	myapplication.setChatuser("bbb");
    	Log.i("curuserchat1", myapplication.getChatuser());
    }
    
    public void setLocOption(){
	    LocationClientOption option = new LocationClientOption();
	    option.setOpenGps(true);
	    option.setAddrType("all");//返回的定位结果包含地址信息
	    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	    option.setScanSpan(2000);//设置发起定位请求的间隔时间为5000ms
	    option.disableCache(true);//禁止启用缓存定位
	    option.setPoiNumber(5); //最多返回POI个数
	    option.setPoiDistance(1000); //poi查询距离
	    option.setPoiExtraInfo(false); //是否需要POI的电话和地址等详细信息
	    option.setPriority(LocationClientOption.NetWorkFirst);
	    mLocClient.setLocOption(option);
	}
    
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            
            //locData.accuracy = location.getRadius();
            //locData.direction = location.getDerect();
            myaddress=location.getAddrStr();
            if (location!=null){
				Log.d("GPSLONG1", "Longitude: " + location.getLongitude());
				Log.d("GPSLAT1", "Latitude: " + location.getLatitude());
				String longitudestr=String.valueOf(location.getLongitude());
				String latitudestr=String.valueOf(location.getLatitude());
				myapplication.setLatitude(latitudestr);
				myapplication.setLongitude(longitudestr);
				//将用户位置坐标上传到服务器
				RequestParams params = new RequestParams();
				params.put("username", myapplication.getUSERID());
				params.put("usernick",myapplication.getUSERNICK());
				params.put("longitude",longitudestr);
				params.put("latitude", latitudestr);
				params.put("myaddress", myaddress);
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(20000);
				client.post("http://spark.iokokok.com/ioksparkService/index.php/Iokspark/uploadloc", params, new AsyncHttpResponseHandler(){
					@Override  
					public void onSuccess(String response) {  
					    System.out.println(response);  
					    //handler.sendEmptyMessage(2);
						
					}  
				});
			}
            //Toast.makeText(MainWeixin.this, "您当前的位置为："+myaddress, Toast.LENGTH_LONG).show();
            
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    /**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};
    
	 /* 页卡切换监听(原作者:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {		
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_pressed));
				//加载我行网新闻
				//LoadNews();
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 1://好友列表
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_pressed));
				getUserFriendList();
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				break;
			}
			
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		
		public void onPageScrollStateChanged(int arg0) {
		}
	}

    //获取并加载我行网新闻
    public void LoadNews(){
    	//LoadTxt.setVisibility(LoadTxt.VISIBLE);
    	//LoadImg.setVisibility(LoadImg.VISIBLE);
    	new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				newslistView=(ListView)MainWeixin.this.findViewById(R.id.newslist);
				newslistdata=new ArrayList<Map<String,Object>>();
				newslistmap=new HashMap<String, Object>();
				newslistmap.put("type", "news");
				newslistmap.put("pic", R.drawable .icon);
				newslistmap.put("title","EMS顺丰速空战升级");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44661");
				//listmap.put("title","我行网新闻标题");
				newslistdata.add(newslistmap);
				newslistmap=new HashMap<String, Object>();
				newslistmap.put("type", "news");
				newslistmap.put("pic", R.drawable .icon);
				newslistmap.put("title","家居展会风光不再");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44660");
				//listmap.put("title","我行网新闻标题");
				newslistdata.add(newslistmap);
				newslistmap=new HashMap<String, Object>();
				newslistmap.put("type", "news");
				newslistmap.put("pic", R.drawable .icon);
				newslistmap.put("title","中国乳业需破解奶源之困");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44659");
				//listmap.put("title","我行网新闻标题");
				newslistdata.add(newslistmap);
				newslistAdapter=new SimpleAdapter(MainWeixin.this, newslistdata, R.layout.newsitem, new String[]{"title","pic"}, new int[]{R.id.newstitle,R.id.head});
				//newslistAdapter.setViewBinder(new ListViewBinder()); 
				newslistView.setAdapter(newslistAdapter); 
				newslistView.setOnItemClickListener(new OnItemClickListener(){

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						String type=(String)newslistdata.get(arg2).get("type");
						if (type.equals("news")){
							Intent intent = new Intent (MainWeixin.this,NewsContentActivity.class);
							intent.putExtra("title",  (String)newslistdata.get(arg2).get("title"));
							intent.putExtra("url", (String)newslistdata.get(arg2).get("url"));
							startActivity(intent);	
						}else if (type.equals("mess")){
							
						}else if (type.equals("notice")){
							
						}
					}
				});
				//handler.sendEmptyMessage(6);
			}
    		
    	}).start();
    }
	//加载好友列表
	public void getUserFriendList(){
		new Thread(new Runnable(){   
			
		    public void run(){   
		    	handler.sendEmptyMessage(1);
		     // TODO Auto-generated method stub
				exlist=(ExpandableListView)MainWeixin.this.findViewById(R.id.exlist);
				listdata=new ArrayList<Map<String,Object>>();
				groupData= new ArrayList<Map<String, String>>();
				childData = new ArrayList<List<Map<String, Object>>>();
				roster=XmppTool.getConnection().getRoster();
				String curgroup="";
				Collection<RosterGroup> entriesGroup = roster.getGroups();
				Map<String, String> curGroupMap = new HashMap<String, String>();
	            groupData.add(curGroupMap);
	            curGroupMap.put("groupname", "等待通过");
	            curGroupMap = new HashMap<String, String>();
	            groupData.add(curGroupMap);
	            curGroupMap.put("groupname", "拒绝通过");
	            List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
                childData.add(children);
                children = new ArrayList<Map<String, Object>>();
                childData.add(children);
                boolean isadd=false;
	            List<RosterEntry> noroster=XmppTool.getAllEntries(roster);
	            for (RosterEntry rosterEntry:noroster) {
	            	if (rosterEntry.getStatus()==null && rosterEntry.getType().name().equals("from")){
	            		if (isadd==false){
		            		curGroupMap = new HashMap<String, String>();
		    	            groupData.add(curGroupMap);
		    	            curGroupMap.put("groupname", "等待验证");
		    	            isadd=true;
	            		}
	            		//FriendMessage myfriend=new FriendMessage();
	            		children = new ArrayList<Map<String, Object>>();
	            		listmap=new HashMap<String, Object>();
	                    children.add(listmap);
	                    String fromuser=rosterEntry.getUser();
	                    listmap.put("username", fromuser.split("@")[0]);
	                    VCard mycard = null;
						try {
							mycard = XmppTool.getUserVCard(XmppTool.getConnection(), fromuser);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (mycard==null||mycard.getNickName().equals("")){
							listmap.put("usernick", fromuser.split("@"));
						}else{
							listmap.put("usernick", mycard.getNickName());
							//myfriend.setUsernick(mycard.getNickName());
						}
	                    //myfriend.setUsername(rosterEntry.getName());
	                    String offmess="离线信息0个";
						listmap.put("offcount",offmess);
						Presence availability = roster.getPresence(rosterEntry.getUser());
						
						//Mode userMode = availability.getMode();
						if (availability.isAvailable()){
							listmap.put("status", "在线");
						}else{
							listmap.put("status", " 离线");
						}
						//listmap.put("usernick", rosterEntry.getName());
						//myfriend.setUsernick(rosterEntry.getName());
						Bitmap mybit=XmppTool.getUserImage(fromuser);
						if (mybit==null){
							Resources res=getResources();
							mybit=BitmapFactory.decodeResource(res, R.drawable.usernohead);
						}else{
							
							byte[] bytes = null; 
							final ByteArrayOutputStream os = new ByteArrayOutputStream();
							// 将Bitmap压缩成PNG编码，质量为100%存储
							mybit.compress(Bitmap.CompressFormat.PNG, 100, os);//除了PNG还有很多常见格式，如jpeg等。
							
						}
						listmap.put("userhead",mybit);
						//myfriend.setUserhead("");
						listmap.put("usergroup", "等待验证");
						//myfriend.setUsergroup("等待验证");
						listmap.put("lastmsg","");
						listmap.put("lastdata","");
						//myfriend.setLastmsg("");
						//myfriend.setLastdata("");
						//messageDB.saveUserFriend(pUSERID, myfriend);
						listmap.put("usertype", "check");
	                    childData.add(children);
	            	}
	            }
				for(RosterGroup group: entriesGroup){
					curgroup=group.getName();
					curGroupMap = new HashMap<String, String>();
		            groupData.add(curGroupMap);
		            curGroupMap.put("groupname", curgroup);		              
		            children = new ArrayList<Map<String, Object>>();				
					Log.i("---", group.getName());
					Collection<RosterEntry> it=group.getEntries(); 
					for (RosterEntry rosterEntry:it) {	
						FriendMessage myfriend=new FriendMessage();
	                    listmap=new HashMap<String, Object>();
	                    children.add(listmap);
						listmap.put("username", rosterEntry.getName());
						myfriend.setUsername(rosterEntry.getName());
						int offmesscount=messageDB.getUserOffCount(pUSERID, rosterEntry.getName());
						String offmess="离线信息"+offmesscount+"个";
						listmap.put("offcount",offmess);
						VCard mycard = null;
						try {
							mycard = XmppTool.getUserVCard(XmppTool.getConnection(), rosterEntry.getName()+"@"+XmppTool.SERVER_NAME);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (mycard==null||mycard.getNickName().equals("")){
							listmap.put("usernick", rosterEntry.getName());
						}else{
							listmap.put("usernick", mycard.getNickName());
							myfriend.setUsernick(mycard.getNickName());
						}
						Presence availability = roster.getPresence(rosterEntry.getUser());
						
						//Mode userMode = availability.getMode();
						if (availability.isAvailable()){
							listmap.put("status", "在线");
						}else{
							listmap.put("status", " 离线");
						}
						Bitmap mybit=XmppTool.getUserImage(rosterEntry.getName()+"@"+XmppTool.SERVER_NAME);
						if (mybit==null){
							Resources res=getResources();
							mybit=BitmapFactory.decodeResource(res, R.drawable.usernohead);
							myfriend.setUserhead("");
						}else{
							
							byte[] bytes = null; 
							final ByteArrayOutputStream os = new ByteArrayOutputStream();
							// 将Bitmap压缩成PNG编码，质量为100%存储
							mybit.compress(Bitmap.CompressFormat.PNG, 100, os);//除了PNG还有很多常见格式，如jpeg等。
							bytes= os.toByteArray();
				            String encodedImage = StringUtils.encodeBase64(bytes);
				            myfriend.setUserhead(encodedImage);
						}
						listmap.put("userhead",mybit);
						listmap.put("usergroup", curgroup);
						myfriend.setUsergroup(curgroup);
						ChatMessage userlastmsg=messageDB.getLastMsg(pUSERID, rosterEntry.getName());
						if (userlastmsg!=null){
							Msg mymsg=Msg.analyseMsgBody(userlastmsg.getmsg());
							listmap.put("lastmsg", mymsg.getMsg());
							myfriend.setLastmsg(userlastmsg.getmsg());
							listmap.put("lastdata",mymsg.getDate());
							myfriend.setLastdata(userlastmsg.getData());
						}
						listmap.put("usertype", "friend");
						messageDB.saveUserFriend(pUSERID, myfriend);
						if (rosterEntry.getType()!=null&&rosterEntry.getType().name().equals("both")){
							childData.add(children);
						}else if (rosterEntry.getType()!=null&&rosterEntry.getType().name().equals("to")){
							childData.add(children);
							children.remove(listmap);
							listmap.put("usertype", "invest");
							childData.get(0).add(listmap);
						}else{
							childData.add(children);
							if (rosterEntry.getStatus()!=null && rosterEntry.getStatus().toString().equals("subscribe")){
								children.remove(listmap);
								listmap.put("usertype", "invest");
								childData.get(0).add(listmap);
							
							}else{
								children.remove(listmap);
								listmap.put("usertype", "back");
								childData.get(1).add(listmap);
							}
							
						}
						
					}
				}
				
				handler.sendEmptyMessage(2);
		         //告诉主线程执行任务   
		    }   

		}).start();
	}
	
	
	/** 未处理、验证好友，添加过的好友，没有得到对方同意 */
	public void getUserOffinvest(){
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				Roster roster = XmppTool.getConnection().getRoster();
		        Collection<RosterEntry> unfiledEntries = roster.getUnfiledEntries();
		        Iterator<RosterEntry> iter = unfiledEntries.iterator();
		        while (iter.hasNext()) {
		            RosterEntry entry = iter.next();
		            if (entry.getType().equals("from")) {      
		            	android.os.Message msg = new android.os.Message();      
		                msg.what = 4;  
		                Bundle data = new Bundle();      
		                data.putString("from", entry.getUser());      
		                //Log.i(ContactManager.class.getCanonicalName(), "from:"+from);      
		                msg.setData(data);      
		                handler.sendMessage(msg);      
		            } else  {      
		            	android.os.Message msg = new android.os.Message();      
		                msg.what = 5;         
		                Bundle data = new Bundle();      
		                data.putString("from", entry.getUser());      
		                msg.setData(data);      
		                handler.sendMessage(msg);      
		            }      
		            fail("Groups: {0}, Name: {1}, Status: {2}, Type: {3}, User: {4}", entry.getGroups(), entry.getName(), entry.getStatus(), entry.getType(), entry);
		        }
			}
			
		}).start();
		
	}
	
	//接收闻线信息
  	public void getOfflinMessage(final String username){
  		new Thread(new Runnable(){

  			public void run() {
  				// TODO Auto-generated method stub
  				//此处获取当前用户的聊天记录和未读记录
  		        OfflineMessageManager offlineManager = new OfflineMessageManager(XmppTool.getConnection());  
  		        try {  
  		            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager.getMessages();  
  		            
  		            //System.out.println(offlineManager.supportsFlexibleRetrieval());   
  		           String messbody="";
  		           String fromuserstring="";
  		           String messcontent="";
  		           int offmessCount=0;
  		         String formusernick="";
  		           while(it.hasNext()){
  		        	   offmessCount+=1;
  		                org.jivesoftware.smack.packet.Message message = it.next();
  		                String fromUser = message.getFrom().split("@")[0]; 
  		                fromuserstring=fromUser;
  		                Log.i("Message:", "收到离线消息, Received from 【" + fromUser + "】 message: " + message.getBody());
  		                messbody=message.getBody();
  		                Msg msg=Msg.analyseMsgBody(messbody);
  		                messcontent=msg.getMsg();
  		                formusernick=XmppTool.getUserNick(XmppTool.getConnection(), fromUser+"@"+XmppTool.SERVER_NAME);
   		                ChatMessage entity=new ChatMessage(1,fromUser,messbody,TimeRender.getDate(),0,0);
  		                messageDB.saveUserMsg(username, entity);
  		                //Log.i("Message:", "收到离线消息, Received from 【" + fromUser + "】 message: " + message.getBody());
  		            }
  		           if (offmessCount>0){
  		        	   
  		        	   if (offmessCount==1){
  		        		 //定义NotificationManager
  		        	        String ns = Context.NOTIFICATION_SERVICE;
  		        	        NotificationManager mNotificationManager = (NotificationManager)MainWeixin.this. getSystemService(ns);
  		        	        //定义通知栏展现的内容信息
  		        	        int icon = R.drawable.icon;
  		        	        CharSequence tickerText = "你有新的消息";
  		        	        long when = System.currentTimeMillis();
  		        	        Notification notification = new Notification(icon, tickerText, when);
  		        	         
  		        	        //定义下拉通知栏时要展现的内容信息
  		        	        Context context = MainWeixin.this.getApplicationContext();
  		        	        CharSequence contentTitle = formusernick;
  		        	        CharSequence contentText =messcontent;
  		        	        Intent notificationIntent = new Intent(MainWeixin.this, ChatActivity.class);
  		        	        notificationIntent.putExtra("user",  fromuserstring+"@"+XmppTool.SERVER_NAME);
  		        	        notificationIntent.putExtra("TOUSERID", fromuserstring);
  		        	        notificationIntent.putExtra("TOUSERNICK", formusernick);
  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
  		        	                notificationIntent, 0);
  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
  		        	                contentIntent);
  		        	         
  		        	        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
  		        	        mNotificationManager.notify(1, notification);
  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
		        	        music.start();
  		        	   }else{
  		        		 //定义NotificationManager
  		        	        String ns = Context.NOTIFICATION_SERVICE;
  		        	        NotificationManager mNotificationManager = (NotificationManager) MainWeixin.this.getSystemService(ns);
  		        	        //定义通知栏展现的内容信息
  		        	        int icon = R.drawable.icon;
  		        	        CharSequence tickerText = "你有新的消息";
  		        	        long when = System.currentTimeMillis();
  		        	        Notification notification = new Notification(icon, tickerText, when);
  		        	         
  		        	        //定义下拉通知栏时要展现的内容信息
  		        	        Context context = MainWeixin.this.getApplicationContext();
  		        	        CharSequence contentTitle = "你的离线信息总数："+offmessCount;
  		        	        CharSequence contentText = "";
  		        	        Intent notificationIntent = new Intent(MainWeixin.this, MainWeixin.class);
  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
  		        	                notificationIntent, 0);
  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
  		        	                contentIntent);
  		        	         
  		        	        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
  		        	        mNotificationManager.notify(1, notification);
  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
  		        	        music.start();
  		        	   }
  		           }
  		              
  		            

  		            offlineManager.deleteMessages();  
  		        //状态
					Presence presence = new Presence(Presence.Type.available);
					XmppTool.getConnection().sendPacket(presence);
					handler.sendEmptyMessage(7);
  		        } catch (Exception e) {  
  		            e.printStackTrace();  
  		        }  
  			}
  			
  		}).start();
  			
  	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
        	if(menu_display){         //如果 Menu已经打开 ，先关闭Menu
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
        		Intent intent = new Intent();
            	intent.setClass(MainWeixin.this,Exit.class);
            	startActivity(intent);
        	}
    	}
    	
    	else if(keyCode == KeyEvent.KEYCODE_MENU){   //获取 Menu键			
			if(!menu_display){
				//获取LayoutInflater实例
				inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				//这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
				//该方法返回的是一个View的对象，是布局中的根
				layout = inflater.inflate(R.layout.main_menu, null);
				
				//下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单
				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); //后两个参数是width和height
				//menuWindow.showAsDropDown(layout); //设置弹出效果
				//menuWindow.showAsDropDown(null, 0, layout.getHeight());
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
				//如何获取我们main中的控件呢？也很简单
				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
				
				
				//下面对每一个Layout进行单击事件的注册吧。。。
				//比如单击某个MenuItem的时候，他的背景色改变
				//事先准备好一些背景图片或者颜色
				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
					
					public void onClick(View arg0) {						
						//Toast.makeText(Main.this, "退出", Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
			        	intent.setClass(MainWeixin.this,Exit.class);
			        	startActivity(intent);
			        	menuWindow.dismiss(); //响应点击事件之后关闭Menu
					}
				});				
				menu_display = true;				
			}else{
				//如果当前已经为显示状态，则隐藏起来
				menuWindow.dismiss();
				menu_display = false;
				}
			
			return false;
		}
    	return false;
    }
	
	//设置标题栏右侧按钮的作用
	public void btnmainright(View v) {  
		Intent intent = new Intent (MainWeixin.this,MainTopRightDialog.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "点击了功能按钮", Toast.LENGTH_LONG).show();
      }  	
	public void startchat(View v) {      //小黑  对话界面
		Intent intent = new Intent (MainWeixin.this,ChatActivity.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
      } 
	//显示用户详细信息
	public void showinfo(View v){
		Intent intent = new Intent (MainWeixin.this,InfoXiaohei.class);
		
		startActivity(intent);	
	}
	//修改密码
	public void edit_pass(View v){
		Intent intent = new Intent (MainWeixin.this,EditPassActivity.class);			
		startActivity(intent);	
	}
	//新浪微博管理
	public void edit_sina(View v){
		RequestParams params=new RequestParams();
		params.put("username", myapplication.getUSERID());
		String url=PhpHttpUtil.SERVICE_HOST+"index.php/Sina/getuser/";
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler(){
			@Override  
			public void onSuccess(String response) {
				System.out.println(response);
				try {
					JSONObject result=new JSONObject(response);
					int state=result.getInt("state");
					if (state==1){
						JSONArray user=result.getJSONArray("result");
						JSONObject obj=user.getJSONObject(0);
						myapplication.setSinaToken(obj.getString("token"));
						//用户已经绑定微博
						Intent intent =new Intent(MainWeixin.this,SinaWeiboActivity.class);
						//intent.putExtra("token", myapplication.getSinaToken());
						MainWeixin.this.startActivity(intent);
					}else{
						//未完成微博绑定
						mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
						mWeibo.anthorize(MainWeixin.this, new AuthDialogListener());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
            
            final String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            final String  uid=values.getString("uid");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(mAccessToken.getExpiresTime()));
               // mText.setText("认证成功: \r\n access_token: " + token + "\r\n" + "expires_in: "
                //        + expires_in + "\r\n有效期：" + date);
                AccessTokenKeeper.keepAccessToken(MainWeixin.this, mAccessToken);
                myapplication.setSinaToken(token);
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
                //Toast.makeText(Login.this, "认证成功"+uid, Toast.LENGTH_SHORT).show();
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
    					    	String puturl = PhpHttpUtil.SERVICE_HOST+"index.php/Sina/saveuser/";
    							JSONObject result=new JSONObject(response);
    							String name=result.getString("name");
    							String profile_image_url=result.getString("profile_image_url");
    							RequestParams putparams = new RequestParams();
    							putparams.put("uid", uid);
    							putparams.put("token", token);
    							putparams.put("name", name);
    							putparams.put("username", myapplication.getUSERID());
    							putparams.put("userpass", myapplication.getUSERPASS());
    							putparams.put("imageurl", profile_image_url);
    			    			AsyncHttpClient client = new AsyncHttpClient();
    			    			client.post(puturl, putparams, new AsyncHttpResponseHandler(){
    			    				@Override  
    			    				public void onSuccess(String response) {
    			    					System.out.println(response);
    			    				}
    			    			});
    						} catch (JSONException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    				    }
    				}  
    				@Override 
    				public void onFailure(Throwable error, String content) {
    			        // By default, call the deprecated onFailure(Throwable) for compatibility
    			        System.out.println(content);
    					onFailure(error);
    			    }
    			});

                Log.i("Weibo", "Weibo.showUser().rlt " + rlt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rlt;
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
	public void exit_settings(View v) {                           //退出  伪“对话框”，其实是一个activity
		Intent intent = new Intent (MainWeixin.this,ExitFromSettings.class);			
		startActivity(intent);	
	 }
	//摇一摇
	public void btn_shake(View v) {                                   //手机摇一摇
		Intent intent = new Intent (MainWeixin.this,ShakeActivity.class);			
		startActivity(intent);	
	}
	//添加朋友
	public void btn_addfriend(View v){
		Intent intent = new Intent (MainWeixin.this,AddFreindActivity.class);			
		startActivity(intent);
	}
	class ListViewBinder implements ViewBinder {  
		  
        public boolean setViewValue(View view, Object data,  
                String textRepresentation) {  
            // TODO Auto-generated method stub  
            if((view instanceof ImageView) && (data instanceof Bitmap)) {  
                ImageView imageView = (ImageView) view;  
                Bitmap bmp = (Bitmap) data;  
                imageView.setImageBitmap(bmp);  
                return true;  
            }  
            return false;  
        }  
          
    }   
	
	 private Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg)
			{
				
				if(msg.what==1)
				{
					
					dialog = new ProgressDialog(MainWeixin.this);
					//设置进度条风格，风格为圆形，旋转的 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//设置ProgressDialog 标题 
					dialog.setTitle("我行聊"); 
					//设置ProgressDialog 提示信息 
					dialog.setMessage("正在努力为你加载好友列表中......"); 
					//设置ProgressDialog 标题图标 
					dialog.setIcon(R.drawable.icon); 
					//设置ProgressDialog 的进度条是否不明确 
					dialog.setIndeterminate(false); 
					//设置ProgressDialog 是否可以按退回按键取消 
					dialog.setCancelable(true); 
					//显示 
					dialog.show(); 
				}
				else if(msg.what==2)
				{
					//layout1.setVisibility(View.GONE);
					//layout2.setVisibility(View.VISIBLE);
					FriendsViewAdapter friendAdapter=new FriendsViewAdapter(MainWeixin.this,groupData,childData);
					//ExpandableListView exList;
					//listView = (ExpandableListView) findViewById(R.id.exlist);
					exlist.setAdapter(friendAdapter);
					exlist.setGroupIndicator(null);
					exlist.setDivider(null);
			        /** 
					listAdapter=new SimpleAdapter(MainWeixin.this, listdata, R.layout.frienditem, new String[]{"usernick","status","userhead","lastdata","offcount"}, new int[]{R.id.f_username,R.id.f_status,R.id.head,R.id.offdata,R.id.offcount});
					listAdapter.setViewBinder(new ListViewBinder()); 
					listView.setAdapter(listAdapter); 
					listView.setOnItemClickListener(new OnItemClickListener(){

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent intent = new Intent (MainWeixin.this,ChatActivity.class);
							//intent.putExtra("user",  (String)listdata.get(arg2).get("user"));
							intent.putExtra("TOUSERID", (String)listdata.get(arg2).get("username"));
							intent.putExtra("TOUSERNICK", (String)listdata.get(arg2).get("usernick"));
							startActivity(intent);	
							//Log.i("MyListView4-click", (String)listdata.get(arg2).get("username"));
						}
					});
					/**/
					dialog.cancel();
					
				}
				else if (msg.what==3){
					dialog.cancel();
					Toast.makeText(MainWeixin.this, "获取列表失败！",Toast.LENGTH_SHORT).show();
				}
				else if (msg.what==4){//获得用户请求加为好友的信息
					Intent intent = new Intent (MainWeixin.this,AddFriendDialog.class);
					Bundle data = new Bundle();
					data=msg.getData();
					String from=data.getString("from");
					intent.putExtra("from", from);
					startActivity(intent);	
				}
				else if (msg.what==5){//获得用户跟你解除好友的信息
					Intent intent = new Intent (MainWeixin.this,AddFriendDialog.class);
					Bundle data = new Bundle();
					data=msg.getData();
					String from=data.getString("from");
					intent.putExtra("from", from);
					startActivity(intent);
				}
				else if (msg.what==6){
					mTabPager.setCurrentItem(1);
				}
				else if (msg.what==7){
					myapplication.setNewMsgNum(0);
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
									Log.i("服务收到信息：",fromuser);
									if (!fromUserchat.equals(myapplication.getChatuser())){
										ChatMessage entity=new ChatMessage(1,fromuser,mess,TimeRender.getDate(),0,0);
										messageDB.saveUserMsg(myapplication.getUSERID(), entity);
				  		                Msg msg=Msg.analyseMsgBody(mess);
				  		                String messcontent=msg.getMsg();
				  		                String formusernick=XmppTool.getUserNick(XmppTool.getConnection(), fromuser+"@"+XmppTool.SERVER_NAME);
										int msgNum=myapplication.getNewMsgNum();
										msgNum++;
										myapplication.setNewMsgNum(msgNum);
										if (msgNum==1){
					  		                String ns = Context.NOTIFICATION_SERVICE;
				  		        	        NotificationManager mNotificationManager = (NotificationManager)MainWeixin.this. getSystemService(ns);
				  		        	        //定义通知栏展现的内容信息
				  		        	        int icon = R.drawable.icon;
				  		        	        CharSequence tickerText = "你有新的消息";
				  		        	        long when = System.currentTimeMillis();
				  		        	        Notification notification = new Notification(icon, tickerText, when);
				  		        	         
				  		        	        //定义下拉通知栏时要展现的内容信息
				  		        	        Context context = MainWeixin.this.getApplicationContext();
				  		        	        CharSequence contentTitle = formusernick;
				  		        	        CharSequence contentText =messcontent;
				  		        	        Intent notificationIntent = new Intent(MainWeixin.this, ChatActivity.class);
				  		        	        notificationIntent.putExtra("user",  fromuser+"@"+XmppTool.SERVER_NAME);
				  		        	        notificationIntent.putExtra("TOUSERID", fromuser);
				  		        	        notificationIntent.putExtra("TOUSERNICK", formusernick);
				  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
				  		        	                notificationIntent, 0);
				  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
				  		        	                contentIntent);
				  		        	         
				  		        	        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
				  		        	        mNotificationManager.notify(1, notification);
				  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
						        	        music.start();
										}else{
											String ns = Context.NOTIFICATION_SERVICE;
				  		        	        NotificationManager mNotificationManager = (NotificationManager) MainWeixin.this.getSystemService(ns);
				  		        	        //定义通知栏展现的内容信息
				  		        	        int icon = R.drawable.icon;
				  		        	        CharSequence tickerText = "你有新的消息";
				  		        	        long when = System.currentTimeMillis();
				  		        	        Notification notification = new Notification(icon, tickerText, when);
				  		        	         
				  		        	        //定义下拉通知栏时要展现的内容信息
				  		        	        Context context = MainWeixin.this.getApplicationContext();
				  		        	        CharSequence contentTitle = "你的离线信息总数："+msgNum;
				  		        	        CharSequence contentText = "";
				  		        	        Intent notificationIntent = new Intent(MainWeixin.this, MainWeixin.class);
				  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
				  		        	                notificationIntent, 0);
				  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
				  		        	                contentIntent);
				  		        	         
				  		        	        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
				  		        	        mNotificationManager.notify(1, notification);
				  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
				  		        	        music.start();
										}
										//Message message = handler.obtainMessage();
										//message.what = MSG;
										//message.obj=args;
										//handler.sendMessage(message);
									}
									
								}
			  				});
			  			}
			  		});
				}
			};
		};
		
		
	    
	   
		
	    private final void fail(Object o, Object... args) {

	        if (o != null && args != null && args.length > 0) {

	            String s = o.toString();

	            for (int i = 0; i < args.length; i++) {

	                String item = args[i] == null ? "" : args[i].toString();

	                if (s.contains("{" + i + "}")) {

	                    s = s.replace("{" + i + "}", item);

	                } else {

	                    s += " " + item;

	                }

	            }

	            System.out.println(s);

	        }

	    }
	    
	    
	 
	    public class mNetworkStateReceiver extends BroadcastReceiver{

	    	@Override
	    	public void onReceive(Context context, Intent intent) {
		    	Log.e("test", "网络状态改变");
		    	String curNet="";
		    	boolean success = false;
		    	
		    	//获得网络连接服务
		    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		    	NetworkInfo netinfo = connManager.getActiveNetworkInfo();
		    	if (netinfo != null && netinfo.isConnected()) {
					if(netinfo.getType() == ConnectivityManager.TYPE_WIFI){
						curNet="TYPE_WIFI";
					}else if(netinfo.getType() == ConnectivityManager.TYPE_MOBILE){
						curNet="TYPE_MOBILE";
					}
					Log.i("NetStatus", "The net was connected");
				} else {
					//result = 0;
					curNet="NONE_NET";
					Log.i("NetStatus", "The net was bad!");
				}
		    	
		    	Log.i("curnetstate:",curNet);
		    	//Iokapplication iok=new Iokapplication();
		    	if (myapplication.getCurNet()==null){
		    		myapplication.setCurNet(curNet);
		    	}else if (!myapplication.getCurNet().equals(curNet)){
		    		Log.i("upnetstate:",myapplication.getCurNet());
		    		myapplication.setCurNet(curNet);
		    		if (!curNet.equals("NONE_NET")){
		    			Log.i("重新登录:","关闭连接并重新登录");
		    			new Thread(new Runnable(){
		    				
							public void run() {
								// TODO Auto-generated method stub
								XmppTool.closeConnection();
				    			try {
									XmppTool.getConnection().login(myapplication.getUSERID(), myapplication.getUSERPASS());
									Log.i("登录状态:","登录成功");
								} catch (XMPPException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
		    			}).start();
		    			
		    		}
		    	}
		    	//if (!success) {
		    	//	Toast.makeText(MainWeixin.this, "您的网络连接已中断", Toast.LENGTH_LONG).show();
		    	//} 

	    	}
	    	
	    }
}
    
    

