package com.example.instaphotos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends Fragment {
    final String TAG = "Carlos";
    FirebaseAuth mAuth;
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


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputValidation()) {
                    mAuth = FirebaseAuth.getInstance();

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                         .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 if (task.isSuccessful()) {
                                     Log.d(TAG, "onComplete: submitted");
                                 } else {
                                     createAlertDialog(getString(R.string.invalid_login), task.getException().getMessage());
                                 }
                             }
                         });
                }
            }
        });


        return view;
    }


    Boolean inputValidation() {
        String alertMessage = "";

        if (email.getText().toString().isEmpty()) {
            alertMessage += getString(R.string.empty_email) + "\n";
        }

        if (password.getText().toString().isEmpty()) {
            alertMessage += getString(R.string.empty_password) + "\n";
        }

        if (!alertMessage.isEmpty()) {
            createAlertDialog(getString(R.string.missing_fields), alertMessage);
            return false;
        }

        return true;
    }


    void createAlertDialog(String alertTitle, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(alertTitle)
                .setMessage(alertMessage)
                .setPositiveButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        builder.create().show();
    }
}