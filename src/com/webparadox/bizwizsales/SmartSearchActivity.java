package com.webparadox.bizwizsales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.SmartSearchAdapter;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class SmartSearchActivity extends Activity{
	
	ListView smartSearchListview;
	Context context;
	SmartSearchAsyncTask searchAsyncTask;
	String dealerId,empId,searchText;
	TextView smartSearchHeader;
	Typeface droidSansBold;
	ImageView imageViewBack, logoBtn;
	SearchView smartSearchView;
	SharedPreferences userData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.smart_search_activity);
		context=this;
		
		init();
	}
	
	public void init(){
		
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		smartSearchHeader=(TextView)findViewById(R.id.smart_search_header);
		smartSearchHeader.setTypeface(droidSansBold);
		smartSearchListview=(ListView)findViewById(R.id.smart_search_listview);
		imageViewBack = (ImageView) findViewById(R.id.smart_search_back_icon);
		
		smartSearchView=(SearchView)findViewById(R.id.smart_search_searchview);
		
		smartSearchView.setOnQueryTextListener(new OnQueryTextListener() {
					
					@Override
					public boolean onQueryTextSubmit(String query) {
						// TODO Auto-generated method stub
					    
					    if(query.trim().length() !=0){
					     
					      searchAsyncTask=new SmartSearchAsyncTask(context,dealerId,empId,query,true,smartSearchListview);
					     searchAsyncTask.execute();
					    }else{
					     Toast.makeText(getApplicationContext(), Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
					    }
						return false;
					}
					
					@Override
					public boolean onQueryTextChange(String newText) {
						// TODO Auto-generated method stub
						return false;
					}
				});
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		logoBtn = (ImageView) findViewById(R.id.smart_search_image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});
		
		smartSearchListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("CustomerFullName",
						Singleton.getInstance().smartSearchModel.get(position).CustomerFullName);
				bundle.putString(
						"CustomerAddress",
						Singleton.getInstance().smartSearchModel.get(position).Address
								+ ", "
								+ Singleton.getInstance().smartSearchModel.get(position).City
								+ ", "
								+ Singleton.getInstance().smartSearchModel.get(position).State
								+ ", "
								+ Singleton.getInstance().smartSearchModel.get(position).Zip);
				bundle.putString("CustomerId",Singleton.getInstance().smartSearchModel.get(position).CustomerId);
				Intent customerDetailsIntent = new Intent(context, CustomerDetailsActivity.class);
				customerDetailsIntent.putExtras(bundle);
				startActivity(customerDetailsIntent);
				finish();
			}
		});
		
		SmartSearchAdapter searchAdapter=new SmartSearchAdapter(context);
		smartSearchListview.setAdapter(searchAdapter);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Smart Search Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}
	public void onDestroy(){
		super.onDestroy();
		if(searchAsyncTask !=null){
			searchAsyncTask.cancel(true);
			searchAsyncTask=null;
		}
	}
	private void gotoHomeActivity() {
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
