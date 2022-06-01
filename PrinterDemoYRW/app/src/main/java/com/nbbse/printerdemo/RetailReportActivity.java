package com.nbbse.printerdemo;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;


import com.nbbse.printerdemo.db.DBService;
import com.nbbse.printerdemo.util.SRUtil;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class RetailReportActivity extends Activity {

	private DBService dbService;
	
	private double totalToday;
	private double totalThisWeek;
	private double totalThisMonth;
	private double totalThisYear;
	
	private TextView textTotalToday = null;
	private TextView textTotalThisWeek = null;
	private TextView textTotalThisMonth = null;
	private TextView textTotalThisYear = null;
	private Button buttonOK = null;
	
	private GraphicalView graphicalView;
	private int archCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.retail_report);
		
		dbService = new DBService(this);
	
		SQLiteDatabase db = dbService.getWritableDatabase();
		String sql_t_sell_lib = "create table if not exists t_sell_lib (_id integer primary key autoincrement,"
				+ "name varchar(10) not null on conflict fail,"
				+ "barcode varchar(15) not null on conflict fail,"
				+ "price double,"
				+ "quantity int,"
				+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
		db.execSQL(sql_t_sell_lib);
		
		initData();
		initViews();
		
	}
	
	private void initData() {
		totalToday = queryToday();
		totalThisWeek = queryThisWeek();
		totalThisMonth = queryThisMonth();
		totalThisYear = queryThisYear();
	}
	private void initViews() {
		
		CategorySeries dataset = buildCategoryDataset("????");
		if(dataset != null) {
//			int[] colors = {Color.BLUE,Color.GREEN,Color.MAGENTA,Color.RED};
			DefaultRenderer renderer = buildCategoryRenderer();
			
			graphicalView = ChartFactory.getPieChartView(getBaseContext(), dataset, renderer);

			
			LinearLayout layout = (LinearLayout)findViewById(R.id.layout_arch);
			layout.removeAllViews();
//			layout.setBackgroundColor(Color.BLACK);
			layout.addView(graphicalView, new LayoutParams(280,260));
			
		}
		
		textTotalToday = (TextView) findViewById(R.id.text_total_today);
		textTotalToday.setText("Total Sales Today: " + SRUtil.doubleFormat(totalToday));
		
		textTotalThisWeek = (TextView) findViewById(R.id.text_total_this_week);
		textTotalThisWeek.setText("Total Sales This Week: " + SRUtil.doubleFormat(totalThisWeek));
		
		textTotalThisMonth = (TextView) findViewById(R.id.text_total_this_month);
		textTotalThisMonth.setText("Total Sales this Month: " + SRUtil.doubleFormat(totalThisMonth));
		
		textTotalThisYear = (TextView) findViewById(R.id.text_total_this_year);
		textTotalThisYear.setText("Total Sales This Year: " + SRUtil.doubleFormat(totalThisYear));
		
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	protected CategorySeries buildCategoryDataset(String title) {
		CategorySeries series = new CategorySeries(title);
		
		SQLiteDatabase db = dbService.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from t_item_lib", null);
		if(cursor.getCount() > 0) {
			int count = cursor.getCount();
			cursor.moveToFirst();
			String[] names = new String[count];
			double[] price = new double[count]; 
			int i = 0;
			while(!cursor.isAfterLast()) {
				String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
				Log.d("yanjunke", "report: barcode: " + barcode);
				Cursor c = db.rawQuery("select * from t_sell_lib where barcode=?", new String[]{barcode});
				if(c.getCount() > 0) {
					c.moveToFirst();
					names[i] = c.getString(c.getColumnIndex("name"));
					while(!c.isAfterLast()) {
						Log.d("yanjunke", "report2: " + c.getDouble(c.getColumnIndex("price")) + " " + c.getInt(c.getColumnIndex("quantity")));
						price[i] += (c.getDouble(c.getColumnIndex("price")) * c.getInt(c.getColumnIndex("quantity")));
						c.moveToNext();
					}
				}
				cursor.moveToNext();
				i++;
			}
			
			for (int j=0; j<count; j++) {
				if(price[j] > 0) {
					series.add(names[j], price[j]);
					archCount++;
				}
			}
			return series;
		} else {
			return null;
		}
		
	}
	
	protected DefaultRenderer buildCategoryRenderer() {
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setLegendTextSize(20);//????????????
		//renderer.setZoomButtonsVisible(true);//??????????
		renderer.setZoomEnabled(false);//?????????.
		renderer.setChartTitleTextSize(10);//???????????
//		renderer.setChartTitle("????");//???????  ?????????
		renderer.setLabelsTextSize(18);//????????????
		renderer.setLabelsColor(Color.DKGRAY);//??????????
		renderer.setPanEnabled(false);//????????
		//renderer.setDisplayValues(true);//?????
		renderer.setClickEnabled(true);//?????????
		renderer.setMargins(new int[] { 20, 30, 15,0 });
		//margins - an array containing the margin size values, in this order: top, left, bottom, right
		
		int[] colors = new int[archCount];
		for(int i=0; i<archCount; i++) {
			int j = i%5;
			if(j == 0) {
				colors[i] = Color.BLUE;
			} else if(j == 1) {
				colors[i] = Color.GREEN;
			} else if(j == 2) {
				colors[i] = Color.RED;
			} else if(j == 3) {
				colors[i] = Color.DKGRAY;
			} else if(j == 4) {
				colors[i] = Color.MAGENTA;
			}
		
		}
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	
	private double queryToday() {
		double ret = 0;
		SQLiteDatabase db = dbService.getReadableDatabase();
		String sql = "SELECT * from t_sell_lib where date(timestamp,'localtime')=date('now','localtime')";
		Cursor cursor = db.rawQuery(sql, null);
		Log.d("yanjunke", "report: queryToday: " + cursor.getCount());
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				ret += (cursor.getDouble(cursor.getColumnIndex("price")) * cursor.getInt(cursor.getColumnIndex("quantity")));
				cursor.moveToNext();
			}
		}
		return ret;
	}
	
	private double queryThisWeek() {
		double ret = 0;
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		String sql = "SELECT * from t_sell_lib where date(timestamp, 'localtime')>=date('now','weekday 0', '-7 day','localtime')";
		Cursor cursor = db.rawQuery(sql, null);
		Log.d("yanjunke", "report: queryThisWeek: " + cursor.getCount());
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				ret += (cursor.getDouble(cursor.getColumnIndex("price")) * cursor.getInt(cursor.getColumnIndex("quantity")));
				cursor.moveToNext();
			}
		}
		return ret;
	}
	
	private double queryThisMonth() {
		double ret = 0;
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		String sql = "SELECT * from t_sell_lib where date(timestamp, 'localtime')>=date('now','start of month','localtime')";
		Cursor cursor = db.rawQuery(sql, null);
		Log.d("yanjunke", "report: queryThisMonth: " + cursor.getCount());
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				ret += (cursor.getDouble(cursor.getColumnIndex("price")) * cursor.getInt(cursor.getColumnIndex("quantity")));
				cursor.moveToNext();
			}
		}
		return ret;
	}

	private double queryThisYear() {
		double ret = 0;
		SQLiteDatabase db = dbService.getReadableDatabase();
		
		String sql = "SELECT * from t_sell_lib where date(timestamp, 'localtime')>=date('now','start of year','localtime')";
		Cursor cursor = db.rawQuery(sql, null);
		Log.d("yanjunke", "report: queryThisYear: " + cursor.getCount());
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				ret += (cursor.getDouble(cursor.getColumnIndex("price")) * cursor.getInt(cursor.getColumnIndex("quantity")));
				cursor.moveToNext();
			}
		}
		return ret;
	}
}
