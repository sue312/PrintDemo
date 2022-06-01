package com.nbbse.printerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoBootBroadcast extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
//			Intent startIntent = new Intent(context, MainActivity.class);
//			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(startIntent);
		}  
	}
}
