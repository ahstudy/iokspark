package com.iokokok.util;

import android.app.Activity;


import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaiduUtil {
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	public static double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
	{
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI)
		dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
		dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}
	public static double GetLongDistance(double lon1, double lat1, double lon2, double lat2)
	{
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
		distance = 1.0;
		else if (distance < -1.0)
		distance = -1.0;
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}
	public void getMapNavi(double mLat1,double mLon1,double mLat2,double mLon2,Activity myactivity){        
		int lat = (int) (mLat1 *1E6);
		int lon = (int) (mLon1 *1E6);           
		GeoPoint pt1 = new GeoPoint(lat, lon);
		lat = (int) (mLat2 *1E6);
		lon = (int) (mLon2 *1E6);
		GeoPoint pt2 = new GeoPoint(lat, lon);
		/*

		 * 导航参数

		 * 导航起点和终点不能为空，当GPS可用时启动从用户位置到终点间的导航，

		 * 当GPS不可用时，启动从起点到终点间的模拟导航。

		 */
		NaviPara para = new NaviPara();
		para.startPoint = pt1;           //起点坐标
		para.startName= "从这里开始";
		para.endPoint  = pt2;            //终点坐标
		para.endName   = "到这里结束";      
		try {
		   //调起百度地图客户端导航功能,参数this为Activity。 
		    BaiduMapNavigation.openBaiduMapNavi(para, myactivity);
		} catch (BaiduMapAppNotSupportNaviException e) {
		    //在此处理异常
		    e.printStackTrace();
		}

	}
}
