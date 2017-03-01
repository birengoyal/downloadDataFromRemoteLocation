package com.agoda.filedownloader.Downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class DownloadRunnable implements Runnable {

   private static final int BUFFER_SIZE = 4096;

   private static int counter = 0;
   private String fileUrl;
   private String saveDirectory;
   private String saveFileName;
   private int start;
   private int end;
   // public final int MISSION_ID;
   public final int ID = counter++;
   private int current;

   public void run() {
      File targetFile;
      synchronized (this) {
         File dir = new File(saveDirectory + File.pathSeparator);
         if (dir.exists() == false) {
            dir.mkdirs();
         }
         targetFile = new File(saveDirectory + File.separator + saveFileName);
         if (targetFile.exists() == false) {
            try {
               targetFile.createNewFile();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

      System.out.println("Download Task   ID:" + Thread.currentThread().getId()
               + " has been started! Range From " + current + " To " + end);
      BufferedInputStream bufferedInputStream = null;
      RandomAccessFile randomAccessFile = null;
      byte[] buf = new byte[BUFFER_SIZE];
      URLConnection urlConnection = null;
      try {
         URL url = new URL(fileUrl);
         urlConnection = url.openConnection();
         urlConnection.setRequestProperty("Range", "bytes=" + current + "-" + end);
         randomAccessFile = new RandomAccessFile(targetFile, "rw");
         randomAccessFile.seek(current);
         bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
         while (current < end) {
            if (Thread.currentThread().isInterrupted()) {
               System.out.println("Download TaskID:" + Thread.currentThread().getId()
                        + " was interrupted, Start:" + start + " Current:" + current + " End:"
                        + end);
               break;
            }
            int len = bufferedInputStream.read(buf, 0, BUFFER_SIZE);
            if (len == -1)
               break;
            else {
               randomAccessFile.write(buf, 0, len);
               current += len;
               // mDownloadMonitor.down(len);
            }
         }
         bufferedInputStream.close();
         randomAccessFile.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public DownloadRunnable(String fileUrl, String saveDirectory, String saveFileName,
            int startPosition, int endPosition) {
      super();
      this.fileUrl = fileUrl;
      this.saveDirectory = saveDirectory;
      this.saveFileName = saveFileName;
      start = startPosition;
      end = endPosition;
      this.current = this.current;
   }

   public DownloadRunnable(String fileUrl, String saveDirectory, String saveFileName,
            int startPosition, int current, int endPosition) {
      this(fileUrl, saveDirectory, saveFileName, startPosition, endPosition);
      this.current = current;
   }

}
