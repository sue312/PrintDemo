package com.nbbse.printerdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.nbbse.printerdemo.db.DBService;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.nbbse.printerdemo.util.SRUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BillPreviewActivity extends Activity {

	private TextView textBillNum = null;
	private TextView textName = null;
	private TextView textAmount = null;
	private TextView textDateTime = null;
	private Button buttonPrint = null;
	
	private String strBillNum;
	private String strName;
	private String strFirstName;
	private String strLastName;
	private double amount;
	private String strDateTime;
	private String strPhone;
	
	//private Printer print = null;
	private DBService dbService = null;
	private PrinterInterface printInterfaceService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bill_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		dbService = new DBService(this);
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			strBillNum = bundle.getString("number");
			amount = bundle.getDouble("amount");
			strFirstName = bundle.getString("first");
			strLastName = bundle.getString("last");
			strPhone = bundle.getString("phone");
		}
	}
	
	private void initViews() {
		textBillNum = (TextView) findViewById(R.id.text_billnum);
		textBillNum.setText("Bill Number: " + strBillNum);
		textName = (TextView) findViewById(R.id.text_name);
		textName.setText("Name: " + strFirstName + ", " + strLastName);
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText("Amount Paid: " + SRUtil.doubleFormat(amount) + " €");
		textDateTime = (TextView) findViewById(R.id.text_datetime);
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy      HH:mm", Locale.ENGLISH);
		strDateTime = df.format(new Date());
		textDateTime.setText(strDateTime);
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*if(print.getPaperStatus() != 1) {
					Toast.makeText(BillPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
				print.printText("  BILL PAYMENT", 2);
				print.printText("\n   Bill Number: " + strBillNum
						+ "\n   Name: " + strFirstName + ", " + strLastName
						+ "\n   Amount paid: " + SRUtil.doubleFormat(amount)
						+ "\n We confirm that your bill is    fully paid."
						+ "\n ------------------------------"
						+ "\n\n  " + strDateTime + "\n");
				
				print.printBitmap(getResources().openRawResource(R.raw.bus_ticket_qr));
				print.printEndLine();*/
				try {
					if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
                        Toast.makeText(BillPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }

				//printInterfaceService.printBitmap("/res/raw/mobiwire_logo.bmp");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
				printInterfaceService.printText_size("  BILL PAYMENT", 1);
				printInterfaceService.printText("\n   Bill Number: " + strBillNum
						+ "\n   Name: " + strFirstName + ", " + strLastName
						+ "\n   Amount paid: " + SRUtil.doubleFormat(amount)
						+ "\n We confirm that your bill is    fully paid."
						+ "\n ------------------------------"
						+ "\n\n  " + strDateTime + "\n");

				//printInterfaceService.printBitmap("/res/raw/bus_ticket_qr.bmp");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.bus_ticket_qr)));
				printInterfaceService.printEndLine();
				printInterfaceService.printEndLine();

				} catch (Exception e) {
					e.printStackTrace();
				}
				strName = strFirstName + " " + strLastName;
				SQLiteDatabase db = dbService.getWritableDatabase();
				db.execSQL("update t_bill_lib set name=?,pay=1 where transnum=?", new String[]{strName, strBillNum});
				db.close();
				
				setResult(RESULT_OK);
				finish();
				
			}
		});
	}

}
