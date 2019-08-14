package nk.mobleprojects.smartagent.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import nk.mobleprojects.smartagent.R;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;
import nk.mobleprojects.smartagent.utils.DBHelper;



public class SmartAgentAdapter extends RecyclerView.Adapter<SmartAgentAdapter.MyViewHolder>{

    Activity context;
    List<SmartAgentPojo> list;
    DBHelper dbHelper;
    ViewDownloadListener listener;


    public SmartAgentAdapter(Activity context, List<SmartAgentPojo> list, ViewDownloadListener listener) {
        this.context = context;
        this.list = list;
        dbHelper = new DBHelper(context);
        this.listener = listener;
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
            holder.tv_filepath.setVisibility(View.GONE);
            holder.progessbar.setVisibility(View.VISIBLE);
            holder.ll_color.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.tv_filesucess.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.tv_filesucess.setText("Download");


        } else {
            holder.ll_color.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.tv_filesucess.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.tv_filesucess.setText("View");
            holder.tv_filesucess.setVisibility(View.GONE);
            holder.progessbar.setVisibility(View.GONE);
            holder.tv_filepath.setText("FilePath :" + agentPojo.getFilePath());
            holder.tv_filepath.setVisibility(View.VISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.viewDownClickListner(agentPojo);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }



    public interface ViewDownloadListener {
        void viewDownClickListner(SmartAgentPojo smartAgentPojo);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //RvListVieeMethods rvListVieeMethods;

        TextView tv_filename, tv_fileType, tv_filesize, tv_filesucess, tv_filepath;
        LinearLayout ll_color;
        ProgressBar progessbar;

        public MyViewHolder(View itemView) {
            super(itemView.getRootView());

            tv_filename = itemView.findViewById(R.id.tv_filename);
            tv_fileType = itemView.findViewById(R.id.tv_fileType);
            tv_filesize = itemView.findViewById(R.id.tv_filesize);
            tv_filesucess = itemView.findViewById(R.id.tv_filesucess);
            ll_color = itemView.findViewById(R.id.ll_color);
            tv_filepath = itemView.findViewById(R.id.tv_filepath);
            progessbar = itemView.findViewById(R.id.progessbar);


        }
    }
}
