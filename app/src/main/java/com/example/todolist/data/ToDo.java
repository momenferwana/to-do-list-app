package com.example.todolist.data;

public class ToDo {
    private String id;
    private String title;
    private String description;
    private boolean checked;
    private String date;

    public ToDo() {
    }

    public ToDo(String id, String title, String description, boolean checked, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.checked = checked;
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
