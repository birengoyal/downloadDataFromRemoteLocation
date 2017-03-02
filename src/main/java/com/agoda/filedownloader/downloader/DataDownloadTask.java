package com.agoda.filedownloader.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Biren
 * 
 */
public class DataDownloadTask {
   private URL url;
   private URLConnection connection;
   private OutputStream outputStream;
   private final List<DataDownloadListener> listeners = new ArrayList<DataDownloadListener>();
   private static final String CONTENT_DISPOSITION = "Content-Disposition";

   private int timeout = 15000;

   public DataDownloadTask(URL url, String targetLocation) throws IOException {

      URLConnection conn = url.openConnection();
      conn.setReadTimeout(timeout);
      conn.setDoOutput(true);
      conn.connect();
      this.connection = conn;

      this.url = url;
      setTargetFolder(targetLocation);

      String fname = "";
      String cd = conn.getHeaderField(CONTENT_DISPOSITION);

      if (cd != null && cd.indexOf("=") != -1) {
         fname = cd.split("=")[1];
      } else {
         String urlString = url.toString();
         fname = targetLocation + urlString.substring(urlString.lastIndexOf('/') + 1);
      }
      int fsize = conn.getContentLength();
      listeners.add(new DefaultDataDownloadListener(fsize, fname));
      this.outputStream = new FileOutputStream(fname);
   }

   public Boolean setTargetFolder(String targetLocation) throws IOException {

      if (targetLocation.lastIndexOf(File.separator) == targetLocation.length() - 1) {
         targetLocation = targetLocation.substring(0, targetLocation.length() - 1);
      }
      File dirFile = new File(targetLocation);
      if (dirFile.exists() == false) {
         if (dirFile.mkdirs() == false) {
            throw new RuntimeException("Error to create directory");
         }
      }
      return true;
   }

   public URLConnection getConnection() {
      return connection;
   }

   public void setConnection(URLConnection connection) {
      this.connection = connection;
   }

   public URL getUrl() {
      return url;
   }

   public DataDownloadTask setUrl(URL url) {
      this.url = url;
      return this;
   }

   public OutputStream getOutputStream() {
      return outputStream;
   }

   public DataDownloadTask setOutputStream(OutputStream outputStream) {
      this.outputStream = outputStream;
      return this;
   }

   public List<DataDownloadListener> getListeners() {
      return listeners;
   }

   public DataDownloadTask addListener(DataDownloadListener listener) {
      listeners.add(listener);
      return this;
   }

   public DataDownloadTask removeListener(DataDownloadListener listener) {
      listeners.remove(listener);
      return this;
   }

   public DataDownloadTask removeAllListener() {
      listeners.clear();
      return this;
   }

   public int getTimeout() {
      return timeout;
   }

   public DataDownloadTask setTimeout(int timeout) {
      this.timeout = timeout;
      return this;
   }

}
