package com.buss;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.R.id;
public class PhotoAdapter extends BaseAdapter {
	private final static String TAG = "PhotoAdapter";
	private Context mContext;
	
	private List<PhotoInfo> mItems = new ArrayList<PhotoInfo>();
	private short mThreadCount = 0;
	private static final int THREAD_TOTAL = PhotoLoader.THREAD_COUNT;
	
	public PhotoAdapter(Context context) {
		this.mContext = context;
	}
	public boolean mIsScrolling = false;
	public void finalize() {
		if (mItems==null) return;
		Log.i(TAG, "finalize");
		for (PhotoInfo item : mItems) {
			item.finalize();
		}
		mItems = null;
	}
	@Override
	public int getCount() {
		if (mItems==null) return 0;
		return mItems.size();
	}
	public GridView mGridView = null;  
	@Override
	public Object getItem(int position) {
		if (mItems==null) return null;
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public ArrayList<PhotoInfo> getItemList(){
		return (ArrayList<PhotoInfo>)mItems;
	}
	private PhotoLoader.PhotoLoaderCallback photoloaderCallback = new PhotoLoader.PhotoLoaderCallback() {
		public void callback(Bitmap bmp,int callbackpos) {
			Log.i(TAG, "download done callback pos="+callbackpos+" pic count ");
			if (mItems!=null) {
				PhotoInfo photoInfo = mItems.get(callbackpos);
				photoInfo.setBitmap(bmp);
				photoInfo.setStatus("");
				
				int fpos = mGridView.getFirstVisiblePosition();
				int lpos = mGridView.getLastVisiblePosition();				
				if (callbackpos >= fpos && callbackpos <= lpos)
					notifyDataSetChanged();
			}
			--mThreadCount;
		}
	};
	

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//1 释放过多的图牄1�7
		//releasePic(); modify to : use soft reference to release memory. 
		
		//2 start thread to get photo
		PhotoInfo photoInfo = mItems.get(position);
		if (!mIsScrolling && photoInfo.mIsDownloading == false && photoInfo.getBitmap() == null && mThreadCount < THREAD_TOTAL )
		{
			photoInfo.mIsDownloading = true;
			photoInfo.setStatus("Loading");
			//2.2	start thread
			
			PhotoLoader.loadPhoto(photoInfo.getImageID(), photoInfo.getPathSmall(),position, true, photoloaderCallback);
			
			++mThreadCount;
		}
		
		//3 视图部分
		ViewGroup itemView;// = (ViewGroup)ViewCreator.getPhotoView(mContext.getApplicationContext());;
		if(convertView == null) {
			itemView = (ViewGroup)View.inflate(mContext, com.main.R.layout.item_photo, null);
		} else {
			itemView = (ViewGroup)convertView;
		}
		ImageView thumb = (ImageView)itemView.findViewById(id.item_photo_img);
		thumb.setImageBitmap(photoInfo.getBitmap());
		TextView textView =  (TextView)itemView.findViewById(id.item_photo_txt);
		textView.setText(photoInfo.getStatus());
		return itemView;
	}

	
	
	public void addLocalItems() {
		//添加本地照片
		Log.i(TAG, "get local start");
		ContentResolver resolver = this.mContext.getApplicationContext().getContentResolver();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] projection = {
								MediaStore.Images.Media._ID,
								MediaStore.Images.Media.DATA, 
								MediaStore.Images.Media.SIZE, 
								MediaStore.Images.Media.TITLE, 
								MediaStore.Images.Media.ORIENTATION,
								MediaStore.Images.Media.BUCKET_ID,
								MediaStore.Images.Media.MINI_THUMB_MAGIC};
		String selection = MediaStore.Images.Media.DATA + " like '%%%" + CommonUtil.getFilterPath() + "%%'";
		String[] selectionArgs = {};
		String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC," + MediaStore.Images.Media._ID + " DESC";//MediaStore.Images.Media.DEFAULT_SORT_ORDER;
		
		Log.d(TAG, "query local cursor start");
		Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		
		String path;
		int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
		int imageIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
		int thumbIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
		
		int initedLocalCount = 0;
		while(cursor.moveToNext()) {
			if(mItems == null) break;
			PhotoInfo photoInfo = new PhotoInfo();
			path =cursor.getString(dataIndex);
			photoInfo.setPathLarge(path);
			photoInfo.setPath(path);
			//photoInfo.setUploadStatus(AlbumDBMgr.isUploaded(Long.valueOf(AlbumHandler.getInstance().mUid), path) ? PhotoInfoBase.UPLOADED : PhotoInfoBase.UNUPLOAD);
			photoInfo.setSize(cursor.getInt(sizeIndex));
			photoInfo.setThumbID(cursor.getInt(thumbIDIndex));
			photoInfo.setImageID(cursor.getInt(imageIDIndex));
			
			int thumbID = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC));
			photoInfo.setThumbID(thumbID);
			
			addLocalItem(photoInfo);
			
			if(++initedLocalCount == 20) {
				//this.notifyDataSetChanged();
			}
		}
		
		cursor.close();
		Log.d(TAG, "query local cursor end");
		Log.i(TAG, "get local end");
	}
	
	public void addLocalItem(PhotoInfo photoInfo) {
		
		if(photoInfo.getSize() == 0) {
			File pfile = new File(photoInfo.getPath());
			photoInfo.setSize((int)pfile.length());
		}
		
		Uri uriThumb = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		String[] projectionThumb = {MediaStore.Images.Thumbnails.DATA};
		String selectionThumb = MediaStore.Images.Thumbnails.IMAGE_ID + " = " + photoInfo.getImageID() + " and " + MediaStore.Images.Thumbnails.KIND + " = " + MediaStore.Images.Thumbnails.MINI_KIND;
		String[] selectionArgsThumb = {};
		String sortOrderThumb = MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER;
		Cursor cursorThumb  = this.mContext.getApplicationContext().getContentResolver().query(uriThumb, 
				projectionThumb, selectionThumb, selectionArgsThumb, sortOrderThumb);
		Log.i(TAG, "query thumb start");
		//cursorThumb = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver, imageID, MediaStore.Images.Thumbnails.MINI_KIND, projectionThumb);
		Log.i(TAG, "query thumb end");
		if(cursorThumb != null && cursorThumb.moveToNext()) {
			int dataIndexThumb = cursorThumb.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
			String thumbPath = cursorThumb.getString(dataIndexThumb);
			Log.i(TAG, "imageID" + photoInfo.getImageID() + "PATH" + thumbPath);
			photoInfo.setPathSmall(thumbPath);
		} else {
			Log.i(TAG, "imageID" + photoInfo.getImageID() + "thumb not found" + photoInfo.getPath());
		}
		cursorThumb.close();
		Log.i(TAG, "query thumb real end");
		
		//if no small path
		if(photoInfo.getPathLarge() == null || photoInfo.getPathLarge().length() == 0) {
			photoInfo.setPathLarge(photoInfo.getPath());
		}
		
		if(photoInfo.getPathSmall() == null || photoInfo.getPathSmall().length() == 0) {
			photoInfo.setPathSmall(photoInfo.getPath());
		}
		
		if (mItems == null) return;
		mItems.add(photoInfo);
	}
	
	public void refreshLocalItems() {
		mItems.removeAll(mItems);
		this.addLocalItems();
	}
	
	public int selectedCount = 0;
}
