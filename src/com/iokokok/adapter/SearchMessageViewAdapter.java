package com.iokokok.adapter;

import java.util.List;

import com.iok.spark.FriendInfoActivity;
import com.iok.spark.R;
import com.iokokok.user.SearchMessage;
import com.iokokok.util.XmppTool;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchMessageViewAdapter extends BaseAdapter {
	private Context c=null;
	private List<SearchMessage> listdata=null;
	private LayoutInflater mInflater;
	public SearchMessageViewAdapter(Context ctx,List<SearchMessage> listdata){
		this.c=ctx;
		this.listdata=listdata;
		mInflater = LayoutInflater.from(ctx);
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return listdata.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listdata.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final SearchMessage curMessage=listdata.get(arg0);
		arg1=mInflater.inflate(R.layout.searchresult, null);
		arg1.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(c,FriendInfoActivity.class);
				intent.putExtra("jid", curMessage.getJid());
				c.startActivity(intent);
			}
			
		});
		ImageView userhead=(ImageView)arg1.findViewById(R.id.head);
		TextView username=(TextView)arg1.findViewById(R.id.f_username);
		TextView name=(TextView)arg1.findViewById(R.id.f_name);
		final Button btnAdd=(Button)arg1.findViewById(R.id.add);
		btnAdd.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				XmppTool.addUser(XmppTool.getConnection().getRoster(), curMessage.getJid(), curMessage.getUsername());
				btnAdd.setText("已添加");
				Toast.makeText(c, "好友添加成功！", Toast.LENGTH_LONG).show();
			}
			
		});
		Bitmap userbitmap=curMessage.getHead();
		if (userbitmap==null){
     	   Resources res=c.getResources();
     	  userbitmap=BitmapFactory.decodeResource(res, R.drawable.usernohead);
        }
		userhead.setImageBitmap(userbitmap);
		username.setText(curMessage.getUsername());
		name.setText(curMessage.getUsernick());
		return arg1;
	}

}
