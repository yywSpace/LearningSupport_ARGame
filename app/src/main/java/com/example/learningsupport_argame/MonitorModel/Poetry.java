package com.example.learningsupport_argame.MonitorModel;

/**
 * 长度为两行的诗词
 */
public class Poetry {
    String mPoetryName;
    String mPoetryAuthor;
    String mPoetryHead;
    String mPoetryTail;

    public Poetry() {

    }

    public Poetry(String poetryName, String poetryAuthor, String poetryHead, String poetryTail) {
        mPoetryName = poetryName;
        mPoetryAuthor = poetryAuthor;
        mPoetryHead = poetryHead;
        mPoetryTail = poetryTail;
    }

    public String getPoetryName() {
        return mPoetryName;
    }

    public void setPoetryName(String poetryName) {
        mPoetryName = poetryName;
    }

    public String getPoetryAuthor() {
        return mPoetryAuthor;
    }

    public void setPoetryAuthor(String poetryAuthor) {
        mPoetryAuthor = poetryAuthor;
    }

    public String getPoetryHead() {
        return mPoetryHead;
    }

    public void setPoetryHead(String poetryHead) {
        mPoetryHead = poetryHead;
    }

    public String getPoetryTail() {
        return mPoetryTail;
    }

    public void setPoetryTail(String poetryTail) {
        mPoetryTail = poetryTail;
    }
}
