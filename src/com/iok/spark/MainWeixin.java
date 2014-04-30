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
	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	private String myaddress ="";
	public MyLocationListenner myListener = new MyLocationListenner();
	
	public static MainWeixin instance = null;
	 
	private ViewPager mTabPager;	
	private TextView LoadTxt;
	private ImageView mTabImg,LoadImg;// ����ͼƬ
	private ImageView mTab1,mTab2,mTab3,mTab4;
	private int zero = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int one;//����ˮƽ����λ��
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
	public static String pUSERID;//��ǰ�û�
	public static String pUSERNICK;//��ǰ�û��ǳ�
	public ProgressDialog dialog ; 
	private Roster roster ;
	private Iokapplication myapplication;
    private MessageDB messageDB;
    /** ΢��API�ӿ��࣬�ṩ��½�ȹ���  */
    private Weibo mWeibo;
    
    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
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
         //����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        instance = this;

      //��λ��ʼ��
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
        Display currDisplay = getWindowManager().getDefaultDisplay();//��ȡ��Ļ��ǰ�ֱ���
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth/4; //����ˮƽ����ƽ�ƴ�С
        two = one*2;
        three = one*3;
        //Log.i("info", "��ȡ����Ļ�ֱ���Ϊ" + one + two + three + "X" + displayHeight);
        IntentFilter filters = new IntentFilter(); 
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateReceiver mReceiver=new mNetworkStateReceiver();
        registerReceiver(mReceiver, filters);
        //InitImageView();//ʹ�ö���
      //��Ҫ��ҳ��ʾ��Viewװ��������
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.main_tab_weixin, null);
        View view2 = mLi.inflate(R.layout.main_tab_address, null);
        View view3 = mLi.inflate(R.layout.main_tab_friends, null);
        View view4 = mLi.inflate(R.layout.main_tab_settings, null);
        
      //ÿ��ҳ���view����
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
      //���ViewPager������������
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
		//����û� ��Ӻ��ѻ�ɾ�����ѵļ���
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
	    option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
	    option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	    option.setScanSpan(2000);//���÷���λ����ļ��ʱ��Ϊ5000ms
	    option.disableCache(true);//��ֹ���û��涨λ
	    option.setPoiNumber(5); //��෵��POI����
	    option.setPoiDistance(1000); //poi��ѯ����
	    option.setPoiExtraInfo(false); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
	    option.setPriority(LocationClientOption.NetWorkFirst);
	    mLocClient.setLocOption(option);
	}
    
    /**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //�������ʾ��λ����Ȧ����accuracy��ֵΪ0����
            
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
				//���û�λ�������ϴ���������
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
            //Toast.makeText(MainWeixin.this, "����ǰ��λ��Ϊ��"+myaddress, Toast.LENGTH_LONG).show();
            
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    /**
	 * ͷ��������
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
    
	 /* ҳ���л�����(ԭ����:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {		
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_pressed));
				//��������������
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
			case 1://�����б�
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
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		
		public void onPageScrollStateChanged(int arg0) {
		}
	}

    //��ȡ����������������
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
				newslistmap.put("title","EMS˳���ٿ�ս����");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44661");
				//listmap.put("title","���������ű���");
				newslistdata.add(newslistmap);
				newslistmap=new HashMap<String, Object>();
				newslistmap.put("type", "news");
				newslistmap.put("pic", R.drawable .icon);
				newslistmap.put("title","�Ҿ�չ���ⲻ��");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44660");
				//listmap.put("title","���������ű���");
				newslistdata.add(newslistmap);
				newslistmap=new HashMap<String, Object>();
				newslistmap.put("type", "news");
				newslistmap.put("pic", R.drawable .icon);
				newslistmap.put("title","�й���ҵ���ƽ���Դ֮��");
				newslistmap.put("url","http://www.iokokok.com/wap/index.php?moduleid=21&itemid=44659");
				//listmap.put("title","���������ű���");
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
	//���غ����б�
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
	            curGroupMap.put("groupname", "�ȴ�ͨ��");
	            curGroupMap = new HashMap<String, String>();
	            groupData.add(curGroupMap);
	            curGroupMap.put("groupname", "�ܾ�ͨ��");
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
		    	            curGroupMap.put("groupname", "�ȴ���֤");
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
	                    String offmess="������Ϣ0��";
						listmap.put("offcount",offmess);
						Presence availability = roster.getPresence(rosterEntry.getUser());
						
						//Mode userMode = availability.getMode();
						if (availability.isAvailable()){
							listmap.put("status", "����");
						}else{
							listmap.put("status", " ����");
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
							// ��Bitmapѹ����PNG���룬����Ϊ100%�洢
							mybit.compress(Bitmap.CompressFormat.PNG, 100, os);//����PNG���кܶೣ����ʽ����jpeg�ȡ�
							
						}
						listmap.put("userhead",mybit);
						//myfriend.setUserhead("");
						listmap.put("usergroup", "�ȴ���֤");
						//myfriend.setUsergroup("�ȴ���֤");
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
						String offmess="������Ϣ"+offmesscount+"��";
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
							listmap.put("status", "����");
						}else{
							listmap.put("status", " ����");
						}
						Bitmap mybit=XmppTool.getUserImage(rosterEntry.getName()+"@"+XmppTool.SERVER_NAME);
						if (mybit==null){
							Resources res=getResources();
							mybit=BitmapFactory.decodeResource(res, R.drawable.usernohead);
							myfriend.setUserhead("");
						}else{
							
							byte[] bytes = null; 
							final ByteArrayOutputStream os = new ByteArrayOutputStream();
							// ��Bitmapѹ����PNG���룬����Ϊ100%�洢
							mybit.compress(Bitmap.CompressFormat.PNG, 100, os);//����PNG���кܶೣ����ʽ����jpeg�ȡ�
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
		         //�������߳�ִ������   
		    }   

		}).start();
	}
	
	
	/** δ������֤���ѣ���ӹ��ĺ��ѣ�û�еõ��Է�ͬ�� */
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
	
	//����������Ϣ
  	public void getOfflinMessage(final String username){
  		new Thread(new Runnable(){

  			public void run() {
  				// TODO Auto-generated method stub
  				//�˴���ȡ��ǰ�û��������¼��δ����¼
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
  		                Log.i("Message:", "�յ�������Ϣ, Received from ��" + fromUser + "�� message: " + message.getBody());
  		                messbody=message.getBody();
  		                Msg msg=Msg.analyseMsgBody(messbody);
  		                messcontent=msg.getMsg();
  		                formusernick=XmppTool.getUserNick(XmppTool.getConnection(), fromUser+"@"+XmppTool.SERVER_NAME);
   		                ChatMessage entity=new ChatMessage(1,fromUser,messbody,TimeRender.getDate(),0,0);
  		                messageDB.saveUserMsg(username, entity);
  		                //Log.i("Message:", "�յ�������Ϣ, Received from ��" + fromUser + "�� message: " + message.getBody());
  		            }
  		           if (offmessCount>0){
  		        	   
  		        	   if (offmessCount==1){
  		        		 //����NotificationManager
  		        	        String ns = Context.NOTIFICATION_SERVICE;
  		        	        NotificationManager mNotificationManager = (NotificationManager)MainWeixin.this. getSystemService(ns);
  		        	        //����֪ͨ��չ�ֵ�������Ϣ
  		        	        int icon = R.drawable.icon;
  		        	        CharSequence tickerText = "�����µ���Ϣ";
  		        	        long when = System.currentTimeMillis();
  		        	        Notification notification = new Notification(icon, tickerText, when);
  		        	         
  		        	        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
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
  		        	         
  		        	        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
  		        	        mNotificationManager.notify(1, notification);
  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
		        	        music.start();
  		        	   }else{
  		        		 //����NotificationManager
  		        	        String ns = Context.NOTIFICATION_SERVICE;
  		        	        NotificationManager mNotificationManager = (NotificationManager) MainWeixin.this.getSystemService(ns);
  		        	        //����֪ͨ��չ�ֵ�������Ϣ
  		        	        int icon = R.drawable.icon;
  		        	        CharSequence tickerText = "�����µ���Ϣ";
  		        	        long when = System.currentTimeMillis();
  		        	        Notification notification = new Notification(icon, tickerText, when);
  		        	         
  		        	        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
  		        	        Context context = MainWeixin.this.getApplicationContext();
  		        	        CharSequence contentTitle = "���������Ϣ������"+offmessCount;
  		        	        CharSequence contentText = "";
  		        	        Intent notificationIntent = new Intent(MainWeixin.this, MainWeixin.class);
  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
  		        	                notificationIntent, 0);
  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
  		        	                contentIntent);
  		        	         
  		        	        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
  		        	        mNotificationManager.notify(1, notification);
  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
  		        	        music.start();
  		        	   }
  		           }
  		              
  		            

  		            offlineManager.deleteMessages();  
  		        //״̬
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
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
    		
        	if(menu_display){         //��� Menu�Ѿ��� ���ȹر�Menu
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
        		Intent intent = new Intent();
            	intent.setClass(MainWeixin.this,Exit.class);
            	startActivity(intent);
        	}
    	}
    	
    	else if(keyCode == KeyEvent.KEYCODE_MENU){   //��ȡ Menu��			
			if(!menu_display){
				//��ȡLayoutInflaterʵ��
				inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				//�����main��������inflate�м����Ŷ����ǰ����ֱ��this.setContentView()�İɣ��Ǻ�
				//�÷������ص���һ��View�Ķ����ǲ����еĸ�
				layout = inflater.inflate(R.layout.main_menu, null);
				
				//��������Ҫ�����ˣ����������ҵ�layout���뵽PopupWindow���أ������ܼ�
				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); //������������width��height
				//menuWindow.showAsDropDown(layout); //���õ���Ч��
				//menuWindow.showAsDropDown(null, 0, layout.getHeight());
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //����layout��PopupWindow����ʾ��λ��
				//��λ�ȡ����main�еĿؼ��أ�Ҳ�ܼ�
				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
				
				
				//�����ÿһ��Layout���е����¼���ע��ɡ�����
				//���絥��ĳ��MenuItem��ʱ�����ı���ɫ�ı�
				//����׼����һЩ����ͼƬ������ɫ
				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
					
					public void onClick(View arg0) {						
						//Toast.makeText(Main.this, "�˳�", Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
			        	intent.setClass(MainWeixin.this,Exit.class);
			        	startActivity(intent);
			        	menuWindow.dismiss(); //��Ӧ����¼�֮��ر�Menu
					}
				});				
				menu_display = true;				
			}else{
				//�����ǰ�Ѿ�Ϊ��ʾ״̬������������
				menuWindow.dismiss();
				menu_display = false;
				}
			
			return false;
		}
    	return false;
    }
	
	//���ñ������Ҳఴť������
	public void btnmainright(View v) {  
		Intent intent = new Intent (MainWeixin.this,MainTopRightDialog.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "����˹��ܰ�ť", Toast.LENGTH_LONG).show();
      }  	
	public void startchat(View v) {      //С��  �Ի�����
		Intent intent = new Intent (MainWeixin.this,ChatActivity.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "��¼�ɹ�", Toast.LENGTH_LONG).show();
      } 
	//��ʾ�û���ϸ��Ϣ
	public void showinfo(View v){
		Intent intent = new Intent (MainWeixin.this,InfoXiaohei.class);
		
		startActivity(intent);	
	}
	//�޸�����
	public void edit_pass(View v){
		Intent intent = new Intent (MainWeixin.this,EditPassActivity.class);			
		startActivity(intent);	
	}
	//����΢������
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
						//�û��Ѿ���΢��
						Intent intent =new Intent(MainWeixin.this,SinaWeiboActivity.class);
						//intent.putExtra("token", myapplication.getSinaToken());
						MainWeixin.this.startActivity(intent);
					}else{
						//δ���΢����
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
                //Toast.makeText(Login.this, "��֤�ɹ�"+uid, Toast.LENGTH_SHORT).show();
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
	public void exit_settings(View v) {                           //�˳�  α���Ի��򡱣���ʵ��һ��activity
		Intent intent = new Intent (MainWeixin.this,ExitFromSettings.class);			
		startActivity(intent);	
	 }
	//ҡһҡ
	public void btn_shake(View v) {                                   //�ֻ�ҡһҡ
		Intent intent = new Intent (MainWeixin.this,ShakeActivity.class);			
		startActivity(intent);	
	}
	//�������
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
					//���ý�������񣬷��ΪԲ�Σ���ת�� 
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
					//����ProgressDialog ���� 
					dialog.setTitle("������"); 
					//����ProgressDialog ��ʾ��Ϣ 
					dialog.setMessage("����Ŭ��Ϊ����غ����б���......"); 
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
					Toast.makeText(MainWeixin.this, "��ȡ�б�ʧ�ܣ�",Toast.LENGTH_SHORT).show();
				}
				else if (msg.what==4){//����û������Ϊ���ѵ���Ϣ
					Intent intent = new Intent (MainWeixin.this,AddFriendDialog.class);
					Bundle data = new Bundle();
					data=msg.getData();
					String from=data.getString("from");
					intent.putExtra("from", from);
					startActivity(intent);	
				}
				else if (msg.what==5){//����û����������ѵ���Ϣ
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
									Log.i("�����յ���Ϣ��",fromuser);
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
				  		        	        //����֪ͨ��չ�ֵ�������Ϣ
				  		        	        int icon = R.drawable.icon;
				  		        	        CharSequence tickerText = "�����µ���Ϣ";
				  		        	        long when = System.currentTimeMillis();
				  		        	        Notification notification = new Notification(icon, tickerText, when);
				  		        	         
				  		        	        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
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
				  		        	         
				  		        	        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
				  		        	        mNotificationManager.notify(1, notification);
				  		        	        MediaPlayer music=MediaPlayer.create(MainWeixin.this, R.raw.msg);
						        	        music.start();
										}else{
											String ns = Context.NOTIFICATION_SERVICE;
				  		        	        NotificationManager mNotificationManager = (NotificationManager) MainWeixin.this.getSystemService(ns);
				  		        	        //����֪ͨ��չ�ֵ�������Ϣ
				  		        	        int icon = R.drawable.icon;
				  		        	        CharSequence tickerText = "�����µ���Ϣ";
				  		        	        long when = System.currentTimeMillis();
				  		        	        Notification notification = new Notification(icon, tickerText, when);
				  		        	         
				  		        	        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
				  		        	        Context context = MainWeixin.this.getApplicationContext();
				  		        	        CharSequence contentTitle = "���������Ϣ������"+msgNum;
				  		        	        CharSequence contentText = "";
				  		        	        Intent notificationIntent = new Intent(MainWeixin.this, MainWeixin.class);
				  		        	        PendingIntent contentIntent = PendingIntent.getActivity(MainWeixin.this, 0,
				  		        	                notificationIntent, 0);
				  		        	        notification.setLatestEventInfo(context, contentTitle, contentText,
				  		        	                contentIntent);
				  		        	         
				  		        	        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
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
		    	Log.e("test", "����״̬�ı�");
		    	String curNet="";
		    	boolean success = false;
		    	
		    	//����������ӷ���
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
		    			Log.i("���µ�¼:","�ر����Ӳ����µ�¼");
		    			new Thread(new Runnable(){
		    				
							public void run() {
								// TODO Auto-generated method stub
								XmppTool.closeConnection();
				    			try {
									XmppTool.getConnection().login(myapplication.getUSERID(), myapplication.getUSERPASS());
									Log.i("��¼״̬:","��¼�ɹ�");
								} catch (XMPPException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
		    			}).start();
		    			
		    		}
		    	}
		    	//if (!success) {
		    	//	Toast.makeText(MainWeixin.this, "���������������ж�", Toast.LENGTH_LONG).show();
		    	//} 

	    	}
	    	
	    }
}
    
    

