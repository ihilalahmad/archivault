package com.example.archivault.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.archivault.R;
import com.example.archivault.model.IncomesModel;

import java.util.ArrayList;
import java.util.List;

public class IncomesAdapter extends RecyclerView.Adapter<IncomesAdapter.ViewHolder> implements Filterable {

    private OnItemClickListener mListener;
    private List<IncomesModel> incomesList;
    private List<IncomesModel> incomeListFull;
    private Context mContext;

    public IncomesAdapter(List<IncomesModel> incomesList, Context mContext ,OnItemClickListener mListener) {
        this.mListener = mListener;
        this.incomesList = incomesList;
        this.mContext = mContext;
        incomeListFull = new ArrayList<>(incomesList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incomes_cardview,parent,false);
        return new ViewHolder(view);
    }


    public interface OnItemClickListener {
        void onItemClick(IncomesModel incomesModel);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tv_summary_title.setText(incomesList.get(position).getIncome_reason());
        holder.tv_summary_username.setText(incomesList.get(position).getIncome_by_user());
        holder.tv_summary_date.setText(incomesList.get(position).getIncome_date());
        holder.tv_total_amount.setText(incomesList.get(position).getTotal_amount());

        holder.delete_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("OnClickTest", incomesList.get(position).toString());
                mListener.onItemClick(incomesList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return incomesList.size();
    }

    @Override
    public Filter getFilter() {
        return incomeFilter;
    }

    private Filter incomeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<IncomesModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(incomeListFull);
            }else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                for(IncomesModel item : incomeListFull){
                    if (item.getIncome_reason().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            incomesList.clear();
            incomesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_summary_title;
        TextView tv_summary_username;
        TextView tv_summary_date;
        TextView tv_total_amount;
        TextView delete_entry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_summary_title = itemView.findViewById(R.id.tv_income_summary_title);
            tv_summary_username = itemView.findViewById(R.id.tv_income_summary_username);
            tv_summary_date = itemView.findViewById(R.id.tv_income_summary_date);
            tv_total_amount = itemView.findViewById(R.id.tv_income_summary_total_amount);
            delete_entry = itemView.findViewById(R.id.delete_income);
        }
    }
}
