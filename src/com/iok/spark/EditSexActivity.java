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
        if (cursex.equals("��")){
        	sexman.setChecked(true);
        }else{
        	sexwoman.setChecked(true);
        }
    }
	
	public void edit_sex(View v){
		cursex="Ů";
		if (sexman.isChecked()){
			cursex="��";
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
				//���ý�������񣬷��ΪԲ�Σ���ת�� 
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				//����ProgressDialog ���� 
				dialog.setTitle("����"); 
				//����ProgressDialog ��ʾ��Ϣ 
				dialog.setMessage("����Ŭ���޸��û��Ա�......"); 
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
				InfoXiaohei.usersex.setText(cursex);
				dialog.cancel();
				EditSexActivity.this.finish();
			}
		}
   };
}
