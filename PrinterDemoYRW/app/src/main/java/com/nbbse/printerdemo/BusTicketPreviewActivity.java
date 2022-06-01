package com.nbbse.printerdemo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Printer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbbse.printerdemo.util.ConstantUtil;
import com.sagereal.printer.PrinterInterface;

public class BusTicketPreviewActivity extends Activity {


	private String departure;
	private String arrival;
	private String duration;
	private String price;
	private String currDateTime;
	
	private TextView textDateTime = null;
	private TextView textDeparture = null;
	private TextView textArrival = null;
	private TextView textDuration = null;
	private TextView textPrice = null;
	
	private Button buttonPrint = null;
	
	//private Printer print;
	private PrinterInterface printInterfaceService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_ticket_preview);
		
		Bundle bundle = getIntent().getExtras();
		departure = bundle.getString("Departure");
		arrival = bundle.getString("Arrival");
		duration = bundle.getString("Duration");
		price = bundle.getString("Price");
		
		//print = Printer.getInstance();
		printInterfaceService = MainActivity.printInterfaceService;

		initViews();
	}
	
	private void initViews() {
		textDateTime = (TextView) findViewById(R.id.text_datetime);
		textDeparture = (TextView) findViewById(R.id.text_departure);
		textArrival = (TextView) findViewById(R.id.text_arrival);
		textDuration = (TextView) findViewById(R.id.text_duration);
		textPrice = (TextView) findViewById(R.id.text_price);
		
		textDeparture.setText(departure);
		textArrival.setText(arrival);
		textDuration.setText(duration);
		textPrice.setText(price);
		
		SimpleDateFormat df = new SimpleDateFormat("dd  MMM  yyyy  hh:mm", Locale.ENGLISH);
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.AM_PM) == 0) {
			currDateTime = df.format(new Date()) + "am";
		} else {
			currDateTime = df.format(new Date()) + "pm";
		}
		textDateTime.setText(currDateTime);
		
		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//if(print.getPaperStatus() != 1) {
				//int status = print.getPrinterStatus();
				int status = 0;
				try {
					status = printInterfaceService.getPrinterStatus();
				//if(status == Printer.PRINTER_STATUS_NO_PAPER) {
					if(status == ConstantUtil.PRINTER_STATUS_NO_PAPER){
					Toast.makeText(BusTicketPreviewActivity.this, "No Paper.", Toast.LENGTH_SHORT).show();
					return;
				}
				//if(status == Printer.PRINTER_STATUS_OVER_HEAT) {
				if(status == ConstantUtil.PRINTER_STATUS_OVER_HEAT){
					Toast.makeText(BusTicketPreviewActivity.this, "Chip over heat.", Toast.LENGTH_SHORT).show();
					return;
				}
				String durationNew = duration.replace("minutes", "min");
				//print.printBitmap(getResources().openRawResource(R.raw.bus_ticket_logo));
					printInterfaceService.printBitmap_bDate(readStream(getResources().openRawResource(R.raw.bus_ticket_logo)));
					//print.printText(" MOBI BUS", 3);
					printInterfaceService.printText_size(" MOBI BUS", 2);
				//print.printText("         ONE WAY TICKET" + "\n     " + currDateTime + "\n  ----------------------------"
					printInterfaceService.printText("         ONE WAY TICKET" + "\n     " + currDateTime + "\n  ----------------------------"
						+ "\nDeparture:     " + departure + "\nArrival:       " + arrival + "\nDuration:      " + durationNew
						+ "\nSingle Fair:   " + price + "\n");
				//print.printBitmap(getResources().openRawResource(R.raw.bus_ticket_qr));
					printInterfaceService.printBitmap_bDate(readStream(getResources().openRawResource(R.raw.bus_ticket_qr)));
				//print.printEndLine();
					printInterfaceService.printEndLine();
					printInterfaceService.printEndLine();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	/**
	 * 得到图片字节流 数组大小
	 * */
	public static byte[] readStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}
}
