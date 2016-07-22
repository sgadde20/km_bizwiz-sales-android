package com.webparadox.bizwizsales.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BizWizUpdateReciver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		intent.setClass(context, BizWizUpdateService.class);
		context.startService(intent);
		
	}

}
