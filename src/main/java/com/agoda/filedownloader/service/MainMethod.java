package com.agoda.filedownloader.service;

import java.io.IOException;

public class MainMethod {
   public static void main(String[] args) {
      String fileURL = "http://www.stephaniequinn.com/Music/Allegro%20from%20Duet%20in%20C%20Major.mp3";
      try {
          FileDownloaderService.downloadFile(fileURL);
      } catch (IOException ex) {
          ex.printStackTrace();
      }
  }
}

