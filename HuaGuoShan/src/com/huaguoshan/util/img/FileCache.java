package com.huaguoshan.util.img;

import java.io.File;

import android.content.Context;

import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.util.Utils;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// 找一个用来缓存图片的路径
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));
			if(getFolderSize() > 100){
				clear();
			}
		}else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		
	}

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	
	public float getFolderSize(){
    	long size = 0;
    	try{
    		size = Utils.getFolderSize(cacheDir);
    	}catch(Exception e){
//    		
    	}
    	float f = ((float) size / (float) (1024 * 1024));
    	return f;
    }

}