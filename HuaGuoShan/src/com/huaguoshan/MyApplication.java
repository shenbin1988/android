package com.huaguoshan;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;


public class MyApplication extends FrontiaApplication {
	@Override
	public void onCreate(){ 
		super.onCreate();
		try{
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
			String api_key = appInfo.metaData.getString("api_key");
			Frontia.init(getApplicationContext(), api_key);
		}catch(Exception e){
			
		}
	}
}
