package com.miot.android.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.miot.android.bluetooth.service.BleSppGattAttributes;

import java.util.UUID;

/**
 * Created by Administrator on 2017/10/16 0016.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MiotBluetoothManager extends BluetoothGattCallback {

	public static String TAG=MiotBluetoothManager.class.getName();

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static UUID UUID_BLE_SPP_NOTIFY = UUID.fromString(BleSppGattAttributes.BLE_SPP_Notify_Characteristic);

	private static MiotBluetoothManager instance=null;

	public static synchronized MiotBluetoothManager getInstance() {
		if (instance==null){
			synchronized (MiotBluetoothManager.class){
				if (instance==null){
					instance=new MiotBluetoothManager();
				}
			}
		}
		return instance;
	}

	private int mConnectionState=0;

	private BluetoothGattCharacteristic mNotifyCharacteristic=null;
	private BluetoothGattCharacteristic mWriteCharacteristic=null;

	private BluetoothAdapter.LeScanCallback leScanCallback=null;

	public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
		this.leScanCallback = leScanCallback;
	}

	private Context context=null;

	private BluetoothManager bluetoothManager =null;

	private BluetoothAdapter mBluetoothAdapter=null;

	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public MiotBluetoothManager(){

	}

	public void init(Context context)throws Exception{
		this.context=context;
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager==null){
			throw  new Exception("init error");
		}
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	/**
	 * 判断蓝牙是否打开
	 * @return
	 */
	public boolean isEnable(){
		if (mBluetoothAdapter==null){
			return false;
		}
		return mBluetoothAdapter.enable();
	}

	/**
	 * 蓝牙开启的权限监听
	 * @param activity
	 * @param requestCode
	 */
	public void startBluetooth(Activity activity,int requestCode){
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, requestCode);
	}

	/**
	 * 扫描蓝牙设备
	 * @param enable
	 * @throws Exception
	 */
	public void scanBluetoothDevice(boolean enable) throws Exception{

		if (!mBluetoothAdapter.enable()){
			throw new Exception("bluetooth permission is not open");
		}
		if (mBluetoothAdapter==null){
			throw new Exception("mBluetoothAdapter is null");
		}
		if (enable) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(leScanCallback);
				}
			}, 10000);
			mBluetoothAdapter.startLeScan(leScanCallback);
			return;
		}
		  mBluetoothAdapter.stopLeScan(leScanCallback);
	}



	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mConnectionState = STATE_CONNECTED;

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mConnectionState = STATE_DISCONNECTED;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// 默认先使用 B-0006/TL8266 服务发现
				BluetoothGattService service = gatt.getService(UUID.fromString(BleSppGattAttributes.BLE_SPP_Service));
				if (service==null)
				{
					Log.v("log","service is null");
					return;
				}
				if (service!=null)
				{
					//找到服务，继续查找特征值
					mNotifyCharacteristic = service.getCharacteristic(UUID.fromString(BleSppGattAttributes.BLE_SPP_Notify_Characteristic));
					mWriteCharacteristic  = service.getCharacteristic(UUID.fromString(BleSppGattAttributes.BLE_SPP_Write_Characteristic));
				}

				if (mNotifyCharacteristic!=null)
				{
					//使能Notify
					setCharacteristicNotification(mNotifyCharacteristic,true);
				}

				if(mWriteCharacteristic==null) //适配没有FEE2的B-0002/04
				{
					mWriteCharacteristic  = service.getCharacteristic(UUID.fromString(BleSppGattAttributes.BLE_SPP_Notify_Characteristic));
				}
			}
		}


		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic,
										 int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {

			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
											BluetoothGattCharacteristic characteristic) {

		}

		//Will call this when write successful
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {

			}
		}
	};


	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
											  boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// This is specific to BLE SPP Notify.
		if (UUID_BLE_SPP_NOTIFY.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
					UUID.fromString(BleSppGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}



	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device.  Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}


	public void writeData(byte[] data) {
		if ( mWriteCharacteristic != null &&
				data != null) {
			mWriteCharacteristic.setValue(data);
			//mBluetoothLeService.writeC
			mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
		}
	}


	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

}
