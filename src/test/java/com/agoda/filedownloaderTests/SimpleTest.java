package com.agoda.filedownloaderTests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.agoda.filedownloader.downloader.DirectDownloader;
import com.agoda.filedownloader.downloader.DownloadListener;
import com.agoda.filedownloader.downloader.DownloadTask;

/**
 * @author Biren
 * 
 */
@RunWith(JUnit4.class)
public class SimpleTest {
	DirectDownloader dd = new DirectDownloader();

	@Test
	public void testSimple() throws MalformedURLException, FileNotFoundException, InterruptedException {
		final Thread t = new Thread(dd);
		final String file = "ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt";
		final String f = "/home/local/JASPERINDIA/biren.goyal/Music/" + file.substring(file.lastIndexOf('/') + 1);

		DownloadTask dt = new DownloadTask(new URL(file), new FileOutputStream(f)).addListener(new DownloadListener() {
			int size;

			public void onUpdate(int bytes, int totalDownloaded) {
				updateProgress((double) totalDownloaded / size);
			}

			public void onStart(String fname, int size) {
				System.out.println("Downloading " + fname + " of size " + size);
				this.size = size;
				updateProgress(0);
			}

			public void onComplete() {
				System.out.println("\n" + f + " downloaded");
			}

			public void onCancel() {

			}
		});

		dd.download(dt);
		t.start();
		t.join();
	}

	void updateProgress(double progressPercentage) {
		final int width = 50;

		System.out.print("\r[");
		int i = 0;
		for (; i <= (int) (progressPercentage * width); i++) {
			System.out.print(".");
		}
		for (; i < width; i++) {
			System.out.print(" ");
		}
		System.out.print("]");
	}

	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, InterruptedException {
		SimpleTest st = new SimpleTest();
		st.testSimple();
	}
}
