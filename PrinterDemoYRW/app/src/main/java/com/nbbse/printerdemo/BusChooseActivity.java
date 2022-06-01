package com.nbbse.printerdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * Created by yrw on 10/14/19.
 */

public class BusChooseActivity extends Activity {
    private String[] busdata = new String[] {
            "Cape Town  -  Port Elizabeth",
            "Paris  -  Lyon",
            "Mwanza  -  Kigali",
            "Nairobi  -  Mombasa"};
    private String payment[] = new String[]{"a. Cash Payment", "b. MOBIBANK"};

    private Spinner spinnerBus = null;
    private TextView textDuration = null;
    private TextView textPrice = null;
    private Button buttonNext = null;

    private String departure;
    private String arrival;
    private String duration;
    private String price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_choose);

        initViews();
    }

    private void initViews() {
        spinnerBus = (Spinner) findViewById(R.id.spinner_bus);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, busdata);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBus.setAdapter(adapter);
        spinnerBus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                setDurationAndPriceText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        textDuration = (TextView) findViewById(R.id.text_duration);
        textPrice = (TextView) findViewById(R.id.text_Price);

        setDurationAndPriceText();

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				showPaymentDialog();
                Intent intent = new Intent(BusChooseActivity.this, BusTicketPaymentChooseActivity.class);
                intent.putExtra("Departure", departure);
                intent.putExtra("Arrival", arrival);
                intent.putExtra("Duration", duration);
                intent.putExtra("Price", price);

                startActivityForResult(intent, 0);
            }
        });
    }

    private void setDurationAndPriceText() {

        int curSel = spinnerBus.getSelectedItemPosition();
        if (curSel == 0) {
            duration = "12 hours";
            price = "419 ZAR";
            departure = "Cape Town";
            arrival = "Port Elizabeth";
        } else if (curSel == 1) {
            duration = "6 hours 30 minutes";
            price = "25 €";
            departure = "Paris";
            arrival = "Lyon";
        } else if (curSel == 2) {
            duration = "3 hours";
            price = "2400 RWF";
            departure = "Mwanza";
            arrival = "Kigali";
        } else if (curSel == 3) {
            duration = "10 hours 15 minutes";
            price = "1700 KSHS";
            departure = "Nairobi";
            arrival = "Mombasa";
        }

        textDuration.setText("Duration: " + duration);
        textPrice.setText("Price: " + price);
    }

    void showPaymentDialog() {
        new AlertDialog.Builder(this).setTitle("Choose Payment: ").setItems(payment, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int which) {
                if(which == 0) {
                    Intent intent = new Intent(BusChooseActivity.this, BusTicketPreviewActivity.class);
                    intent.putExtra("Departure", departure);
                    intent.putExtra("Arrival", arrival);
                    intent.putExtra("Duration", duration);
                    intent.putExtra("Price", price);

                    startActivity(intent);
                } else if (which == 1) {

                }

            }

        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }

}