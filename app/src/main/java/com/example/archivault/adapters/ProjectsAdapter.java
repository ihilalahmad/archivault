package com.example.archivault.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.archivault.R;
import com.example.archivault.activities.ProjectDetails;
import com.example.archivault.interfaces.ItemClickListener;
import com.example.archivault.model.ProjectModel;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    private List<ProjectModel> projectModelList;
    Context mContext;
    private ItemClickListener listener;

    public ProjectsAdapter(List<ProjectModel> projectModelList, Context context) {
        this.projectModelList = projectModelList;
        this.mContext = context;
        listener = (ItemClickListener)mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.projects_cardview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.project_name.setText(projectModelList.get(position).getProject_name());

        holder.btn_project_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openProjectDetails(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return projectModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView project_name;
        Button btn_project_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            project_name = itemView.findViewById(R.id.project_title);
            btn_project_details = itemView.findViewById(R.id.btn_project_details);

            btn_project_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
