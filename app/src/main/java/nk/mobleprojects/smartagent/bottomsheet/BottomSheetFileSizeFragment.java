package nk.mobleprojects.smartagent.bottomsheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import nk.mobleprojects.smartagent.R;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;

public class BottomSheetFileSizeFragment extends BottomSheetDialogFragment {

    String path;
    SmartAgentPojo smartAgentPojo;

    public BottomSheetFileSizeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smartAgentPojo = (SmartAgentPojo) getArguments().getSerializable("filePath");

    }

    private String fileSize(String path) {
        String filesize = "";
        try {
            File file = new File(path);
            long length = file.length();
            // length = length / 1024;
            filesize = String.valueOf(length);
            System.out.println("File Path : " + file.getPath() + ", File size : " + length + " KB");
        } catch (Exception e) {
            System.out.println("File not found : " + e.getMessage() + e);
        }

        return filesize;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_size_bottom_sheet, container, false);
        TextView tv_filename = view.findViewById(R.id.tv_filename);
        TextView tv_filepath = view.findViewById(R.id.tv_filepath);
        TextView tv_filesize = view.findViewById(R.id.tv_filesize);
        TextView tv_dbsize = view.findViewById(R.id.tv_dbsize);
        ImageView iv_show = view.findViewById(R.id.iv_show);

        tv_filename.setText("File Name : " + smartAgentPojo.getName());
        tv_dbsize.setText(smartAgentPojo.getSizeInBytes());
        tv_filepath.setText("SD Path : " + smartAgentPojo.getFilePath());

        tv_filesize.setText(fileSize(smartAgentPojo.getFilePath()));

        if (smartAgentPojo.getSizeInBytes().equals(fileSize(smartAgentPojo.getFilePath()))) {
            iv_show.setVisibility(View.VISIBLE);
            iv_show.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
        } else {
            iv_show.setVisibility(View.VISIBLE);
            iv_show.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_matched));
        }


        return view;
    }


}
