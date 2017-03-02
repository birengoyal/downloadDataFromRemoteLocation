
package com.agoda.filedownloader.downloader;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Biren
 * 
 */
public class DownloadTask {
	private URL url;
	private OutputStream outputStream;
	private final List<DownloadListener> listeners = new ArrayList<DownloadListener>();

	private boolean paused = false;
	private boolean cancelled = false;
	private int timeout = 15000;

	public DownloadTask(URL url, OutputStream outputStream) {
		this.url = url;
		this.outputStream = outputStream;
	}

	public DownloadTask(URL url, OutputStream outputStream, DownloadListener listener) {
		this.url = url;
		this.outputStream = outputStream;
		listeners.add(listener);
	}

	public URL getUrl() {
		return url;
	}

	public DownloadTask setUrl(URL url) {
		this.url = url;
		return this;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public DownloadTask setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		return this;
	}

	public List<DownloadListener> getListeners() {
		return listeners;
	}

	public DownloadTask addListener(DownloadListener listener) {
		listeners.add(listener);
		return this;
	}

	public DownloadTask removeListener(DownloadListener listener) {
		listeners.remove(listener);
		return this;
	}

	public DownloadTask removeAllListener() {
		listeners.clear();
		return this;
	}

	public boolean isPaused() {
		return paused;
	}

	public DownloadTask setPaused(boolean paused) {
		this.paused = paused;
		return this;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public DownloadTask setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public DownloadTask setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}
	
}
