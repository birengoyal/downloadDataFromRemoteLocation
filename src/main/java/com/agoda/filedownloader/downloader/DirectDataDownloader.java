package com.agoda.filedownloader.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author Biren
 * 
 */
public class DirectDataDownloader implements Runnable {
   private int poolSize = 3;
   private int bufferSize = 2048;

   private DirectDownloadThread[] dts;
   private final BlockingQueue<DataDownloadTask> tasks = new LinkedBlockingQueue<DataDownloadTask>();

   private static Logger logger = Logger.getLogger(DirectDataDownloader.class.getName());

   public DirectDataDownloader() {
   }

   public DirectDataDownloader(int poolSize) {
      this.poolSize = poolSize;
   }
   // Download the file in chunk using multithreading 
   protected class DirectDownloadThread extends Thread {
      private final BlockingQueue<DataDownloadTask> tasks;

      public DirectDownloadThread(BlockingQueue<DataDownloadTask> tasks) {
         this.tasks = tasks;
      }

      protected void download(DataDownloadTask dt) {
         try {
            InputStream is = dt.getConnection().getInputStream();

            OutputStream os = dt.getOutputStream();
            List<DataDownloadListener> listeners = dt.getListeners();

            byte[] buff = new byte[bufferSize];
            int res;

            for (DataDownloadListener listener : listeners) {
               listener.onStart();
            }

            int total = 0;
            while ((res = is.read(buff)) != -1) {
               os.write(buff, 0, res);
               total += res;
               for (DataDownloadListener listener : listeners) {
                  listener.onUpdate(res, total);
               }
            }

            for (DataDownloadListener listener : listeners) {
               listener.onComplete();
            }
            close(is, os);
            //delete the file if any exception found 
         } catch (Exception e) {
            new File(dt.getListeners().get(0).getFileName()).delete();
            e.printStackTrace();
         }
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
         while (!tasks.isEmpty()) {
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

   }

   public void download(DataDownloadTask dt) {
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

}
