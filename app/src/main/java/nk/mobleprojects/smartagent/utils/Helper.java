package nk.mobleprojects.smartagent.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

/**
 * Created by Prudhvi on 09-08-2019.
 */

public class Helper {

    public static final String url = "https://demo6977317.mockable.io/fetch_config";
    public static final String FOLDER_NAME ="SMART_AGENT";


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
}
