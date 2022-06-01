package com.nbbse.printerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BusTicketPaymentChooseActivity extends Activity {

	private String departure;
	private String arrival;
	private String duration;
	private String price;

	private TextView textAmount = null;
	private Button buttonCash = null;
	private Button buttonBank = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bus_payment_choose);
		
		initData();
		initViews();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		departure = bundle.getString("Departure");
		arrival = bundle.getString("Arrival");
		duration = bundle.getString("Duration");
		price = bundle.getString("Price");
	}
	
	private void initViews() {
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText(price);
		
		buttonCash = (Button) findViewById(R.id.button_cash);
		buttonCash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BusTicketPaymentChooseActivity.this, BusTicketPreviewActivity.class);
				intent.putExtra("Departure", departure);
				intent.putExtra("Arrival", arrival);
				intent.putExtra("Duration", duration);
				intent.putExtra("Price", price);
				
				startActivityForResult(intent, 0);
			}
		});
		
		buttonBank = (Button) findViewById(R.id.button_mobibank);
		buttonBank.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BusTicketPaymentChooseActivity.this, BusTicketBankActivity.class);
				intent.putExtra("Departure", departure);
				intent.putExtra("Arrival", arrival);
				intent.putExtra("Duration", duration);
				intent.putExtra("Price", price);
				intent.putExtra("type", 1);
				
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
