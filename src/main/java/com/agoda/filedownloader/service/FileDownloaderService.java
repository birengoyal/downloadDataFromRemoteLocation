package com.agoda.filedownloader.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloaderService {

   private static final int BUFFER_SIZE = 4096;

   public static void downloadFile(String fileURL) throws IOException {

      
      URL url = new URL(fileURL);
      String protocol = url.getProtocol();
      String saveDir = FolderLocation.downloadLocation(protocol);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      int responseCode = httpConn.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {

         String fileName = "";
         String disposition = httpConn.getHeaderField("Content-Disposition");
         String contentType = httpConn.getContentType();
         int contentLength = httpConn.getContentLength();

         if (disposition != null) {
            // extracts file name from header field
            int index = disposition.indexOf("filename=");
            if (index > 0) {
               fileName = disposition.substring(index + 10, disposition.length() - 1);
            }
         } else {
            // extracts file name from URL
            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
         }
         
         System.out.println(" Downloading " + fileName  
                  + " which has size of " + (contentLength / 1000) + "KB");  
         
         // for files with same names with different protocol will be downloaded in different folders   
       
         InputStream inputStream = httpConn.getInputStream();
         String savedLocation = saveDir + File.separator + fileName;

         FileOutputStream outputStream = new FileOutputStream( savedLocation);

         long total = 0;
         int bytesRead = -1;
         byte[] buffer = new byte[BUFFER_SIZE];
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            total += bytesRead;
            // publishing the progress....
            // After this onProgressUpdate will be called
            publishProgress(""
                    + (int) ((total * 100) / contentLength));
            outputStream.write(buffer, 0, bytesRead);
         }

         outputStream.close();
         inputStream.close();
         
      } else {
         throw new IOException("Http connection is not been established, response is "
                  + responseCode);
      }
      httpConn.disconnect();
   }
// need to correct 
   private static void publishProgress(String value) {
      String result = null;
      if(result!=value)
         result = value ;
         System.out.println(value);
   }
}
