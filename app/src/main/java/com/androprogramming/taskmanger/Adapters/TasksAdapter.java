package com.androprogramming.taskmanger.Adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.androprogramming.taskmanger.Activities.MainActivity;
import com.androprogramming.taskmanger.Models.TasksModel;
import com.androprogramming.taskmanger.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    Context context;
    List<TasksModel> list;
    onTaskClick onTaskClick;

    public TasksAdapter(Context context, List<TasksModel> list, TasksAdapter.onTaskClick onTaskClick) {
        this.context = context;
        this.list = list;
        this.onTaskClick = onTaskClick;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TasksViewHolder(LayoutInflater.from(context).inflate(R.layout.task_item_layout, parent, false), onTaskClick);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {

        holder.checkBox.setText(list.get(position).getTaskName());
        holder.textViewDueDate.setText(list.get(position).getTaskDueDate());
        holder.checkBox.setChecked(list.get(position).isChecked());
        holder.toggleButtonImportant.setChecked(list.get(position).isImportant());

        final float scale = context.getResources().getDisplayMetrics().density;
        holder.checkBox.setPadding(holder.checkBox.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                holder.checkBox.getPaddingTop(),
                holder.checkBox.getPaddingRight(),
                holder.checkBox.getPaddingBottom());


        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date taskDueDate = format.parse(list.get(position).getTaskDueDate());

            Date today = format.parse(String.valueOf(Calendar.getInstance().getTime()));

            if (String.valueOf(taskDueDate).equals(String.valueOf(today))){
                holder.textViewDueDate.setTextColor(Color.GREEN);
            }

            if (taskDueDate.before(Calendar.getInstance().getTime())){
                holder.textViewDueDate.setTextColor(Color.RED);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class TasksViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ToggleButton toggleButtonImportant;
        TextView textViewDueDate;
        onTaskClick onTaskClick;

        public TasksViewHolder(@NonNull View itemView, onTaskClick onTaskClick) {
            super(itemView);

            this.onTaskClick = onTaskClick;
            checkBox = itemView.findViewById(R.id.checkBoxTaskName);
            toggleButtonImportant = itemView.findViewById(R.id.toggleButtonTaskImportant);
            textViewDueDate = itemView.findViewById(R.id.textViewTaskDueDate);


            itemView.setOnClickListener(view -> onTaskClick.onTaskItemClick(getAdapterPosition()));

            itemView.setOnLongClickListener(view -> onTaskClick.onTaskItemLongClick(getAdapterPosition()));

            toggleButtonImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onTaskClick.onImportantClick(getAdapterPosition(), b);
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onTaskClick.onCheckBoxClick(getAdapterPosition(), b);
                    if (b) {
                        checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });



        }
    }


    public interface onTaskClick {
        void onImportantClick(int position, boolean checked);

        void onCheckBoxClick(int position, boolean checked);

        void onTaskItemClick(int position);

        boolean onTaskItemLongClick(int position);
    }
}
