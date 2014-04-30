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
	int nodeIndex = -2;//节点索引,供浏览节点时使用
	MKRoute route = null;//保存驾车/步行路线数据的变量，供浏览节点时使用
	TransitOverlay transitOverlay = null;//保存公交路线图层数据的变量，供浏览节点时使用
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	public double lat1;
	public double lon1;
	int searchType = -1;//记录搜索的类型，区分驾车/步行和公交
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	//地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null;	// 地图View
	//搜索相关
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Iokapplication app = (Iokapplication)this.getApplication();
		setContentView(R.layout.baidurouteline);
		//初始化地图
        mMapView = (MapView)findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(false);
        mMapView.getController().setZoom(12);
        mMapView.getController().enableClick(true);
        lat1=getIntent().getDoubleExtra("latitude", 0);
        lon1=getIntent().getDoubleExtra("longitude", 0);
        createPaopao();
      //定位初始化
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
     // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(BaiduRouteLine.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
			    routeOverlay = new RouteOverlay(BaiduRouteLine.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    //清除其他图层
			    mMapView.getOverlays().clear();
			    //添加路线图层
			    mMapView.getOverlays().add(routeOverlay);
			    //执行刷新使生效
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    //移动地图到起点
			    mMapView.getController().animateTo(res.getStart().pt);
			    //将路线数据保存给全局变量
			    route = res.getPlan(0).getRoute(0);
			    //重置路线节点索引，节点浏览时使用
			    nodeIndex = -1;
			    //mBtnPre.setVisibility(View.VISIBLE);
				//mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(BaiduRouteLine.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				
				searchType = 1;
				transitOverlay = new TransitOverlay (BaiduRouteLine.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    transitOverlay.setData(res.getPlan(0));
			  //清除其他图层
			    mMapView.getOverlays().clear();
			  //添加路线图层
			    mMapView.getOverlays().add(transitOverlay);
			  //执行刷新使生效
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
			  //移动地图到起点
			    mMapView.getController().animateTo(res.getStart().pt);
			  //重置路线节点索引，节点浏览时使用
			    nodeIndex = 0;
			   // mBtnPre.setVisibility(View.VISIBLE);
				//mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(BaiduRouteLine.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(BaiduRouteLine.this, mMapView);
			    // 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				//清除其他图层
			    mMapView.getOverlays().clear();
			  //添加路线图层
			    mMapView.getOverlays().add(routeOverlay);
			  //执行刷新使生效
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			  //移动地图到起点
			    mMapView.getController().animateTo(res.getStart().pt);
			    //将路线数据保存给全局变量
			    route = res.getPlan(0).getRoute(0);
			    //重置路线节点索引，节点浏览时使用
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
	    option.setAddrType("all");//返回的定位结果包含地址信息
	    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	    option.setScanSpan(2000);//设置发起定位请求的间隔时间为5000ms
	    option.disableCache(true);//禁止启用缓存定位
	    option.setPoiNumber(5); //最多返回POI个数
	    option.setPoiDistance(1000); //poi查询距离
	    option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
	    option.setPriority(LocationClientOption.NetWorkFirst);
	    mLocClient.setLocOption(option);
	}
	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){
		
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
	}
	/**
	 * 发起路线规划搜索示例
	 * @param v
	 */
	public void searchCarRoute(double lat1,double lon1,double lat2,double lon2){
		//重置浏览节点的路线数据
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		MKPlanNode start = new MKPlanNode();
		start.pt = new GeoPoint((int) (lat1 * 1E6), (int) (lon1 * 1E6));
		MKPlanNode end = new MKPlanNode();
		end.pt = new GeoPoint((int) (lat2 * 1E6), (int) (lon2 * 1E6));// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, start, null, end);
	}
	
	
	/**
     * 定位SDK监听函数
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
