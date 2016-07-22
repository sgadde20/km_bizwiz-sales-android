package com.webparadox.bizwizsales.helper;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.DownSyncServiceController;
import com.webparadox.bizwizsales.datacontroller.UpSyncServiceController;
import com.webparadox.bizwizsales.datacontroller.UpSyncServiceController.UpSyncServiceInterface;

public class BizWizUpdateService extends Service implements UpSyncServiceInterface{
	Context mcontext=this;
	SharedPreferences userData;
	String mDealerId;
	String mEmployeeId;
	DatabaseHandler dbHandler;
	ServiceHelper serviceHelper;
	JSONObject jsonResultText,jsonDataResultText;
	DownSyncServiceController mDownSyncServiceController;
	UpSyncServiceController mUpSyncServiceController;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		 UploadBizWizData();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO Auto-generated method stub
		super.unbindService(conn);
	}
	
	
	private void UploadBizWizData() {
		// TODO Auto-generated method stub
		mUpSyncServiceController=new UpSyncServiceController(this.mcontext);

	}
	@Override
	public void upSyncCompletedInterface() {
		// TODO Auto-generated method stub
		downloadBizWizData();
	}

	private void downloadBizWizData() {
		// TODO Auto-generated method stub
		mDownSyncServiceController=new DownSyncServiceController(this.mcontext);
	}
	
}
