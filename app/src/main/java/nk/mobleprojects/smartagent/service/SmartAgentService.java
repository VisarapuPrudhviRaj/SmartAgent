package nk.mobleprojects.smartagent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import nk.mobleprojects.smartagent.broadcastrecevier.DownloadBroadcastReceiver;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.DownloadFile;
import nk.mobleprojects.smartagent.utils.DownloadURLFile;
import nk.mobleprojects.smartagent.utils.Helper;

public class SmartAgentService extends Service {
    DownloadFile file;
    Context applicationContext;
    DBHelper dbHelper;


    public SmartAgentService(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SmartAgentService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
//        android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        dbHelper = new DBHelper(this);
        checkDB();
        return START_STICKY;
    }

    private void checkDB() {
        List<List<String>> ll_data = dbHelper.getTableColDataByCond(DBTables.SmartProject.TABLE_NAME, "*",
                new String[]{DBTables.SmartProject.downloadStatus}, new String[]{"0"});
        if (ll_data.size() > 0) {
            //id, name, type,  sizeInBytes, cdn_path, filePath,downloadStatus
            downloadFile(ll_data.get(0).get(2).trim(), ll_data.get(0).get(5).trim(), ll_data.get(0).get(1).trim());
        }
    }

    private void downloadFile(String fileName, String downloadUrl, String file_id) {
        File filePath = new File(Environment.getExternalStorageDirectory() +
                "/" + Helper.FOLDER_NAME + "/" + fileName);
        if (!filePath.exists()) {
            Toast.makeText(applicationContext, "File Downloading...", Toast.LENGTH_LONG).show();
            DownloadURLFile downloadApk = new DownloadURLFile(applicationContext, dbHelper, file_id);
            downloadApk.execute(downloadUrl, fileName);
        } else {
            //File Size Check
            dbHelper.updateByValues(DBTables.SmartProject.TABLE_NAME,
                    new String[]{DBTables.SmartProject.downloadStatus, DBTables.SmartProject.filePath},
                    new String[]{"1", filePath.getAbsolutePath()},
                    new String[]{DBTables.SmartProject.id},
                    new String[]{file_id});
            //update Adapter
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "ondestroy!");
        Intent broadcastIntent = new Intent(this, DownloadBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
       // checkDB();
    }
}
