package com.androprogramming.taskmanger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androprogramming.taskmanger.Models.ListsModel;
import com.androprogramming.taskmanger.R;

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsViewHolder> {

    Context context;
    List<ListsModel> list;
    onListsClick onListsClick;

    public ListsAdapter(Context context, List<ListsModel> list, ListsAdapter.onListsClick onListsClick) {
        this.context = context;
        this.list = list;
        this.onListsClick = onListsClick;
    }

    @NonNull
    @Override
    public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListsViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list_layout, parent, false), onListsClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ListsViewHolder holder, int position) {
        holder.listName.setText(list.get(position).getListName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ListsViewHolder extends RecyclerView.ViewHolder {

        TextView listName;
        onListsClick onListsClick;

        public ListsViewHolder(@NonNull View itemView, onListsClick onListsClick) {
            super(itemView);
            listName = itemView.findViewById(R.id.textViewListName);

            this.onListsClick = onListsClick;

            itemView.setOnClickListener(view -> onListsClick.onListClick(getAdapterPosition()));
        }
    }

    public interface onListsClick {
        void onListClick(int position);
    }
}
