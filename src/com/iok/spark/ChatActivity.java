package com.iok.spark;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import com.iok.spark.PullToRefreshListView.OnRefreshListener;
import com.iokokok.app.Iokapplication;
import com.iokokok.http.AsyncHttpClient;
import com.iokokok.http.AsyncHttpResponseHandler;
import com.iokokok.http.RequestParams;
import com.iokokok.user.ChatMessage;
import com.iokokok.util.Expressions;
import com.iokokok.util.ImageCacheManager;
import com.iokokok.util.MessageDB;
import com.iokokok.util.Msg;
import com.iokokok.util.TimeRender;
import com.iokokok.util.XmppTool;
import com.sys.android.selfview.RecordButton;
import com.sys.android.selfview.RecordButton.OnFinishedRecordListener;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * 
 * @author geniuseoe2012
 *  ���ྫ�ʣ����ע�ҵ�CSDN����http://blog.csdn.net/geniuseoe2012
 *  android��������Ⱥ��200102476
 */
public class ChatActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private Context mCon;
	private ViewPager viewPager;
	private ArrayList<GridView> grids;
	private int[] expressionImages;
	private String[] expressionImageNames;
	private int[] expressionImages1;
	private String[] expressionImageNames1;
	private int[] expressionImages2;
	private String[] expressionImageNames2;
	private ImageButton rightBtn;
	private ImageButton voiceBtn;
	private ImageButton keyboardBtn;
	private ImageButton biaoqingBtn;
	private ImageButton biaoqingfocuseBtn;
	private ImageButton setmodetypeBtn;
	private ImageButton setmodetypefocuseBtn;
	private LinearLayout ll_fasong;
	private LinearLayout ll_yuyin;
	private LinearLayout page_select;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;
	private LinearLayout select_add;
	private ImageButton sel0;
	private ImageButton sel1;
	private ImageButton sel2;
	private ImageButton sel3;
	private ImageButton sel4;
	private ImageButton sel5;
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private PullToRefreshListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private List<ChatMessage> listMsg = new ArrayList<ChatMessage>();
	private ProgressBar pb;
	private String userChat="";//��ǰ����
	public static String pUSERID;//��ǰ�û�
	public static String pTOUSERID;//�Ի����û�
	public static String pUSERNICK;
	public static String pTOUSERNICK;
	public Chat newchat=null;
	private Iokapplication myapplication;
	private MessageDB messageDB;
	private FileTransferRequest request;
	private File file;
	private RecordButton mRecordButton;
	private String sdcardTempFilePath="";
	private File sdcardTempFile=null;
	private File BigsdcardTempFile=null;
	public ProgressDialog dialog ;
	private int crop = 300;
	private static String sendfileName;
	private Bitmap myBitmap;
    private byte[] mContent;
    public static ImageCacheManager mybitmapcache;
    private static int curpage=1;
    private static int PAGE_COUNT=10;
	// �����ļ�
	private OutgoingFileTransfer sendTransfer;
	public static String FILE_ROOT_PATH = Environment
				.getExternalStorageDirectory().getPath() + "/chat/file";
	public static String RECORD_ROOT_PATH = Environment
				.getExternalStorageDirectory().getPath() + "/chat/record";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myapplication=(Iokapplication)this.getApplicationContext();
        pUSERID = myapplication.getUSERID();
        pUSERNICK=myapplication.getUSERNICK();
        messageDB=myapplication.getMyDB();
        pTOUSERID = getIntent().getStringExtra("TOUSERID");
        pTOUSERNICK=getIntent().getStringExtra("TOUSERNICK");
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancel(1);
        userChat=pTOUSERID+"@"+XmppTool.SERVER_NAME;
        this.pb = (ProgressBar) findViewById(R.id.formclient_pb);
        myapplication.setChatuser(userChat);
        setContentView(R.layout.chat_user);
        TextView toUser=(TextView)findViewById(R.id.tousername);
        if(pTOUSERNICK.equals("")){
        	toUser.setText(pTOUSERID);
        }else{
        	toUser.setText(pTOUSERNICK);
        }
     
        //����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        mCon = ChatActivity.this;
		ll_fasong = (LinearLayout) findViewById(R.id.ll_fasong);
		ll_yuyin = (LinearLayout) findViewById(R.id.ll_yuyin);
		page_select = (LinearLayout) findViewById(R.id.page_select);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
		select_add=(LinearLayout) findViewById(R.id.select_add);
		sel0 = (ImageButton) findViewById(R.id.pic_select);
		sel0.setOnClickListener(this);
		sel1 = (ImageButton) findViewById(R.id.cam_select);
		sel1.setOnClickListener(this);
		sel2 = (ImageButton) findViewById(R.id.image_select);
		sel2.setOnClickListener(this);
		sel3=(ImageButton)findViewById(R.id.loc_select);
		sel3.setOnClickListener(this);
		sel4=(ImageButton)findViewById(R.id.video_select);
		sel4.setOnClickListener(this);
		sel5=(ImageButton)findViewById(R.id.voice_select);
		sel5.setOnClickListener(this);
		//mListView = (PullToRefreshListView) findViewById(R.id.listview);
		// �������
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;
		// ����ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		// ����
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		// ����
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		// ������Ϣ
		rightBtn = (ImageButton) findViewById(R.id.right_btn);
		rightBtn.setOnClickListener(this);
		// ����
		voiceBtn = (ImageButton) findViewById(R.id.chatting_voice_btn);
		voiceBtn.setOnClickListener(this);
		// ����
		keyboardBtn = (ImageButton) findViewById(R.id.chatting_keyboard_btn);
		keyboardBtn.setOnClickListener(this);
		// ����
		biaoqingBtn = (ImageButton) findViewById(R.id.image_select);
		biaoqingBtn.setOnClickListener(this);
		biaoqingfocuseBtn = (ImageButton) findViewById(R.id.chatting_biaoqing_focuse_btn);
		biaoqingfocuseBtn.setOnClickListener(this);
		setmodetypeBtn=(ImageButton)findViewById(R.id.chatting_add_btn);
		setmodetypeBtn.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        initView();
      	initData();
      	//getOfflinMessage();
      	setChatMsgListen();
      		
       
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
    protected void onDestory(){
    	myapplication.setChatuser("bbb");
    	Log.i("curuserchat1", myapplication.getChatuser());
    }
    public void initView()
    {
    	mListView = (PullToRefreshListView) findViewById(R.id.listview);
    	mListView.setOnRefreshListener(new OnRefreshListener() {
    	    public void onRefresh() {
    	        // Do work to refresh the list here.
    	        new GetDataTask().execute();
    	    }
    	});
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	
    	mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
    	LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		gView1 = (GridView) inflater.inflate(R.layout.grid1, null);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// ����24������
		for (int i = 0; i < 24; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(mCon, listItems,
				R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.image });
		gView1.setAdapter(simpleAdapter);
		gView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						expressionImages[arg2 % expressionImages.length]);
				ImageSpan imageSpan = new ImageSpan(mCon, bitmap);
				SpannableString spannableString = new SpannableString(
						expressionImageNames[arg2].substring(1,
								expressionImageNames[arg2].length() - 1));
				spannableString.setSpan(imageSpan, 0,
						expressionImageNames[arg2].length() - 2,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// �༭����������
				mEditTextContent.append(spannableString);
				System.out.println("edit������ = " + spannableString);
			}
		});
		grids.add(gView1);

		gView2 = (GridView) inflater.inflate(R.layout.grid2, null);
		grids.add(gView2);

		gView3 = (GridView) inflater.inflate(R.layout.grid3, null);
		grids.add(gView3);
		System.out.println("GridView�ĳ��� = " + grids.size());

		// ���ViewPager������������
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}
		};

		viewPager.setAdapter(mPagerAdapter);
		// viewPager.setAdapter();

		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }
    
    
    public void initData()
    {
    	//if (messageDB.istable(pUSERID))
    	listMsg=messageDB.getUserMsgList(pUSERID,pTOUSERID,curpage,PAGE_COUNT);
    	listMsg=ChatMessage.sort(listMsg);
    	mAdapter = new ChatMsgViewAdapter(this, listMsg);
		mListView.setAdapter(mAdapter);
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected void onPostExecute(String[] result) {
            //mListItems.addFirst("Added after refresh...");
            // Call onRefreshComplete when the list has been refreshed.
        	curpage+=1;
        	listMsg=messageDB.getUserMsgList(pUSERID,pTOUSERID,curpage,PAGE_COUNT);
        	listMsg=ChatMessage.sort(listMsg);
        	mAdapter = new ChatMsgViewAdapter(ChatActivity.this, listMsg);
    		mListView.setAdapter(mAdapter);
            mListView.onRefreshComplete();
            super.onPostExecute(result);
        }

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
    }
    public void setChatMsgListen(){
    	//��Ϣ����
      	ChatManager cm = XmppTool.getConnection().getChatManager();
      	//������Ϣ��yinghan-pc��������С������ȡ�Լ��ķ��������ͺ��ѣ�
      	newchat = cm.createChat(userChat, null);
      	cm.addChatListener(new ChatManagerListener() {
      			public void chatCreated(Chat chat, boolean able) 
      			{
      				chat.addMessageListener(new MessageListener() {
      					public void processMessage(Chat chat2, Message message)
      					{
      						//�յ�����yinghan-pc������С������Ϣ����ȡ�Լ��ķ��������ͺ��ѣ�
      						if(message.getFrom().contains(userChat))
      						{
      							//��ȡ�û�����Ϣ��ʱ�䡢IN
      							String fromuser=message.getFrom().split("@")[0];
      							//if (message.getBody().indexOf("<AMR>")>0){
      								//String mess=message.getBody().split("<AMR>")[0]+"<AMR>"+file.getPath();
      							//}else{
      							String mess=message.getBody();
      							//}
      							//Msg mymsg=Msg.analyseMsgBody(mess);
      							String[] args = new String[] {fromuser,mess};
      							//��handler��ȡ������ʾ��Ϣ
      							android.os.Message msg = handler.obtainMessage();
      							msg.what = 1;
      							msg.obj = args;
      							msg.sendToTarget();
      						}
      						else
      						{
      							//message.getFrom().contains(cs);
      						}
      						
      					}
      				});
      			}
      		});
    }
    public void onClick(View v) {
		boolean isFoused = false;
		switch (v.getId()) {
		// ����
		case R.id.btn_back:
			//Log.i("curuserchat2", myapplication.getChatuser());
			myapplication.setChatuser(" ");
			//Log.i("curuserchat3", myapplication.getChatuser());
			finish();
			break;
		// ����
		case R.id.btn_send:
			send();
			break;
		// ������Ϣ
		case R.id.right_btn:
			Intent intenta=new Intent(this,FriendInfoActivity.class);
			intenta.putExtra("jid",pTOUSERID);
			intenta.putExtra("usertype", "friend");
			startActivity(intenta);
			break;
		// ����
		case R.id.chatting_voice_btn:
			//Toast.makeText(this, "��鹦�ܻ�δ����ร��ף��ȵȰɣ���", Toast.LENGTH_LONG).show();
			voiceBtn.setVisibility(voiceBtn.GONE);
			keyboardBtn.setVisibility(keyboardBtn.VISIBLE);
			ll_fasong.setVisibility(ll_fasong.GONE);
			ll_yuyin.setVisibility(ll_yuyin.VISIBLE);
			mRecordButton = (RecordButton) findViewById(R.id.btn_yuyin);

			String path = RECORD_ROOT_PATH;
			File file = new File(path);
			if (!file.isDirectory())
				file.mkdirs();
			path +="/"+System.currentTimeMillis() + ".flac";
			mRecordButton.setSavePath(path);
			mRecordButton
					.setOnFinishedRecordListener(new OnFinishedRecordListener() {

						public void onFinishedRecord(String audioPath, int time) {
							Log.i("RECORD!!!", "finished!!!!!!!!!! save to "
									+ audioPath);

							if (audioPath != null) {
								try {
									// �Լ���ʾ��Ϣ
									Msg myMsg = new Msg(pUSERID, time + "��������Ϣ",
											TimeRender.getDate(), Msg.FROM_TYPE[1],
											Msg.TYPE[0], Msg.STATUS[3], time + "",
											audioPath);
									String messstr=Msg.toJson(myMsg);
									ChatMessage myChatMsg=new ChatMessage(1,pTOUSERID,messstr,TimeRender.getDate(),1,1);
									//ChatMsgEntity myChatMsg=new ChatMsgEntity(1,pTOUSERID,TimeRender.getDate(),pUSERID+"@"+XmppTool.SERVER_NAME,
											//time + "��������Ϣ<AMR>"+audioPath,pUSERID,"OUT",1,XmppTool.getUserImage(pUSERID+"@"+XmppTool.SERVER_NAME),true,"ARM","send...");
									listMsg.add(myChatMsg);
									int curcount=listMsg.size();
									messageDB.saveUserMsg(pUSERID, myChatMsg);
									viewPager.setVisibility(ViewPager.GONE);
									page_select.setVisibility(page_select.GONE);
									mAdapter.notifyDataSetChanged();
									mEditTextContent.setText("");
									if(mListView.getCount()>0)
										mListView.setSelection(mListView.getCount() - 1);
									
									String[] pathStrings = audioPath.split("/"); // �ļ���
									
									//���� �Է�����Ϣ
									String fileName = null ;
									if (pathStrings!=null && pathStrings.length>0) {
										fileName = pathStrings[pathStrings.length-1];
									}

									myMsg = new Msg(pUSERID, time + "��������Ϣ",
											TimeRender.getDate(), Msg.FROM_TYPE[0],
											Msg.TYPE[0], Msg.STATUS[3], time + "",
											"http://221.123.160.26/uploads/amr/"+fileName);
									String sendmess=Msg.toJson(myMsg);
										//������Ϣ��xiaowang
									newchat.sendMessage(sendmess);
									sendFilePhp(audioPath,curcount);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(ChatActivity.this, "����ʧ��",
										Toast.LENGTH_SHORT).show();
							}

						}
					});
			break;
		// ����
		case R.id.chatting_keyboard_btn:
			voiceBtn.setVisibility(voiceBtn.VISIBLE);
			keyboardBtn.setVisibility(keyboardBtn.GONE);
			ll_fasong.setVisibility(ll_fasong.VISIBLE);
			ll_yuyin.setVisibility(ll_yuyin.GONE);
			break;
		// ����
		case R.id.chatting_add_btn:
			if (select_add.getVisibility()==select_add.GONE)
				select_add.setVisibility(select_add.VISIBLE);
			else
				select_add.setVisibility(select_add.GONE);
			break;
		case R.id.image_select://��ʾѡ���Զ���ͼ���VIEW
			select_add.setVisibility(select_add.GONE);
			viewPager.setVisibility(viewPager.VISIBLE);
			page_select.setVisibility(page_select.VISIBLE);
			break;
		case R.id.pic_select://ѡ������е�ͼƬ
			Intent intent = new Intent("android.intent.action.PICK");
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, 100); 
			break;
		case R.id.cam_select://ѡ��ʹ�������ȡͼƬ
			Intent camintent=new Intent(this,IokCamera.class);
			startActivityForResult(camintent, 101); 
			break;
		case R.id.loc_select:
			Intent mapintent=new Intent(this,BaiduMapActivity.class);
			startActivityForResult(mapintent,103);
			break;
		case R.id.video_select:
			Intent videointent=new Intent(this,VideoActivity.class);
			startActivityForResult(videointent,104); 
			//Toast.makeText(this, "��Ƶ����¼��30��", Toast.LENGTH_LONG).show();
			//Intent videointent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			//videointent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			//����ʱ��
			//videointent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
			//���ƴ�С
			//videointent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024*1024);
			//startActivityForResult(videointent, 104);
			break;
		case R.id.voice_select:
			PackageManager pm = getPackageManager();
	        /* ��ѯ���ް�װGoogle Voice Search Engine */
	        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
	            RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	        if (activities.size()>0)
	        	startVoiceRecognitionActivity();
	        else
	        	Toast.makeText(this, "δ��װGoogle������������������ʹ����������", Toast.LENGTH_LONG).show();
			break;
		case R.id.chatting_biaoqing_focuse_btn:
			biaoqingBtn.setVisibility(biaoqingBtn.VISIBLE);
			biaoqingfocuseBtn.setVisibility(biaoqingfocuseBtn.GONE);
			viewPager.setVisibility(viewPager.GONE);
			page_select.setVisibility(page_select.GONE);
			break;
		}

	}
    //�����û����˷��ؼ�����¼�
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			myapplication.setChatuser(" ");
			finish();

		}
		
		return false;
		
	}
	private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			String timestr=TimeRender.getDate();
			Msg msg=new Msg(pUSERID,contString,timestr,Msg.FROM_TYPE[1]);
			String savemsg=Msg.toJson(msg);
			ChatMessage entity=new ChatMessage(1,pTOUSERID,savemsg,timestr,0,0);
			//ChatMsgEntity entity = new ChatMsgEntity(1,this.pTOUSERID,TimeRender.getDate(),this.pUSERID+"@"+XmppTool.SERVER_NAME,
				//	contString,this.pUSERID,"OUT",1,XmppTool.getUserImage(this.pUSERID+"@"+XmppTool.SERVER_NAME),true,"TXT","send...");
			listMsg.add(entity);
			Log.i("������Ϣ��",pUSERID);
			messageDB.saveUserMsg(pUSERID, entity);
			viewPager.setVisibility(ViewPager.GONE);
			page_select.setVisibility(page_select.GONE);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			if(mListView.getCount()>0)
				mListView.setSelection(mListView.getCount() - 1);
			msg=new Msg(pUSERID,contString,timestr,Msg.FROM_TYPE[0]);;
			String sendmsg=Msg.toJson(msg);
			try {
				//������Ϣ��xiaowang
				newchat.sendMessage(sendmsg);
				
			} 
			catch (XMPPException e)
			{
				e.printStackTrace();
			}
		}else{
			Toast.makeText(this, "���ܷ��Ϳ���Ϣ������", Toast.LENGTH_LONG).show();
		}
	}
	
    //�����ļ���PHP������
	
	public void sendFilePhp(String path, final int curcount){
		RequestParams params = new RequestParams();
		final ChatMessage mychat=listMsg.get(curcount-1);
		final Msg msg=Msg.analyseMsgBody(mychat.getmsg());
		final android.os.Message message = new android.os.Message();// handle
		message.what = 3;
		try {
			params.put("uploadpath", "amr");
			params.put("uploadfile", new File(path));
			AsyncHttpClient client = new AsyncHttpClient();
			client.post("http://221.123.160.26", params, new AsyncHttpResponseHandler(){
				@Override  
				public void onSuccess(String response) {  
				    System.out.println(response);  
				    
					msg.setReceive(Msg.STATUS[0]);
					String mess=Msg.toJson(msg);
					listMsg.get(curcount-1).setmsg(mess);
					if (curcount>0)
						handler.sendMessage(message);
				}  
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setReceive(Msg.STATUS[2]);
			String mess=Msg.toJson(msg);
			listMsg.get(curcount-1).setmsg(mess);
			if (curcount>0)
				handler.sendMessage(message);
			handler.sendMessage(message);
		} // Upload a File
		
	}
	
	/**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, 106);
    }

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressLint("NewApi")
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//ContentResolver resolver = getContentResolver();
        /**
         * ��������� ���߲�ѡ��ͼƬ���� ��ִ���κβ���
         */
		if (requestCode == 101 && intent!=null) {
			/**
			Bundle bundle = intent.getExtras();
    		if (bundle != null) {
    			if (myBitmap!=null)
            		destoryBimap();
    			myBitmap = (Bitmap) bundle.get("data");
    			System.out.println("file size:"+myBitmap.getByteCount());
    			Intent showintent=new Intent(this,ShowImageActivity.class);
    			//Bundle b = new Bundle();
                //b.putParcelable("image", myBitmap);
                showintent.putExtra("bundle", myBitmap);
	            this.startActivityForResult(showintent,102);
    		}
    		/**/
			String path=intent.getExtras().getString("path");  
    		Intent showintent=new Intent(this,ShowImageActivity.class);
	         //showintent.setData(originalUri);
	         showintent.putExtra("picurl", path);
	         this.startActivityForResult(showintent,102);
    		
	    }else if (requestCode==100 && intent!=null){
	    	ContentResolver resolver = getContentResolver();
	    	try {
	            // ���ͼƬ��uri
		    	Uri originalUri = intent.getData();
		    	//myBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);    
		    	String[] proj = {MediaStore.Images.Media.DATA};

		    	 

		        //������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�

		        @SuppressWarnings("deprecation")
		        Cursor cursor = managedQuery(originalUri, proj, null, null, null); 

		            //���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ

		        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		            //�����������ͷ ���������Ҫ����С�ĺ���������Խ��

		         cursor.moveToFirst();

		            //����������ֵ��ȡͼƬ·��

		         String path = cursor.getString(column_index);
		         Intent showintent=new Intent(this,ShowImageActivity.class);
		         //showintent.setData(originalUri);
		         showintent.putExtra("picurl", path);
		         this.startActivityForResult(showintent,102);
		    } catch (Exception e) {
		            System.out.println(e.getMessage());
		    }
	
	    }else if (requestCode==102){
	    	System.out.println(requestCode);
	    	String fileName=intent.getExtras().getString("picUrl");  
            String smallfileName=intent.getExtras().getString("smallpicUrl"); 
            if (!fileName.equals("") && !smallfileName.equals("")){
		    	// �Լ���ʾ��Ϣ
				Msg myMsg = new Msg(pUSERID, "ͼƬ��Ϣ",
						TimeRender.getDate(), Msg.FROM_TYPE[1],
						Msg.TYPE[1], Msg.STATUS[3], 1234 + "",
						smallfileName,fileName);
				String messstr=Msg.toJson(myMsg);
				ChatMessage myChatMsg=new ChatMessage(1,pTOUSERID,messstr,TimeRender.getDate(),1,1);
				//ChatMsgEntity myChatMsg=new ChatMsgEntity(1,pTOUSERID,TimeRender.getDate(),pUSERID+"@"+XmppTool.SERVER_NAME,
						//time + "��������Ϣ<AMR>"+audioPath,pUSERID,"OUT",1,XmppTool.getUserImage(pUSERID+"@"+XmppTool.SERVER_NAME),true,"ARM","send...");
				listMsg.add(myChatMsg);
				int curcount=listMsg.size();
				//UploadUtil.uploadFile(new File(fileName), "http://"+XmppTool.SERVER_HOST);
				messageDB.saveUserMsg(pUSERID, myChatMsg);
				//viewPager.setVisibility(ViewPager.GONE);
				select_add.setVisibility(select_add.GONE);
				mAdapter.notifyDataSetChanged();
				//mEditTextContent.setText("");
				if(mListView.getCount()>0)
					mListView.setSelection(mListView.getCount() - 1);
				
				String[] pathStrings = fileName.split("/"); // �ļ���
				String[] smallpathStrings=smallfileName.split("/");
				String smallfileNamestr=null;
				if (smallpathStrings!=null && smallpathStrings.length>0){
					smallfileNamestr= smallpathStrings[smallpathStrings.length-1];
				}
				//���� �Է�����Ϣ
				String fileNamestr = null ;
				if (pathStrings!=null && pathStrings.length>0) {
					fileNamestr = pathStrings[pathStrings.length-1];
				}
				//myChatMsg.setText(time + "��������Ϣ<AMR>"+fileName);
				
				// ˢ��������
				//adapter.notifyDataSetChanged();
				myMsg = new Msg(pUSERID, "ͼƬ��Ϣ",
						TimeRender.getDate(), Msg.FROM_TYPE[0],
						Msg.TYPE[1], Msg.STATUS[3], 1234 + "",
						"http://221.123.160.26/uploads/pic/"+smallfileNamestr,"http://221.123.160.26/uploads/pic/"+fileNamestr);
				String sendmess=Msg.toJson(myMsg);
					//������Ϣ��xiaowang
				try {
					newchat.sendMessage(sendmess);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
	    }else if (requestCode==103){
	    	String myaddress=intent.getExtras().getString("myaddress");
	    	if (!myaddress.equals("")){
	    		double longitude=intent.getDoubleExtra("longitude", 0);
		    	double latitude=intent.getDoubleExtra("latitude", 0);
		    	System.out.println("longitude:"+longitude+",latitude:"+latitude+",myaddress:"+myaddress);
		    	// �Լ���ʾ��Ϣ
				Msg myMsg = new Msg(pUSERID, "������Ϣ",myaddress,
						latitude,longitude,
						TimeRender.getDate(), Msg.FROM_TYPE[1],
						Msg.TYPE[3]);
				String messstr=Msg.toJson(myMsg);
				ChatMessage myChatMsg=new ChatMessage(1,pTOUSERID,messstr,TimeRender.getDate(),1,1);
				//ChatMsgEntity myChatMsg=new ChatMsgEntity(1,pTOUSERID,TimeRender.getDate(),pUSERID+"@"+XmppTool.SERVER_NAME,
						//time + "��������Ϣ<AMR>"+audioPath,pUSERID,"OUT",1,XmppTool.getUserImage(pUSERID+"@"+XmppTool.SERVER_NAME),true,"ARM","send...");
				listMsg.add(myChatMsg);
				int curcount=listMsg.size();
				//UploadUtil.uploadFile(new File(fileName), "http://"+XmppTool.SERVER_HOST);
				messageDB.saveUserMsg(pUSERID, myChatMsg);
				//viewPager.setVisibility(ViewPager.GONE);
				select_add.setVisibility(select_add.GONE);
				mAdapter.notifyDataSetChanged();
				//mEditTextContent.setText("");
				if(mListView.getCount()>0)
					mListView.setSelection(mListView.getCount() - 1);
				
				myMsg = new Msg(pUSERID, "������Ϣ",myaddress,
						latitude,longitude,
						TimeRender.getDate(), Msg.FROM_TYPE[0],
						Msg.TYPE[3]);
				String sendmess=Msg.toJson(myMsg);
					//������Ϣ��xiaowang
				try {
					newchat.sendMessage(sendmess);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }else if (requestCode==104){
	    	String videoPath=intent.getStringExtra("videopath");
	    	String videourl=intent.getStringExtra("videourl");
	    	int time=intent.getIntExtra("videotime", 0);
	    	if (videoPath != null) {
				try {
					// �Լ���ʾ��Ϣ
					Msg myMsg = new Msg(pUSERID,  time+"����Ƶ��Ϣ",
							TimeRender.getDate(), Msg.FROM_TYPE[1],
							Msg.TYPE[4], Msg.STATUS[3], "",
							videoPath);
					String messstr=Msg.toJson(myMsg);
					ChatMessage myChatMsg=new ChatMessage(1,pTOUSERID,messstr,TimeRender.getDate(),1,1);
					//ChatMsgEntity myChatMsg=new ChatMsgEntity(1,pTOUSERID,TimeRender.getDate(),pUSERID+"@"+XmppTool.SERVER_NAME,
							//time + "��������Ϣ<AMR>"+audioPath,pUSERID,"OUT",1,XmppTool.getUserImage(pUSERID+"@"+XmppTool.SERVER_NAME),true,"ARM","send...");
					listMsg.add(myChatMsg);
					int curcount=listMsg.size();
					messageDB.saveUserMsg(pUSERID, myChatMsg);
					viewPager.setVisibility(ViewPager.GONE);
					page_select.setVisibility(page_select.GONE);
					mAdapter.notifyDataSetChanged();
					if(mListView.getCount()>0)
						mListView.setSelection(mListView.getCount() - 1);
					
					String[] pathStrings = videoPath.split("/"); // �ļ���
					
					//���� �Է�����Ϣ
					String fileName = null ;
					if (pathStrings!=null && pathStrings.length>0) {
						fileName = pathStrings[pathStrings.length-1];
					}

					myMsg = new Msg(pUSERID, time+"����Ƶ��Ϣ",
							TimeRender.getDate(), Msg.FROM_TYPE[0],
							Msg.TYPE[4], Msg.STATUS[3],"",
							videourl);
					String sendmess=Msg.toJson(myMsg);
						//������Ϣ��xiaowang
					newchat.sendMessage(sendmess);
					//sendFilePhp(audioPath,curcount);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(ChatActivity.this, "����ʧ��",
						Toast.LENGTH_SHORT).show();
			}

	    }else if (requestCode==105){
	    	
	    }else if (requestCode==106){
	    	ArrayList<String> matches = intent.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
	    	System.out.println(matches.get(0));
	    }

          
		
	}
	private Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //retriever.setMode(MediaMetadataRetriever.);
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch(IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }
	private Handler pichandler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(ChatActivity.this);
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ���ϴ���ͷ��......"); 
				//����ProgressDialog ����ͼ�� 
				dialog.setIcon(R.drawable.icon); 
				//����ProgressDialog �Ľ������Ƿ���ȷ 
				dialog.setIndeterminate(false); 
				//����ProgressDialog �Ƿ���԰��˻ذ���ȡ�� 
				dialog.setCancelable(true); 
				//��ʾ 
				dialog.show(); 
			}else if(msg.what==2){
				Log.i("test:","thread is end");
				Toast.makeText(ChatActivity.this, "ͷ����³ɹ�", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				ChatActivity.this.finish();
			}else if(msg.what==3){
				Toast.makeText(ChatActivity.this, "ͷ�����ʧ��", Toast.LENGTH_LONG).show();
				//if (!daysparkmess.equals("") && daysparkmess!=null){
				//	dayspark.setText(daysparkmess);
				//}
				dialog.cancel();
				ChatActivity.this.finish();
			}
		}
   };
    private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) 
		{
							
			switch (msg.what) {
			case 1:
				//��ȡ��Ϣ����ʾ
				String[] args =(String[])msg.obj;
				
				//Msg args = (Msg) msg.obj;
				String sendMsg=args[1];
				//ChatMsgEntity curcme=new ChatMsgEntity(1,args[0], args[1], args[2], args[3], args[4], args[5],1,XmppTool.getUserImage(args[2]),true,"TXT","success");
				ChatMessage entity=new ChatMessage(1,args[0],sendMsg,TimeRender.getDate(),0,0);
				listMsg.add(entity);
				messageDB.saveUserMsg(pUSERID, entity);
				//mAdapter = new ChatMsgViewAdapter(ChatActivity.this, listMsg);
				//ˢ��������
				
				//listMsg.add(entity);
				mAdapter.notifyDataSetChanged();
				if(mListView.getCount()>0)
					mListView.setSelection(mListView.getCount() - 1);
				break;			
			case 2:
				//����������
				if(pb.getVisibility()==View.GONE){
					pb.setMax(100);
					pb.setProgress(1);
					pb.setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				//pb.setProgress(msg.arg1);
				mAdapter.notifyDataSetChanged();
				break;
			case 4:
				pb.setVisibility(View.GONE);
				break;
			case 5:
				final IncomingFileTransfer infiletransfer = request.accept();
				
				//��ʾ��
				AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
				
				builder.setTitle("������")
						.setCancelable(false)
						.setMessage("�Ƿ�����ļ���"+file.getName()+"?")
						.setPositiveButton("����",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										try 
										{
											infiletransfer.recieveFile(file);
										} 
										catch (XMPPException e)
										{
											Toast.makeText(ChatActivity.this,"����ʧ��!",Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
										
										handler.sendEmptyMessage(2);
										
										Timer timer = new Timer();
										TimerTask updateProgessBar = new TimerTask() {
											public void run() {
												if ((infiletransfer.getAmountWritten() >= request.getFileSize())
														|| (infiletransfer.getStatus() == FileTransfer.Status.error)
														|| (infiletransfer.getStatus() == FileTransfer.Status.refused)
														|| (infiletransfer.getStatus() == FileTransfer.Status.cancelled)
														|| (infiletransfer.getStatus() == FileTransfer.Status.complete)) 
												{
													cancel();
													handler.sendEmptyMessage(4);
												} 
												else
												{
													long p = infiletransfer.getAmountWritten() * 100L / infiletransfer.getFileSize();													
													
													android.os.Message message = handler.obtainMessage();
													message.arg1 = Math.round((float) p);
													message.what = 3;
													message.sendToTarget();
													Toast.makeText(ChatActivity.this,"�������!",Toast.LENGTH_SHORT).show();
												}
											}
										};
										timer.scheduleAtFixedRate(updateProgessBar, 10L, 10L);
										dialog.dismiss();
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id)
									{
										request.reject();
										dialog.cancel();
									}
								}).show();
				break;
			default:
				break;
			}
		};
	};
    
	
	
	
	// ** ָ��ҳ��ļ����� */
		class GuidePageChangeListener implements OnPageChangeListener {

			public void onPageScrollStateChanged(int arg0) {
				System.out.println("ҳ�����" + arg0);

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				System.out.println("��ҳ��" + arg0);
			}

			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					page0.setImageDrawable(getResources().getDrawable(
							R.drawable.page_focused));
					page1.setImageDrawable(getResources().getDrawable(
							R.drawable.page_unfocused));

					break;
				case 1:
					page1.setImageDrawable(getResources().getDrawable(
							R.drawable.page_focused));
					page0.setImageDrawable(getResources().getDrawable(
							R.drawable.page_unfocused));
					page2.setImageDrawable(getResources().getDrawable(
							R.drawable.page_unfocused));
					List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
					// ����24������
					for (int i = 0; i < 24; i++) {
						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("image", expressionImages1[i]);
						listItems.add(listItem);
					}

					SimpleAdapter simpleAdapter = new SimpleAdapter(mCon,
							listItems, R.layout.singleexpression,
							new String[] { "image" }, new int[] { R.id.image });
					gView2.setAdapter(simpleAdapter);
					gView2.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Bitmap bitmap = null;
							bitmap = BitmapFactory.decodeResource(getResources(),
									expressionImages1[arg2
											% expressionImages1.length]);
							ImageSpan imageSpan = new ImageSpan(mCon, bitmap);
							SpannableString spannableString = new SpannableString(
									expressionImageNames1[arg2]
											.substring(1,
													expressionImageNames1[arg2]
															.length() - 1));
							spannableString.setSpan(imageSpan, 0,
									expressionImageNames1[arg2].length() - 2,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							// �༭����������
							mEditTextContent.append(spannableString);
							System.out.println("edit������ = " + spannableString);
						}
					});
					break;
				case 2:
					page2.setImageDrawable(getResources().getDrawable(
							R.drawable.page_focused));
					page1.setImageDrawable(getResources().getDrawable(
							R.drawable.page_unfocused));
					page0.setImageDrawable(getResources().getDrawable(
							R.drawable.page_unfocused));
					List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();
					// ����24������
					for (int i = 0; i < 24; i++) {
						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("image", expressionImages2[i]);
						listItems1.add(listItem);
					}

					SimpleAdapter simpleAdapter1 = new SimpleAdapter(mCon,
							listItems1, R.layout.singleexpression,
							new String[] { "image" }, new int[] { R.id.image });
					gView3.setAdapter(simpleAdapter1);
					gView3.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Bitmap bitmap = null;
							bitmap = BitmapFactory.decodeResource(getResources(),
									expressionImages2[arg2
											% expressionImages2.length]);
							ImageSpan imageSpan = new ImageSpan(mCon, bitmap);
							SpannableString spannableString = new SpannableString(
									expressionImageNames2[arg2]
											.substring(1,
													expressionImageNames2[arg2]
															.length() - 1));
							spannableString.setSpan(imageSpan, 0,
									expressionImageNames2[arg2].length() - 2,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							// �༭����������
							mEditTextContent.append(spannableString);
							System.out.println("edit������ = " + spannableString);
						}
					});
					break;

				}
			}
		}
    public void head_xiaohei(View v) {     //������ ���ذ�ť
    	Intent intent = new Intent (ChatActivity.this,InfoXiaohei.class);			
		startActivity(intent);	
      } 
    
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
	
	class MyFileStatusThread extends Thread {
		private FileTransfer transfer;
		private ChatMessage msg;
		private Msg mymsg;
		public MyFileStatusThread(FileTransfer tf, ChatMessage msgInfo) {
			transfer = tf;
			this.msg = msgInfo;
			this.mymsg=Msg.analyseMsgBody(msgInfo.getmsg());
		}

		public void run() {
			System.out.println(transfer.getStatus());
			System.out.println(transfer.getProgress());
			System.out.println("file is send...." + transfer.getFileSize());
			android.os.Message message = new android.os.Message();// handle
			message.what = 3;
			while (!transfer.isDone()) {
				System.out.println(transfer.getStatus());
				System.out.println(transfer.getProgress());
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (transfer.getStatus().equals(Status.error)) {
				mymsg.setReceive(Msg.STATUS[2]);
				//msg.setMsgState("error");
			} else if (transfer.getStatus().equals(Status.refused)) {
				mymsg.setReceive(Msg.STATUS[1]);
				//msg.setMsgState("error");
			} else {
				mymsg.setReceive(Msg.STATUS[0]);// �ɹ�
				//msg.setMsgState("success");

			}
			String mess=Msg.toJson(mymsg);
			msg.setmsg(mess);
			handler.sendMessage(message);
			/*
			 * System.out.println(transfer.getStatus());
			 * System.out.println(transfer.getProgress());
			 */
		}
	}
	
	/**
	 * ����ͼƬ�ļ�
	 */
	private void destoryBimap() {
		if (myBitmap != null && !myBitmap.isRecycled()) {
			myBitmap.recycle();
			myBitmap = null;
		}
	}
}