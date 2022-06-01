package com.nbbse.printerdemo;

import java.util.Random;

import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends Activity {

	private EditText editFirstName = null;
	private EditText editLastName = null;
	private EditText editPhoneNumber = null;
	private EditText editDeposit = null;
	private Button buttonCreate = null;
	
	private String strAccountNumber = null;
	private String strPassword = null;
	private String strFullName = null;
	private Double fAmount = null;
	private DBService dbService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
		
		dbService = new DBService(this);
		
		initViews();
	}

	private void initViews() {
		editFirstName = (EditText) findViewById(R.id.edit_firstname);
		editLastName = (EditText) findViewById(R.id.edit_lastname);
		editPhoneNumber = (EditText) findViewById(R.id.edit_phonenumber);
		editDeposit = (EditText) findViewById(R.id.edit_deposit);
		buttonCreate = (Button) findViewById(R.id.button_create);
		buttonCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(editFirstName.getText().toString())
					|| TextUtils.isEmpty(editLastName.getText().toString())) {
//					strFullName = editFirstName.getText().toString() + editLastName.getText().toString();
//					if(TextUtils.isEmpty(strFullName)) {
//						showResultDialog(2);
//						return;
//					}
					Toast.makeText(CreateAccountActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
					return;
				} else {
					strFullName = editFirstName.getText() + " " + editLastName.getText();
				}
				
				if(TextUtils.isEmpty(editPhoneNumber.getText().toString())) {
//					showResultDialog(2);
					Toast.makeText(CreateAccountActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				strAccountNumber = getAvailableAccountNumber();
				if(strAccountNumber.equals("0")) {
//					showResultDialog(1);
					Toast.makeText(CreateAccountActivity.this, "This PhoneNumber has already been registed! Ignore this operation!", Toast.LENGTH_SHORT).show();
					return;
				}
				strPassword = genPassword();
				
				String amount = editDeposit.getText().toString();
				if(TextUtils.isEmpty(amount)) {
//					amount = "0";
					Toast.makeText(CreateAccountActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
					return;
				}
				fAmount = Double.parseDouble(amount);
				String sql = "insert into t_account(account,name,phonenumber,amount,password) values(?, ?, ?, ?, ?)";
				Object[] args = new Object[]{
					strAccountNumber, strFullName, editPhoneNumber.getText(), fAmount, strPassword 
				};
				SQLiteDatabase db = dbService.getWritableDatabase();
				db.execSQL(sql, args);
				db.close();
//				showResultDialog(0);
				Intent intent = new Intent(CreateAccountActivity.this, CreateAccountPreviewActivity.class);
				intent.putExtra("firstname", editFirstName.getText().toString());
				intent.putExtra("lastname", editLastName.getText().toString());
				intent.putExtra("phonenumber", editPhoneNumber.getText().toString());
				intent.putExtra("account", strAccountNumber);
				intent.putExtra("password", strPassword);
				intent.putExtra("balance", fAmount);
				startActivityForResult(intent, 0);
			}
		});
	}
	
	private String getAvailableAccountNumber() {
		String sql = "select * from t_account";
		
		SQLiteDatabase db = dbService.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return "12345678";
		} else {
			int indexPhoneNumber = cursor.getColumnIndex("phonenumber");
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
				if(cursor.getString(indexPhoneNumber).equals(editPhoneNumber.getText().toString())) {
					cursor.close();
					db.close();
					return "0";
				}
			}
			
			cursor.moveToLast();
			int indexAccount = cursor.getColumnIndex("account");
			String lastAccount = cursor.getString(indexAccount);
			int nAccount = Integer.parseInt(lastAccount) + 1;
			cursor.close();
			db.close();
			return String.valueOf(nAccount);
		}
	}
	
	private String genPassword() {
		String s = "";
		Random random = new Random();
		for(int i=0; i<8; i++) {
			char c = (char)(97 + random.nextInt(26));
			s += c;
		}
		return "1234";
	}
	
	private void showResultDialog(int result) {
		String message;
		if(result == 1){
			message = "This PhoneNumber has already been registed! Ignore this operation!";
		} else if (result == 2) {
			message = "Infomation should not be blank. Abort!";
		}else {
			message = "Name: " + strFullName + "\nPhoneNumber: " + editPhoneNumber.getText() + "\n Created!"
				+ "\n\nAccountNumber: " + strAccountNumber + "\nPassword: " + strPassword + "\nCredit amount: "
				+ fAmount;
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
