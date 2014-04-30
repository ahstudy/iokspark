package com.iok.spark;

import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;

import com.iokokok.adapter.SearchMessageViewAdapter;
import com.iokokok.user.SearchMessage;
import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

public class SearchListActivity extends Activity {
	List<SearchMessage> users=null;
	private ProgressDialog dialog;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        final String searchuser=getIntent().getStringExtra("searchuser");
        if (searchuser!=null){
        	handler.sendEmptyMessage(1);
        	new Thread(new Runnable(){

				public void run() {
					// TODO Auto-generated method stub
					try {
						users=XmppTool.searchUsers(XmppTool.getConnection(), searchuser);
						
						handler.sendEmptyMessage(2);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
        		
        	}).start();
        }
               
    }
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what==1)
			{
				
				dialog = new ProgressDialog(SearchListActivity.this);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("我行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力为你加载搜索结果中......"); 
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
				dialog.cancel();
				SearchMessageViewAdapter mAdapter=new SearchMessageViewAdapter(SearchListActivity.this,users);
		        ListView mlist=(ListView)findViewById(R.id.searchlist);
		        mlist.setAdapter(mAdapter);
			}
			
		};
	};
}
