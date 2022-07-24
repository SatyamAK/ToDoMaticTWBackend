package com.neev.ToDoMaticTW.models;

public class UsersTasks {
    private Integer id;
    private String title;
    private boolean isDone;

    public UsersTasks(){

    }

    public UsersTasks(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
