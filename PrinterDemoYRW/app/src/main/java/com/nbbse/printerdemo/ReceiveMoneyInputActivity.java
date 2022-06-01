package com.nbbse.printerdemo;

import com.nbbse.printerdemo.db.DBService;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReceiveMoneyInputActivity extends Activity {
	public static String TAG = "PrintDemo_ReceiveMoneyInputActivity";
	
	private EditText editTransNum = null;
	private Button buttonNext = null;
	
	private DBService dbService = null;
	
	private String strPhone = null;
	private String strName = null;
	private String strLocation = null;
	private String strBirthday = null;
	private double fAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_money);
		
		dbService = new DBService(this);
		initViews();
	}

	private void initViews() {
		editTransNum = (EditText) findViewById(R.id.edit_trans_num);
		buttonNext = (Button) findViewById(R.id.button_next);
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				boolean exist = checkTransNum();
				if(exist) {
					Intent intent = new Intent(ReceiveMoneyInputActivity.this, ReceiveMoneyDisplayActivity.class);
					intent.putExtra("phonenumber", strPhone);
					intent.putExtra("name", strName);
					intent.putExtra("location", strLocation);
					intent.putExtra("birthday", strBirthday);
					intent.putExtra("amount", fAmount);
					intent.putExtra("trans", editTransNum.getText().toString());
					startActivityForResult(intent, 0);
				} else {
					showNotFoundDialog();
				}
			}
		});
	}
	
	private boolean checkTransNum() {
		boolean ret = false;
		String strTransNum = editTransNum.getText().toString();
		SQLiteDatabase db = dbService.getReadableDatabase();
		String sql_query = "select * from t_money where transnum=?";
		Cursor cursor = db.rawQuery(sql_query, new String[]{strTransNum});
		if(cursor.getCount() == 0) {
			ret = false;
		} else {
			cursor.moveToFirst();
			int checkout = cursor.getInt(cursor.getColumnIndex("checkout"));
			Log.d(TAG, "checkout: " + checkout);
			if(checkout == 0) {
				strName = cursor.getString(cursor.getColumnIndex("name"));
				strPhone = cursor.getString(cursor.getColumnIndex("phonenumber"));
				strLocation = cursor.getString(cursor.getColumnIndex("location"));
				strBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
				fAmount = cursor.getDouble(cursor.getColumnIndex("amount"));
				ret = true;
			}
		}
		cursor.close();
		db.close();
		return ret;
	}
	
	private void showNotFoundDialog() {
		Toast.makeText(this, "This Transaction does not exist.", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
	}
	
}
