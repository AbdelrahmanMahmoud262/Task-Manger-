package com.androprogramming.taskmanger.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androprogramming.taskmanger.Models.ListsModel;
import com.androprogramming.taskmanger.Models.TasksModel;

import java.util.ArrayList;
import java.util.List;

public class MainListsDatabase extends SQLiteOpenHelper {


    private static final String LISTS_DATABASE = "lists_database";
    private static final String COLUMN_ID = "COLUMN_ID";
    private static final String COLUMN_LIST_NAME = "COLUMN_LIST_NAME";

    public MainListsDatabase(@Nullable Context context) {
        super(context, "listsDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryString = "CREATE TABLE " + LISTS_DATABASE + "( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COLUMN_LIST_NAME + " TEXT)";

        sqLiteDatabase.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteOne(ListsModel model) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + LISTS_DATABASE + " WHERE "
                + COLUMN_ID + "= " + model.getId();
        db.execSQL(query);
    }

    public boolean addOne(ListsModel model) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LIST_NAME, model.getListName());


        long insert = db.insert(LISTS_DATABASE, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<ListsModel> getAll() {

        List<ListsModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + LISTS_DATABASE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                ListsModel model = new ListsModel(id, name);
                returnList.add(model);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return returnList;
    }

    public List<ListsModel> searchData(String s) {

        List<ListsModel> returnSearch = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT * FROM " + LISTS_DATABASE;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                if (name.contains(s)) {
                    ListsModel model = new ListsModel(id, name);
                    returnSearch.add(model);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnSearch;
    }

    public ListsModel searchDataById(int s) {

        List<ListsModel> returnSearch = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT * FROM " + LISTS_DATABASE;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                if (id == s) {
                    ListsModel model = new ListsModel(id, name);
                    returnSearch.add(model);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return new ListsModel(returnSearch.get(0).getId(),returnSearch.get(0).getListName());
    }
}
