package nk.mobleprojects.smartagent.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import nk.mobleprojects.smartagent.R;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DownloadFile;
import nk.mobleprojects.smartagent.utils.Helper;

/**
 * Created by Prudhvi on 07-08-2019.
 */

public class SmartAgentAdapter extends RecyclerView.Adapter<SmartAgentAdapter.MyViewHolder> {

    Context context;
    List<SmartAgentPojo> list;

    DBHelper dbHelper;


    public SmartAgentAdapter(Context context, List<SmartAgentPojo> list) {
        this.context = context;
        this.list = list;
        dbHelper = new DBHelper(context);
    }


    @NonNull
    @Override
    public SmartAgentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SmartAgentAdapter.MyViewHolder holder, int position) {
        final SmartAgentPojo agentPojo = list.get(position);
        holder.tv_filename.setText(agentPojo.getName());
        holder.tv_filesize.setText("SizeInBytes :" + agentPojo.getSizeInBytes());
        holder.tv_fileType.setText(agentPojo.getType());

        if (agentPojo.getFilePath().equals("")) {
            holder.tv_filesucess.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.tv_filesucess.setText("Download");
            holder.tv_filesucess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(agentPojo.getName(), agentPojo.getCdn_path(), holder.tv_filesucess, holder.ll_color, agentPojo.getId());
                }
            });

        } else {
            holder.tv_filesucess.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.tv_filesucess.setText("View");
            holder.tv_filesucess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewFile(agentPojo.getFilePath());
                }
            });
        }
    }

    private void viewFile(String file_path) {
        File file = new File(file_path);
        Uri url = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (file_path.contains(".jpg") || file_path.contains(".jpeg") || file_path.contains(".png") || file_path.contains(".svg")) {
            intent.setDataAndType(url, "image/jpeg");
        } else if (file_path.contains(".3gp") || file_path.contains(".mpg") ||
                file_path.contains(".mpeg") || file_path.contains(".mpe") ||
                file_path.contains(".mp4") || file_path.contains(".avi")) {
            // Video files
            intent.setDataAndType(url, "video/*");
        } else {
            intent.setDataAndType(url, "*/*");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(url, "application/pdf");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, e.getMessage().trim(), Toast.LENGTH_LONG).show();
        }


    }


    private void downloadFile(String fileName, String downloadUrl,
                              TextView textView, LinearLayout linearLayout,
                              String file_id) {
        File apkfile = new File(Environment.getExternalStorageDirectory() +
                "/" + Helper.FOLDER_NAME + "/" + fileName);
        if (!apkfile.exists()) {
            Toast.makeText(context, "File Downloading...", Toast.LENGTH_LONG).show();
            DownloadFile downloadApk = new DownloadFile(context, textView, linearLayout, dbHelper, file_id);
            downloadApk.execute(downloadUrl, fileName);
        } else {
            //File Size Check

        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        //RvListVieeMethods rvListVieeMethods;

        TextView tv_filename, tv_fileType, tv_filesize, tv_filesucess;
        LinearLayout ll_color;

        public MyViewHolder(View itemView) {
            super(itemView.getRootView());

            tv_filename = itemView.findViewById(R.id.tv_filename);
            tv_fileType = itemView.findViewById(R.id.tv_fileType);
            tv_filesize = itemView.findViewById(R.id.tv_filesize);
            tv_filesucess = itemView.findViewById(R.id.tv_filesucess);
            ll_color = itemView.findViewById(R.id.ll_color);


        }
    }
}
