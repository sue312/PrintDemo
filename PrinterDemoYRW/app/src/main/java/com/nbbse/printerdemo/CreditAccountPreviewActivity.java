package com.nbbse.printerdemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.sagereal.printer.PrinterInterface;

public class CreditAccountPreviewActivity extends Activity {

	private TextView textDateTime = null;
//	private TextView textFirstName = null;
//	private TextView textLastName = null;
	private TextView textName = null;
	private TextView textPhoneNumber = null;
	private TextView textAccount = null;
	private TextView textCredit = null;
	private TextView textPreBalance = null;
	private TextView textNewBalance = null;
	
	private Button buttonPrint = null;
	
	private String strDateTime;
//	private String strFirstName;
//	private String strLastName;
	private String strName;
	private String strPhoneNumber;
	private String strAccount;
	
	private double credit;
	private double preBalance;
	private double newBalance;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.credit_account_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			strName = bundle.getString("name");
			strPhoneNumber = bundle.getString("phone");
			strAccount = bundle.getString("account");
			credit = bundle.getDouble("credit");
			preBalance = bundle.getDouble("prebalance");
			newBalance = bundle.getDouble("newbalance");
		}
	}
	
	private void initViews() {
		textDateTime = (TextView) findViewById(R.id.text_datetime);
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy      HH:mm", Locale.ENGLISH);
		strDateTime = df.format(new Date());
		textDateTime.setText(strDateTime);
		
		textName = (TextView) findViewById(R.id.text_name);
		textName.setText("Name: " + strName);
		
		textPhoneNumber = (TextView) findViewById(R.id.text_phonenumber);
		textPhoneNumber.setText("Phone Number: " + strPhoneNumber);
		textAccount = (TextView) findViewById(R.id.text_account);
		textAccount.setText("Account Number: " + strAccount);
		
		textCredit = (TextView) findViewById(R.id.text_credit_amount);
		textCredit.setText("Your account has been credited with " + credit + " €");
		textPreBalance = (TextView) findViewById(R.id.text_pre_balance);
		textPreBalance.setText("Previous Balance: " + preBalance + " €");
		textNewBalance = (TextView) findViewById(R.id.text_new_balance);
		textNewBalance.setText("New Balance: " + newBalance + " €");
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*if(print.getPaperStatus() != 1) {
					Toast.makeText(CreditAccountPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
				print.printText("     CASH IN", 2);
				print.printText("\n  " + strDateTime
						+ "\n\nName: " + strName
						+ "\nPhone Number: " + strPhoneNumber
						+ "\nAccount Number: " + strAccount
						+ "\n--------------------------------"
						+ "\nYour account has been credited  with " + credit + " €"
						+ "\nPreview Balance: " + preBalance + " €"
						+ "\nNew Balance: " + newBalance + " €"
						+ "\n--------------------------------"
						+ "\n\nThank you for chosing MobiWire  Solution!");

				print.printEndLine();*/
				try {
					if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
                        Toast.makeText(CreditAccountPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }

					//printInterfaceService.printBitmap("/res/raw/mobiwire_logo.bmp");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
				printInterfaceService.printText_size("     CASH IN", 2);
				printInterfaceService.printText("\n  " + strDateTime
						+ "\n\nName: " + strName
						+ "\nPhone Number: " + strPhoneNumber
						+ "\nAccount Number: " + strAccount
						+ "\n--------------------------------"
						+ "\nYour account has been credited  with " + credit + " €"
						+ "\nPreview Balance: " + preBalance + " €"
						+ "\nNew Balance: " + newBalance + " €"
						+ "\n--------------------------------"
						+ "\n\nThank you for chosing MobiWire  Solution!");

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
