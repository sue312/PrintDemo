package com.nbbse.printerdemo;

import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.nbbse.printerdemo.barcoder.util.CodeUtils;
import com.nbbse.printerdemo.util.BitmapUtil;
import com.nbbse.printerdemo.util.ConstantUtil;
import com.sagereal.printer.PrinterInterface;

public class TopupPreviewActivity extends Activity {

	//private static final String bmp_path = "/data/data/com.nbbse.printerdemo/code.bmp";
    private static final String bmp_path ="/sdcard/code.bmp";
    private TextView textCode = null;
	private TextView textTrans = null;
	private ImageView imageBarcode = null;
	private Button buttonPrint = null;
	private Bitmap bmp = null;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.topup_preview);
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;
		initData();
		initViews();
	}
	
	private void initData() {
		
	}
	
	private void initViews() {
		textCode = (TextView) findViewById(R.id.text_topup_code);
		textTrans = (TextView) findViewById(R.id.text_transnum);
		imageBarcode = (ImageView) findViewById(R.id.image_barcode);
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//if(print.getPaperStatus() != 1) {
				try {
					if(printInterfaceService.getPrinterStatus()!= ConstantUtil.PRINTER_STATUS_OK){
                        Toast.makeText(TopupPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
                        return;
                    }

				/*print.printText("     TOP UP", 2);
				print.printText("You have requested an airtime   voucher."
						+ "\nPlease use the following code to credit your account."
						+ "\n           TOP UP CODE");
				print.printText(" " + textCode.getText(), 2);
				print.printText("         Transaction ID" + "\n         " + textTrans.getText());

				print.printBitmap(bmp_path);
				print.printEndLine();*/
					printInterfaceService.printText_size("  TOP UP", 2);
					printInterfaceService.printText("You have requested an airtime   voucher."
							+ "\nPlease use the following code to credit your account."
							+ "\n           TOP UP CODE");
					printInterfaceService.printText_size(" " + textCode.getText(), 1);
					printInterfaceService.printText("         Transaction ID" + "\n         " + textTrans.getText());

					//printInterfaceService.printBitmap(bmp_path);
                    //Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.raw.abcdef, null);
					printInterfaceService.printBitmap_btm_r(bmp);
                    printInterfaceService.printEndLine();
					printInterfaceService.printEndLine();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				setResult(RESULT_OK);
				finish();
				
			}
		});
		
		
		textCode.setText(genCode());
		textTrans.setText(genTransId());
		
		String str = textTrans.getText().toString().trim();
		
		//Bitmap bmp = null;
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
        
	}
	
	private String genTransId() {
		String s = "";
		Random random = new Random();
		for(int i=0; i<13; i++) {
			char c = (char)(48 + random.nextInt(10));
			s += c;
		}
		return s;
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
}
