package com.example.instaphotos;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements Login.LoginListener {
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
        Log.d("Carlos", "loginButtonClicked: about to go to the main page");
    }

    @Override
    public void createNewAccountButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.container, new CreateNewAccount())
                                   .commit();
    }
}