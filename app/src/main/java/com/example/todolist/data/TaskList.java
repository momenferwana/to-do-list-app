package com.example.todolist.data;

public class TaskList {

    private String id;


    private String title;
    private int size;
    private String date;

    public TaskList(String id, String title, int size,  String date) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TaskList() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
