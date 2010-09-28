package com.main;

import com.buss.PhotoLoader;
import com.main.R.id;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MarkResult extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mark_result);
	        
	        Intent intent = this.getIntent();
	        String path =  intent.getStringExtra("path");
	        
	        final ImageView image = (ImageView)this.findViewById(id.mark_img);
	        
	        PhotoLoader.PhotoLoaderCallback photoloaderCallback = new PhotoLoader.PhotoLoaderCallback() {
				public void callback(Bitmap bmp,int callbackpos) {
					image.setImageBitmap(bmp);
				}
			};
	        PhotoLoader.loadPhoto(path, 0, false,photoloaderCallback );
	    }
	 
}
