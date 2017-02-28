package com.agoda.filedownloader.service;

import java.io.File;

import com.agoda.filedownloader.constants.URIConstants;

public class FolderLocation {

   public static String downloadLocation(String protocol) {
      String saveDir = URIConstants.FIXED_FOLDER ;
      File directory = new File(saveDir + String.valueOf(protocol));
      if (!directory.exists()) {
         directory.mkdir();
      }
      saveDir = saveDir + protocol;

      return saveDir;

   }

}
