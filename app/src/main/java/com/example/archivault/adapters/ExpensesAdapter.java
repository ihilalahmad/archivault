package com.example.archivault.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.archivault.R;
import com.example.archivault.model.ExpensesModel;


import java.util.List;


public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {


   private OnItemClickListener mListener;
   private List<ExpensesModel> expensesList;
   private Context mContext;

    public ExpensesAdapter(List<ExpensesModel> expensesList, Context mContext, OnItemClickListener mListener) {
        this.expensesList = expensesList;
        this.mContext = mContext;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_summary_cardview,parent,false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(ExpensesModel expensesModel);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tv_summary_title.setText(expensesList.get(position).getTitle_summary());
        holder.tv_summary_username.setText(expensesList.get(position).getUsername_summary());
        holder.tv_summary_date.setText(expensesList.get(position).getDate_summary());
        holder.tv_total_amount.setText(expensesList.get(position).getTotal_amount_summary());

        if (expensesList.get(position).getItem_quantity().equals("0")){

            holder.tv_item_quantity.setText(R.string.no_quantity);
        }else {

            holder.tv_item_quantity.setText(expensesList.get(position).getItem_quantity());
        }

        holder.delte_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onItemClick(expensesList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_summary_title;
        TextView tv_summary_username;
        TextView tv_summary_date;
        TextView tv_total_amount;
        TextView tv_item_quantity;
        TextView delte_entry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_summary_title = itemView.findViewById(R.id.tv_summary_title);
            tv_summary_username = itemView.findViewById(R.id.tv_summary_username);
            tv_summary_date = itemView.findViewById(R.id.tv_summary_date);
            tv_total_amount = itemView.findViewById(R.id.tv_summary_total_amount);
            tv_item_quantity = itemView.findViewById(R.id.tv_summary_quantity);
            delte_entry = itemView.findViewById(R.id.delete_entry);
        }
    }
}
