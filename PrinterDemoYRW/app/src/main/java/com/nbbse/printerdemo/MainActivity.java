package com.nbbse.printerdemo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.sagereal.printer.PrinterInterface;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	public static String TAG = "PrintDemo_MainActivity";
	private static int MAIN_MENU_NUM = 8;
	private ListView mainlistview;
	public static PrinterInterface printInterfaceService;

	//private Printer print;
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			printInterfaceService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			printInterfaceService = PrinterInterface.Stub.asInterface(service);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		this.bindService(getPrintIntent(), serviceConnection, Service.BIND_AUTO_CREATE);

	}
	@Override
	public void onPause() {//? -----
		super.onPause();
		this.unbindService(serviceConnection);
	}

	private Intent getPrintIntent() {
		Intent aidlIntent = new Intent();
		aidlIntent.setAction("sagereal.intent.action.START_PRINTER_SERVICE_AIDL");
		aidlIntent.setPackage("com.sagereal.printer");
		return aidlIntent;
	}

	private String[] tvdata = new String[] {
		"Ticketing", "Top-Up Airtime", "Money Transfer", "Bill Payment", "Lottery", "Retail Solution", "Transactions Report", "Settings"
	};
	
	private int[] ivdata = new int[] {
		R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5, R.drawable.icon6, R.drawable.icon7, R.drawable.icon8
	};

	private String ticket_list[] = new String[]{"a. Bus Ticket", "b. Parking ticket"};
	private String topup_list[] = new String[]{"a. Cash Payment", "b. m-Payment"};
	private String moneytrans_list[] = new String[]{"a. Send Money", "b. Receive Money"};
	private String lottory_list[] = new String[]{"a. LOTTO", "b. BINGO"};
	private String account_feature[] = new String[]{"a. Create Customer Account", "b. Credit Customer Account"};
	
	private Button button1 = null;
	private Button button2 = null;
	private Button button3 = null;
	private Button button4 = null;
	private Button button5 = null;
	private Button button6 = null;
	private Button button7 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	private void initViews() {
		/*
		mainlistview = (ListView) findViewById(R.id.mainlistview);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for(int i=0; i<MAIN_MENU_NUM; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("itemicon", ivdata[i]);
			item.put("itemtext", tvdata[i]);
			data.add(item);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.mainlist, new String[]{"itemicon", "itemtext"}, new int[]{R.id.itemicon, R.id.itemtext});
		mainlistview.setAdapter(simpleAdapter);
		
		mainlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.d(TAG, "item" + position +"clicked!");
				switch(position) {
				case 0:
					showTicketListDialog();
					break;
				case 1:
					showTopUpListDialog();
					break;
				case 2:
					showMoneyTransListDialog();
					break;
				case 4:
					showLottoryListDialog();
					break;
				case 7:
					showAccountFeatureDialog();
				default:
					break;	
				}
				
			}
		});
	*/
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(myButtonClickListener);
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(myButtonClickListener);
		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(myButtonClickListener);
		button4 = (Button) findViewById(R.id.button4);
		button4.setOnClickListener(myButtonClickListener);
		button5 = (Button) findViewById(R.id.button5);
		button5.setOnClickListener(myButtonClickListener);
		button6 = (Button) findViewById(R.id.button6);
		button6.setOnClickListener(myButtonClickListener);
		button7 = (Button) findViewById(R.id.button7);
		button7.setOnClickListener(myButtonClickListener);
	}
	
	MyButtonClickListener myButtonClickListener = new MyButtonClickListener();
	
	class MyButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				Intent intent = new Intent(MainActivity.this, BusChooseActivity.class);
				startActivity(intent);
				break;
			case R.id.button2:
				Intent intent2 = new Intent(MainActivity.this, TopupEnterNumberActivity.class);
				startActivity(intent2);
				break;	
			case R.id.button3:
				showMoneyTransListDialog();
				break;
			case R.id.button4:
				Intent intent4 = new Intent(MainActivity.this, BillPaymentActivity.class);
				startActivity(intent4);
				break;
			case R.id.button5:
				Intent intent5 = new Intent(MainActivity.this, LottoInputActivity.class);
				startActivity(intent5);
				break;		
			case R.id.button6:
				Intent intent6 = new Intent(MainActivity.this, RetailMainActivity.class);
				startActivity(intent6);
				break;		
			case R.id.button7:
				Intent intent7 = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent7);
				break;				
			default:
				break;
			}
			
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_about) {
			showAboutDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showTicketListDialog() {
		new AlertDialog.Builder(this).setTitle("Choose:").setItems(ticket_list, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				Log.d(TAG, "Dialog Item " + which + " clicked!");
				if(which == 0) {
					Intent intent = new Intent(MainActivity.this, BusChooseActivity.class);
					startActivity(intent);
				} else if(which == 1) {
					
				}
			}
		}).show();
	}
	
	private void showTopUpListDialog() {
		new AlertDialog.Builder(this).setTitle("Choose:").setItems(topup_list, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				Log.d(TAG, "Dialog Item " + which + " clicked!");
				if(which == 0) {
					Intent intent = new Intent(MainActivity.this, TopupEnterNumberActivity.class);
					startActivity(intent);
				} else if(which == 1) {
					
				}
			}
		}).show();
	}
	
	private void showMoneyTransListDialog() {
		new AlertDialog.Builder(this).setTitle("Choose:").setItems(moneytrans_list, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				if(which == 0) {
					Intent intent = new Intent(MainActivity.this, SendMoneyInputActivity.class);
					startActivity(intent);
				} else if (which == 1) {
					Intent intent = new Intent(MainActivity.this, ReceiveMoneyInputActivity.class);
					startActivity(intent);
				}
			}
		}).show();
	}

	private void showLottoryListDialog() {
		new AlertDialog.Builder(this).setTitle("Choose:").setItems(lottory_list, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				if(which == 0) {
					Intent intent = new Intent(MainActivity.this, LottoInputActivity.class);
					startActivity(intent);
				} else if (which == 1) {
					
				}
			}
		}).show();
	}
	private void showAccountFeatureDialog() {
		new AlertDialog.Builder(this).setTitle("Account:").setItems(account_feature, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int which) {
				if(which == 0) {
					Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
					startActivity(intent);
				} else if(which == 1) {
					Intent intent = new Intent(MainActivity.this, CreditAccountActivity.class);
					startActivity(intent);
				}
			}
		}).show();
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Exit").setMessage("Sure to Exit?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Log.d(TAG, "Dialog Item " + arg1 + " clicked!");
				onBackReally();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Log.d(TAG, "Dialog Item " + arg1 + " clicked!");
			}
		}).show();
//		
	}
	
	private void onBackReally() {
		super.onBackPressed();
	}
	
	private String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "GetVersion Error!";
		}
	}
	
	private void showAboutDialog() {
		new AlertDialog.Builder(this).setTitle("About PrintDemo").setMessage("Version: " + getVersion()).show(); 
	}

}
