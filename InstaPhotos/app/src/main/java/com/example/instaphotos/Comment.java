package com.example.instaphotos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Comment {
    public static final String DATE_FORMAT = "dd MMM yy HH:mm";
    private String comment;
    private String authorID;
    private String postID;
    private Date commentDate;

    Comment (String comment, String authorID, String postID, Date date) {
        this.comment = comment;
        this.authorID = authorID;
        this.postID = postID;
        this.commentDate = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getDateCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        return dateFormat.format(commentDate);
    }
}
