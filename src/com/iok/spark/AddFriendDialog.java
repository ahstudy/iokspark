package com.iok.spark;
import org.jivesoftware.smack.packet.Presence;

import com.iokokok.util.XmppTool;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddFriendDialog extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	private String from="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfrienddialog);
		//dialog=new MyDialog(this);
		from=getIntent().getStringExtra("from");
		TextView addmess=(TextView)findViewById(R.id.addmess);
		addmess.setText("用户"+from+"请求添加你为好友！");
		layout=(LinearLayout)findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {
		
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		Presence response = new Presence(Presence.Type.unsubscribed);
        response.setTo(from);
        XmppTool.getConnection().sendPacket(response);
		//finish();
		return true;
	}
	
	public void exitbutton1(View v) {
		Presence response = new Presence(Presence.Type.unsubscribed);
        response.setTo(from);
        XmppTool.getConnection().sendPacket(response);
    	this.finish();    	
      }  
	public void exitbutton0(View v) {  
		Presence response = new Presence(Presence.Type.subscribe);
        response.setTo(from);
        XmppTool.getConnection().sendPacket(response);
        XmppTool.addUser(XmppTool.getConnection().getRoster(), from, from.split("@")[0]);
    	this.finish();
    	
      }  
	
}
