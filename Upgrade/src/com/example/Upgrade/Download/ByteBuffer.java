package com.example.Upgrade.Download;

import java.io.RandomAccessFile;
import java.lang.System;

import com.example.Upgrade.UpgradeActivity;

import android.util.Log;

public class ByteBuffer {
	private static final String TAG = "ByteBuffer";
	private static final int BUFFER_SIZE = 64*1024;
	private byte[] byte_buffer;
	private int buffer_len;
	//private int thread_id;
	private RandomAccessFile raf;
	
	public ByteBuffer(RandomAccessFile raf, int thread_id) {
		this.raf = raf;
		//this.thread_id = thread_id;
		byte_buffer = new byte[BUFFER_SIZE];
		buffer_len = 0;
	}
	
	public void fillBuffer(byte[] buffer, int len) throws Exception {
		if((buffer == null) || (len < 0)) {
			throw new RuntimeException("parameters is wrong!");
		}
		if((buffer_len < 0) || (buffer_len > BUFFER_SIZE)) {
			throw new RuntimeException("buffer len is wrong!");
		}

		int temp = buffer_len + len;
		if(temp < BUFFER_SIZE) {
			System.arraycopy(buffer, 0, byte_buffer, buffer_len, len);
			buffer_len += len;
		}
		else if(temp == BUFFER_SIZE) {
			System.arraycopy(buffer, 0, byte_buffer, buffer_len, len);
			buffer_len += len;
			write();
		}
		else {
			write();
			System.arraycopy(buffer, 0, byte_buffer, buffer_len, len);
			buffer_len += len;
		}
	}
	
	public void write() throws Exception {
		if((raf != null) && (buffer_len > 0))
			raf.write(byte_buffer, 0, buffer_len);
		buffer_len = 0;
	}
	
	public int getBufferSize() {
		if(UpgradeActivity.DEBUG)
			Log.d(TAG, "resume buffer size:" + Integer.toString(buffer_len));
		return buffer_len;
	}
}
