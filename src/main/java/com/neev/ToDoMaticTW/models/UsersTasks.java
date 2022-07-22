package com.neev.ToDoMaticTW.models;

import org.springframework.data.annotation.Id;

public class UsersTasks {
    @Id
    private String id;
    private String title;
    private boolean isDone;

    public UsersTasks(){

    }

    public UsersTasks(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
