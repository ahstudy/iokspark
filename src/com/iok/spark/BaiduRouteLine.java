package com.iok.spark;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.iokokok.app.Iokapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BaiduRouteLine extends Activity {
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	int nodeIndex = -2;//�ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;//����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	TransitOverlay transitOverlay = null;//���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	public double lat1;
	public double lon1;
	int searchType = -1;//��¼���������ͣ����ּݳ�/���к͹���
	private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
	//��ͼ��أ�ʹ�ü̳�MapView��MyRouteMapViewĿ������дtouch�¼�ʵ�����ݴ���
	//���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MapView mMapView = null;	// ��ͼView
	//�������
	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Iokapplication app = (Iokapplication)this.getApplication();
		setContentView(R.layout.baidurouteline);
		//��ʼ����ͼ
        mMapView = (MapView)findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(false);
        mMapView.getController().setZoom(12);
        mMapView.getController().enableClick(true);
        lat1=getIntent().getDoubleExtra("latitude", 0);
        lon1=getIntent().getDoubleExtra("longitude", 0);
        createPaopao();
      //��λ��ʼ��
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
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
					Toast.makeText(BaiduRouteLine.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
			    routeOverlay = new RouteOverlay(BaiduRouteLine.this, mMapView);
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
					Toast.makeText(BaiduRouteLine.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
				
				searchType = 1;
				transitOverlay = new TransitOverlay (BaiduRouteLine.this, mMapView);
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
			   // mBtnPre.setVisibility(View.VISIBLE);
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
					Toast.makeText(BaiduRouteLine.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(BaiduRouteLine.this, mMapView);
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
	/**
	 * ������������ͼ��
	 */
	public void createPaopao(){
		
        //���ݵ����Ӧ�ص�
        PopupClickListener popListener = new PopupClickListener(){
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
	}
	/**
	 * ����·�߹滮����ʾ��
	 * @param v
	 */
	public void searchCarRoute(double lat1,double lon1,double lat2,double lon2){
		//��������ڵ��·������
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		MKPlanNode start = new MKPlanNode();
		start.pt = new GeoPoint((int) (lat1 * 1E6), (int) (lon1 * 1E6));
		MKPlanNode end = new MKPlanNode();
		end.pt = new GeoPoint((int) (lat2 * 1E6), (int) (lon2 * 1E6));// ���üݳ�·���������ԣ�ʱ�����ȡ��������ٻ�������
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, start, null, end);
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
            searchCarRoute(lat1,lon1,location.getLatitude(),location.getLongitude());
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
}
