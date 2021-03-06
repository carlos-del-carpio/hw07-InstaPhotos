/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Comment {
    public static final String DATE_FORMAT = "dd MMM yy HH:mm";
    private String commentID;
    private String comment;
    private String authorID;
    private String postID;
    private Date commentDate;


    Comment (String commentID, String comment, String authorID, String postID, Date date) {
        this.commentID = commentID;
        this.comment = comment;
        this.authorID = authorID;
        this.postID = postID;
        this.commentDate = date;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getAuthorID() {
        return authorID;
    }


    public String getComment() {
        return comment;
    }


    public String getPostID() {
        return postID;
    }


    public String getCommentID() {
        return commentID;
    }


    public String getDateCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));


        return dateFormat.format(commentDate);
    }
}
