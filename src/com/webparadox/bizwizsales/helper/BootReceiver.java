package com.webparadox.bizwizsales.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {
	@Override
	  public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, SendLocationService.class);
        context.startService(startServiceIntent);
        
        Intent startUpdateServiceIntent = new Intent(context, BizWizUpdateService.class);
        context.startService(startUpdateServiceIntent);
    }
	

}
