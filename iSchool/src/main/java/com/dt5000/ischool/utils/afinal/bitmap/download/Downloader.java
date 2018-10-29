package com.dt5000.ischool.utils.afinal.bitmap.download;

public interface Downloader {

	/**
	 * ���������inputStream���outputStream
	 * 
	 * @param urlString
	 * @param outputStream
	 * @return
	 */
	public byte[] download(String urlString);
}
