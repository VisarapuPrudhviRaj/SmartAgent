package nk.mobleprojects.smartagent.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

import nk.mobleprojects.smartagent.BuildConfig;


public class Helper {

    public static final String url = "https://demo6977317.mockable.io/fetch_config";
    public static final String FOLDER_NAME ="SMART_AGENT";
    public static final String FILEDOWLOADED ="FILEDOWNLOAD";
    public static final String FILEDOWLOADEDFAILED ="FILEDOWLOADEDFAILED";


    public static boolean isNetworkAvailable(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        return connected;
    }

    public static void createFolder(){
        File dir =new File(Environment.getExternalStorageDirectory(), "SmartProjectData");
        try{
            if(dir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void viewFile(String file_path,Context context) {
        File file = new File(file_path);
        // Uri url = Uri.fromFile(file);
        Uri url = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (file_path.contains(".jpg") || file_path.contains(".jpeg") || file_path.contains(".png")) {
            intent.setDataAndType(url, "image/jpeg");
        } else if (file_path.contains(".svg")) {
            //intent.setDataAndType(url, "image/svg");
        } else if (file_path.contains(".3gp") || file_path.contains(".mpg") ||
                file_path.contains(".mpeg") || file_path.contains(".mpe") ||
                file_path.contains(".mp4") || file_path.contains(".avi")) {
            // Video files
            intent.setDataAndType(url, "video/*");
        } else {
            // intent.setDataAndType(url, "*/*");
        }
        intent.addCategory(Intent.CATEGORY_APP_BROWSER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, e.getMessage().trim(), Toast.LENGTH_LONG).show();
        }


    }

}
