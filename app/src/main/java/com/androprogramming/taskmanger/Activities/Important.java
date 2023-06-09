package com.androprogramming.taskmanger.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.androprogramming.taskmanger.Adapters.TasksAdapter;
import com.androprogramming.taskmanger.Databases.TasksDatabase;
import com.androprogramming.taskmanger.Models.TasksModel;
import com.androprogramming.taskmanger.R;

import java.util.List;

public class Important extends AppCompatActivity implements TasksAdapter.onTaskClick
{

    TasksDatabase tasksDatabase;

    TasksAdapter adapter;
    RecyclerView recyclerView;
    List<TasksModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important);

        tasksDatabase = new TasksDatabase(this);

        recyclerView = findViewById(R.id.recyclerImportant);

        setRecycler();
    }

    private void setRecycler() {
        list = tasksDatabase.getImportant();
        adapter = new TasksAdapter(this,list,this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    
    @Override
    public void onImportantClick(int position, boolean checked) {
        tasksDatabase.setImportant(list.get(position), checked);
    }

    @Override
    public void onCheckBoxClick(int position, boolean checked) {
        tasksDatabase.setChecked(list.get(position), checked);
    }

    @Override
    public void onTaskItemClick(int position) {
        Intent intent = new Intent(Important.this, TaskDetails.class);
        intent.putExtra("taskId", list.get(position).getTaskId());
        startActivity(intent);
    }

    @Override
    public boolean onTaskItemLongClick(int position) {
        return false;
    }
}