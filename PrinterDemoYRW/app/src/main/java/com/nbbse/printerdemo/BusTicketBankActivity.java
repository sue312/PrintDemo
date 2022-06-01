package com.nbbse.printerdemo;

import com.nbbse.printerdemo.barcoder.util.CaptureActivity;
import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BusTicketBankActivity extends Activity {
	private static final int TO_SCAN = 99;
	
	private String departure;
	private String arrival;
	private String duration;
	private String price;
	
	private String number;
	private String amount;
	
	private int type;
	
	private EditText editFirstName = null;
	private EditText editLastName = null;
	private EditText editPhone = null;
	private EditText editAccount = null;
	private EditText editPassword = null;
	
	
	private String strBillNum;
	private String strBillName;
	private String strBillFirstName;
	private String strBillLastName;
	private double billAmount;
	private String strDateTime;
	private String strBillPhone;
	
	private String strPhone;
	private String strAccount;
	private String strPassword;
	private double payment;
	DBService dbService;
	
	
	private Button buttonNext = null;
	private Button buttonScan = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bus_bank);
		dbService = new DBService(this);
		initData();
		initViews();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		type = bundle.getInt("type");
		if(type == 1) {
			departure = bundle.getString("Departure");
			arrival = bundle.getString("Arrival");
			duration = bundle.getString("Duration");
			price = bundle.getString("Price");
			int index = price.indexOf(" ");
			payment = Double.parseDouble(price.substring(0, index));
			Log.d("yanjunke", "BusTicketBank: payment: " + payment);
		} else if(type == 2) {
			number = bundle.getString("number");
			amount = bundle.getString("amount");
			payment = Double.parseDouble(amount);
		} else if(type == 3) {
			payment = bundle.getDouble("amount");
		} else if(type == 4) {
			strBillNum = bundle.getString("number");
			billAmount = bundle.getDouble("amount");
			strBillFirstName = bundle.getString("first");
			strBillLastName = bundle.getString("last");
			strBillPhone = bundle.getString("phone");
		} 
	}
	
	private void initViews() {
		editFirstName = (EditText) findViewById(R.id.edit_firstname);
		editLastName = (EditText) findViewById(R.id.edit_lastname);
		editPhone = (EditText) findViewById(R.id.edit_phonenumber);
		editAccount = (EditText) findViewById(R.id.edit_accountnumber);
		editPassword = (EditText) findViewById(R.id.edit_password);
		
		buttonScan = (Button) findViewById(R.id.button_scan);
		buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BusTicketBankActivity.this, CaptureActivity.class);
                startActivityForResult(intent, TO_SCAN);
			}
		});
		
		buttonNext = (Button) findViewById(R.id.button_next);
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				strPhone = editPhone.getText().toString();
				strAccount = editAccount.getText().toString();
				strPassword = editPassword.getText().toString();
				
				if(TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strAccount) || TextUtils.isEmpty(strPassword)) {
					Toast.makeText(BusTicketBankActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				SQLiteDatabase db = dbService.getWritableDatabase();
				Cursor cursor = db.rawQuery("select * from t_account where account=?", new String[]{strAccount});
				if(cursor.getCount() == 0) {
					Toast.makeText(BusTicketBankActivity.this, "Account not exists.", Toast.LENGTH_SHORT).show();
					cursor.close();
					db.close();
					return;
				}
				
				cursor.moveToFirst();
				String libPhone = cursor.getString(cursor.getColumnIndex("phonenumber"));
				String libPassword = cursor.getString(cursor.getColumnIndex("password"));
				
				if(!libPhone.equals(strPhone) || !libPassword.equals(strPassword)) {
					Toast.makeText(BusTicketBankActivity.this, "Phone or Password not match the account.", Toast.LENGTH_SHORT).show();
					cursor.close();
					db.close();
					return;
				}
				
				double libRemain = cursor.getDouble(cursor.getColumnIndex("amount"));
				if(libRemain < payment) {
					Toast.makeText(BusTicketBankActivity.this, "Sorry you credit is running low.", Toast.LENGTH_SHORT).show();
					cursor.close();
					db.close();
					return;
				}
				
				cursor.close();
				db.execSQL("update t_account set amount=amount-? where account=?", new Object[]{payment, strAccount});
				
				db.close();
				
				Intent intent;
				if(type == 1) {
					intent = new Intent(BusTicketBankActivity.this, BusTicketPreviewActivity.class);
					intent.putExtra("Departure", departure);
					intent.putExtra("Arrival", arrival);
					intent.putExtra("Duration", duration);
					intent.putExtra("Price", price);
					
					startActivityForResult(intent, 0);
				} else if (type == 2) {
					intent = new Intent(BusTicketBankActivity.this, TopupPreviewActivity.class);
					intent.putExtra("number", number);
					intent.putExtra("amount", amount);
					
					startActivityForResult(intent, 0);
				} else if (type == 3) {
					intent = new Intent(BusTicketBankActivity.this, RetailPreviewActivity.class);
					intent.putExtra("total", payment);
					startActivityForResult(intent, 0);
				} else if (type == 4) {
					intent = new Intent(BusTicketBankActivity.this, BillPreviewActivity.class);
					intent.putExtra("number", strBillNum);
					intent.putExtra("amount", billAmount);
					intent.putExtra("phone", strBillPhone);
					intent.putExtra("first", strBillFirstName);
					intent.putExtra("last", strBillLastName);
					startActivityForResult(intent, 0);
				}
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
		if(requestCode == TO_SCAN) {
			if (resultCode == RESULT_OK) {
                String scanStr = data.getStringExtra("RESULT");
                String[] acc = scanStr.split(";");
                if(acc.length != 4) {
                	Toast.makeText(this, "Parse error, Must be \"First Name; Last Name; Phone Number; Account Number\"",
                		Toast.LENGTH_SHORT).show();
                	return;
                }
                Log.d("yanjunke", "scan: " + acc[0] + " " + acc[1] + " " + acc[2] + " " + acc[3]);
                editFirstName.setText(acc[0]);
                editLastName.setText(acc[1]);
                editPhone.setText(acc[2]);
                editAccount.setText(acc[3]);
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
