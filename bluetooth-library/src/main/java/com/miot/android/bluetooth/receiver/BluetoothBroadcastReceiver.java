package com.miot.android.bluetooth.receiver;

import android.content.Context;
import android.content.Intent;

import com.miot.android.bluetooth.service.BluetoothLeService;

/**
 * Created by Administrator on 2017/10/16 0016.
 */
public class BluetoothBroadcastReceiver extends android.content.BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

		} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//			mConnected = false;
//			updateConnectionState(R.string.disconnected);
//			invalidateOptionsMenu();
//			mBluetoothLeService.connect(mDeviceAddress);
		} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
			//特征值找到才代表连接成功
//			mConnected = true;
//			invalidateOptionsMenu();
//			updateConnectionState(R.string.connected);
		}else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
//			mBluetoothLeService.connect(mDeviceAddress);
		}else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                final StringBuilder stringBuilder = new StringBuilder();
//                 for(byte byteChar : data)
//                      stringBuilder.append(String.format("%02X ", byteChar));
//                Log.v("log",stringBuilder.toString());
//			displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
		}else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
//			mSendBytes.setText(sendBytes + " ");
//			if (sendDataLen>0)
//			{
//				Log.v("log","Write OK,Send again");
//				onSendBtnClicked();
//			}
//			else {
//				Log.v("log","Write Finish");
//			}
		}
	}
}
