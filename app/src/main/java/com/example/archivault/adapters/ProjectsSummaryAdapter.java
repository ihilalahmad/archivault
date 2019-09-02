package com.example.archivault.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.archivault.R;
import com.example.archivault.model.SummaryModel;


import java.util.List;


public class ProjectsSummaryAdapter extends RecyclerView.Adapter<ProjectsSummaryAdapter.ViewHolder> {

    List<SummaryModel> summaryList;
    Context mContext;

    public ProjectsSummaryAdapter(List<SummaryModel> summaryList, Context mContext) {
        this.summaryList = summaryList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_summary_cardview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_summary_title.setText(summaryList.get(position).getTitle_summary());
        holder.tv_summary_username.setText(summaryList.get(position).getUsername_summary());
        holder.tv_summary_date.setText(summaryList.get(position).getDate_summary());
        holder.tv_total_amount.setText(summaryList.get(position).getTotal_amount_summary());

    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_summary_title;
        TextView tv_summary_username;
        TextView tv_summary_date;
        TextView tv_total_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_summary_title = itemView.findViewById(R.id.tv_summary_title);
            tv_summary_username = itemView.findViewById(R.id.tv_summary_username);
            tv_summary_date = itemView.findViewById(R.id.tv_summary_date);
            tv_total_amount = itemView.findViewById(R.id.tv_summary_total_amount);
        }
    }
}
