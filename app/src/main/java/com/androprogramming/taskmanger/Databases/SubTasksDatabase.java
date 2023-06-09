package com.androprogramming.taskmanger.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androprogramming.taskmanger.Models.TasksModel;

import java.util.ArrayList;
import java.util.List;

public class SubTasksDatabase extends SQLiteOpenHelper {


    public static final String COLUMN_ID = "COLUMN_ID";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String COLUMN_DUE_DATE = "COLUMN_DUE_DATE";
    public static final String COLUMN_IS_IMPORTANT = "COLUMN_IS_IMPORTANT";
    public static final String COLUMN_IS_COMPLETED = "COLUMN_IS_COMPLETED";
    public static final String SUBTASKS_DATABASE = "SUBTASKS_DATABASE";
    private static final String COLUMN_TASK_ID = "COLUMN_TASK_ID";
    public static final String COLUMN_NOTES = "COLUMN_NOTES";

    public SubTasksDatabase(@Nullable Context context) {
        super(context, "subTasks", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String queryString = "CREATE TABLE " + SUBTASKS_DATABASE + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_NAME + " TEXT , "
                + COLUMN_DUE_DATE + " TEXT , "
                + COLUMN_IS_IMPORTANT + " BOOL , "
                + COLUMN_IS_COMPLETED + " BOOL ,"
                + COLUMN_NOTES + " TEXT , "
                + COLUMN_TASK_ID + " INTEGER)";

        sqLiteDatabase.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(TasksModel model) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, model.getTaskName());
        cv.put(COLUMN_DUE_DATE, model.getTaskDueDate());
        cv.put(COLUMN_IS_COMPLETED, model.isChecked());
        cv.put(COLUMN_IS_IMPORTANT, model.isImportant());
        cv.put(COLUMN_TASK_ID, model.getTaskListId());
        cv.put(COLUMN_NOTES,model.getTaskNotes());


        long insert = db.insert(SUBTASKS_DATABASE, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<TasksModel> getAll(int taskId) {

        List<TasksModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + SUBTASKS_DATABASE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String dueDate = cursor.getString(2);
                boolean isImportant = cursor.getInt(3) == 1 ? true : false;
                boolean isCompleted = cursor.getInt(4) == 1 ? true : false;
                String notes = cursor.getString(5);
                int taskListId = cursor.getInt(6);

                if (taskId == taskListId) {
                    TasksModel model = new TasksModel(id, name, dueDate,notes, isCompleted, isImportant, taskListId);
                    returnList.add(model);
                }
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return returnList;
    }

    public List<TasksModel> searchDataByName(String s) {

        List<TasksModel> returnSearch = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT * FROM " + SUBTASKS_DATABASE;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String dueDate = cursor.getString(2);
                boolean isImportant = cursor.getInt(3) == 1 ? true : false;
                boolean isCompleted = cursor.getInt(4) == 1 ? true : false;
                String notes = cursor.getString(5);
                int taskListId = cursor.getInt(6);


                if (name.contains(s)) {
                    TasksModel model = new TasksModel(id, name, dueDate,notes, isCompleted, isImportant, taskListId);
                    returnSearch.add(model);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnSearch;
    }

    public TasksModel searchDataById(int s) {

        List<TasksModel> returnSearch = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT * FROM " + SUBTASKS_DATABASE;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String dueDate = cursor.getString(2);
                boolean isImportant = cursor.getInt(3) == 1 ? true : false;
                boolean isCompleted = cursor.getInt(4) == 1 ? true : false;
                String notes = cursor.getString(5);
                int taskListId = cursor.getInt(6);


                if (id == s) {
                    TasksModel model = new TasksModel(id, name, dueDate,notes, isCompleted, isImportant, taskListId);
                    returnSearch.add(model);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return new TasksModel(returnSearch.get(0).getTaskId(), returnSearch.get(0).getTaskName(), returnSearch.get(0).getTaskDueDate(),returnSearch.get(0).getTaskNotes(), returnSearch.get(0).isChecked(), returnSearch.get(0).isImportant(), returnSearch.get(0).getTaskListId());
    }





    public boolean setChecked(TasksModel model, boolean isChecked) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, model.getTaskId());
        contentValues.put(COLUMN_NAME, model.getTaskName());
        contentValues.put(COLUMN_IS_COMPLETED, isChecked);
        contentValues.put(COLUMN_IS_IMPORTANT, model.isImportant());
        contentValues.put(COLUMN_DUE_DATE, model.getTaskDueDate());
        contentValues.put(COLUMN_TASK_ID, model.getTaskListId());
        contentValues.put(COLUMN_NOTES,model.getTaskNotes());

        long update = db.update(SUBTASKS_DATABASE, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(model.getTaskId())});

        db.close();
        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean setImportant(TasksModel model, boolean isChecked) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, model.getTaskId());
        contentValues.put(COLUMN_NAME, model.getTaskName());
        contentValues.put(COLUMN_IS_COMPLETED, model.isChecked());
        contentValues.put(COLUMN_IS_IMPORTANT, isChecked);
        contentValues.put(COLUMN_DUE_DATE, model.getTaskDueDate());
        contentValues.put(COLUMN_TASK_ID, model.getTaskListId());
        contentValues.put(COLUMN_NOTES,model.getTaskNotes());


        long update = db.update(SUBTASKS_DATABASE, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(model.getTaskId())});

        db.close();
        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }


    public void deleteOne(TasksModel model) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + SUBTASKS_DATABASE + " WHERE "
                + COLUMN_ID + "= " + model.getTaskId();
        db.execSQL(query);
    }

    public boolean setNotes(TasksModel model,String taskNotes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, model.getTaskId());
        contentValues.put(COLUMN_NAME, model.getTaskName());
        contentValues.put(COLUMN_IS_COMPLETED, model.isChecked());
        contentValues.put(COLUMN_IS_IMPORTANT, model.isImportant());
        contentValues.put(COLUMN_DUE_DATE, model.getTaskDueDate());
        contentValues.put(COLUMN_TASK_ID, model.getTaskListId());
        contentValues.put(COLUMN_NOTES,taskNotes);


        long update = db.update(SUBTASKS_DATABASE, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(model.getTaskId())});

        db.close();
        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

}
