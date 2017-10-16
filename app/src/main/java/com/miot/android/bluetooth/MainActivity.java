package com.miot.android.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			MiotBluetoothManager.getInstance().init(this);
			boolean isEn=MiotBluetoothManager.getInstance().isEnable();
			Log.e("MainActivity",isEn+"");
			if (!isEn){
				MiotBluetoothManager.getInstance().startBluetooth(this,1001111);
				return;
			}
			MiotBluetoothManager.getInstance().setLeScanCallback(this);
			MiotBluetoothManager.getInstance().scanBluetoothDevice(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MiotBluetoothManager.getInstance().connect("70:89:0C:F8:9D:6D");

	}

	@Override
	public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

		Log.e("BluetoothDevice",bluetoothDevice.getAddress());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==1001111){
			MiotBluetoothManager.getInstance().setLeScanCallback(this);
			try {
				MiotBluetoothManager.getInstance().scanBluetoothDevice(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
