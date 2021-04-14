/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Post {
    public static final String DATE_FORMAT = "dd MMM yy HH:mm";
    private String userID;
    private String postID;
    private Date dateCreated;


    Post (String userID, String postID, Date date) {
        this.userID = userID;
        this.postID = postID;
        this.dateCreated = date;
    }


    public String getPostID() {
        return postID;
    }


    public String getUserID() {
        return userID;
    }


    public String getDateCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        return dateFormat.format(dateCreated);
    }


    @Override
    public String toString() {
        return "Post{" +
                "postID='" + postID + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
