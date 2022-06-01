package com.nbbse.printerdemo;

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

public class CreateAccountPreviewActivity extends Activity {
	private TextView textFirstName = null;
	private TextView textLastName = null;
	private TextView textPhoneNumber = null;
	private TextView textAccount = null;
	private TextView textBalance = null;
	private TextView textPassword = null;
	private Button buttonPrint = null;
	
	private String strFirstName;
	private String strLastName;
	private String strPhoneNumber;
	private String strAccount;
	private String strPassword;
	private double balance;
	
	//Printer print;
	private PrinterInterface printInterfaceService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			strFirstName = bundle.getString("firstname");
			strLastName = bundle.getString("lastname");
			strPhoneNumber = bundle.getString("phonenumber");
			strAccount = bundle.getString("account");
			strPassword = bundle.getString("password");
			balance = bundle.getDouble("balance");
		}
	}
	private void initViews() {
		textFirstName = (TextView) findViewById(R.id.text_firstname);
		textFirstName.setText("First Name: " + strFirstName);
		textLastName = (TextView) findViewById(R.id.text_lastname);
		textLastName.setText("Last Name: " + strLastName);
		textPhoneNumber = (TextView) findViewById(R.id.text_phonenumber);
		textPhoneNumber.setText("Phone Number: " + strPhoneNumber);
		textAccount = (TextView) findViewById(R.id.text_account);
		textAccount.setText("Account Number: " + strAccount);
		textBalance = (TextView) findViewById(R.id.text_balance);
		textBalance.setText("Balance: " + balance + " €");
		textPassword = (TextView) findViewById(R.id.text_password);
		textPassword.setText("Password: " + strPassword);
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//if(print.getPaperStatus() != 1) {
				try {
					if(printInterfaceService.getPrinterStatus()!= ConstantUtil.PRINTER_STATUS_OK){
                        Toast.makeText(CreateAccountPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }

				//print.printBitmap(getResources().openRawResource(R.raw.mobiwire_logo));
					//printInterfaceService.printBitmap("/res/drawable/mobiwire_logo.png");
					printInterfaceService.printBitmap_bDate(BitmapUtil.readStream(getResources().openRawResource(R.raw.mobiwire_logo)));
				//print.printText("MobiBank Account", 2);
					printInterfaceService.printText_size("MobiBank Account", 2);
				//print.printText("\n Please find the details below:"
					printInterfaceService.printText("\n Please find the details below:"
						+ "\n\nFirst Name: " + strFirstName
						+ "\nLast Name: " + strLastName
						+ "\nPhone Number: " + strPhoneNumber + "\n");
				//print.printText("Account Number: " + strAccount);
					printInterfaceService.printText("Account Number: " + strAccount);
				//print.printText("Account Balance: " + balance + " €");
					printInterfaceService.printText("Account Balance: " + balance + " €");
				//print.printText("Password: " + strPassword);
					printInterfaceService.printText("Password: " + strPassword);
				//print.printText("\nThank you for chosing MobiWire  solution!");
					printInterfaceService.printText("\nThank you for chosing MobiWire  solution!");
				//print.printEndLine();
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
