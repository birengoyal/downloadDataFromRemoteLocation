package com.agoda.filedownloaderTests;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.agoda.filedownloader.downloader.DataDownloadTask;
import com.agoda.filedownloader.downloader.DirectDataDownloader;

/**
 * @author Biren
 * 
 */
@RunWith(JUnit4.class)
public class SimpleTest {
   DirectDataDownloader directDataDownloader = new DirectDataDownloader();

   @Test
   public void testSimple() throws InterruptedException, IOException {
      final Thread thread = new Thread(directDataDownloader);
      final String file = "http://dldir1.qq.com/qqfile/qq/QQ2013/QQ2013Beta2.exe";
     
      final String target = "...SET TARGET LOCATION...";
      DataDownloadTask dataDownloadTask = new DataDownloadTask(new URL(file), target);

      directDataDownloader.download(dataDownloadTask);
      thread.start();
      thread.join();
   }

   public static void main(String[] args) throws InterruptedException, IOException {
      SimpleTest st = new SimpleTest();
      st.testSimple();
   }
}
