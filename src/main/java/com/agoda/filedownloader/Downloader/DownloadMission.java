package com.agoda.filedownloader.Downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadMission {

   public static final int READY = 1;
   public static final int DOWNLOADING = 2;
   public static final int FINISHED = 4;

   public static int DEFAULT_THREAD_COUNT = 4;

   protected String mUrl;
   protected String mSaveDirectory;
   protected String mSaveName;
   // protected int mMissionID = MISSION_ID_COUNTER++;
   private ArrayList<DownloadRunnable> mDownloadParts = new ArrayList<DownloadRunnable>();

   private ArrayList<RecoveryRunnableInfo> mRecoveryRunnableInfos = new ArrayList<DownloadMission.RecoveryRunnableInfo>();

   private int mMissionStatus = READY;

   protected DownloadThreadPool mThreadPoolRef;
   private String mProgressDir;
   private String mProgressFileName;
   private int mFileSize;
   private int mThreadCount = DEFAULT_THREAD_COUNT;
   private boolean isFinished = false;

   public DownloadMission(String url, String saveDirectory, String saveName) throws IOException {
      this.mUrl = url;

      setTargetFile(saveDirectory, saveName);

      setProgessFile(mSaveDirectory, mSaveName);
   }

   public Boolean setTargetFile(String saveDir, String saveName) throws IOException {

      if (saveDir.lastIndexOf(File.separator) == saveDir.length() - 1) {
         saveDir = saveDir.substring(0, saveDir.length() - 1);
      }
      mSaveDirectory = saveDir;
      File dirFile = new File(saveDir);
      if (dirFile.exists() == false) {
         if (dirFile.mkdirs() == false) {
            throw new RuntimeException("Error to create directory");
         }
      }

      File file = new File(dirFile.getPath() + File.separator + saveName);
      if (file.exists() == false) {
         file.createNewFile();
      }
      mSaveName = saveName;
      return true;
   }

   static class RecoveryRunnableInfo {

      private int mStartPosition;
      private int mEndPosition;
      private int mCurrentPosition;
      private boolean isFinished = false;

      public RecoveryRunnableInfo(int start, int current, int end) {
         if (end > start && current > start) {
            mStartPosition = start;
            mEndPosition = end;
            mCurrentPosition = current;
         } else {
            throw new RuntimeException("position logical error");
         }
         if (mCurrentPosition >= mEndPosition) {
            isFinished = true;
         }
      }

      public int getStartPosition() {
         return mStartPosition;
      }

      public int getEndPosition() {
         return mEndPosition;
      }

      public int getCurrentPosition() {
         return mCurrentPosition;
      }

      public boolean isFinished() {
         return isFinished;
      }
   }

   public void startMission(DownloadThreadPool threadPool) {
      // setDownloadStatus(DOWNLOADING);

      mThreadPoolRef = threadPool;
      if (mRecoveryRunnableInfos.size() != 0) {
         for (RecoveryRunnableInfo runnableInfo : mRecoveryRunnableInfos) {
            if (runnableInfo.isFinished == false) {
               DownloadRunnable runnable = new DownloadRunnable(mUrl, mSaveDirectory, mSaveName,
                        runnableInfo.getStartPosition(), runnableInfo.getCurrentPosition(),
                        runnableInfo.getEndPosition());
               mDownloadParts.add(runnable);
               threadPool.submit(runnable);
            }
         }
      } else {
         for (DownloadRunnable runnable : splitDownload(mThreadCount)) {
            mDownloadParts.add(runnable);
            threadPool.submit(runnable);
         }
      }
   }

   private ArrayList<DownloadRunnable> splitDownload(int thread_count) {
      ArrayList<DownloadRunnable> runnables = new ArrayList<DownloadRunnable>();
      try {
         int size = getContentLength(mUrl);
         mFileSize = size;
         int sublen = size / thread_count;
         for (int i = 0; i < thread_count; i++) {
            int startPos = sublen * i;
            int endPos = (i == thread_count - 1) ? size : (sublen * (i + 1) - 1);
            DownloadRunnable runnable = new DownloadRunnable(mUrl, mSaveDirectory, mSaveName,
                     startPos, endPos);
            runnables.add(runnable);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return runnables;
   }

   private int getContentLength(String fileUrl) throws IOException {
      URL url = new URL(fileUrl);
      URLConnection connection = url.openConnection();
      return connection.getContentLength();
   }

   private Boolean setProgessFile(String dir, String filename) throws IOException {
      if (dir.lastIndexOf(File.separator) == dir.length() - 1) {
         dir = dir.substring(0, dir.length() - 1);
      }
      File dirFile = new File(dir);
      if (dirFile.exists() == false) {
         if (dirFile.mkdirs() == false) {
            throw new RuntimeException("Error to create directory");
         }
      }
      mProgressDir = dirFile.getPath();
      File file = new File(dirFile.getPath() + File.separator + filename + ".tmp");
      if (file.exists() == false) {
         file.createNewFile();
      }
      mProgressFileName = file.getName();
      return true;
   }

   public File getProgressFile() {
      return new File(mProgressDir + File.separator + mProgressFileName);
   }

   public File getDownloadFile() {
      return new File(mSaveDirectory + File.separator + mSaveName);
   }

   public String getProgressDir() {
      return mProgressDir;
   }

   public String getProgressFileName() {
      return mProgressFileName;
   }

   private void deleteProgressFile() {
      getProgressFile().delete();
   }

   public ArrayList<RecoveryRunnableInfo> getDownloadProgress() {
      return mRecoveryRunnableInfos;
   }

}
