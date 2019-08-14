package nk.mobleprojects.smartagent.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import nk.mobleprojects.smartagent.BuildConfig;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.Helper;

public class SmartAdapaterPresenter {

    Context context;
    DBHelper dbHelper;

    public SmartAdapaterPresenter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void viewFile(String file_path) {
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
