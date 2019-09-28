package com.example.learningsupport_argame.community.data;


public class Msg {

    //这里之后直接让他继承PariofBean类即可！！！！
    //下面的代码其实都是无用的
    private int id;

    private int imageResourceID;

    private String title;

    private String content;

    public Msg() {

    }

    public Msg(int id, int imageResourceID, String title, String content) {

        this.id = id;

        this.imageResourceID = imageResourceID;

        this.title = title;

        this.content = content;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public int getImageResourceID() {

        return imageResourceID;

    }

    public void setImageResourceID(int imageResourceID) {

        this.imageResourceID = imageResourceID;

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
