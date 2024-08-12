package com.nisvschoolug;

public class Model {
    private String id;
    private String title;
    private String content;
    private String image;

    public Model(String id, String title, String content,String image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public Model() {
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

