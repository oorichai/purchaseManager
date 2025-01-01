package com.example.myapplicationrv.models;

public class Data {

    private String name;
    private String version;
    private int image;
    private int id;

    public Data(String name, String version, int image, int id) {
        this.name = name;
        this.version = version;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
