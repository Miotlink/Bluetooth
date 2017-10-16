package com.miot.android.bluetooth.callback;

/**
 * Created by Administrator on 2017/10/16 0016.
 */
public interface BluetoolthIReceiver {
	/**
	 *
	 * @param errorCode 1 连接成功 -1，连接失败，2，连接断开，
	 * @param errorMessage
	 * @param data
	 */
	public void connection(int errorCode,String errorMessage,String data);

	public void onReceiver(byte[] receiverData,int len);
}
