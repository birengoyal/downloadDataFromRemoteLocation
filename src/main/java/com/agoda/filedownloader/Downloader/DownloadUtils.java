package com.agoda.filedownloader.Downloader;

import java.text.DecimalFormat;

public class DownloadUtils {

   public static String getReadableSize(long bytes) {
      if (bytes <= 0)
         return "0";
      final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
      int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
      return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " "
               + units[digitGroups];
   }

   public static String getReadableSpeed(long speed) {
      return getReadableSize(speed) + "/S";
   }
}
