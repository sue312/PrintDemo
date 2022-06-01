package com.nbbse.printerdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity {
	
	private String account_feature[] = new String[]{"Create MobiBank Account", "Credit MobiBank Account"};
	
	private Button buttonItem = null;
	private Button buttonBill = null;
	private Button buttonCustomer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		
		initViews();
	}

	private void initViews() {
		buttonItem = (Button) findViewById(R.id.button_item_db);
		buttonItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingsActivity.this, SettingsItemDataActivity.class);
				startActivity(intent);
			}
		});
		
		buttonBill = (Button) findViewById(R.id.button_bill_db);
		buttonBill.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingsActivity.this, SettingsBillDataActivity.class);
				startActivity(intent);
				
			}
		});
		
		buttonCustomer = (Button) findViewById(R.id.button_customer_mgmt);
		buttonCustomer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showAccountFeatureDialog();
			}
		});
		
	}
	
	private void showAccountFeatureDialog() {
		new AlertDialog.Builder(this).setTitle("Account:").setItems(account_feature, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				if(which == 0) {
					Intent intent = new Intent(SettingsActivity.this, CreateAccountActivity.class);
					startActivityForResult(intent, 0);
				} else if(which == 1) {
					Intent intent = new Intent(SettingsActivity.this, CreditAccountActivity.class);
					startActivityForResult(intent, 0);
				}
			}
		}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
	}
	
}
