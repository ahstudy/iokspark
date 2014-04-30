package com.iokokok.adapter;

import java.util.List;
import java.util.Map;

import com.iok.spark.ChatActivity;
import com.iok.spark.FriendInfoActivity;
import com.iok.spark.R;
import com.iokokok.util.XmppTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class FriendsViewAdapter extends BaseExpandableListAdapter{
	private Context ctx;
	private List<Map<String, String>>groupData;
	private List<List<Map<String, Object>>> childData;
	private LayoutInflater mInflater;
	public FriendsViewAdapter(Context ctx,List<Map<String, String>>groupData,List<List<Map<String, Object>>> childData) {
		super();
		this.ctx = ctx;
		this.groupData=groupData;
		this.childData=childData;
		mInflater = LayoutInflater.from(ctx);
	}
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.member_listview, null);
			
		}

			TextView title = (TextView) view.findViewById(R.id.groupname);
			title.setText(getGroup(groupPosition).toString()+"("+childData.get(groupPosition).size()+")");

			ImageView image=(ImageView) view.findViewById(R.id.listarrow);
			if(isExpanded)
				image.setImageResource(R.drawable.mm_submenu_normal1);
			else image.setImageResource(R.drawable.mm_submenu_normal);
			
		return view;
	}


	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	public Object getGroup(int groupPosition) {
		return groupData.get(groupPosition).get("groupname").toString();
	}

	public int getGroupCount() {
		return groupData.size();

	}
	
	
	//**************************************
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.frienditem, null);	
		}
		
		 
		if(childData.get(groupPosition).get(childPosition).get("usertype").equals("friend")){
			view.setOnClickListener(new OnClickListener(){
	
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent (ctx,ChatActivity.class);
					//intent.putExtra("user",  (String)listdata.get(arg2).get("user"));
					intent.putExtra("TOUSERID",childData.get(groupPosition).get(childPosition).get("username").toString());
					intent.putExtra("TOUSERNICK", childData.get(groupPosition).get(childPosition).get("usernick").toString());
					ctx.startActivity(intent);	
				}
				
			});
			view.setOnLongClickListener(new OnLongClickListener() {  
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(ctx,FriendInfoActivity.class);
					intent.putExtra("jid",childData.get(groupPosition).get(childPosition).get("username").toString()+"@"+XmppTool.SERVER_NAME);
					intent.putExtra("usertype", childData.get(groupPosition).get(childPosition).get("usertype").toString());
					ctx.startActivity(intent); 
					return true;
				}    
			}); 
		}else{
			view.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(ctx,FriendInfoActivity.class);
					intent.putExtra("jid",childData.get(groupPosition).get(childPosition).get("username").toString()+"@"+XmppTool.SERVER_NAME);
					intent.putExtra("usertype", childData.get(groupPosition).get(childPosition).get("usertype").toString());
					ctx.startActivity(intent);
				}
				
			});
		}
		final ImageView head=(ImageView)view.findViewById(R.id.head);
		head.setImageBitmap((Bitmap)childData.get(groupPosition).get(childPosition).get("userhead"));
		final TextView nick=(TextView)view.findViewById(R.id.f_username);
		nick.setText(childData.get(groupPosition).get(childPosition).get("usernick").toString());
		final TextView status=(TextView)view.findViewById(R.id.f_status);
		final TextView offdata=(TextView)view.findViewById(R.id.offdata);
		final TextView offcount=(TextView)view.findViewById(R.id.offcount);
		status.setText(childData.get(groupPosition).get(childPosition).get("status").toString());
		if (childData.get(groupPosition).get(childPosition).get("lastdata")!=null)
			offdata.setText(childData.get(groupPosition).get(childPosition).get("lastdata").toString());
		else
			offdata.setText("");
		offcount.setText(childData.get(groupPosition).get(childPosition).get("offcount").toString());
		   	//final TextView title = (TextView) view.findViewById(R.id.child_text);
			//	title.setText(childData.get(groupPosition).get(childPosition).toString());			
			//final TextView title2 = (TextView) view.findViewById(R.id.child_text2);
			//	title2.setText(childData.get(groupPosition).get(childPosition).toString());
		 
		return view;
	}
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition).toString();
	}

	public int getChildrenCount(int groupPosition) {
		return childData.get(groupPosition).size();
	}
	//**************************************
	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
