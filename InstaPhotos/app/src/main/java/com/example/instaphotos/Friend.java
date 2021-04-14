/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


public class Friend {
    String name;
    String userID;

    public Friend (String name, String userID) {
        this.name = name;
        this.userID = userID;
    }


    public String getName() {
        return name;
    }


    public String getUserID() {
        return userID;
    }


    @Override
    public String toString() {
        return name;
    }
}
