package com.agoda.filedownloader.Downloader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

   private static DownloadManager instance;

   private static DownloadThreadPool downloadThreadPool;

   public static final int DEFAULT_FILE_THREAD_COUNT = 4;
   public static final int DEFAULT_CORE_POOL_SIZE = 10;

   public static final int DEFAULT_MAX_POOL_SIZE = Integer.MAX_VALUE;
   public static final int DEFAULT_KEEP_ALIVE_TIME = 0;

   private static int ID = 0;
   private Hashtable<Integer, DownloadMission> mMissions = new Hashtable<Integer, DownloadMission>();

   private DownloadManager() {
      downloadThreadPool = new DownloadThreadPool(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
               DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
   }

   public static DownloadManager getInstance() {
      if (instance == null) {
         instance = new DownloadManager();
      }
      if (downloadThreadPool.isShutdown()) {
         downloadThreadPool = new DownloadThreadPool(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
                  DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
      }
      return instance;
   }

   public void addMission(DownloadMission downloadTask) {
      mMissions.put(ID++, downloadTask);
   }

   public DownloadMission addMission(String url, String saveDirectory, String saveName)
            throws IOException {
      DownloadMission downloadMission = new DownloadMission(url, saveDirectory, saveName);
      addMission(downloadMission);
      return downloadMission;
   }

   public void start() {
      for (DownloadMission mission : mMissions.values()) {
         mission.startMission(downloadThreadPool);
      }
   }
}
