package com.nbbse.printerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TopupEnterNumberActivity extends Activity {
	public static String TAG = "PrintDemo_TopupEnterNumberActivity";
	private EditText editNumber = null;
	private EditText editAmount = null;
	private Button buttonOK = null;
	
	String number;
	String amount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topup_enter_number);
		
		initViews();
	}

	private void initViews() {
		editNumber = (EditText) findViewById(R.id.edit_number);
		editAmount = (EditText) findViewById(R.id.edit_amount);
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				number = editNumber.getText().toString();
				amount = editAmount.getText().toString();
				Log.d(TAG, "number:" + number + " amount:" + amount);
				
				if(TextUtils.isEmpty(number) || TextUtils.isEmpty(amount)) {
					Toast.makeText(TopupEnterNumberActivity.this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Intent intent = new Intent(TopupEnterNumberActivity.this, TopupPaymentChooseActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				
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
