package com.nbbse.printerdemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nbbse.printerdemo.barcoder.util.CaptureActivity;
import com.nbbse.printerdemo.db.DBService;

public class SettingsItemDetailActivity extends Activity {

	private static final int TO_SCAN = 99;
	
	private TextView textTitle = null;
	
	private Button buttonScan = null;
	private Button buttonOK = null;
	private Button buttonCancel = null;
	
	private EditText editName = null;
	private EditText editBarcode = null;
	private EditText editPrice = null;
	private EditText editQuantity = null;

	private String strTitle;
	
	private String strName;
	private String strBarcode;
	private double price;
	private int quantity;
	private int id;
	
	private DBService dbService;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_item_detail);
		
		dbService = new DBService(this);
		initData();
		initViews();
		
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if(bundle == null) {
			id = 0;
			strName = "";
			strBarcode = "";
			price = 0;
			quantity = 0;
			
			strTitle = "Add Item";
		} else {
			id = bundle.getInt("id", 0);
			strName = bundle.getString("name", "");
			strBarcode = bundle.getString("barcode", "");
			price = bundle.getDouble("price", 0);
			quantity = bundle.getInt("quantity", 0);
			
			strTitle = "Edit Item";
		}
	}
	
	private void initViews() {
		textTitle = (TextView) findViewById(R.id.text_title);
		textTitle.setText(strTitle);
		
		buttonScan = (Button) findViewById(R.id.button_scan);
		buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingsItemDetailActivity.this, CaptureActivity.class);
                startActivityForResult(intent, TO_SCAN);
			}
		});
		
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				strName = editName.getText().toString();
				strBarcode = editBarcode.getText().toString();
				String strPrice = editPrice.getText().toString();
				String strQuantity = editQuantity.getText().toString();
				if(TextUtils.isEmpty(strName) || TextUtils.isEmpty(strBarcode) 
						|| TextUtils.isEmpty(strPrice) || TextUtils.isEmpty(strQuantity)) {
					Toast.makeText(SettingsItemDetailActivity.this, "All fields are mandatory.", Toast.LENGTH_LONG).show();
					return;
				}
				price = Double.parseDouble(strPrice);
				quantity = Integer.parseInt(strQuantity);
				if(price <= 0) {
					Toast.makeText(SettingsItemDetailActivity.this, "Price must above 0 .", Toast.LENGTH_LONG).show();
					return;
				}
				if(quantity < 0) {
					Toast.makeText(SettingsItemDetailActivity.this, "quantity must above 0 .", Toast.LENGTH_LONG).show();
					return;
				}
				
				commitToDB();
				finish();
			}
		});
		
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		editName = (EditText) findViewById(R.id.edit_item_name);
		editBarcode = (EditText) findViewById(R.id.edit_item_barcode);
		editPrice = (EditText) findViewById(R.id.edit_price);
		editQuantity = (EditText) findViewById(R.id.edit_qtty);
		
		editName.setText(strName);
		editBarcode.setText(strBarcode);
		if(price > 0) {
			editPrice.setText(String.valueOf(price));
		}
		if(quantity > 0) {
			editQuantity.setText(String.valueOf(quantity));
		}
		
	}
	
	
	
	private void commitToDB() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		if(id > 0) {
			String sql = "update t_item_lib set name=?,barcode=?,price=?,quantity=? where _id=?";
			Object[] args = new Object[] {
					strName, strBarcode, price, quantity, id
			};
			db.execSQL(sql, args);
		} else {
			String sql = "select * from t_item_lib where barcode=?";
			Cursor cursor = db.rawQuery(sql, new String[]{strBarcode});
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id2 = cursor.getInt(cursor.getColumnIndex("_id"));
				String sql2 = "update t_item_lib set name=?,barcode=?,price=?,quantity=? where _id=?";
				Object[] args = new Object[] {
						strName, strBarcode, price, quantity, id2
				};
				db.execSQL(sql2, args);
				
				cursor.close();
				
			} else {
				String sql3 = "insert into t_item_lib(name,barcode,price,quantity) values(?,?,?,?)";
				Object[] args = new Object[]{
						strName, strBarcode, price, quantity
				};
				
				db.execSQL(sql3, args);
				
			}
			
		}
		db.close();
			
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case TO_SCAN:
			if (resultCode == RESULT_OK) {
                String scanStr = data.getStringExtra("RESULT");
                editBarcode.setText(scanStr);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "scan canceled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "scan error",
                        Toast.LENGTH_SHORT).show();
            }
            break;
        default:
         	break;
		}
	}
	
	
}
