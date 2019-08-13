package nk.mobleprojects.smartagent.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadURLFile extends AsyncTask<String, Void, String> {

    public ProgressDialog pd;
    Context context;
    DBHelper dbHelper;
    String file_id;


    public DownloadURLFile(Context context, DBHelper dbHelper, String file_id) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.file_id = file_id;

    }


    private String downloadfile(String urlLink, String fileName) {
        String status = "";
        try {
            URL url = new URL(urlLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(false);
            urlConnection.connect();

            File sdcard = new File(Environment.getExternalStorageDirectory() + "/" + Helper.FOLDER_NAME);
            if (!sdcard.exists()) {
                sdcard.mkdir();
            }
            File file = new File(sdcard, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            inputStream.close();
            //this.checkUnknownSourceEnability();
            //this.initiateInstallation();

            status = "done," + file.getAbsolutePath();
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return status;
    }

    @Override
    protected void onPreExecute() {
//        showProgressDialog("Please Wait...File Downloading", context);
    }

    @Override
    protected String doInBackground(String... arg0) {
        return downloadfile(arg0[0], arg0[1]);
    }

    protected void onPostExecute(String result) {
//        closeProgressDialog();
        if (result.startsWith("done")) {
            Toast.makeText(context, "file downloaded!", Toast.LENGTH_SHORT).show();
            //View
            //DB Update
            dbHelper.updateByValues(DBTables.SmartProject.TABLE_NAME,
                    new String[]{DBTables.SmartProject.downloadStatus, DBTables.SmartProject.filePath},
                    new String[]{"1", result.split("\\,")[1].trim()},
                    new String[]{DBTables.SmartProject.id},
                    new String[]{file_id});
            //update Adapter
            Intent pushNotification = new Intent(Helper.FILEDOWLOADED);
            pushNotification.putExtra("Downloaded", "DONE");
            LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
        } else {
            //Retry
            //update Adapter
            Intent pushNotification = new Intent(Helper.FILEDOWLOADED);
            pushNotification.putExtra("DownloadedFailed", "RETRY");
            LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
        }
    }

    public void showProgressDialog(String msg, Context context) {
        try {
            pd = new ProgressDialog(context);
            // pd = CustomProgressDialog.ctor(this, msg);
            // pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            pd.setMessage(msg);
            pd.setCancelable(true);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeProgressDialog() {
        try {
            if (pd != null)
                pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

