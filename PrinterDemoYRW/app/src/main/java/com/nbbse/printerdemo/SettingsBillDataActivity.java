package com.nbbse.printerdemo;

import java.util.Random;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.nbbse.printerdemo.barcoder.util.CodeUtils;
import com.nbbse.printerdemo.db.DBService;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.nbbse.printerdemo.util.SRUtil;
import com.sagereal.printer.PrinterInterface;

public class SettingsBillDataActivity extends Activity {
	private static final String bmp_path = "/data/data/com.nbbse.printerdemo/code.bmp"; 
	private ListView lv = null;
	private EditText editAmount = null;
	private Button buttonAdd = null;
	
	private DBService dbService = null;
	private Cursor cursor;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_bill_data);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		dbService = new DBService(this);
		initViews();
	}

	private void initViews(){
		editAmount = (EditText) findViewById(R.id.edit_amount);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//if(print.getPaperStatus() != 1) {
				try {
					if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK){
                        Toast.makeText(SettingsBillDataActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }

				double amount = Double.parseDouble(editAmount.getText().toString());
				String transnum = genTrans();
				String sql = "insert into t_bill_lib(transnum,name,amount,pay) values(?,?,?,0)"; 
				Object[] args = new Object[]{transnum, null, amount};
				SQLiteDatabase db = dbService.getReadableDatabase();
				db.execSQL(sql, args);
				db.close();
				updateListView();
				
				//print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
					//printInterfaceService.printBitmap("/res/raw/mobiwire_logo.bmp");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
				//print.printText("      Bill", 2);
					printInterfaceService.printText_size("    Bill", 2);
				//print.printText("\nPlease Pay the Bill:"
					printInterfaceService.printText("\nPlease Pay the Bill:"
						+ "\n\n Amount: " + SRUtil.doubleFormat(amount) + " €"
						+ "\n Transaction ID: " + transnum);
				
				String str = transnum;
				
				Bitmap bmp = null;
				try {
		            if (str != null && !"".equals(str)) {
		                bmp = CodeUtils.CreateOneDCode(str);
		            }
		        } catch (WriterException e) {
		            e.printStackTrace();
		        }
		        if (bmp != null) {
		        	BitmapUtil.saveBmp(bmp, bmp_path);
		        }
		        
		       // print.printBitmap(bmp_path);
					printInterfaceService.printBitmap(bmp_path);
		        //print.printEndLine();
					printInterfaceService.printEndLine();
					printInterfaceService.printEndLine();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		lv = (ListView) findViewById(R.id.lv);
		
	}

	private String genTrans() {
		
		String s = "";
		Random random = new Random();
		for(int i=0; i<13; i++) {
			char c = (char)(48 + random.nextInt(10));
			s += c;
		}
		return s;
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql_table = "create table if not exists t_bill_lib (_id integer primary key autoincrement,"
				+ "transnum varchar(15) not null on conflict fail,"
				+ "name varchar(30),phonenumber varchar(20),"
				+ "amount double,pay int)";
		db.execSQL(sql_table);
		db.close();
		updateListView();
		
	}
	
	private void updateListView() {
		String sql = "select * from t_bill_lib where pay=0"; 
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		cursor = db.rawQuery(sql, null);
		
		Log.d("yanjunke", "cursor_count:" + cursor.getCount());
		if(cursor.getCount() > 0) { 
			String[] columnNames = {"transnum", "amount"};
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.settings_bill_list, cursor, columnNames, new int[] { 
	                R.id.barcode, R.id.amount}, 0);
			
			lv.setAdapter(adapter);
		}
//		cursor.close();
		db.close();
	}
	
	
}
