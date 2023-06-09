package com.androprogramming.taskmanger.Models;

public class TasksModel {

    int taskId;
    String taskName,taskDueDate,taskNotes;
    boolean isChecked,isImportant;
    int taskListId;

    public TasksModel(int taskId, String taskName, String taskDueDate, String taskNotes, boolean isChecked, boolean isImportant, int taskListId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDueDate = taskDueDate;
        this.taskNotes = taskNotes;
        this.isChecked = isChecked;
        this.isImportant = isImportant;
        this.taskListId = taskListId;
    }


    public String getTaskNotes() {
        return taskNotes;
    }

    public void setTaskNotes(String taskNotes) {
        this.taskNotes = taskNotes;
    }

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }
}
