package nk.mobleprojects.smartagent.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.DownloadURLFile;
import nk.mobleprojects.smartagent.utils.Helper;

public class SmartAgentService extends Service {

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
        dbHelper = new DBHelper(this);
        checkDB();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    private void checkDB() {
        List<List<String>> ll_data = dbHelper.getTableColDataByCond(DBTables.SmartProject.TABLE_NAME, "*",
                new String[]{DBTables.SmartProject.downloadStatus}, new String[]{"0"});
        if (ll_data.size() > 0) {
            //id, name, type,  sizeInBytes, cdn_path, filePath,downloadStatus
            if (Helper.isNetworkAvailable(applicationContext)) {
                downloadFile(ll_data.get(0).get(2).trim(), ll_data.get(0).get(5).trim(), ll_data.get(0).get(1).trim());

            } else {
                Toast.makeText(applicationContext, "No Network available", Toast.LENGTH_SHORT).show();
            }
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

        Log.i("onDestroy", "ondestroy!");
        super.onDestroy();

        Intent broadcastIntent = new Intent("nk.mobleprojects.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        // checkDB();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), SmartAgentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
