package com.nbbse.printerdemo;

import com.nbbse.printerdemo.util.SRUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BillPaymentChooseActivity extends Activity {

	private String number;
	private double amount;
	private String strFirstName;
	private String strLastName;
	private String strPhone;
	
	private TextView textAmount = null;
	private TextView textTrans = null;
	private Button buttonCash = null;
	private Button buttonBank = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bill_payment_choose);
		
		initData();
		initViews();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			number = bundle.getString("transnum");
			amount = bundle.getDouble("amount");
			strFirstName = bundle.getString("first");
			strLastName = bundle.getString("last");
			strPhone = bundle.getString("phone");
		}
	}
	
	private void initViews() {
		textTrans = (TextView) findViewById(R.id.text_trans);
		textTrans.setText(number);
		
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText(SRUtil.doubleFormat(amount) + " $");
		
		buttonCash = (Button) findViewById(R.id.button_cash);
		buttonCash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BillPaymentChooseActivity.this, BillPreviewActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				intent.putExtra("phone", strPhone);
				intent.putExtra("first", strFirstName);
				intent.putExtra("last", strLastName);
				startActivityForResult(intent, 0);
			}
		});
		
		buttonBank = (Button) findViewById(R.id.button_mobibank);
		buttonBank.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BillPaymentChooseActivity.this, BusTicketBankActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				intent.putExtra("phone", strPhone);
				intent.putExtra("first", strFirstName);
				intent.putExtra("last", strLastName);
				intent.putExtra("type", 4);
				
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
