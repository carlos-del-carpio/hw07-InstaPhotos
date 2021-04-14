package com.example.instaphotos;


import java.util.Date;


public class Post {
    private String postID;
    private Date dateCreated;


    Post (String postID, Date date) {
        this.postID = postID;
        this.dateCreated = date;
    }

    public String getPostID() {
        return postID;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postID='" + postID + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
