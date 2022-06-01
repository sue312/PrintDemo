package com.nbbse.printerdemo;

import com.nbbse.printerdemo.util.SRUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TopupPaymentChooseActivity extends Activity {

	private String number;
	private String amount;
	
	private TextView textAmount = null;
	private Button buttonCash = null;
	private Button buttonBank = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.topup_payment_choose);
		
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		number = bundle.getString("number");
		amount = bundle.getString("amount");
	}
	
	private void initViews() {
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText(SRUtil.doubleFormat(Double.parseDouble(amount)) + " $");
		
		buttonCash = (Button) findViewById(R.id.button_cash);
		buttonCash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TopupPaymentChooseActivity.this, TopupPreviewActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				
				startActivityForResult(intent, 0);
			}
		});
		
		buttonBank = (Button) findViewById(R.id.button_mobibank);
		buttonBank.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TopupPaymentChooseActivity.this, BusTicketBankActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				intent.putExtra("type", 2);
				
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
