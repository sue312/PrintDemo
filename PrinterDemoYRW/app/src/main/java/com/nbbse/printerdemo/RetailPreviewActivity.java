package com.nbbse.printerdemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.google.zxing.WriterException;
import com.nbbse.printerdemo.barcoder.util.CodeUtils;
import com.nbbse.printerdemo.db.DBService;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.nbbse.printerdemo.util.SRUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RetailPreviewActivity extends Activity {

	private static final String bmp_path = "/data/data/com.nbbse.printerdemo/code.bmp";
	private Button buttonPrint = null;
	private ListView lv = null;
	private TextView textTrans = null;
	private TextView textDateTime = null;
	private TextView textTotalPrice = null;
	private TextView textVat = null;
	
	private ImageView imageBarcode = null;
	
	private DBService dbService = null; 
	
	private String transId;
	private String datetime;
	private double totalPrice;
	private Cursor cursor;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	private Bitmap bmp ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_preview);
		
		dbService = new DBService(this);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		totalPrice = bundle.getDouble("total", 0);
	}

	private void initViews() {
		lv = (ListView) findViewById(R.id.lv);
		updateListView();
		
		SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM yyyy  HH:mm", Locale.ENGLISH);
		datetime = df.format(new Date());
		textDateTime = (TextView) findViewById(R.id.text_datetime);
		textDateTime.setText(datetime);
		
		textTotalPrice = (TextView) findViewById(R.id.text_totalprice);
		textTotalPrice.setText("TOTAL (EUR)           " + SRUtil.doubleFormat(totalPrice));
		
		textVat = (TextView) findViewById(R.id.text_vat);
		textVat.setText("%20 VAT included: " + SRUtil.doubleFormat(totalPrice/6));
		
		transId = genCode();
		textTrans = (TextView) findViewById(R.id.text_trans);
		textTrans.setText("Transaction ID: " + transId);
		
		imageBarcode = (ImageView) findViewById(R.id.image_barcode);
		
		String str = transId.trim();
		int size = str.length();

		try {
            if (str != null && !"".equals(str)) {
                bmp = CodeUtils.CreateOneDCode(str);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
        	BitmapUtil.saveBmp(bmp, bmp_path);
        	imageBarcode.setImageBitmap(bmp);
        }
		
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					print();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				addToHistoryDB();
				clearCurr();
				
				setResult(RESULT_OK);
				finish();
			}
		});
		
	}
	
	
	
	private void updateListView() {
		String sql = "select * from t_curr_lib"; 
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
	
	private String genCode() {
		String s = "";
		Random random = new Random();
		for(int i=0; i<13; i++) {
			int x = random.nextInt(36);
			if(x >= 10) {
				x += 7;
			}
			
			char c = (char)(48 + x);
			s += c;
		}
		return s;
	}
	
	private void print() throws RemoteException {
		if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
			Toast.makeText(RetailPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
		//printInterfaceService.printBitmap_path_r("/res/raw/mobiwire_logo.bmp");
		try {
			printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		printInterfaceService.printText_size("  MobiWire", 2);
		printInterfaceService.printText("     79 Avenue France Arage" + "\n         32000 Nanterre\n\n" + datetime
				+ "\n ------------------------------" + "\nItem List        price   qtty");
		String strItemList = "";
		String strItem = "";
		String itemname;
		String itemcode;
		double price;
		int qtty;
		
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				itemname = cursor.getString(cursor.getColumnIndex("name"));
				itemcode = cursor.getString(cursor.getColumnIndex("barcode"));
				price = cursor.getDouble(cursor.getColumnIndex("price"));
				qtty = cursor.getInt(cursor.getColumnIndex("quantity"));
				
				int len = itemname.length();
				if(len > 9) {
					itemname = itemname.substring(0, 9) + " ";
				} else {
					for(int i=len; i<10; i++) {
						itemname += " ";
					}
				}
				strItem = itemname + itemcode + "\n                 " + SRUtil.doubleFormat(price) + "    " + qtty + "\n"; 
				strItemList += strItem;
				//print.printText(strItemList);
				printInterfaceService.printText(strItemList);
				cursor.moveToNext();
			}
		}
		//print.printText("\n ------------------------------"
		printInterfaceService.printText("\n ------------------------------"
				+ "\n  TOTAL(EUR)    " + SRUtil.doubleFormat(totalPrice)
				+ "\n        *** VAT EUR ***"
				+ "\n    %20 VAT included: " + SRUtil.doubleFormat(totalPrice/6)
				+ "\nTransaction ID: " + transId);
		//print.printBitmap(bmp_path);
		printInterfaceService.printBitmap_btm_r(bmp);
		//print.printText("Thank you for chosing MobiWire  Solution!");
		printInterfaceService.printText("Thank you for chosing MobiWire  Solution!");
		//print.printEndLine();
		printInterfaceService.printEndLine();
		printInterfaceService.printEndLine();
	}
	private void addToHistoryDB() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql_t_sell_lib = "create table if not exists t_sell_lib (_id integer primary key autoincrement,"
				+ "name varchar(10) not null on conflict fail,"
				+ "barcode varchar(15) not null on conflict fail,"
				+ "price double,"
				+ "quantity int,"
				+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
		db.execSQL(sql_t_sell_lib);
		
		String name;
		String barcode;
		double price;
		int quantity;
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				name = cursor.getString(cursor.getColumnIndex("name"));
				barcode = cursor.getString(cursor.getColumnIndex("barcode"));
				price = cursor.getDouble(cursor.getColumnIndex("price"));
				quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
				db.execSQL("insert into t_sell_lib(name,barcode,price,quantity) values(?,?,?,?)", new Object[]{name,barcode,price,quantity});
				
				db.execSQL("update t_item_lib set quantity=quantity-? where barcode=?", new Object[]{quantity, barcode});
				cursor.moveToNext();
			}
		}
		
	}
	private void clearCurr() {
		SQLiteDatabase db = dbService.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS t_curr_lib");
	}
}
