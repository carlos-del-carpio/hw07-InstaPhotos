package com.example.instaphotos;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements Login.LoginListener,
                                                               CreateNewAccount.CreateNewAccountListener,
                                                               Timeline.TimelineListener {
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

    }
}