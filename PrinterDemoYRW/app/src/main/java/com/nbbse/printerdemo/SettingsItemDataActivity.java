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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SettingsItemDataActivity extends Activity {

	private Button buttonAdd = null;
	
	private ListView lv = null;
	
	private DBService dbService;
	private Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_item_data);
		
		dbService = new DBService(this);
		
		initViews();
		
//		updateListView();
	}
	
	private void initViews() {
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingsItemDataActivity.this, SettingsItemDetailActivity.class);
				startActivity(intent);
			}
		});
		
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				cursor.moveToPosition(position);
				int cursorId = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
				double price = cursor.getDouble(cursor.getColumnIndex("price"));
				int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
				
				Log.d("yanjunke", "item " + position + " click " + name + " " + barcode + " " + price + " " + quantity);
				
				Intent intent = new Intent(SettingsItemDataActivity.this, SettingsItemDetailActivity.class);
				intent.putExtra("id", cursorId);
				intent.putExtra("name", name);
				intent.putExtra("barcode", barcode);
				intent.putExtra("price", price);
				intent.putExtra("quantity", quantity);
				startActivity(intent);
			}
		});
	}
	
	private void updateListView() {
		String sql = "select * from t_item_lib"; 
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		cursor = db.rawQuery(sql, null);
		
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
	protected void onResume() {
		super.onResume();
		
		updateListView();
	}

}
