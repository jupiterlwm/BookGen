package com.bluejupiter.bookgen;

import javax.xml.bind.ValidationEvent;
import java.util.List;
import java.util.Vector;

/**
 * Created by Will on 15/4/23.
 */
public class Book {
    private String title;
    private String author;

    public Book() {
    }

    public Book(String txtFileName){
//        this.txtFileName = txtFileName;
        int indexofslash = txtFileName.lastIndexOf("/");
        int indexofpoint = txtFileName.lastIndexOf(".");
        this.title = txtFileName.substring(indexofslash+1,indexofpoint).trim();
//        epubFileName = txtFileName.substring(0,indexofslash).trim() + "/" + title + ".epub";
//        this.epubFileName = txtFileName.substring(0,indexofpoint) + ".epub";
    }
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
//        this.txtFileName = txtFileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
