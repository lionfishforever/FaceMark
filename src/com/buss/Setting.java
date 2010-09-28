package com.buss;

public final class Setting {
	public static final String VERSION = "V0.1.0.Beta.121130";
	
	public static final Boolean DEBUG = false;
	public static final Boolean NEED_FILTER = true;
	
	public static final String PHOTO_API_URL = DEBUG ? "http://192.168.94.19/yoyophoto/" : "http://api.pp.91.com/";//"http://192.168.9.128:8080/photo/"
	public static final String UAP_API_URL = DEBUG ? "http://192.168.94.19/uaps/" : "https://uap.91.com/";
	public static final String APP_ID = "46";
	public static final String PHOTO_THUMB_URL = DEBUG ? "http://192.168.94.19/yoyophoto/thumb/" : "http://img0.pp.91.com/thumb/";//http://192.168.9.128:8080/photo/thumb/
	public static final String PHOTO_UPLOAD_URL = DEBUG ? "http://192.168.94.19/yoyophoto/update/"/*+"91Album_setup_" +VERSION+".apk"*/ : "http://img0.pp.91.com/thumb/";//http://192.168.9.128:8080/photo/thumb/
	public static final int PHOTO_POS = 1000;
	
	public static final int MAX_THUMB_SIZE = 20480;
	public static final int MAX_ORIG_SIZE = 114400;
	public static final int MAX_IMG_WIDTH = 1024;
	public static final int MAX_IMG_HEIGHT = 1024;
	public static final int POST_LEN = 204800;
	
	public static final int PHOTO_SMALL = 160;
	public static final int PHOTO_BIG = 480;
	public static final int PHOTO_UPLOAD = 780;
	
	public static final int UPLOADWAY_ORIGIN = 0;
	public static final int UPLOADWAY_THUMB = 1;
	public static final int UPLOADWAY_AUTO = 2;
	public static int P_UPLOADWAY = 0;
	
	public static final int MAX_CAMERA_BOUND = 1600;
}
