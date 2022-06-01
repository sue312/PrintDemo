package com.nbbse.printerdemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.zxing.WriterException;
import com.nbbse.printerdemo.barcoder.util.CodeUtils;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.sagereal.printer.PrinterInterface;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LottoPreviewActivity extends Activity {

	private static final String bmp_path = "/data/data/com.nbbse.printerdemo/code.bmp";
	private TextView textNumberList = null;
	private TextView textTransNum = null;
	private TextView textAmount = null;
	private Button buttonPrint = null;
	private ImageView imageBarcode = null;
	private TextView textDateTime = null;
	
	private String strNumberList;
	private String strTransNum;
	private String strAmount;
	private String strDateTime;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	
	private Bitmap bmp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lottery_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}
	
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		strNumberList = bundle.getString("numberlist");
		strTransNum = bundle.getString("transnum");
		strAmount = bundle.getString("amount");
	}
	
	private void initViews() {
		textDateTime = (TextView) findViewById(R.id.text_lotto_datetime);
		SimpleDateFormat df = new SimpleDateFormat("E dd MMM yyyy     HH:mm", Locale.ENGLISH);
		strDateTime = df.format(new Date());
		textDateTime.setText(strDateTime);
		
		textNumberList = (TextView) findViewById(R.id.text_numlist);
		textNumberList.setText(strNumberList);
		
		textTransNum = (TextView) findViewById(R.id.text_transnum);
		textTransNum.setText("Transaction Number: " + strTransNum);
		
		textAmount = (TextView) findViewById(R.id.text_amount);
		textAmount.setText(strAmount);
		
		imageBarcode = (ImageView) findViewById(R.id.image_barcode);
		String str = strTransNum.trim();
		Log.d("yanjunke", "loto trans: " + str);
		int size = str.length();
		
		try {
            if (str != null && !"".equals(str)) {
                //bmp = CodeUtils.CreateOneDCode(str);
            	bmp = CodeUtils.CreateTwoDCode(str);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
//        	BitmapUtil.saveBmp(bmp, bmp_path);
        	imageBarcode.setImageBitmap(bmp);
        }
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				print();
			}
		});
	}

	private void print() {
		/*if(print.getPaperStatus() != 1) {
			Toast.makeText(LottoPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
			return;
		}
		print.printText("     LOTTO", 2);
		print.printText("\nGood Luck for you draw on\n   " + strDateTime + "\n\n         Your Numbers:");
		print.printText(" " + strNumberList, 2);
		print.printText("\n        Amount ($): " + strAmount + "\nThank you for chosing MobiWire  solution!"
				+ "\nTransaction Number: \n         " + strTransNum + "\n");
//		print.printBitmap(bmp_path);
		print.printBitmap(bmp);
		print.printEndLine();
		
		setResult(RESULT_OK);*/

		try {
			if(printInterfaceService.getPrinterStatus() != ConstantUtil.PRINTER_STATUS_OK) {
                Toast.makeText(LottoPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                return;
            }

		printInterfaceService.printText_size("   LOTTO", 2);
		printInterfaceService.printText("\nGood Luck for you draw on\n   " + strDateTime + "\n\n         Your Numbers:");
			printInterfaceService.printText_size(" " + strNumberList, 1);
		printInterfaceService.printText("\n        Amount ($): " + strAmount + "\nThank you for chosing MobiWire  solution!"
				+ "\nTransaction Number: \n         " + strTransNum + "\n");
		printInterfaceService.printBitmap_btm_r(bmp);
		printInterfaceService.printEndLine();
			printInterfaceService.printEndLine();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		finish();
	}

}
