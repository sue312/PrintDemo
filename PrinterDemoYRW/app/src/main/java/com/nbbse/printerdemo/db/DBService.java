package com.nbbse.printerdemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper {
	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "printerdb.db";
	
	public DBService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_t_account = "CREATE TABLE t_account (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "account VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
				+ "name VARCHAR(30) NOT NULL ON CONFLICT FAIL,"
				+ "phonenumber VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
				+ "amount DOUBLE,"
				+ "password VARCHAR(20) NOT NULL ON CONFLICT FAIL)";
		
		db.execSQL(sql_t_account);
		
		String sql_t_money = "create table t_money (_id integer primary key autoincrement,"
				+ "transnum varchar(10) not null on conflict fail,"
				+ "name varchar(30) not null on conflict fail,"
				+ "phonenumber varchar(20) not null on conflict fail,"
				+ "location varchar(50) not null on conflict fail,"
				+ "birthday varchar(20) not null on conflict fail,"
				+ "amount double, checkout int)";
		db.execSQL(sql_t_money);
		
		String sql_t_item_lib = "create table t_item_lib (_id integer primary key autoincrement,"
				+ "name varchar(10) not null on conflict fail,"
				+ "barcode varchar(15) not null on conflict fail,"
				+ "price double,"
				+ "quantity int)";
		db.execSQL(sql_t_item_lib);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void insert(String sql) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL(sql);
	}
	
	public void insert(String sql, Object[] bindArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL(sql, bindArgs);
	}
	
	public Cursor query(String sql, String[] args) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, args);

		return cursor;
	}
}
