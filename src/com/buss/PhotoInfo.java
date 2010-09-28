package com.buss;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class PhotoInfo {
	private long mPicId = 0;
	private String mStatus = "";
	
	public String getStatus() {
		return mStatus;
	}
	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}


	private SoftReference<Bitmap> mBitmapThumb = null;
	private SoftReference<Bitmap> mBitmap = null;
	 
	public boolean mIsDownloading=false;
	
	
	public String getRemoteUrl(int size) {
		return CommonUtil.getThumbUrl(mPicId, size);
	}
	
	// mBitmapThumb handler
	public void setBitmap(Bitmap bitmapThumb) {
		if (bitmapThumb==null) return;
		mIsDownloading = false;
		mBitmapThumb = new SoftReference<Bitmap>(bitmapThumb);
		bitmapThumb = null;
	}
	public Bitmap getBitmap() {
		
		return mBitmapThumb==null?null:mBitmapThumb.get();
	}
	public void setBitmapLarge(Bitmap bitmap) {
		mIsDownloading = false;		
		mBitmap = new SoftReference<Bitmap>(bitmap);
		bitmap = null;
	}

	public Bitmap getBitmapLarge() {
		return mBitmap==null?null:mBitmap.get();
	}
	
	public void finalize() {
		if (mBitmap != null) {
			mBitmap = null;
		}
		if (mBitmapThumb != null) {
			mBitmapThumb = null;
		}
	}
	public void finalizeBig() {
		if (mBitmap != null) {
			mBitmap = null;
		}
	}
	
private String path;
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	private int size = 0;
	
	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
	
	public void setImageID(long imageID) {
		this.imageID = imageID;
	}

	public long getImageID() {
		return imageID;
	}

	private long imageID = 0;
	
	public void setThumbID(long thumbID) {
		this.thumbID = thumbID;
	}

	public long getThumbID() {
		return thumbID;
	}
	
	private long thumbID = 0;
	
	// 本地部分
	private Drawable drawable;

	
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	private String pathSmall;
	
	public void setPathSmall(String pathSmall) {
		this.pathSmall = pathSmall;
	}

	public String getPathSmall() {
		return pathSmall;
	}
	
	private String pathLarge;
	
	public void setPathLarge(String pathLarge) {
		this.pathLarge = pathLarge;
	}

	public String getPathLarge() {
		return pathLarge;
	}
}
