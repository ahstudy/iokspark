package com.iok.spark;

import java.text.DecimalFormat;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.iok.spark.BaiduMapActivity.MyLocationListenner;
import com.iok.spark.BaiduMapActivity.locationOverlay;
import com.iokokok.app.Iokapplication;
import com.iokokok.util.BMapUtil;
import com.iokokok.util.BaiduUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FriendBaiduMapActivity extends Activity {
	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	LocationData mylocData=null;
	DistanceUtil myDistance=null;
	private String myaddress ="";
	private String friendaddress="";
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//���·�߽ڵ����
	Button mBtnPre = null;//��һ���ڵ�
	Button mBtnNext = null;//��һ���ڵ�
	int nodeIndex = -2;//�ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;//����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	TransitOverlay transitOverlay = null;//���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	int searchType = -1;//��¼���������ͣ����ּݳ�/���к͹���
	
	//��λͼ��
	locationOverlay myLocationOverlay = null;
	//��������ͼ��
	private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
	private TextView  popupText = null;//����view
	private View viewCache = null;
	BMapManager mBMapMan = null;
 	MapView mMapView = null;
 	private boolean isFirst=false;
 	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	@Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
    	Iokapplication app = (Iokapplication)this.getApplication(); 
    	//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
    	setContentView(R.layout.friendbaidumap);
    	double latitude=getIntent().getDoubleExtra("latitude", 0);
    	double longitude=getIntent().getDoubleExtra("longitude", 0);
    	friendaddress=getIntent().getStringExtra("myaddress");
    	mMapView=(MapView)findViewById(R.id.bmapsView);
    	//�����������õ����ſؼ�
    	MapController mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        createPaopao();
        isFirst=false;
        locData = new LocationData();
        mylocData = new LocationData();
        locData.latitude = latitude;
        locData.longitude = longitude;
        GeoPoint point =new GeoPoint((int)(latitude* 1E6),(int)(longitude* 1E6));  
	    //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)  
	    mMapController.setCenter(point);//���õ�ͼ���ĵ�  
	    //��λ��ʼ��
        mLocClient = new LocationClient( this );
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
        
        myDistance=new DistanceUtil();
        //��λͼ���ʼ��
      	myLocationOverlay = new locationOverlay(mMapView);
      	//���ö�λ����
      	myLocationOverlay.setData(locData);
      	mMapView.getOverlays().clear();
      	
      	//�����������
     // ��ʼ������ģ�飬ע���¼�����
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(FriendBaiduMapActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
			    routeOverlay = new RouteOverlay(FriendBaiduMapActivity.this, mMapView);
			    
			    // �˴���չʾһ��������Ϊʾ��
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    //�������ͼ��
			    mMapView.getOverlays().clear();
			    //���·��ͼ��
			    mMapView.getOverlays().add(routeOverlay);
			    //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			    //��·�����ݱ����ȫ�ֱ���
			    route = res.getPlan(0).getRoute(0);
			    //����·�߽ڵ��������ڵ����ʱʹ��
			    nodeIndex = -1;
			    MKRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "Ԥ�ƾ���Ϊ��"+myplan.getDistance()+",����ʱ���ţ�"+myplan.getTime(), 
			    		Toast.LENGTH_LONG).show();
			    //mBtnPre.setVisibility(View.VISIBLE);
				//mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(FriendBaiduMapActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
				
				searchType = 1;
				transitOverlay = new TransitOverlay (FriendBaiduMapActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
			    transitOverlay.setData(res.getPlan(0));
			  //�������ͼ��
			    mMapView.getOverlays().clear();
			  //���·��ͼ��
			    mMapView.getOverlays().add(transitOverlay);
			  //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
			  //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			  //����·�߽ڵ��������ڵ����ʱʹ��
			    nodeIndex = 0;
			    MKTransitRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "Ԥ�ƾ���Ϊ��"+myplan.getDistance()+",����ʱ���ţ�"+myplan.getTime(), 
			    		Toast.LENGTH_LONG).show();
			    //mBtnPre.setVisibility(View.VISIBLE);
				//mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(FriendBaiduMapActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(FriendBaiduMapActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				//�������ͼ��
			    mMapView.getOverlays().clear();
			  //���·��ͼ��
			    mMapView.getOverlays().add(routeOverlay);
			  //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			  //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			    //��·�����ݱ����ȫ�ֱ���
			    route = res.getPlan(0).getRoute(0);
			    //����·�߽ڵ��������ڵ����ʱʹ��
			    nodeIndex = -1;
			    MKRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "Ԥ�ƾ���Ϊ��"+myplan.getDistance()+",����ʱ���ţ�"+myplan.getTime(), 
			    		Toast.LENGTH_LONG).show();
			    //mBtnPre.setVisibility(View.VISIBLE);
				//mBtnNext.setVisibility(View.VISIBLE);
			    
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			public void onGetPoiDetailSearchResult(int type, int iError) {
				// TODO Auto-generated method stub
			}

			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub
				
			}
        });
      	
      	//��Ӷ�λͼ��
      	mMapView.getOverlays().add(myLocationOverlay);
      	myLocationOverlay.enableCompass();
      	
        //�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
      	mMapView.refresh();
      	//mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	}
	//���ؼ��¼�
	public void find_back(View v){
		this.finish();
	}
	
	//�ݳ�����
	public void car_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // ���üݳ�·���������ԣ�ʱ�����ȡ��������ٻ�������
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, start, null, end);
	}
	
	//����������
	public void bus_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // ���üݳ�·���������ԣ�ʱ�����ȡ��������ٻ�������
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.transitSearch(null, start, end);
	}
	
	//���е���
	public void walk_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // ���üݳ�·���������ԣ�ʱ�����ȡ��������ٻ�������
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.walkingSearch(null, start,null, end);
	}
	
	//�����ٶȵ�ͼ����
	public void car_gps_click(View v){
		/*

		 * ��������

		 * ���������յ㲻��Ϊ�գ���GPS����ʱ�������û�λ�õ��յ��ĵ�����

		 * ��GPS������ʱ����������㵽�յ���ģ�⵼����

		 */
		NaviPara para = new NaviPara();
		GeoPoint startpoint =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6)); 
        GeoPoint endpoint =new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); 

		para.startPoint = startpoint;           //�������
		para.startName= myaddress;
		para.endPoint  = endpoint; 
		para.endName   = friendaddress;      
		try {
		   //����ٶȵ�ͼ�ͻ��˵�������,����thisΪActivity�� 
		        BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
		        //�ڴ˴����쳣
		        e.printStackTrace();
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
				  builder.setMessage("����δ��װ�ٶȵ�ͼapp��app�汾���ͣ����ȷ�ϰ�װ��");
				  builder.setTitle("��ʾ");
				  builder.setPositiveButton("ȷ��", new OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
					 dialog.dismiss();
					 BaiduMapNavigation.GetLatestBaiduMapApp(FriendBaiduMapActivity.this);
				   }
				  });

				  builder.setNegativeButton("ȡ��", new OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				   }
				  });

				  builder.create().show();
		}
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
	/*
	 * Ҫ����overlay����¼�ʱ��Ҫ�̳�ItemizedOverlay
	 * ���������¼�ʱ��ֱ������ItemizedOverlay.
	 */
	class IokOverlay extends ItemizedOverlay<OverlayItem> {
	    //��MapView����ItemizedOverlay
	    public IokOverlay(Drawable mark,MapView mapView){
	            super(mark,mapView);
	    }
	    protected boolean onTap(int index) {
	        //�ڴ˴���item����¼�
	        System.out.println("item onTap: "+index);
	        return true;
	    }
	    public boolean onTap(GeoPoint pt, MapView mapView){
	        //�ڴ˴���MapView�ĵ���¼��������� trueʱ
	        super.onTap(pt,mapView);
	        return false;
	    }
	    
	}        
	
	/**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            mylocData.latitude = location.getLatitude();
            mylocData.longitude = location.getLongitude();
            myaddress=location.getAddrStr();
            GeoPoint startpoint =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6)); 
            GeoPoint endpoint =new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); 
            double mydistance=myDistance.getDistance(startpoint, endpoint);
            DecimalFormat df = new DecimalFormat("0.00");
            String distance=df.format(mydistance);
            if (isFirst==false){
	            if (mydistance>=1000){
	            	distance=df.format(mydistance/1000);
	            	Toast.makeText(FriendBaiduMapActivity.this, "����������֮��ľ���Ϊ��"+distance+"����", Toast.LENGTH_LONG).show();
	            }else{
	            	Toast.makeText(FriendBaiduMapActivity.this, "����������֮��ľ���Ϊ��"+distance+"��", Toast.LENGTH_LONG).show();
	            }
	            isFirst=true;
            }
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
