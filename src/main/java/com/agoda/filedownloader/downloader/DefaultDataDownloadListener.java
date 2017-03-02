package com.agoda.filedownloader.downloader;

import java.io.File;

public class DefaultDataDownloadListener implements DataDownloadListener {

   public DefaultDataDownloadListener(int size, String fName) {
      super();
      this.size = size;
      this.fName = fName;
   }

   int size;
   String fName;

   public void onUpdate(int bytes, int totalDownloaded) {
      updateProgress((double) totalDownloaded / size);
   }

   public void onStart() {
      System.out.println("Downloading " + new File(fName).getName() + " of size " + size);
      updateProgress(0);
   }

   public void onComplete() {
      System.out.println("\n" + new File(fName).getName() + " downloaded");
   }

   
   // progress bar that shows the progress of the downloading file 
   // "\r" Does not work in eclipse IDE so shows inappropriate progress bar  
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

   public String getFileName() {
      return fName;
   }

   public int getFileSize() {
      return size;
   }
}
