package com.agoda.filedownloader.downloader;

/**
 * @author Biren
 * 
 */
public interface DataDownloadListener {
	public void onStart();

	public void onUpdate(int bytes, int totalDownloaded);

	public void onComplete();

	public String getFileName();
	
	public int getFileSize();
}
