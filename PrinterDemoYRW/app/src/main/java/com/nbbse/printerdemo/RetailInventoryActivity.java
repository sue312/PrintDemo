package com.nbbse.printerdemo;

import com.nbbse.printerdemo.db.DBService;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.nbbse.printerdemo.util.SRUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.InputStream;

public class RetailInventoryActivity extends Activity {

	private ListView lv = null;
	private Button buttonPrint = null;
	
	private DBService dbService;
	private Cursor cursor;
	private PrinterInterface printInterfaceService;
/*	public static PrinterInterface printInterfaceService;
	
	//private Printer print;
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			printInterfaceService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			printInterfaceService = PrinterInterface.Stub.asInterface(service);
		}
	}*/;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_inventory);
		printInterfaceService = MainActivity.printInterfaceService;
		//print= Printer.getInstance();
		dbService = new DBService(this);
		
		initViews();
	}

	private void initViews() {
		lv = (ListView) findViewById(R.id.lv);
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
				//if(print.getPaperStatus() != 1) {
				if (printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
					Toast.makeText(RetailInventoryActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				//print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
				//print.printText("    INVENTORY", 2);
					printInterfaceService.printText_size(" INVENTORY", 2);
				//print.printText("\n\nItem List        price   qtty");
					printInterfaceService.printText("\n\nItem List        price   qtty");
				String strItemList = "";
				String strItem = "";
				String itemname;
				String itemcode;
				double price;
				int qtty;
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						itemname = cursor.getString(cursor.getColumnIndex("name"));
						itemcode = cursor.getString(cursor.getColumnIndex("barcode"));
						price = cursor.getDouble(cursor.getColumnIndex("price"));
						qtty = cursor.getInt(cursor.getColumnIndex("quantity"));
						int len = itemname.length();
						if (len > 9) {
							itemname = itemname.substring(0, 9) + " ";
						} else {
							for (int i = len; i < 10; i++) {
								itemname += " ";
							}
						}
						strItem = itemname + itemcode + "\n                 " + SRUtil.doubleFormat(price) + "    " + qtty + "\n";
						strItemList += strItem;
						cursor.moveToNext();
					}
				}
				//print.printText(strItemList);
					printInterfaceService.printText(strItemList);
				//print.printEndLine();
					printInterfaceService.printEndLine();
					printInterfaceService.printEndLine();
			}catch (Exception e) {
					e.printStackTrace();
				}
				
				setResult(RESULT_OK);
				finish();
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
		//this.bindService(getPrintIntent(), serviceConnection, Service.BIND_AUTO_CREATE);
		updateListView();
	}
	/*@Override
	public void onPause() {//? -----
		super.onPause();
		this.unbindService(serviceConnection);
	}

	private Intent getPrintIntent() {
		Intent aidlIntent = new Intent();
		aidlIntent.setAction("sagereal.intent.action.START_PRINTER_SERVICE_AIDL");
		aidlIntent.setPackage("com.sagereal.printer");
		return aidlIntent;
	}*/
}
