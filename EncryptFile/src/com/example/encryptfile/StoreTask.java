package com.example.encryptfile;

import android.os.AsyncTask;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brycendavis on 3/20/16.
 */
public class StoreTask extends AsyncTask<String, Void, String>{
    String user, pass, server;
    int port;
    FTPClient ftpClient;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ftpClient = new FTPClient();
        this.server = "52.38.25.137";
        this.port = 21;
        this.user = "ftp_user";
        this.pass = "ftp_pass";

    }

    @Override
    protected String doInBackground(String... params) {

        String currentSourcePath = params[0];

        // Uploads a single file using an InputStream
        File firstLocalFile = new File(currentSourcePath);

        String firstRemoteFile = currentSourcePath.substring(currentSourcePath.lastIndexOf("/") + 1);
        InputStream inputStream = null;

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            inputStream = new FileInputStream(firstLocalFile);
            System.out.println("Start uploading first file");
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("The first file is uploaded successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
