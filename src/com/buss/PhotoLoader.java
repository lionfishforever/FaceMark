package com.buss;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PhotoLoader {
	private static final String TAG = "PhotoLoader";
	public static int THREAD_COUNT = 8;
	/** --------all
	 *  path : the target bitmap's path
	 *  position : for callback 
	 * 	--------net part 
	 * 	pid	 : pic_id
	 * 	size : Setting.PHOTO_SMALL/160.....
	*/   
	public static void loadPhoto(long pid, int size,int degree ,int position, PhotoLoaderCallback callback) {
		mThreadPool.execute(new ThreadPoolTask(pid, size, degree, position, callback));
	}
	public static void loadPhoto(String path ,int position, boolean thumb, PhotoLoaderCallback callback) {
		mThreadPool.execute(new ThreadPoolTask(path, position, thumb, callback));
	}
	public static void loadPhoto(long id ,String path , int position, boolean thumb, PhotoLoaderCallback callback) {
		mThreadPool.execute(new ThreadPoolTask(id, path, position, thumb, callback));
	}
	
	// callback
	public interface  PhotoLoaderCallback {
		public void callback(Bitmap bmp,int position);
	}
	
	private static ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 3,
			TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100),
			new ThreadPoolExecutor.DiscardOldestPolicy()); 
	
	public static class ThreadPoolTask implements Runnable, Serializable {
		private Bitmap mBmp = null;
		
		// for download  
		private String mLocalPath = "";
		private boolean mThumb = true;
		private long mImageID = 0;
		
		// for save to local
		private long mPid = 0;
		private int mSize = 0;// for callback 
		private int mDegree = 0;
		private int mPosition = 0;
		private PhotoLoaderCallback mCallback = null;
		
		private Handler handler = new Handler(){
			  public void  handleMessage(Message message){
				  mCallback.callback(mBmp,mPosition);
				  super.handleMessage(message);
			}
		};
			
		private static final long serialVersionUID = 0;
		private boolean mIsNet;
		public ThreadPoolTask(long pid, int size,int degree ,int position, PhotoLoaderCallback callback) {
			mPid = pid;
			mSize = size;
			mDegree = degree;
			mPosition = position;
			mCallback = callback;
			
			mIsNet = true;
		}
		
		public ThreadPoolTask(String path ,int position, boolean thumb, PhotoLoaderCallback callback){
			mLocalPath = path;
			mPosition = position;
			mThumb = thumb;
			mCallback = callback;
			
			mIsNet = false;
		}
		public ThreadPoolTask(long id , String path , int position, boolean thumb, PhotoLoaderCallback callback){
			mImageID = id;
			mLocalPath = path;
			mPosition = position;
			mThumb = thumb;
			mCallback = callback;
			
			mIsNet = false;
		}
		public void run() {
			try {
				if (mIsNet) {
					//LoadNet();
				}
				else {
					LoadLocal();
				}
			} catch(Exception e) {
				Log.e(TAG, "run");
				e.printStackTrace();
			}
			finally{
				if (mCallback!=null) {
					handler.sendMessage(new Message());
				}
			}
		} 
			//threadPoolTaskData = null;
//		public void LoadNet() throws Exception{
//				Log.i("PhotoAdapter", "run pos="+mPosition + " pid=" + mPid);  
//				// 读取本地图片
//				mBmp = ImageIOUtil.loadBitmap(mPid, mSize,mDegree);
//
//				//读取远程图片
//				if (mBmp==null) {
//					URL aryURI = null;
//					URLConnection conn = null;
//					InputStream is = null;
//					
//					aryURI = new URL(CommonUtil.addUriParam(CommonUtil.getThumbUrl(mPid, mSize), "sid", AlbumHandler.getInstance().mSid));
//					conn = aryURI.openConnection();
//					conn.connect(); 
//					is = conn.getInputStream();
//					
//					mBmp = BitmapFactory.decodeStream(new FlushedInputStream(is)); 
//					is.close();
//					//保存到本地
//					ImageIOUtil.saveBitmap(mBmp, mPid, mSize);
//					if (mDegree!=0){
//						mBmp = ImageIOUtil.rotateBitmap(mBmp, mDegree);
//					}
//				}
//				
//				Log.i("PhotoAdapter", "run done pos="+mPosition+mBmp.toString());
//		}
		
		public void LoadLocal() {
			Log.i("PhotoAdapter", "doInBackground start pos="+mPosition + "imageid:" + mImageID);  
	
			// 读取本地图片
			if(mThumb) {
				mBmp = CommonUtil.getFileBitmapSmall(mLocalPath);
			} else {
				mBmp = CommonUtil.getFileBitmapLarge(mLocalPath);
			}
		}
	}
}
