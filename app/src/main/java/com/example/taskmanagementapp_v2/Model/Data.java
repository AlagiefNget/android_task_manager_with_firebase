package com.example.taskmanagementapp_v2.Model;

public class Data {
    private String title;
    private String note;
    private String date;
    private  String id;

    public Data(){

    }

    public Data(String title, String note, String date, String id) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
