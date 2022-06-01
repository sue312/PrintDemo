package com.nbbse.printerdemo;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class LottoInputActivity extends Activity {

	private GridView gridNumber = null;
	private TextView textNumSet = null;
	private EditText editAmount = null;
	private Button buttonNext = null;
	private Button buttonCancel = null;
	private Button buttonReset = null;
	private MyViewAdapter myAdapter;
	
	private int[] cellstate; 
	private int mSelCount = 0;
	
	private String strNumberList;
	private String strTransNum;
	private String strAmount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lottory_input);
		initData();
		initViews();
	}

	private void initViews() {
		textNumSet = (TextView) findViewById(R.id.text_numset);
		
		
		gridNumber = (GridView) findViewById(R.id.grid_number);
		gridNumber.setAdapter(myAdapter);
		gridNumber.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(cellstate[position] == 0) {
					if(mSelCount < 5) {
						cellstate[position] = 1;
						mSelCount ++;
					}
				} else {
					cellstate[position] = 0;
					mSelCount --;
				}
				textNumSet.setText(getSelectedNumber());
				myAdapter.notifyDataSetChanged();
			}
		});
		
		editAmount = (EditText) findViewById(R.id.edit_amount);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		buttonReset = (Button) findViewById(R.id.button_reset);
		buttonReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				for(int i=0; i<49; i++) {
					cellstate[i] = 0;
				}
				mSelCount = 0;
				textNumSet.setText(getSelectedNumber());
				myAdapter.notifyDataSetChanged();
			}
		});
		buttonNext = (Button) findViewById(R.id.button_next);
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onValidate();
			}
		});
	}
	
	private void onValidate() {

		if(checkInputOK() == true) {
			strAmount = editAmount.getText().toString();
			if(TextUtils.isEmpty(strAmount)) {
				Toast.makeText(LottoInputActivity.this, "Amount field not right.", Toast.LENGTH_SHORT).show();
				return;
			}
			double fAmount = Double.parseDouble(strAmount);
			if(fAmount <= 0) {
				Toast.makeText(LottoInputActivity.this, "Amount field not right.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(mSelCount != 5) {
				Toast.makeText(LottoInputActivity.this, "Need to choose 5 numbers.", Toast.LENGTH_SHORT).show();
				return;
			}
			strTransNum = genTransNum();
			Intent intent = new Intent(LottoInputActivity.this, LottoPreviewActivity.class);
			intent.putExtra("numberlist", strNumberList);
			intent.putExtra("transnum", strTransNum);
			intent.putExtra("amount", strAmount);
			startActivityForResult(intent, 0);
		}
	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_CALL) {
			onValidate();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initData() {
		cellstate = new int[49];
		myAdapter = new MyViewAdapter(this);
	}
	
	private String getSelectedNumber() {
		String s = "You choosed:";
		String number = "";
		
		for(int i=0; i<cellstate.length; i++) {
			if(cellstate[i] == 1) {
				number += String.valueOf(i+1) + " ";
			}
		}
		strNumberList = number;
		return s + number;
	}
	
	private String genTransNum() {
		String s = "";
		Random random = new Random();
		for(int i=0; i<13; i++) {
			char c = (char)(48 + random.nextInt(10));
			s += c;
		}
		return s;
	}
	
	private boolean checkInputOK() {
		return true;
	}
	
	private class MyViewAdapter extends BaseAdapter {

		private Context mContext;
		
		public MyViewAdapter(Context context) {
			this.mContext = context;
		}
		@Override
		public int getCount() {
			return 49;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			TextView tv;
			if(convertView == null) {
				tv = new TextView(mContext);
				tv.setLayoutParams(new GridView.LayoutParams(42, 38));
				tv.setGravity(Gravity.CENTER);
				tv.setBackgroundColor(0xFF00FF00);
			} else {
				tv = (TextView) convertView;
			}
			if(cellstate[position] == 1) {
				tv.setBackgroundColor(0xFFFF0000);
			} else {
				tv.setBackgroundColor(0xFF00FF00);
			}
			tv.setText(String.valueOf(position+1));
			return tv;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
	}
	
}
