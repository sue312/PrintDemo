package com.nbbse.printerdemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveMoneyPreviewActivity extends Activity {
	private String trans;
	private String amount;
	private String datetime;
	
	private TextView textTrans = null;
	private TextView textAmount = null;
	private TextView textDateTime = null;
	
	private Button buttonPrint = null;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.receive_money_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		trans = bundle.getString("trans");
		amount = bundle.getString("amount");
	}
	
	private void initViews() {
		textTrans = (TextView) findViewById(R.id.text_trans);
		textTrans.setText("Transaction Number: " + trans);
		
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText("hereby declare that I received " + amount + " € from ACME Company.");
		
		textDateTime = (TextView) findViewById(R.id.text_datetime);
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy    HH:mm", Locale.ENGLISH);
		datetime = df.format(new Date());
		textDateTime.setText(datetime);
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			/*	if(print.getPaperStatus() != 1) {
					Toast.makeText(ReceiveMoneyPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				print.printText("  MobiTransfer", 2);
				print.printText("\nTransaction Number:" + trans + "\n\nhereby declare that I received  " + amount + "$ from ACME Company."
						+ "\n\nSignature: _____________________" + "\n\n    " + datetime + "\n");
				print.printBitmap(getResources().openRawResource(R.raw.bus_ticket_qr));
				print.printEndLine();*/
				try {
					if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
                        Toast.makeText(ReceiveMoneyPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }
				printInterfaceService.printText_size("  MobiTransfer", 1);
				printInterfaceService.printText("\nTransaction Number:" + trans + "\n\nhereby declare that I received  " + amount + "$ from ACME Company."
						+ "\n\nSignature: _____________________" + "\n\n    " + datetime + "\n");
					//printInterfaceService.printBitmap("/res/raw/bus_ticket_qr.bmp");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.bus_ticket_qr)));
				printInterfaceService.printEndLine();
					printInterfaceService.printEndLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}
