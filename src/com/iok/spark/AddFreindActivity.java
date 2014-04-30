package com.iok.spark;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ReportedData.Row;

import com.iokokok.user.SearchMessage;
import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddFreindActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findfriends);
    }
	
	public void addfriend(){
		
	}
	
	public void find_friend(View v){
		EditText usertxt=(EditText)findViewById(R.id.find_user_edit);
		if(usertxt.getText().equals("")){
			Toast.makeText(this, "请输入你要添加的用户名", Toast.LENGTH_LONG).show();
		}else{
			Intent intent = new Intent();   
            intent.setClass(AddFreindActivity.this, SearchListActivity.class);  
            intent.putExtra("searchuser",usertxt.getText().toString());  
            startActivity(intent);  
		}
			
	}
	
	public void find_back(View v){
		AddFreindActivity.this.finish();
	}
	
}
