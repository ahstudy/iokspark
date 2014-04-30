package com.iokokok.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


 
public class Msg {
	String userid;//�����û�
	String msg;//��Ϣ����
	String date;//����ʱ��
	String from;//�����û�
	String type  =TYPE[2];//���� normal ��ͨ��Ϣ
	String receive;// ����
	String time;//����ʱ�����ļ���С
	String filePath;//�ļ�·��
	String bigfilepath;//��ͼƬ·��
	double latitude;//��γ��
	double longitude;//��γ��
	String myaddress;//��λ��ַ��Ϣ
	
	public static final String USERID ="userid";
	public static final String MSG_CONTENT ="msg";//��Ϣ����
	public static final String DATE ="date";
	public static final String FROM ="from";
	public static final String MSG_TYPE ="type";
	public static final String RECEIVE_STAUTS="receive";// ����״̬
	public static final String TIME_REDIO="time";
	public static final String FIL_PAHT="filePath";
	public static final String BIG_FIL_PATH="bigfilepath";
	public static final String LOC_ADDRESS="myaddress";
	public static final String LOC_LATITUDE="latitude";
	public static final String LOC_LONGITUDE="longitude";
	
	
	public String getMyAddress(){
		return myaddress;
	}
	public void setMyAddress(String myaddress){
		this.myaddress=myaddress;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public void setLatitude(double latitude){
		this.latitude=latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public void setLongitude(double longitude){
		this.longitude=longitude;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	public String getBigFilePath(){
		return bigfilepath;
	}
	
	public void setBigFilePath(String bigfilepath){
		this.bigfilepath=bigfilepath;
	}
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public static final String[] STATUS={"success","refused","fail","wait"};
	public static final String[] TYPE= {"record","photo","normal","maploc","video"};
	public static final String[] FROM_TYPE= {"IN","OUT"};

	public Msg(){
		
	}
	public Msg(String userid, String msg, String date, String from) {
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
	}
	
	public Msg(String userid, String msg,String myaddress, double latitude,double longitude,String date, String from,String type) {
		this.userid = userid;
		this.msg = msg;
		this.myaddress=myaddress;
		this.latitude=latitude;
		this.longitude=longitude;
		this.date = date;
		this.from = from;
		this.type=type;
	}
	 

	public Msg(String userid, String msg, String date, String from,
			String type, String receive, String time, String filePath) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.type = type;
		this.receive = receive;
		this.time = time;
		this.filePath = filePath;
	}
	public Msg(String userid, String msg, String date, String from,
			String type, String receive, String time, String filePath,String bigfilepath) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.type = type;
		this.receive = receive;
		this.time = time;
		this.filePath = filePath;
		this.bigfilepath=bigfilepath;
	}
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Msg [userid=" + userid + ", msg=" + msg +",myaddress="+myaddress+",latitude="+latitude+",longitude="+longitude+ ", date=" + date
				+ ", from=" + from + ", type=" + type + ", receive=" + receive
				+ ", time=" + time + ", filePath=" + filePath  + ", bigfilepath=" + bigfilepath +"]";
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	public static String[] getStatus() {
		return STATUS;
	}

	public Msg(String userid, String msg, String date, String from,
			String type, String receive) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.type = type;
		this.receive = receive;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * ������Ϣ����
	 * @param body
	 * Json
	 */
	public static Msg analyseMsgBody(String jsonStr) {
		Msg msg = new Msg();
		// ��ȡ�û�����Ϣ��ʱ�䡢IN
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			msg.setUserid(jsonObject.getString(Msg.USERID));
			msg.setFrom(jsonObject.getString(Msg.FROM));
			msg.setMsg(jsonObject.getString(Msg.MSG_CONTENT));
			msg.setDate(jsonObject.getString(Msg.DATE));
			msg.setType(jsonObject.getString(Msg.MSG_TYPE));
			msg.setReceive(jsonObject.getString(Msg.RECEIVE_STAUTS));
			if (jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[3])){
				msg.setMyAddress(jsonObject.getString(Msg.LOC_ADDRESS));
				msg.setLatitude(jsonObject.getDouble(Msg.LOC_LATITUDE));
				msg.setLongitude(jsonObject.getDouble(Msg.LOC_LONGITUDE));
			}
			if (jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[4])||jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[0])||jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[1])||jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[3])){
				msg.setTime(jsonObject.getString(Msg.TIME_REDIO));
				msg.setFilePath(jsonObject.getString(Msg.FIL_PAHT));
			}
			if (jsonObject.getString(Msg.MSG_TYPE).equals(Msg.TYPE[1])){
				msg.setBigFilePath(jsonObject.getString(Msg.BIG_FIL_PATH));
			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			return msg;
		}
		
		
		 
		
		 
		 
	}
	
	
	/**
	 * ��json 
	 */
	public static  String  toJson(Msg msg){
		JSONObject jsonObject=new JSONObject();
		String jsonStr="";
		try {
			jsonObject.put(Msg.USERID, msg.getUserid()+"");
			jsonObject.put(Msg.MSG_CONTENT, msg.getMsg()+"");
			jsonObject.put(Msg.LOC_ADDRESS, msg.getMyAddress()+"");
			jsonObject.put(Msg.LOC_LATITUDE, msg.getLatitude()+"");
			jsonObject.put(Msg.LOC_LONGITUDE, msg.getLongitude()+"");
			jsonObject.put(Msg.DATE, msg.getDate()+"");
			jsonObject.put(Msg.FROM, msg.getFrom()+"");
			jsonObject.put(Msg.MSG_TYPE, msg.getType()+"");
			jsonObject.put(Msg.RECEIVE_STAUTS, msg.getReceive()+"");
			jsonObject.put(Msg.TIME_REDIO, msg.getTime());
			jsonObject.put(Msg.FIL_PAHT, msg.getFilePath());
			jsonObject.put(Msg.BIG_FIL_PATH, msg.getBigFilePath());
			jsonStr= jsonObject.toString();
			Log.d("msg json", jsonStr+""); 
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			return jsonStr;
		}
	}
	
}