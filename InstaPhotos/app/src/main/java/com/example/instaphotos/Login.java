package com.example.instaphotos;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Login extends Fragment {
    EditText email;
    EditText password;
    Button login;
    TextView createNewAccount;


    public Login() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        email = view.findViewById(R.id.inputEmailLogin);
        password = view.findViewById(R.id.passwordLogin);
        login = view.findViewById(R.id.buttonLogin);
        createNewAccount = view.findViewById(R.id.createNewAccount);


        return view;
    }
}