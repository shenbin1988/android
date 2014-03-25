package com.huaguoshan.util.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class HttpConnect {
	public static String getContent(String url,List<? extends NameValuePair> parameters) {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost post=new HttpPost(url);
		
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 30000);

		try {
			UrlEncodedFormEntity sendDate = new UrlEncodedFormEntity(parameters);
			post.setEntity(sendDate);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, "UTF-8");
				}
			}
			
		} catch (Exception e) {

		}

		return null;
	}
	
	/**
	 * 获取图片资源
	 * @param url
	 * @return
	 */
	public static Bitmap getImage(String address,String filename){
		File file=new File(Environment.getExternalStorageDirectory()+"/chinanews",filename);
		try{
			FileOutputStream out=new FileOutputStream(file);
			URL url=new URL(address);
			
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode()==200){
				InputStream is=conn.getInputStream();
				byte[]result = getbytes(is);
				out.write(result);
				
				Bitmap bitmap=BitmapFactory.decodeStream(is);
				return bitmap;
			}
		}catch(Exception e){
			
		}
		
		return null;
	}
	
	public static boolean IsHaveInternet(final Context context) {  
        try {  
            ConnectivityManager manger = (ConnectivityManager)  
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);  
  
            NetworkInfo info = manger.getActiveNetworkInfo();  
            return (info!=null && info.isConnected());  
        } catch (Exception e) {  
            return false;  
        }
    }
	
	public static byte[] getbytes(InputStream is){
		try{
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			byte[] arr=new byte[1024];
			int len=-1;
			while((len=is.read(arr))!=-1){
				out.write(arr,0,len);
			}
			out.close();
			is.close();
			return out.toByteArray();
		}catch(Exception e){
			
		}
		
		return null;
	}

}
