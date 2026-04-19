package com.royal.educare.data;

import java.io.Serializable;

public class Topic implements Serializable {
    public int id;
    public int courseId;
    public String title;
    public String content;

    public Topic(int id, int courseId, String title, String content) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
    }
}
