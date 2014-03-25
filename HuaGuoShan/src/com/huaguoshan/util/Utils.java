package com.huaguoshan.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class Utils {
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    /**
	 * 获取文件或目录实际大小
	 * @param file File
	 * @return long
	 * @throws Exception
	 */
	public static long getFolderSize(java.io.File file)throws Exception{
		long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
                size = size + getFolderSize(fileList[i]);
            } else
            {
                size = size + fileList[i].length();
            }
        }
        return size;
	}
	
	/**
	 * 转化文件的大小为M和K单位
	 * 
	 * @param size
	 * @return
	 */
	public static String setFileSize(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f = ((float) size / (float) (1024 * 1024));

		if (f < 1.0) {
			float f2 = ((float) size / (float) (1024));

			return df.format(new Float(f2).doubleValue()) + "KB";

		} else {
			return df.format(new Float(f).doubleValue()) + "M";
		}

	}
	
	public static String getTimeDifference(long before){
		long now = System.currentTimeMillis();
		long diff = now - before*1000;
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		String result = "刚刚";
		if(diffDays >= 30){
			result = "1个月前";
		}else if(diffDays >= 7){
			result = "一周前";
		}else if(diffDays > 0){
			result = diffDays + "天前";
		}else if(diffHours > 0){
			result = diffHours + "小时前";
		}else if(diffMinutes > 0){
			result = diffMinutes + "分钟前";
		}else if(diffSeconds > 0){
			result = diffSeconds + "秒前";
		}
		return result;
	}
}