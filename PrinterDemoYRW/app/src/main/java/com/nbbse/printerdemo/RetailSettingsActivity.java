package com.nbbse.printerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RetailSettingsActivity extends Activity {

	private Button buttonInventory = null;
	private Button buttonReport = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_settings);
		
		initViews();
	}
	
	private void initViews() {
		buttonInventory = (Button) findViewById(R.id.button_inventory);
		buttonInventory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RetailSettingsActivity.this, RetailInventoryActivity.class);
				startActivity(intent);
			}
		});
		
		buttonReport = (Button) findViewById(R.id.button_report);
		buttonReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RetailSettingsActivity.this, RetailReportActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	
}
