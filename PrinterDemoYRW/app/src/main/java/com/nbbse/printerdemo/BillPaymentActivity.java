package com.nbbse.printerdemo;

import com.nbbse.printerdemo.barcoder.util.CaptureActivity;
import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BillPaymentActivity extends Activity {
	private static final int TO_SCAN1 = 99;
	private static final int TO_SCAN2 = 98;
	private Button buttonOK = null;
	
	private EditText editBillNum = null;
	private EditText editFirstName = null;
	private EditText editLastName = null;
	private EditText editPhoneNumber = null;
	
	private Button buttonScan1 = null;
	private Button buttonScan2 = null;
	
	
	private String strBillNum;
	
	private DBService dbService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbService = new DBService(this);
		
		setContentView(R.layout.bill_payment);
		initViews();
	}

	private void initViews() {
		editBillNum = (EditText) findViewById(R.id.edit_bill);
		editPhoneNumber = (EditText) findViewById(R.id.edit_phonenumber);
		editFirstName = (EditText) findViewById(R.id.edit_firstname);
		editLastName = (EditText) findViewById(R.id.edit_lastname);
		
		buttonScan1 = (Button) findViewById(R.id.button_scan1);
		buttonScan1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BillPaymentActivity.this, CaptureActivity.class);
                startActivityForResult(intent, TO_SCAN1);
			}
		});
		buttonScan2 = (Button) findViewById(R.id.button_scan2);
		buttonScan2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BillPaymentActivity.this, CaptureActivity.class);
                startActivityForResult(intent, TO_SCAN2);
			}
		});
		
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				strBillNum = editBillNum.getText().toString();
				String firstname = editFirstName.getText().toString();
				String lastname = editFirstName.getText().toString();
				String phone = editPhoneNumber.getText().toString();
				
				if(TextUtils.isEmpty(strBillNum) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(firstname)
						|| TextUtils.isEmpty(phone)) {
					Toast.makeText(BillPaymentActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				SQLiteDatabase db = dbService.getWritableDatabase();
				
				
				String sql_table = "create table if not exists t_bill_lib (_id integer primary key autoincrement,"
						+ "transnum varchar(15) not null on conflict fail,"
						+ "name varchar(30),phonenumber varchar(20),"
						+ "amount double,pay int)";
				db.execSQL(sql_table);
				
				Cursor c = db.rawQuery("select * from t_bill_lib where transnum=?", new String[]{strBillNum});
				if(c.getCount() > 0) {
					c.moveToFirst();
					int pay = c.getInt(c.getColumnIndex("pay"));
					if(pay == 1) {
						Toast.makeText(BillPaymentActivity.this, "Bill already paid.", Toast.LENGTH_SHORT).show();
						return;
					}
					double amount = c.getDouble(c.getColumnIndex("amount"));
					Intent intent = new Intent(BillPaymentActivity.this, BillPaymentChooseActivity.class);
					intent.putExtra("transnum", strBillNum);
					intent.putExtra("amount", amount);
					intent.putExtra("phone", phone);
					intent.putExtra("first", firstname);
					intent.putExtra("last", lastname);
					startActivityForResult(intent, 0);
				} else {
					Toast.makeText(BillPaymentActivity.this, "Bill does not exists.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
		if(requestCode == TO_SCAN1) {
			if (resultCode == RESULT_OK) {
                String scanStr = data.getStringExtra("RESULT");
                editBillNum.setText(scanStr);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "scan canceled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "scan error",
                        Toast.LENGTH_SHORT).show();
            }
		} else if (requestCode == TO_SCAN2) {
			if (resultCode == RESULT_OK) {
                String scanStr = data.getStringExtra("RESULT");
                String[] acc = scanStr.split(";");
                if(acc.length < 3) {
                	Toast.makeText(this, "Parse error, Must be \"First Name; Last Name; Phone Number\"",
                		Toast.LENGTH_SHORT).show();
                	return;
                }
                editFirstName.setText(acc[0]);
                editLastName.setText(acc[1]);
                editPhoneNumber.setText(acc[2]);
                
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "scan canceled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "scan error",
                        Toast.LENGTH_SHORT).show();
            }
		}
	}
	
	
}
