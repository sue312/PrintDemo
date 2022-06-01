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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class RetailShoppingActivity extends Activity {

	private static final int TO_SCAN = 99;
	
	private EditText editBarcode = null;
	private Button buttonAdd = null;
	private Button buttonScan  = null;
	private Button buttonOK = null;
	
	private ListView lv = null;
	
	private DBService dbService;
	
	private double totalPrice = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_shopping);
		
		dbService = new DBService(this);
		
		initViews();
	}
	
	private void initViews() {
		editBarcode = (EditText) findViewById(R.id.edit_barcode);
		
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String barcode = editBarcode.getText().toString();
				if(!TextUtils.isEmpty(barcode)) {
					addItemByBarcode(barcode);
				}
				editBarcode.setText("");
			}
		});
		
		buttonScan = (Button) findViewById(R.id.button_scan);
		buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RetailShoppingActivity.this, CaptureActivity.class);
                startActivityForResult(intent, TO_SCAN);
			}
		});
		
		lv = (ListView) findViewById(R.id.lv);
		
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				totalPrice = calcTotalPrice();
				Intent intent = new Intent(RetailShoppingActivity.this, RetailPaymentActivity.class);
				intent.putExtra("type", 3);
				intent.putExtra("total", totalPrice);
				startActivityForResult(intent, 0);
			}
		});
	}

	private double calcTotalPrice() {
		double total = 0;
		double a;
		int b;
		SQLiteDatabase db = dbService.getReadableDatabase();
		String sql = "select * from t_curr_lib";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				a = cursor.getDouble(cursor.getColumnIndex("price"));
				b = cursor.getInt(cursor.getColumnIndex("quantity"));
				total += (cursor.getDouble(cursor.getColumnIndex("price")) * cursor.getInt(cursor.getColumnIndex("quantity")));
				Log.d("yanjunke", "a = " + a + ", b = " + b + ", total = " + total);
				cursor.moveToNext();

			}
		}
		cursor.close();
		return total;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case TO_SCAN:
			if (resultCode == RESULT_OK) {
                String scanStr = data.getStringExtra("RESULT");
                addItemByBarcode(scanStr);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "scan canceled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "scan error",
                        Toast.LENGTH_SHORT).show();
            }
            break;
		case 0:
			if(resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
        default:
         	break;
		}
	}
	
	private void addItemByBarcode(String barcode) {
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql = "select * from t_item_lib where barcode=?";
		String[] str = new String[]{barcode};
		Cursor cursor = db.rawQuery(sql, str);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			int qtty = cursor.getInt(cursor.getColumnIndex("quantity"));
			String tempname = cursor.getString(cursor.getColumnIndex("name"));
			String tempbar = cursor.getString(cursor.getColumnIndex("barcode"));
			Double tempprice = cursor.getDouble(cursor.getColumnIndex("price"));
			if(qtty > 0) {
				//sql = "update t_item_lib set quantity=quantity-1 where barcode=?";
				//db.execSQL(sql, str);
				
				
				String sql2 = "select * from t_curr_lib where barcode=?";
				Cursor cursor2 = db.rawQuery(sql2, str);
				if(cursor2.getCount() > 0) {
					cursor2.moveToFirst();
					int currQtty = cursor2.getInt(cursor2.getColumnIndex("quantity"));
					if(currQtty == qtty) {
						Toast.makeText(this, "This item is sold out.", Toast.LENGTH_SHORT).show();
						cursor2.close();
						cursor.close();
						return;
					}
					String sql3 = "update t_curr_lib set quantity=quantity+1 where barcode=?";
					db.execSQL(sql3, str);
				} else {
					String sql4 = "insert into t_curr_lib(name,barcode,price,quantity) values(?,?,?,1)";
					Object[] args = new Object[] {tempname, tempbar, tempprice};
					db.execSQL(sql4, args);
				}
				cursor2.close();
				totalPrice += tempprice;
				updateListView();
				
			} else {
				Toast.makeText(this, "This item is sold out.", Toast.LENGTH_SHORT).show();
			}
			
		} else {
			Toast.makeText(this, "This item is not in library.", Toast.LENGTH_SHORT).show();
		}
		cursor.close();
	}
	
	
	private void updateListView() {
		String sql = "select * from t_curr_lib"; 
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(sql, null);
		
		Log.d("yanjunke", "cursor_count:" + cursor.getCount());
		if(cursor.getCount() > 0) { 
			String[] columnNames = {"name", "barcode", "price", "quantity"};
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.retail_item_list, cursor, columnNames, new int[] { R.id.item, 
	                R.id.barcode, R.id.price, R.id.qtty}, 0);
			
			lv.setAdapter(adapter);
		}
//		cursor.close();
		db.close();
	}

	@Override
	protected void onStart() {
		super.onStart();
		createCurrTable();
		
		updateListView();
	}
	
	private void createCurrTable() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql_t_curr_lib = "create table if not exists t_curr_lib(_id integer primary key autoincrement,"
				+ "name varchar(10) not null on conflict fail,"
				+ "barcode varchar(15) not null on conflict fail,"
				+ "price double,"
				+ "quantity int)";
		db.execSQL(sql_t_curr_lib);
	}

	
	@Override
	protected void onDestroy() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS t_curr_lib");
		
		super.onDestroy();
	}
		
}
