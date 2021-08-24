package com.example.travelbuddy;

public class Model {
    private String title, context, id;

    public Model() {
    }

    public Model(String title, String context, String id) {
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String task) {
        this.title = task;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String description) {
        this.context = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
