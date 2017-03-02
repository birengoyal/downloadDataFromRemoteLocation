package com.agoda.filedownloader.downloader;

/**
 * @author Biren
 * 
 */
public interface DownloadListener {
	public void onStart(String fname, int fsize);

	public void onUpdate(int bytes, int totalDownloaded);

	public void onComplete();

	public void onCancel();
}
