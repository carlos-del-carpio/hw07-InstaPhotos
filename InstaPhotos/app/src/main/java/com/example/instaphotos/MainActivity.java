/**Assignment: HW07
  *File name: HW07
  *Student: Carlos Del Carpio
*/


package com.example.instaphotos;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements Login.LoginListener,
                                                               CreateNewAccount.CreateNewAccountListener,
                                                               Timeline.TimelineListener,
                                                               Friends.FriendListener,
                                                               FriendTimeline.FriendTimelineListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.container, new Login())
                                   .commit();
    }


    @Override
    public void loginButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.container, new Timeline())
                                   .commit();
    }


    @Override
    public void createNewAccountButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new CreateNewAccount())
                                   .commit();
    }


    @Override
    public void userCanceledAccountCreation() {
        getSupportFragmentManager().popBackStack();
    }


    @Override
    public void userSubmittedNewAccount() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.container, new Timeline())
                                   .commit();
    }


    @Override
    public void userLoggedOut() {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.container, new Login())
                                   .commit();
    }


    @Override
    public void userClickedFriends() {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new Friends())
                                   .commit();
    }


    @Override
    public void userSelectedPost(String creatorID, String postID) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new PostWithComments(creatorID, postID))
                                   .commit();
    }


    @Override
    public void friendSelected(Friend friend) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new FriendTimeline(friend))
                                   .commit();
    }


    @Override
    public void getPostWithComments(String authorID, String postID) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new PostWithComments(authorID, postID))
                                   .commit();
    }
}