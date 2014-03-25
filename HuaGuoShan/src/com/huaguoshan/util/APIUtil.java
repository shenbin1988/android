package com.huaguoshan.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class APIUtil {
	
	public static final String URL = "http://r.ztmhs.com";
	public static final String SEND_CANDY = URL + "/send_candy";
	public static final String GET_RT = URL + "/get_rt";
	public static final String GET_COLLECT = URL + "/get_collect";
	public static final String GET_FEED = URL + "/get_feed";
	public static final String SEND_FEELING = URL + "/send_feeling";
	
	private Context context;
	private PackageManager packageManager;
	private PackageInfo packageInfo;
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private NetworkInfo networkInfo;
	private List<PackageInfo> appList = new ArrayList<PackageInfo>();
	
	private String v = "1.0";                   //API版本
	private String ts;                         	//请求时间错
	private String ipb = "testapp";				//应用渠道id
	private String ipv;							//应用版本信息
	private String ipk;							//应用包名
	private String idv;							//设备ID
	private String network;						//当前网络
	private String ua;							//User Agent
	private String pma;							//手机WIFI MAC
	private String uma;							//连接的wifiMAC
	private String ama;							//可连接的wifiMAC列表
	private String coord;						//经纬度
	private String coord_acc;					//经纬度精度
	private String region;						//详细地址
	private String apps;						//用户安装应用列表
	private String napps;					//用户当前活跃应用列表
	
	
	public APIUtil(Context context){
		this.context = context;
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			ipb = appInfo.metaData.getString("APP_CHANNEL_ID");
			packageManager = context.getPackageManager();
			List<PackageInfo> temp_apps = packageManager.getInstalledPackages(0);
			for (int i = 0; i < temp_apps.size(); i++) {
	            PackageInfo pak = (PackageInfo) temp_apps.get(i);
	            //判断是否为非系统预装的应用程序
	            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
	                // customs applications
	            	appList.add(pak);
	            }
	        }
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifiInfo =  wifiManager.getConnectionInfo();
			networkInfo = ((ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			init();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init(){
		ipv = getIpv();
		ipk = getIpk();
		idv = getIdv();
		ua = getUa();
	}
	
	public List<NameValuePair> getSendCandy(){
		ts = getTs();
		network = getNetwork();
		pma = getPma();
		uma = getUma();
		ama = getAma();
		apps = getApps();
		napps = getActiveApps();
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("v", v));
		values.add(new BasicNameValuePair("ts", ts));
		values.add(new BasicNameValuePair("ipb", ipb));
		values.add(new BasicNameValuePair("ipv", ipv));
		values.add(new BasicNameValuePair("ipk", ipk));
		values.add(new BasicNameValuePair("idv", idv));
		values.add(new BasicNameValuePair("network", network));
		values.add(new BasicNameValuePair("ua", ua));
		values.add(new BasicNameValuePair("pma", pma));
		values.add(new BasicNameValuePair("uma", uma));
		values.add(new BasicNameValuePair("ama", ama));
		if(!coord.equals("")){
			values.add(new BasicNameValuePair("coord", coord));
		}
		if(!coord_acc.equals("")){
			values.add(new BasicNameValuePair("coord_acc", coord_acc));
		}
		if(!region.equals("")){
			values.add(new BasicNameValuePair("region", region));
		}
		values.add(new BasicNameValuePair("apps", apps));
		values.add(new BasicNameValuePair("napps", napps));
		return values;
	}
	
	public List<NameValuePair> getRt(){
		ts = getTs();
		network = getNetwork();
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("v", v));
		values.add(new BasicNameValuePair("ts", ts));
		values.add(new BasicNameValuePair("ipb", ipb));
		values.add(new BasicNameValuePair("ipv", ipv));
		values.add(new BasicNameValuePair("ipk", ipk));
		values.add(new BasicNameValuePair("idv", idv));
		values.add(new BasicNameValuePair("network", network));
		values.add(new BasicNameValuePair("ua", ua));
		return values;
	}
	
	public List<NameValuePair> getCollect(){
		ts = getTs();
		network = getNetwork();
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("v", v));
		values.add(new BasicNameValuePair("ts", ts));
		values.add(new BasicNameValuePair("ipb", ipb));
		values.add(new BasicNameValuePair("ipv", ipv));
		values.add(new BasicNameValuePair("ipk", ipk));
		values.add(new BasicNameValuePair("idv", idv));
		values.add(new BasicNameValuePair("network", network));
		values.add(new BasicNameValuePair("ua", ua));
		return values;
	}
	
	public List<NameValuePair> getFeed(String sid, String fid){
		ts = getTs();
		network = getNetwork();
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("v", v));
		values.add(new BasicNameValuePair("ts", ts));
		values.add(new BasicNameValuePair("ipb", ipb));
		values.add(new BasicNameValuePair("ipv", ipv));
		values.add(new BasicNameValuePair("ipk", ipk));
		values.add(new BasicNameValuePair("idv", idv));
		values.add(new BasicNameValuePair("network", network));
		values.add(new BasicNameValuePair("ua", ua));
		values.add(new BasicNameValuePair("sid", sid));
		values.add(new BasicNameValuePair("fid", fid));
		return values;
	}
	
	public List<NameValuePair> getSendFeeling(String sid, String fid, String type, String content){
		ts = getTs();
		network = getNetwork();
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("v", v));
		values.add(new BasicNameValuePair("ts", ts));
		values.add(new BasicNameValuePair("ipb", ipb));
		values.add(new BasicNameValuePair("ipv", ipv));
		values.add(new BasicNameValuePair("ipk", ipk));
		values.add(new BasicNameValuePair("idv", idv));
		values.add(new BasicNameValuePair("network", network));
		values.add(new BasicNameValuePair("ua", ua));
		values.add(new BasicNameValuePair("sid", sid));
		values.add(new BasicNameValuePair("fid", fid));
		values.add(new BasicNameValuePair("type", type));
		values.add(new BasicNameValuePair("content", content));
		return values;
	}
	
	public String getTs(){
		return String.valueOf( System.currentTimeMillis() );
	}
	
	public String getIpv(){
		return packageInfo.versionName + "," + packageInfo.versionCode;
	}
	
	public String getIpk(){
		return packageInfo.packageName;
	}
	
	public String getIdv(){
		String imei = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return imei + "," + android_id;
	}

	public String getNetwork(){
		String network = "";
		//获取网络的状态信息，有下面三种方式
		if(networkInfo != null){
			if( networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
				network = "WIFI";
			}else{
				network = networkInfo.getExtraInfo();
			}
		}
		return network;
	}
	
	public String getUa(){
		//平台标示，SDK版本，系统版本，设备品牌，手机型号
		return "android," + android.os.Build.VERSION.SDK + "," + android.os.Build.VERSION.RELEASE + "," + android.os.Build.BRAND + "," + android.os.Build.MODEL;
	}
	
	//手机WIFI MAC
	private String getPma(){
		return wifiInfo.getMacAddress();
	}
	
	//连接的wifiMAC
	private String getUma(){
		return wifiInfo.getBSSID();
	}
	
	//可连接的wifiMAC列表
	private String getAma(){
		String macs = "";
		List<ScanResult> wifiList = wifiManager.getScanResults();
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult result = wifiList.get(i);
			if(i == 0){
				macs += result.BSSID;
			}else{
				macs += "," + result.BSSID;
			}
		}
		return macs;
	}
	
	//设置经纬度
	public void setCoord(String coord){
		this.coord = coord;
	}
	//设置经纬度
	public void setCoordAcc(String coord_acc){
		this.coord_acc = coord_acc;
	}
	//设置经纬度
	public void setRegion(String region){
		this.region = region;
	}
	
	//获取app列表
	@SuppressLint("NewApi")
	public String getApps(){
		String appsInfo = "";
		ArrayList<String> apps = new ArrayList<String>();
		for(int i = 0; i < appList.size(); i++){
			PackageInfo pak = (PackageInfo) appList.get(i);
			apps.add(pak.applicationInfo.loadLabel(packageManager).toString() + "|" 
					+ pak.packageName + "|" + pak.versionCode + "|" 
					+ pak.versionName + "|" + pak.firstInstallTime + "|" + pak.lastUpdateTime);
		}
		return TextUtils.join(",",apps);
	}
	
	@SuppressLint("NewApi")
	public String getActiveApps(){
		ArrayList<String> ac_apps = new ArrayList<String>();
		ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		//获取正在运行的应用
		List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		for(RunningAppProcessInfo ra : run){
			//这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
			if(ra.processName.equals("system") || ra.processName.equals("com.android.phone")){
				continue;
			}
			for(PackageInfo pak : appList){
				if(pak.packageName.equals(ra.processName)){
					ac_apps.add( pak.applicationInfo.loadLabel(packageManager).toString() + "|" 
						+ pak.packageName + "|" + pak.versionCode + "|" 
						+ pak.versionName + "|" + pak.firstInstallTime + "|" + pak.lastUpdateTime);
				}
			}
		}
		return TextUtils.join(",",ac_apps);
	}
}
