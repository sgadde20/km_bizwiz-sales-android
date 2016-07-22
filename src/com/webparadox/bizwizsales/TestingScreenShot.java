package com.webparadox.bizwizsales;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TestingScreenShot extends Activity {
	TextView test;
	ImageView iv;
	@Override


protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.screenshot_testing);
	test = (TextView) findViewById(R.id.checkoyut_agree);
	test.setDrawingCacheEnabled(true);
	iv = (ImageView) findViewById(R.id.imageView1);
	
	test.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			test.buildDrawingCache(); 
			
		    iv.setImageBitmap(test.getDrawingCache()); 
		}
	});
	
}
}
