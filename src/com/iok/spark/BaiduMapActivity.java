package com.iok.spark;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.iokokok.app.Iokapplication;
import com.iokokok.util.BMapUtil;
/**
 * ��demo����չʾ��ν�϶�λSDKʵ�ֶ�λ����ʹ��MyLocationOverlay���ƶ�λλ��
 * ͬʱչʾ���ʹ���Զ���ͼ����Ʋ����ʱ��������
 *
 */
public class BaiduMapActivity extends Activity {
	
	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	private String myaddress ="";
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//��λͼ��
	locationOverlay myLocationOverlay = null;
	//��������ͼ��
	private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
	private TextView  popupText = null;//����view
	private View viewCache = null;
	
	//��ͼ��أ�ʹ�ü̳�MapView��MyLocationMapViewĿ������дtouch�¼�ʵ�����ݴ���
	//���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MyLocationMapView mMapView = null;	// ��ͼView
	private MapController mMapController = null;

	//UI���
	OnCheckedChangeListener radioButtonListener = null;
	Button requestLocButton = null;
	boolean isRequest = false;//�Ƿ��ֶ���������λ
	boolean isFirstLoc = true;//�Ƿ��״ζ�λ
	BMapManager mBMapMan = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iokapplication app = (Iokapplication)this.getApplication();  
        setContentView(R.layout.baidumap);
     
        
		//��ͼ��ʼ��
        mMapView = (MyLocationMapView)findViewById(R.id.bmapsView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
      //���� ��������ͼ��
        createPaopao();
        
        //��λ��ʼ��
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
       
        //��λͼ���ʼ��
		myLocationOverlay = new locationOverlay(mMapView);
		//���ö�λ����
	    myLocationOverlay.setData(locData);
	    //��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		
		//�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();
		
    }
    
    
    //�ϴ�λ����Ϣ
    
    public void update_loc(View v){
    	Toast.makeText(BaiduMapActivity.this, "����ǰλ�ã�"+myaddress, Toast.LENGTH_LONG).show();
    	Intent data=new Intent();  
    	data.putExtra("latitude", locData.latitude);
    	data.putExtra("longitude", locData.longitude);
        data.putExtra("myaddress", myaddress); 
        //�����������Լ����ã��������ó�20  
        setResult(103, data);  
		BaiduMapActivity.this.finish();
    }
    public void setLocOption(){
	    LocationClientOption option = new LocationClientOption();
	    option.setOpenGps(true);
	    option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
	    option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	    option.setScanSpan(2000);//���÷���λ����ļ��ʱ��Ϊ5000ms
	    option.disableCache(true);//��ֹ���û��涨λ
	    option.setPoiNumber(5); //��෵��POI����
	    option.setPoiDistance(1000); //poi��ѯ����
	    option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
	    option.setPriority(LocationClientOption.NetWorkFirst);
	    mLocClient.setLocOption(option);
	}
    
    
    //���ؼ�
    public void back_btn(View v){

    	Intent data=new Intent();  
    	data.putExtra("latitude", 0);
    	data.putExtra("longitude", 0);
        data.putExtra("myaddress", "");  
        setResult(103, data);  
		BaiduMapActivity.this.finish();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
    	if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
    		Intent data=new Intent();  
        	data.putExtra("latitude", 0);
        	data.putExtra("longitude", 0);
            data.putExtra("myaddress", "");  
            setResult(103, data);  
    	}

    	return super.onKeyDown(keyCode, event);
    	}
    /**
     * �ֶ�����һ�ζ�λ����
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(BaiduMapActivity.this,"���ڶ�λ����", Toast.LENGTH_SHORT).show();
    }
    /**
     * �޸�λ��ͼ��
     * @param marker
     */
    public void modifyLocationOverlayIcon(Drawable marker){
    	//������markerΪnullʱ��ʹ��Ĭ��ͼ�����
    	myLocationOverlay.setMarker(marker);
    	//�޸�ͼ�㣬��Ҫˢ��MapView��Ч
    	mMapView.refresh();
    }
    /**
	 * ������������ͼ��
	 */
	public void createPaopao(){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //���ݵ����Ӧ�ص�
        PopupClickListener popListener = new PopupClickListener(){
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
        MyLocationMapView.pop = pop;
	}
	/**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //�������ʾ��λ����Ȧ����accuracy��ֵΪ0����
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myaddress=location.getAddrStr();
            //���¶�λ����
            myLocationOverlay.setData(locData);
            //����ͼ������ִ��ˢ�º���Ч
            mMapView.refresh();
            //���ֶ�����������״ζ�λʱ���ƶ�����λ��
            if (isRequest || isFirstLoc){
            	//�ƶ���ͼ����λ��
                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                popupText.setBackgroundResource(R.drawable.popup);
    			popupText.setText(myaddress);
    			pop.showPopup(BMapUtil.getBitmapFromView(popupText),
    					new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
    					8);
                isRequest = false;
            }
            //�״ζ�λ���
            isFirstLoc = false;
            
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    //�̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
  	public class locationOverlay extends MyLocationOverlay{

  		public locationOverlay(MapView mapView) {
  			super(mapView);
  			// TODO Auto-generated constructor stub

  		}
  		@Override
  		protected boolean dispatchTap() {
  			// TODO Auto-generated method stub
  			//�������¼�,��������
  			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText(myaddress);
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),
					new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
					8);
  			return true;
  		}
  		
  	}

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//�˳�ʱ���ٶ�λ
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
/**
 * �̳�MapView��дonTouchEventʵ�����ݴ������
 * @author hejin
 *
 */
class MyLocationMapView extends MapView{
	static PopupOverlay   pop  = null;//��������ͼ�㣬���ͼ��ʹ��
	public MyLocationMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public MyLocationMapView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	@Override
    public boolean onTouchEvent(MotionEvent event){
		if (!super.onTouchEvent(event)){
			//��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}

