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
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	LocationData mylocData=null;
	DistanceUtil myDistance=null;
	private String myaddress ="";
	private String friendaddress="";
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//浏览路线节点相关
	Button mBtnPre = null;//上一个节点
	Button mBtnNext = null;//下一个节点
	int nodeIndex = -2;//节点索引,供浏览节点时使用
	MKRoute route = null;//保存驾车/步行路线数据的变量，供浏览节点时使用
	TransitOverlay transitOverlay = null;//保存公交路线图层数据的变量，供浏览节点时使用
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	int searchType = -1;//记录搜索的类型，区分驾车/步行和公交
	
	//定位图层
	locationOverlay myLocationOverlay = null;
	//弹出泡泡图层
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText = null;//泡泡view
	private View viewCache = null;
	BMapManager mBMapMan = null;
 	MapView mMapView = null;
 	private boolean isFirst=false;
 	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	@Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
    	Iokapplication app = (Iokapplication)this.getApplication(); 
    	//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
    	setContentView(R.layout.friendbaidumap);
    	double latitude=getIntent().getDoubleExtra("latitude", 0);
    	double longitude=getIntent().getDoubleExtra("longitude", 0);
    	friendaddress=getIntent().getStringExtra("myaddress");
    	mMapView=(MapView)findViewById(R.id.bmapsView);
    	//设置启用内置的缩放控件
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
	    //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
	    mMapController.setCenter(point);//设置地图中心点  
	    //定位初始化
        mLocClient = new LocationClient( this );
        mLocClient.registerLocationListener( myListener );
        setLocOption();
        mLocClient.start();
        
        myDistance=new DistanceUtil();
        //定位图层初始化
      	myLocationOverlay = new locationOverlay(mMapView);
      	//设置定位数据
      	myLocationOverlay.setData(locData);
      	mMapView.getOverlays().clear();
      	
      	//搜索相关设置
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
					Toast.makeText(FriendBaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
			    routeOverlay = new RouteOverlay(FriendBaiduMapActivity.this, mMapView);
			    
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
			    MKRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "预计距离为："+myplan.getDistance()+",花费时间大概："+myplan.getTime(), 
			    		Toast.LENGTH_LONG).show();
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
					Toast.makeText(FriendBaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				
				searchType = 1;
				transitOverlay = new TransitOverlay (FriendBaiduMapActivity.this, mMapView);
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
			    MKTransitRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "预计距离为："+myplan.getDistance()+",花费时间大概："+myplan.getTime(), 
			    		Toast.LENGTH_LONG).show();
			    //mBtnPre.setVisibility(View.VISIBLE);
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
					Toast.makeText(FriendBaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(FriendBaiduMapActivity.this, mMapView);
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
			    MKRoutePlan myplan=null;
			    myplan=res.getPlan(0);
			    Toast.makeText(FriendBaiduMapActivity.this, "预计距离为："+myplan.getDistance()+",花费时间大概："+myplan.getTime(), 
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
      	
      	//添加定位图层
      	mMapView.getOverlays().add(myLocationOverlay);
      	myLocationOverlay.enableCompass();
      	
        //修改定位数据后刷新图层生效
      	mMapView.refresh();
      	//mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	}
	//返回键事件
	public void find_back(View v){
		this.finish();
	}
	
	//驾车导航
	public void car_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, start, null, end);
	}
	
	//公交车导航
	public void bus_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.transitSearch(null, start, end);
	}
	
	//步行导航
	public void walk_click(View v){
		MKPlanNode start = new MKPlanNode();
		MKPlanNode end = new MKPlanNode();
		start.pt = new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); // 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		end.pt =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6));
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.walkingSearch(null, start,null, end);
	}
	
	//启动百度地图导航
	public void car_gps_click(View v){
		/*

		 * 导航参数

		 * 导航起点和终点不能为空，当GPS可用时启动从用户位置到终点间的导航，

		 * 当GPS不可用时，启动从起点到终点间的模拟导航。

		 */
		NaviPara para = new NaviPara();
		GeoPoint startpoint =new GeoPoint((int)(locData.latitude* 1E6),(int)(locData.longitude* 1E6)); 
        GeoPoint endpoint =new GeoPoint((int)(mylocData.latitude* 1E6),(int)(mylocData.longitude* 1E6)); 

		para.startPoint = startpoint;           //起点坐标
		para.startName= myaddress;
		para.endPoint  = endpoint; 
		para.endName   = friendaddress;      
		try {
		   //调起百度地图客户端导航功能,参数this为Activity。 
		        BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
		        //在此处理异常
		        e.printStackTrace();
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
				  builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
				  builder.setTitle("提示");
				  builder.setPositiveButton("确认", new OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
					 dialog.dismiss();
					 BaiduMapNavigation.GetLatestBaiduMapApp(FriendBaiduMapActivity.this);
				   }
				  });

				  builder.setNegativeButton("取消", new OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				   }
				  });

				  builder.create().show();
		}
	}
	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //泡泡点击响应回调
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
	/*
	 * 要处理overlay点击事件时需要继承ItemizedOverlay
	 * 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class IokOverlay extends ItemizedOverlay<OverlayItem> {
	    //用MapView构造ItemizedOverlay
	    public IokOverlay(Drawable mark,MapView mapView){
	            super(mark,mapView);
	    }
	    protected boolean onTap(int index) {
	        //在此处理item点击事件
	        System.out.println("item onTap: "+index);
	        return true;
	    }
	    public boolean onTap(GeoPoint pt, MapView mapView){
	        //在此处理MapView的点击事件，当返回 true时
	        super.onTap(pt,mapView);
	        return false;
	    }
	    
	}        
	
	/**
     * 定位SDK监听函数
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
	            	Toast.makeText(FriendBaiduMapActivity.this, "您跟您朋友之间的距离为："+distance+"公里", Toast.LENGTH_LONG).show();
	            }else{
	            	Toast.makeText(FriendBaiduMapActivity.this, "您跟您朋友之间的距离为："+distance+"米", Toast.LENGTH_LONG).show();
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
    //继承MyLocationOverlay重写dispatchTap实现点击处理
  	public class locationOverlay extends MyLocationOverlay{

  		public locationOverlay(MapView mapView) {
  			super(mapView);
  			// TODO Auto-generated constructor stub

  		}
  		@Override
  		protected boolean dispatchTap() {
  			// TODO Auto-generated method stub
  			//处理点击事件,弹出泡泡
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
    	//退出时销毁定位
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
