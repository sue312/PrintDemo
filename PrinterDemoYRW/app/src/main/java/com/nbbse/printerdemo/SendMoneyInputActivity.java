package com.nbbse.printerdemo;

import java.util.Calendar;
import java.util.Random;

import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class SendMoneyInputActivity extends Activity {
	public static String TAG = "PrintDemo_SendMoneyInputActivity";

	private EditText editBirthday = null;
	private EditText editFirstName = null;
	private EditText editLastName = null;
	private EditText editPhoneNumber = null;
	private EditText editLocation = null;
	private EditText editAmount = null;
	private Button buttonCommit = null;
	
	private String strTransNum;
	private String strTransNum2;
	
	
	private DBService dbService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_money);
		dbService = new DBService(this);
		initViews();
	}

	private void initViews() {
		editFirstName = (EditText) findViewById(R.id.edit_receiver_firstname);
		editLastName = (EditText) findViewById(R.id.edit_receiver_lastname);
		editPhoneNumber = (EditText) findViewById(R.id.edit_receiver_phonenumber);
		editLocation = (EditText) findViewById(R.id.edit_receiver_location);
		editAmount = (EditText) findViewById(R.id.edit_send_amount);
		
		editBirthday = (EditText) findViewById(R.id.edit_receiver_birthday);
		editBirthday.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int action = arg1.getAction();
				Log.d(TAG, "onTouch: " + action);
				if(action == MotionEvent.ACTION_DOWN) {
					Calendar c = Calendar.getInstance();
					DatePickerDialog dlg = new DatePickerDialog(SendMoneyInputActivity.this, new OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
							Calendar c = Calendar.getInstance();
	                        c.set(year, monthOfYear, dayOfMonth);
	                        editBirthday.setText(DateFormat.format("yyy-MM-dd", c));
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
					dlg.show();
				}
				
				return false;
			}
		});
		
		buttonCommit = (Button) findViewById(R.id.button_commit);
		buttonCommit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "button commit clicked.");
				if (TextUtils.isEmpty(editPhoneNumber.getText().toString()) ||
						TextUtils.isEmpty(editFirstName.getText().toString()) ||
						TextUtils.isEmpty(editLastName.getText().toString()) ||
						TextUtils.isEmpty(editBirthday.getText().toString()) ||
						TextUtils.isEmpty(editLocation.getText().toString()) ||
						TextUtils.isEmpty(editAmount.getText().toString())) {
					Toast.makeText(SendMoneyInputActivity.this, "All fields are mandatory.", Toast.LENGTH_LONG).show();
					return;
				}
				commitToDB();
				showResultDialog();
			}
		});
	}
	
	private void commitToDB() {
		String strName;
		if(TextUtils.isEmpty(editFirstName.getText().toString()) || TextUtils.isEmpty(editLastName.getText().toString())) {
			strName = editFirstName.getText().toString() + editLastName.getText().toString();
		} else {
			strName = editFirstName.getText() + " " + editLastName.getText();
		}
		strTransNum = genTransNum();
		String sql_query = "select * from t_money where transnum=?";
		SQLiteDatabase db = dbService.getReadableDatabase();
		Cursor cursor;
		while(true) {
			cursor = db.rawQuery(sql_query, new String[]{strTransNum});
			if(cursor.getCount() == 0) {
				cursor.close();
				break;
			}
			cursor.close();
			strTransNum = genTransNum();
		}
		db.close();
		String strPhone = editPhoneNumber.getText().toString();
		String strLocation = editLocation.getText().toString();
		String strBirthday = editBirthday.getText().toString();
		Double fAmount = Double.parseDouble(editAmount.getText().toString());
		
		Log.d(TAG, strTransNum + " " + strName + " " + strPhone + " " + strLocation + " " + strBirthday);
		String sql = "insert into t_money(transnum, name, phonenumber, location, birthday, amount, checkout) values(?,?,?,?,?,?, 0)";
		Object[] args = new Object[] {strTransNum, strName, strPhone, strLocation, strBirthday, fAmount};
		db = dbService.getWritableDatabase();
		db.execSQL(sql, args);
		db.close();
	}
	
	private String genTransNum() {
		String s = "";
		Random random = new Random();
		for(int i=0; i<8; i++) {
			char c = (char)(48 + random.nextInt(10));
			s += c;
		}
		return s;
	}
	
	private void showResultDialog() {
		
		new AlertDialog.Builder(this).setMessage(strTransNum + "\nThis Number is supposed Send by SMS by the Server.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
				Intent intent = new Intent(SendMoneyInputActivity.this, SendMoneyPreviewActivity.class);
				intent.putExtra("amount", editAmount.getText().toString());
				intent.putExtra("trans", strTransNum);
				startActivityForResult(intent, 0);
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
