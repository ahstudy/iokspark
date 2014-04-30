
package com.iok.spark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.iokokok.app.Iokapplication;
import com.iokokok.user.ChatMessage;
import com.iokokok.util.ExpressionUtil;
import com.iokokok.util.MessageDB;
import com.iokokok.util.Msg;
import com.loopj.android.image.SmartImageView;


public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMessage> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMessage> listMsg) {
        ctx = context;
        this.coll = listMsg;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    


	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMessage entity = coll.get(position);
	 	Msg msg=Msg.analyseMsgBody(entity.getmsg());
	 	if (msg.getFrom().equals("IN"))
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}


	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
    @SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMessage msg = coll.get(position);

    	final Msg entity=Msg.analyseMsgBody(msg.getmsg());
    	ViewHolder viewHolder = null;	
    	convertView=null;
	    if (convertView == null)
	    {
	    	if (entity.getType().equals(Msg.TYPE[1])){
	    		if (entity.getFrom().equals("IN"))
				  {
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_image_left, null);
				  }else{
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_image_right, null);
				  }
	    		viewHolder = new ViewHolder();
				viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
				//viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				viewHolder.tvuserhead=(ImageView)convertView.findViewById(R.id.iv_userhead);
				viewHolder.tvMsgState=(TextView)convertView.findViewById(R.id.msg_state);
				viewHolder.tvPicContent=(SmartImageView)convertView.findViewById(R.id.tv_chatcontent);
	    	}else if(entity.getType().equals(Msg.TYPE[3])){
	    		if (entity.getFrom().equals("IN"))
				  {
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_loc_left, null);
				  }else{
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_loc_right, null);
				  }
	    		viewHolder = new ViewHolder();
				viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
				//viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				viewHolder.tvuserhead=(ImageView)convertView.findViewById(R.id.iv_userhead);
				viewHolder.tvMsgState=(TextView)convertView.findViewById(R.id.msg_state);
				viewHolder.tvPicContent=(SmartImageView)convertView.findViewById(R.id.tv_chatcontent);
	    	}else if(entity.getType().equals(Msg.TYPE[4])){
	    		if (entity.getFrom().equals("IN"))
				  {
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_video_left, null);
				  }else{
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_video_right, null);
				  }
	    		viewHolder = new ViewHolder();
				viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
				//viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				viewHolder.tvuserhead=(ImageView)convertView.findViewById(R.id.iv_userhead);
				viewHolder.tvMsgState=(TextView)convertView.findViewById(R.id.msg_state);
				viewHolder.tvPicContent=(SmartImageView)convertView.findViewById(R.id.tv_chatcontent);
	    	}else{
	    		if (entity.getFrom().equals("IN"))
				  {
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
				  }else{
					  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
				  }

		    	  viewHolder = new ViewHolder();
				  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
				  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				  viewHolder.tvuserhead=(ImageView)convertView.findViewById(R.id.iv_userhead);
				  viewHolder.tvMsgState=(TextView)convertView.findViewById(R.id.msg_state);
				  //viewHolder.tvPicContent=(ImageView)convertView.findViewById(R.id.pic_content);
	    	}			  
			  convertView.setTag(viewHolder);
			  
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	
	    
	    viewHolder.tvSendTime.setText(entity.getDate());
	    //if (entity.getUserid().equals("")){
	    	//viewHolder.tvUserName.setText(entity.getFrom().split("@")[0]);
	    //}else{
	    	//viewHolder.tvUserName.setText(entity.getUserNick());
	    //}
	    Iokapplication myapplication=(Iokapplication)ctx.getApplicationContext();
	    MessageDB messageDB=myapplication.getMyDB();
	    Bitmap userhead=null;
	    String headStr="";
	    String nick="";
	    if(!entity.getFrom().equals("IN")){
	    	viewHolder.tvUserName.setText("我");
	    	headStr=messageDB.getUserSet(entity.getUserid(), "head");
	    	
	    }else{
	    	nick=messageDB.getFriendNick(myapplication.getUSERID(), entity.getUserid());
	    	if (nick.equals("")){
	    		viewHolder.tvUserName.setText(entity.getUserid());
	    	}else{
	    		viewHolder.tvUserName.setText(nick);
	    	}
	    	headStr=messageDB.getFriendHead(myapplication.getUSERID(), entity.getUserid());
	    }
	    if (headStr.equals("")){
	    	viewHolder.tvuserhead.setImageResource(R.drawable.usernohead);
	    }else{
	    	byte[] bitmapArray;
	    	bitmapArray=Base64.decode(headStr, Base64.DEFAULT);
	        userhead=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	    	if (userhead==null){
				viewHolder.tvuserhead.setImageResource(R.drawable.usernohead);
			}else{
				viewHolder.tvuserhead.setImageBitmap(userhead);
			}
	    }
	    String str = entity.getMsg(); 
		String zhengze = "f0[0-9]{2}|f10[0-7]"; 
		if (entity.getType().equals(Msg.TYPE[0])){
			String msgstr="f072"+str;
			try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(ctx, msgstr, zhengze);
				viewHolder.tvContent.setText(spannableString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//viewHolder.tvPicContent.setVisibility(View.GONE);
			final String filepath=entity.getFilePath();
			if (msg.getisread()==1){
				viewHolder.tvMsgState.setVisibility(View.GONE);
				viewHolder.tvMsgState.setText("");
			}else{
				viewHolder.tvMsgState.setVisibility(View.VISIBLE);
				viewHolder.tvMsgState.setText(entity.getReceive()+"");
			}
			
			entity.setReceive(entity.getReceive());
			viewHolder.tvContent.setOnClickListener(new OnClickListener() {// 点击查看
				public void onClick(View v) {					
					try {
						Uri uri=null;
						if (filepath.indexOf("http://")>=0){
							uri = Uri.parse(filepath);
						}else{
							uri = Uri.fromFile(new File(filepath ));
						}
						
						System.out.println("播放路径："+uri);
						MediaPlayer music=MediaPlayer.create(ctx, uri);
						music.start();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}else if (entity.getType().equals(Msg.TYPE[1])){
			//viewHolder.tvPicContent.setVisibility(View.VISIBLE);
			if(!entity.getFilePath().equals("")){
				//BitmapFactory.Options options = new BitmapFactory.Options(); 
				Uri uri=null;
				if (entity.getFilePath().indexOf("http://")>=0){
					//uri = Uri.parse(entity.getFilePath());
					viewHolder.tvPicContent.setImageUrl(entity.getFilePath());
					//setViewImage(viewHolder.tvPicContent,entity.getFilePath());
				}else{
					uri = Uri.fromFile(new File(entity.getFilePath() ));
					viewHolder.tvPicContent.setImageURI(uri);
				}
				
				viewHolder.tvPicContent.setOnClickListener(new OnClickListener() {// 点击查看
					public void onClick(View v) {
						Intent intent = new Intent (v.getContext(),InfoXiaoheiHead.class);
						if (entity.getBigFilePath().equals("")){
							intent.putExtra("picurl", entity.getFilePath());
						}else{
							intent.putExtra("picurl", entity.getBigFilePath());
						}
						
						v.getContext().startActivity(intent);	
					}
				});
			}
			
			//viewHolder.tvContent.setVisibility(View.GONE);
			viewHolder.tvMsgState.setText("");
			viewHolder.tvMsgState.setVisibility(View.GONE);
		}else if(entity.getType().equals(Msg.TYPE[3])){
			viewHolder.tvMsgState.setText(entity.getMyAddress());
			viewHolder.tvMsgState.setVisibility(View.VISIBLE);
			viewHolder.tvPicContent.setOnClickListener(new OnClickListener() {// 点击查看
				public void onClick(View v) {
					Intent intent = new Intent (v.getContext(),FriendBaiduMapActivity.class);
					intent.putExtra("latitude", entity.getLatitude());
					intent.putExtra("longitude", entity.getLongitude());
					intent.putExtra("myaddress", entity.getMyAddress());
					v.getContext().startActivity(intent);	
				}
			});
			
		}else if (entity.getType().equals(Msg.TYPE[4])){
			viewHolder.tvMsgState.setText(entity.getMsg());
			viewHolder.tvMsgState.setVisibility(View.VISIBLE);
			viewHolder.tvPicContent.setOnClickListener(new OnClickListener() {// 点击查看
				public void onClick(View v) {
					//调用系统自带的播放器  
					Uri uri=null;
					if (entity.getFilePath().indexOf("http://")>=0){
						uri = Uri.parse(entity.getFilePath());
					}else{
						uri = Uri.fromFile(new File(entity.getFilePath() ));
					}
					 Intent intent = new Intent(Intent.ACTION_VIEW);  
					 intent.setDataAndType(uri, "video/mp4");   
					v.getContext().startActivity(intent);	
				}
			});
			
		}else{
			try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(ctx, str, zhengze);
				viewHolder.tvContent.setText(spannableString);
				viewHolder.tvPicContent.setVisibility(View.GONE);
				viewHolder.tvMsgState.setText("");
				viewHolder.tvMsgState.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	    return convertView;
    }
    public boolean onContextItemSelected(MenuItem item) {
	  System.out.println("ok");
	  return true;
	  }
    public void setViewImage(ImageView v, String value) {
        new ImageDownloadTask().execute(value, v);
    }
    private class ImageDownloadTask extends AsyncTask<Object, Object, Bitmap>{
    		private ImageView imageView = null;
    		@Override
    		protected Bitmap doInBackground(Object... params) {
    			// TODO Auto-generated method stub
    			Bitmap bmp = null;
    			imageView = (ImageView) params[1];
    			try {
    				bmp = BitmapFactory.decodeStream(new URL((String)params[0]).openStream());
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return bmp;
    		}
    		
    		protected void onPostExecute(Bitmap result){
    			imageView.setImageBitmap(result);
    		}
    	}
    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvMsgState;
        public ImageView tvuserhead;
        public SmartImageView  tvPicContent;
        //public boolean isComMsg = true;
    }


}
