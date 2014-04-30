package com.iok.spark;

import org.jivesoftware.smack.XMPPException;

import com.iokokok.app.Iokapplication;
import com.iokokok.util.XmppTool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class EditSexActivity extends Activity {
	public Iokapplication iok=null;
	public ProgressDialog dialog ;
	public String cursex;
	private RadioButton sexman,sexwoman;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editsex);
        cursex=getIntent().getStringExtra("sex");
        sexman=(RadioButton)findViewById(R.id.radsexman);
        sexwoman=(RadioButton)findViewById(R.id.radsexwoman);
        if (cursex.equals("男")){
        	sexman.setChecked(true);
        }else{
        	sexwoman.setChecked(true);
        }
    }
	
	public void edit_sex(View v){
		cursex="女";
		if (sexman.isChecked()){
			cursex="男";
		}
		handler.sendEmptyMessage(1);
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				try {
					XmppTool.setUserSex(XmppTool.getConnection(), cursex);
					handler.sendEmptyMessage(2);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what==1){
				dialog = new ProgressDialog(EditSexActivity.this);
				//设置进度条风格，风格为圆形，旋转的 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//设置ProgressDialog 标题 
				dialog.setTitle("行聊"); 
				//设置ProgressDialog 提示信息 
				dialog.setMessage("正在努力修改用户性别......"); 
				//设置ProgressDialog 标题图标 
				dialog.setIcon(R.drawable.icon); 
				//设置ProgressDialog 的进度条是否不明确 
				dialog.setIndeterminate(false); 
				//设置ProgressDialog 是否可以按退回按键取消 
				dialog.setCancelable(true); 
				//显示 
				dialog.show(); 
			}else if(msg.what==2){
				Log.i("test:","thread is end");
				InfoXiaohei.usersex.setText(cursex);
				dialog.cancel();
				EditSexActivity.this.finish();
			}
		}
   };
}
