package com.nbbse.printerdemo;

import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreditAccountActivity extends Activity {
	public static String TAG = "PrintDemo_CreditAccountActivity";
	
	private EditText editFirstName = null;
	private EditText editLastName = null;
	private EditText editPhoneNumber = null;
	private EditText editCredit = null;
	private EditText editAccountNumber = null;
	private Button buttonCredit = null;
	
	private DBService dbService;
	
	private Double mAmount = 0.0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credit_account);
		
		dbService = new DBService(this);
		initViews();
	}

	private void initViews() {
		editFirstName = (EditText) findViewById(R.id.edit_firstname);
		editLastName = (EditText) findViewById(R.id.edit_lastname);
		editPhoneNumber = (EditText) findViewById(R.id.edit_phonenumber);
		editCredit = (EditText) findViewById(R.id.edit_credit);
		editAccountNumber = (EditText) findViewById(R.id.edit_accountnumber);
		buttonCredit = (Button) findViewById(R.id.button_credit);
		buttonCredit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				creditAccount();
			}
		});
	}
	
	private void creditAccount() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql = "select * from t_account where account=?";
		
		Cursor cursor = db.rawQuery(sql, new String[]{editAccountNumber.getText().toString()});
		Log.d(TAG, "creditAccount -- cursor count: " + cursor.getCount());
		if(cursor.getCount() == 0) {
			//showResultDialog(1);
			Toast.makeText(CreditAccountActivity.this, "Can't find this account in database, please check!", Toast.LENGTH_SHORT).show();
		} else {
			cursor.moveToFirst();
			//String phonenumber = cursor.getString(cursor.getColumnIndex("phonenumber"));
			//if(phonenumber.equals(editPhoneNumber.getText().toString())) {
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String account = cursor.getString(cursor.getColumnIndex("account"));
				String phone = cursor.getString(cursor.getColumnIndex("phonenumber"));
				double prebalance = cursor.getDouble(cursor.getColumnIndex("amount")); 
			
			
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				Double fAmount = cursor.getDouble(cursor.getColumnIndex("amount"));
				Double fCredit = Double.parseDouble(editCredit.getText().toString());
				if(fCredit > 0) {
					fAmount += fCredit;
					String sql_update = "update t_account set amount=? where _id=?";
					db.execSQL(sql_update, new Object[]{fAmount,id});
					mAmount = fAmount;
					//showResultDialog(0);
					Intent intent = new Intent(CreditAccountActivity.this, CreditAccountPreviewActivity.class);
					intent.putExtra("name", name);
					intent.putExtra("account", account);
					intent.putExtra("phone", phone);
					intent.putExtra("prebalance", prebalance);
					intent.putExtra("credit", fCredit);
					intent.putExtra("newbalance", mAmount);
					startActivityForResult(intent, 0);
				} else {
					//showResultDialog(3);
					Toast.makeText(CreditAccountActivity.this, "Credit amount must be larger than 0, please check!", Toast.LENGTH_SHORT).show();
				}
//			} else {
//				showResultDialog(2);
//			}
			
		}
		cursor.close();
		db.close();
	}
	
	private void showResultDialog(int result) {
		String message;
		if(result == 1) {
			message = "Can't find this account in database, please check!";
		} else if (result == 2) {
			message = "account and phonenumber don't match, please check!";
		} else if (result == 3) {
			message = "Credit amount must be larger than 0, please check!";
		} else {
			message ="Account: " + editAccountNumber.getText() + "\nCredit: " + editCredit.getText() + "\nTotal: " + mAmount;
		}
		
		new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		}).show();
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
