package com.example.ordibehesht;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bt;
    TextView textStatus;
    Menu menu;

    private NumberPicker fan_picker;
    private NumberPicker temperature_picker;
    private String[] pickerVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        fan_picker = findViewById(R.id.fan);
        fan_picker.setMaxValue(9);
        fan_picker.setMinValue(0);
        pickerVals  = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        fan_picker.setDisplayedValues(pickerVals);

        fan_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = fan_picker.getValue();
                Log.d("picker value", pickerVals[valuePicker1]);
            }
        });

        temperature_picker = findViewById(R.id.temperature);
        temperature_picker.setMaxValue(9);
        temperature_picker.setMinValue(0);
        pickerVals  = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        temperature_picker.setDisplayedValues(pickerVals);

        temperature_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = temperature_picker.getValue();
                Log.d("picker value", pickerVals[valuePicker1]);
            }
        });

        textStatus = (TextView) findViewById(R.id.textStatus);

        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable()){
            Toast.makeText(this, "Bluetooth is not Available", Toast.LENGTH_SHORT).show();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Status: Connected to " + name);


            }

            public void onDeviceDisconnected() {
                textStatus.setText("Status: Not Connected");
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);

            }

            public void onDeviceConnectionFailed() {
                textStatus.setText("Status: Connection Failed");

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.menu_android_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        } else if(id == R.id.menu_disconnect) {
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);

        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setup(){

        final TextView Left=(TextView) findViewById(R.id.left);
        final TextView Right=(TextView) findViewById(R.id.right);



        Left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    bt.send("1", true);

                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    bt.send("-1", true);
                }

                return true;
            }


        });

        Right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    bt.send("2", true);


                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    bt.send("-2", true);


                }

                return true;
            }
        });

    }
}