package com.example.instaphotos;


import android.app.AlertDialog;
import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;


public class CreateNewAccount extends Fragment {
    final String TAG = "Carlos";
    CreateNewAccountListener createNewAccountListener;
    FirebaseAuth mAuth;
    EditText name;
    EditText email;
    EditText password;
    TextView cancel;
    Button submit;


    public CreateNewAccount() {}

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_account, container, false);
        getActivity().setTitle(R.string.create_new_account);


        name = view.findViewById(R.id.inputName);
        email = view.findViewById(R.id.inputEmail);
        password = view.findViewById(R.id.inputPassword);
        cancel = view.findViewById(R.id.buttonCancel);
        submit = view.findViewById(R.id.buttonSubmit);
        mAuth = FirebaseAuth.getInstance();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputValidation()) {
                    createNewAuthAccount(name.getText().toString(),
                                         email.getText().toString(),
                                         password.getText().toString());
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccountListener.userCanceledAccountCreation();
            }
        });


        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CreateNewAccountListener) {
            createNewAccountListener = (CreateNewAccountListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }


    void addNewUser(String name, String email, String userID) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();


        user.put("name", name);
        user.put("email", email);


        database.collection("users").document(mAuth.getUid()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createNewAccountListener.userSubmittedNewAccount();
                    }
                });
    }


    void createNewAuthAccount(String name,String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addNewUser(name, email, FirebaseAuth.getInstance().getUid());
                        } else {
                            createAlertDialog(getString(R.string.invalid_submission), task.getException().getMessage());
                        }
                    }
                });

    }


    Boolean inputValidation() {
        String alertMessage = "";

        if (name.getText().toString().isEmpty()) {
            alertMessage += getString(R.string.empty_name) + "\n";
        }

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


    public interface CreateNewAccountListener {
        void userCanceledAccountCreation();
        void userSubmittedNewAccount();
    }
}