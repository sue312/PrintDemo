package com.nbbse.printerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RetailMainActivity extends Activity {

	private Button buttonShopping = null;
	private Button buttonSetting = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_main);
		
		initViews();
	}
	
	private void initViews() {
		buttonShopping = (Button) findViewById(R.id.button_shopping);
		buttonShopping.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RetailMainActivity.this, RetailShoppingActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		buttonSetting = (Button) findViewById(R.id.button_setting);
		buttonSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RetailMainActivity.this, RetailSettingsActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
	}

	
}
