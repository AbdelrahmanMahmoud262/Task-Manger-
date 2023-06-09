package com.androprogramming.taskmanger.Models;

public class ListsModel {

    int id;
    String listName;

    public ListsModel(int id, String listName) {
        this.id = id;
        this.listName = listName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
