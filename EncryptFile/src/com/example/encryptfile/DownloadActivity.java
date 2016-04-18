package com.example.encryptfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by brycendavis on 4/17/16.
 */
public class DownloadActivity extends Activity {
    private ListView lstView;
    private FilesAdapter mAdapter;
    public static FTPFile[] result;
    private List<FileItem> lstFiles = new ArrayList<FileItem>();
    private Context mContext;
    private ListFTPTask listFTPTask = new ListFTPTask();
    private TextView tvFileList;
    private String allFTPFiles = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        lstView = (ListView) findViewById(R.id.listViewFile);
        tvFileList = (TextView) findViewById(R.id.tvFileList);
        try {
            result = listFTPTask.execute().get();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        mContext = this;

        for(FTPFile file: result){
            System.out.println(file.getName());
            allFTPFiles += file.getName() + "\n";
        }

        tvFileList.setText(allFTPFiles);
    }

    public void btnDownloadAllClick(View view) {
        new DownloadTask().execute();

        Toast.makeText(this, "All Files Have Been Downloaded to '/storage/sdcard/Download'!", Toast.LENGTH_LONG).show();

        // Send Back to Main Activity
        Intent mainIntent = new Intent(DownloadActivity.this,
                MainActivity.class);

        DownloadActivity.this.startActivity(mainIntent);
    }
}
class ListFTPTask extends AsyncTask<String, Void, FTPFile[]>{
    private String user = "ftp_user";
    private String pass = "ftp_pass";
    private String server = "52.38.25.137";
    private int port = 21;
    FTPClient ftpClient = new FTPClient();

    @Override
    protected FTPFile[] doInBackground(String... params) {

        try{
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);

            return ftpClient.listFiles("/");

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }
}


class DownloadTask extends AsyncTask<String, Void, String>{
    private String user = "ftp_user";
    private String pass = "ftp_pass";
    private String server = "52.38.25.137";
    private int port = 21;
    FTPClient ftpClient = new FTPClient();

    @Override
    protected String doInBackground(String... result) {

        try{
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);

            OutputStream outputStream = null;
            try {
                for(FTPFile file: DownloadActivity.result){
                    outputStream = new BufferedOutputStream(new FileOutputStream(
                            "/storage/sdcard/Download/" + file.getName()));
                    ftpClient.retrieveFile(file.getName(), outputStream);
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }
}
