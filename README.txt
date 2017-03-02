Overview

This downloader is a simple java library, which downloads multiple files from different protocols and shows the progress of each download.

It support almost all protocols inc http, ftp, https, sftp etc.
This library is multi threaded which can download multiple files in parallel. In input only URL and TARGET LOCATION(where file need to be stored) is required. It will fetch the file name from ULR and will download it.  

It includes Code with Test

Below is the example of code for test

<code>


 // Create a DirectDownloader instance
DirectDataDownloader directDataDownloader = new DirectDataDownloader();

//provide the URL of download data 
 String file = "http://dldir1.qq.com/qqfile/qq/QQ2013/QQ2013Beta2.exe"

//Set the target location where file need to be stored
      final String target = "...SET TARGET LOCATION...";


 // Add files to be downloaded

      DataDownloadTask dataDownloadTask = new DataDownloadTask(new URL(file), target);
      directDataDownloader.download(dataDownloadTask);

   }

 } ) );

 // Start downloading
 Thread thread = new Thread( dd );
 thread.start();
 thread.join();
</code>
 

