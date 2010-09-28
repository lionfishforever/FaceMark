package com.buss;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public final class CommonUtil {
	private static String TAG = "CommonUtil";
	public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	public static String convertIntToString(int i) {
		return ((Integer)i).toString();
	}
	
	public static String addUriParam(String uri, String name, String value) {
		if(value != null && value.length() > 0) {
			Boolean hasParam = uri.indexOf("?") != -1;
			return uri + (hasParam ? "&" : "?") + name + "=" + value;
		} else {
			return uri;
		}
	}
	
	public static String addRandomString(String uri) {
		return addUriParam(uri, "nocache", (new Date()).getTime() + "");
	}
	
	public static String convertStringToBlowfish(String str) {
		return md5(str + ((Date)new Date()).getTime());
	}
	
	public static String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	    
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	public static boolean isWifiEnable( Context context ) {
		WifiManager wifiManager = (WifiManager)context
		.getSystemService(Context.WIFI_SERVICE);
		
		Log.i(TAG,"wifiEnable"+wifiManager.isWifiEnabled());
		return wifiManager.isWifiEnabled();
	}
	public static String getActiveNetWorkName( Context context ) {
	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    String result = null;
	    do {
	    	// 无连接
	    	if (connectivity == null) {
	    		break;
	    	}
	    	
	    	NetworkInfo[] info = connectivity.getAllNetworkInfo();    
	        if (info != null) {        
	            for (int i = 0; i < info.length; i++) {           
	            	
	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {              
	                	result = info[i].getTypeName(); 
	                }        
	            }     
	        }    
	    }while(false);
	   Log.i(TAG, "getActiveNetWorkName : "+ result);
	   return result;
	}
	
	public static JSONArray getJSONArrayData(JSONObject o)
	{	
		JSONArray jaResult = null;
		try
		{
			/*if (o.getInt("code") != 200)
			{
				Log.e(TAG, "code != 200");
			}*/
			
			jaResult = o.getJSONArray("data");
		}
		catch (Exception e) 
        {
           e.toString();
        }
		
		return jaResult;
	}
	
	public static JSONObject getJSONObjectData(JSONObject o) {
		JSONObject jResult = null;
		try
		{
			jResult = o.getJSONObject("data");
		}
		catch (Exception e) 
        {
           e.toString();
        }
		
		return jResult;
	}
	
//	public static String getImageByPicInfo(JSONObject o,String v)
//	{
//		String url = "";
//		try 
//		{	//http://192.168.9.128:Setting.PHOTO_SMALLSetting.PHOTO_SMALL/photo/pic/900_9333.jpg"
//			//url = //o.getJSONObject("thumb").getJSONObject(v).getString("url");
//			
//			url = o.getString("file_url");
//			url = url.split("/pic/")[0]+"/thumb/"+o.getString("pic_id")+"_"+v+".jpg";
//		}
//		catch (Exception e) 
//        {
//           e.toString();
//        }
//		return  url;
//	}
	
	
	public static String getThumbUrl(long pid, int size)
	{
		return Setting.PHOTO_THUMB_URL+ pid+ "_"+size+".jpg";
	}

	public static Bitmap getFileBitmap(String path, boolean thumb) {
		int maxSize = thumb ? Setting.MAX_THUMB_SIZE : Setting.MAX_ORIG_SIZE;
		File file = new File(path);
		
		Bitmap bitmap = null;
		
		if(file.length() > maxSize) {
			try {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = (int)file.length()/maxSize;
				InputStream is = new FileInputStream(path);
				bitmap=BitmapFactory.decodeStream(new FlushedInputStream(is),null,options);
				is.close();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		} else {
			bitmap = BitmapFactory.decodeFile(path);
		}
		
		if(bitmap != null && (thumb || (bitmap.getWidth() > Setting.MAX_IMG_WIDTH || bitmap.getHeight() > Setting.MAX_IMG_HEIGHT))) {
			boolean isLong = bitmap.getWidth() > bitmap.getHeight();
			float scale = (float)bitmap.getWidth()/(float)bitmap.getHeight();
			//Log.i(TAG, "scale " + scale + " width " + bitmap.getWidth() + " height " + bitmap.getHeight() + " path " + path);
			int boundWidth = thumb ? Setting.PHOTO_SMALL : Setting.PHOTO_BIG;
			int dWidth = (int)(isLong ? boundWidth*scale : boundWidth);
			int dHeight = (int)(isLong ? boundWidth : boundWidth*scale);
			bitmap = Bitmap.createScaledBitmap(bitmap, dWidth, dHeight, true);
		}
		
		Log.i(TAG, "width " + bitmap.getWidth() + " height " + bitmap.getHeight());
		
		return bitmap;
	}
	
	// 本地图片 test1
	//http://www.cnmsdn.com/html/201006/1276372230ID6068.html 文章
//	public static Bitmap getLocalBitmap(String path, int size) {
//		BitmapFactory.Options options = createBmOptions();
//
//		// 获取这个图片的宽和高
//		Bitmap bm = BitmapFactory.decodeFile(path, options); //此时返回bm为空
//
//		options = getBmOptions(options, size);
//		bm = BitmapFactory.decodeFile(path, options);
//		Log.i(TAG, "getLocalBitmap width " + bm.getWidth() + " height " + bm.getHeight());
//		
//		return bm;
//	}
	public static Bitmap getLocalBitmap(String path, int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.outHeight = size;

		options.inJustDecodeBounds = true;

		// 获取这个图片的宽和高
		Bitmap bm = BitmapFactory.decodeFile(path, options); //此时返回bm为空

		options.inJustDecodeBounds = false;
		int be = options.outHeight / (int)(size/10);
		if(be%10 !=0) 
			be+=10;

		be=be/10;
		if (be <= 0)
			be = 1;

		options.inSampleSize = be;
		bm = BitmapFactory.decodeFile(path, options);
		Log.i(TAG, "getLocalBitmap width " + bm.getWidth() + " height " + bm.getHeight());
		
		return bm;
	}
	
	public static BitmapFactory.Options createBmOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		return options;
	}
	
	public static BitmapFactory.Options getBmOptions(BitmapFactory.Options options, int size) {
		options.inJustDecodeBounds = false;
		int width_tmp=options.outWidth, height_tmp=options.outHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<size || height_tmp/2<size)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale++;
        }
//		int be = options.outHeight / (int)(size/10);
//		if(be%10 !=0) 
//			be+=10;
//
//		be=be/10;
//		if (be <= 0)
//			be = 1;

		options.inSampleSize = scale;
		
		return options;
	}
	
	
	public static Bitmap getFileBitmapSmall(String path) {
		return getLocalBitmap(path, Setting.PHOTO_SMALL);
//		return ThumbnailUtils.createImageThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
	}

	public static Bitmap getFileBitmapLarge(String path) {
		return getLocalBitmap(path, Setting.PHOTO_BIG);
//		return ThumbnailUtils.createImageThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
	}
	
	public static byte[] getUploadBytes(String path) {
		Bitmap bm = getLocalBitmap(path, Setting.PHOTO_UPLOAD);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, stream);
		
		byte[] result = stream.toByteArray();
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getFilterPath() {
//		return "download";
		return (Setting.NEED_FILTER ? "/DCIM/" : "");//Camera
	}
	
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}
	
	public static String getMD5Checksum(String filename) {
		String result = "";
		
		try {
			byte[] b = createChecksum(filename);
			for (int i = 0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String getMD5OfBytes(byte[] buffer) {
		String result = "";
		
		MessageDigest complete;
		try {
			complete = MessageDigest.getInstance("MD5");
			byte[] b = complete.digest(buffer);
			for (int i = 0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void DialogNetWork(final Activity context) {
		new AlertDialog.Builder(context)
		.setTitle("由于网络读写错误,或者服务器忙,连接服务器失败,请查看手机网络情况,稍后重新登录?")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				context.startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
			}})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			}}).show();	
	}
	
	public static void DialogExit(final Activity context) {
		new AlertDialog.Builder(context)
		.setTitle("确定退出程序?")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				context.finish();
			}})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			}}).show();
	}
}
