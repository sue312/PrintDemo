package com.nbbse.printerdemo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ReceiveMoneyDisplayActivity extends Activity {
	
	private TextView textPhone = null;
	private TextView textName = null;
	private TextView textBirthday = null;
	private TextView textLocation = null;
	private TextView textAmount = null;
	private Button buttonOK = null;
	
	private String strPhone = null;
	private String strName = null;
	private String strLocation = null;
	private String strBirthday = null;
	private String strAmount = null;
	private String strTrans = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_money_display);
		
		initData();
		initViews();
	}

	private void initViews() {
		textPhone = (TextView) findViewById(R.id.text_phonenumber);
		textName = (TextView) findViewById(R.id.text_name);
		textBirthday = (TextView) findViewById(R.id.text_birthday);
		textLocation = (TextView) findViewById(R.id.text_location);
		textAmount = (TextView) findViewById(R.id.text_amount);
		
		textPhone.setText(strPhone);
		textName.setText(strName);
		textLocation.setText(strLocation);
		textBirthday.setText(strBirthday);
		textAmount.setText(strAmount + " €");
		
		buttonOK = (Button) findViewById(R.id.button_commit);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ReceiveMoneyDisplayActivity.this, ReceiveMoneyPreviewActivity.class);
				intent.putExtra("trans", strTrans);
				intent.putExtra("amount", strAmount);
				startActivityForResult(intent, 0);
			}
		});
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		
		strPhone = bundle.getString("phonenumber");
		strName = bundle.getString("name");
		strLocation = bundle.getString("location");
		strBirthday = bundle.getString("birthday");
		strAmount = String.valueOf(bundle.getDouble("amount"));
		strTrans = bundle.getString("trans");
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
