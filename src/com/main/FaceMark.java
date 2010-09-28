package com.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.buss.PhotoAdapter;
import com.buss.PhotoInfo;
import com.main.R.id;

public class FaceMark extends Activity {
	private static final String TAG = "FaceMark";
    /** Called when the activity is first created. */
	private GridView mGridView;
	private Button mCamera;
	private PhotoAdapter mPhotoAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initView();
        setOnEventListen();
    }
    
    private void initView() {
    	mGridView = (GridView)this.findViewById(id.grid_photo);
    	mCamera = (Button)this.findViewById(id.btn_camera);
    	mPhotoAdapter = new PhotoAdapter(FaceMark.this);
    	mGridView.setAdapter(mPhotoAdapter);
    	mPhotoAdapter.mGridView = mGridView; 
    	
    	mPhotoAdapter.addLocalItems();
    }
    
    private void setOnEventListen() {
    	mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				PhotoInfo info = (PhotoInfo)(mPhotoAdapter.getItem(arg2)); 
				if (info.getBitmap() == null)
					return;
				navToMark(info.getPathLarge());
				
			}
		});
    	mCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNativeTakePictureApplication();
			}
		});
    }
	
    private void navToMark(String path) {
    	Intent intent = new Intent(FaceMark.this,MarkResult.class);
		intent.putExtra("path", path);
		startActivity(intent);
    }
    
    //  CAMERA
    private final static int REQUEST_TAKE_PICTURE = 0;
    Uri pictureUri;
    public boolean hasImageCaptureBug() { 
    	 
        // list of known devices that have the bug 
        ArrayList<String> devices = new ArrayList<String>(); 
        devices.add("android-devphone1/dream_devphone/dream"); 
        devices.add("generic/sdk/generic"); 
        devices.add("vodafone/vfpioneer/sapphire"); 
        devices.add("tmobile/kila/dream"); 
        devices.add("verizon/voles/sholes"); 
        devices.add("google_ion/google_ion/sapphire"); 
     
        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/" 
                + android.os.Build.DEVICE); 
     
    }
    // SHOW
    private void showNativeTakePictureApplication(){
    	Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
    	if (hasImageCaptureBug()) { 
    	    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp"))); 
    	} else { 
    	    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
    	} 
    	startActivityForResult(i, REQUEST_TAKE_PICTURE); 
    }
    
    private void onPictureTaken() {
        Bitmap selectedBitmap = null;
        try {
            selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
            Log.i("camera", selectedBitmap.getWidth()+":"+selectedBitmap.getHeight());
//            selectedBitmap = ImageManager.resize(selectedBitmap);
//
//            ImageLoader.saveImage(pictureUri.toString(), selectedBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "not saved", Toast.LENGTH_SHORT).show();   
        } finally {
            if (selectedBitmap != null) {
                selectedBitmap.recycle();
            }
            selectedBitmap = null;
        }
    }
    
    // BACK
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	Uri u; 
        	if (hasImageCaptureBug()) {
        		File fi = new File("/sdcard/tmp");
        		try { 
        			u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null,null)); 
                        if (!fi.delete()) { 
                        } 
                    } catch (FileNotFoundException e) { 
                        e.printStackTrace(); 
                    } 
                } 
        	else {
        		u = intent.getData();
        		Bitmap bitmap = (Bitmap)intent.getExtras().get("data");
        		Log.i("camera", bitmap.getWidth()+":"+bitmap.getHeight());
        	}
    }
}
	        // on activity return                       
//	         File f = new File(SD_CARD_TEMP_DIR);                       
//	          try {                 
//	      		String timestamp = DateFormat.format("yyyy-MM-dd_kk.mm.ss", System.currentTimeMillis()).toString();
//	      		Bitmap bitmap = (Bitmap)intent.getExtras().get("data");
//	      		Log.i("camera", bitmap.getWidth()+":"+bitmap.getHeight());
//	                //Uri capturedImage =     Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),  f.getAbsolutePath(), null, null));
//	        	  Uri capturedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), 
//	      				bitmap, timestamp, "Taken by 91Album"));
//	                 Log.i("camera", "Selected image: " + capturedImage.toString());
//	                 f.delete();      
//	                 String[] projection = {MediaStore.Images.Media._ID, 
//	         				MediaStore.Images.Media.DATA,
//	         				MediaStore.Images.Media.SIZE};
//	         		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), capturedImage, projection);
//	         		if(cursor != null) {
//	         			if(cursor.moveToNext()) {
//	         				String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//	         				int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
//	         				long imageID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//	         				
//	         				if(size == 0) {
//	         					File tmp = new File(path);
//	         					size = (int)tmp.length();
//	         				}
//	         				
//	         				PhotoInfo photoInfo = new PhotoInfo();
//	         				photoInfo.setPath(path);
//	         				photoInfo.setImageID(imageID);
//	         				mPhotoAdapter.addLocalItem(photoInfo);
//	         				navToMark(path);
//	         				
//	         			}
//	         			cursor.close();
//	         			
//	         		}
//	          } 
//	         catch (Exception e) {                            
//	         // TODO Auto-generated catch block                           
//	          e.printStackTrace();                        
//	          }               
//	           }                
//	      else {                        
//	      Log.i("Camera", "Result code was " + resultCode);                
//	      }        
//	      }      
	  
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	 Bundle dd;
//    	 if (requestCode == ACTIVITY_TAKE_PICTURE_WITH_INTENT) {                
//    	     if (resultCode == Activity.RESULT_OK) {
//    	    	 //Log.i(TAG,data.toString()+ data.getExtras().toString()+data.getStringExtra("path")+data.getStringExtra("url")+data.getStringExtra("data"));
//    	    	 dd = data.getExtras();
//    	    	 Log.i(TAG,data.toString());
//    	    	 //navToMark();
//    	     }
//    	 }
//    }
//    	         File f = new File(SD_CARD_TEMP_DIR);                       
//    	          try {     Uri capturedImage =     Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),  f.getAbsolutePath(), null, null));                               
//                  Log.i("camera", "Selected image: " + capturedImage.toString());                            
//                  f.delete();                        
//                  } 
//          catch (FileNotFoundException e) {                            
//          // TODO Auto-generated catch block                           
//           e.printStackTrace();                        
//           }               
//            }                
//       else {                        
//       Log.i("Camera", "Result code was " + resultCode);                
//       }        
//       }            
//       }                        
//    }