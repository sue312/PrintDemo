package com.nbbse.printerdemo;

import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.nbbse.printerdemo.util.SRUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SendMoneyPreviewActivity extends Activity {

	private TextView textAmount = null;
	private TextView textDeclare = null;
	private TextView textTrans = null;
	private Button buttonPrint = null;
	private String amount;
	private String trans; 
	
	//private Printer print = null;
	private PrinterInterface printInterfaceService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_money_preview);
		
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		amount = bundle.getString("amount");
		trans = bundle.getString("trans");
		
	}
	private void initViews() {
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText("You have sent: " + SRUtil.doubleFormat(Double.parseDouble(amount)) + " €");
		textDeclare = (TextView) findViewById(R.id.text_declare);
		textDeclare.setText("We ACME company hereby declare that we received " + SRUtil.doubleFormat(Double.parseDouble(amount)) + " € for money transfer.");
		textTrans = (TextView) findViewById(R.id.text_trans);
		textTrans.setText("Please find the transaction number: " + trans);
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*if(print.getPaperStatus() != 1) {
					Toast.makeText(SendMoneyPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				print.printText("   MobiTransfer", 2);
				print.printText("    " + textAmount.getText() + "\n    " + textTrans.getText()
						+ "\n  " + textDeclare.getText()
						+ "\n\nACME Signature:_________________\n"
						+ "\nYou will receive an SMS with a secret code. This secret code will be requested when withdrawing the money."
						+ "\nThank you for chosing MobiWire  solution!");
				print.printBitmap(getResources().openRawResource(R.raw.bus_ticket_qr));
				print.printEndLine();*/
				try {
					if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
                        Toast.makeText(SendMoneyPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }


				printInterfaceService.printText_size("   MobiTransfer", 1);
				printInterfaceService.printText("    " + textAmount.getText() + "\n    " + textTrans.getText()
						+ "\n  " + textDeclare.getText()
						+ "\n\nACME Signature:_________________\n"
						+ "\nYou will receive an SMS with a secret code. This secret code will be requested when withdrawing the money."
						+ "\nThank you for chosing MobiWire  solution!");
				//printInterfaceService.printBitmap(getResources().openRawResource(R.raw.bus_ticket_qr));
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
