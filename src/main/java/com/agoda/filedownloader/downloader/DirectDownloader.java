
package com.agoda.filedownloader.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author Biren
 * 
 */
public class DirectDownloader  implements Runnable {
	private int poolSize = 3;
	private int bufferSize = 2048;

	private DirectDownloadThread[] dts;
	private Proxy proxy;
	private final BlockingQueue<DownloadTask> tasks = new LinkedBlockingQueue<DownloadTask>();

	private static Logger logger = Logger.getLogger(DirectDownloader.class.getName());

	public DirectDownloader() {
	}

	public DirectDownloader(int poolSize) {
		this.poolSize = poolSize;
	}

	public DirectDownloader(Proxy proxy) {
		this.proxy = proxy;
	}

	public DirectDownloader(Proxy proxy, int poolSize) {
		this.poolSize = poolSize;
		this.proxy = proxy;
	}

	protected class DirectDownloadThread extends Thread {
		private static final String CD_FNAME = "fname=";
		private static final String CONTENT_DISPOSITION = "Content-Disposition";

		private boolean cancel = false;
		private boolean stop = false;

		private final BlockingQueue<DownloadTask> tasks;

		public DirectDownloadThread(BlockingQueue<DownloadTask> tasks) {
			this.tasks = tasks;
		}

		protected void download(DownloadTask dt) throws IOException, InterruptedException, KeyManagementException,
				NoSuchAlgorithmException {
			URLConnection conn = dt.getUrl().openConnection();
		   
			conn.setReadTimeout(dt.getTimeout());
			conn.setDoOutput(true);
			conn.connect();

			int fsize = conn.getContentLength();
			String fname;

			String cd = conn.getHeaderField(CONTENT_DISPOSITION);

			if (cd != null) {
				fname = cd.substring(cd.indexOf(CD_FNAME) + 1, cd.length() - 1);
			} else {
				String url = dt.getUrl().toString();
				fname = url.substring(url.lastIndexOf('/') + 1);
			}

			InputStream is = conn.getInputStream();

			OutputStream os = dt.getOutputStream();
			List<DownloadListener> listeners = dt.getListeners();

			byte[] buff = new byte[bufferSize];
			int res;

			for (DownloadListener listener : listeners) {
				listener.onStart(fname, fsize);
			}

			int total = 0;
			while ((res = is.read(buff)) != -1) {
				os.write(buff, 0, res);
				total += res;
				for (DownloadListener listener : listeners) {
					listener.onUpdate(res, total);
				}

				synchronized (dt) {
					// cancel download
					if (cancel || dt.isCancelled()) {
						close(is, os);
						for (DownloadListener listener : listeners) {
							listener.onCancel();
						}

						throw new RuntimeException("Cancelled download");
					}

					// stop thread
					if (stop) {
						close(is, os);
						for (DownloadListener listener : listeners) {
							listener.onCancel();
						}

						throw new InterruptedException("Shutdown");
					}

					// pause thread
					while (dt.isPaused()) {
						try {
							wait();
						} catch (Exception e) {
						}
					}
				}
			}

			for (DownloadListener listener : listeners) {
				listener.onComplete();
			}

			close(is, os);
		}

		private void close(InputStream is, OutputStream os) {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					download(tasks.take());
				} catch (InterruptedException e) {
					logger.info("Stopping download thread");
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void cancel() {
			cancel = true;
		}

		public void shutdown() {
			stop = true;
		}
	}

	public void download(DownloadTask dt) {
		tasks.add(dt);
	}

	public void run() {
		logger.info("Initializing downloader...");

		dts = new DirectDownloadThread[poolSize];

		for (int i = 0; i < dts.length; i++) {
			dts[i] = new DirectDownloadThread(tasks);
			dts[i].start();
		}

		logger.info("Downloader started, waiting for tasks.");
	}

	public void shutdown() {
		for (int i = 0; i < dts.length; i++) {
			if (dts[i] != null) {
				dts[i].shutdown();
			}
		}
	}

	public void cancelAll() {
		for (int i = 0; i < dts.length; i++) {
			if (dts[i] != null) {
				dts[i].cancel();
			}
		}
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
