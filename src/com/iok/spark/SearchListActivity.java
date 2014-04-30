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
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("������"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ��Ϊ��������������......"); 
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
				dialog.cancel();
				SearchMessageViewAdapter mAdapter=new SearchMessageViewAdapter(SearchListActivity.this,users);
		        ListView mlist=(ListView)findViewById(R.id.searchlist);
		        mlist.setAdapter(mAdapter);
			}
			
		};
	};
}
