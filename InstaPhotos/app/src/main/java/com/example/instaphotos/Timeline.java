package com.example.instaphotos;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class Timeline extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    final String TAG = "Carlos";
    static final int REQUEST_IMAGE_GET = 714;
    TimelineListener timelineListener;
    ImageView logout;
    Button friends;
    Button add;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;


    public Timeline() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        getActivity().setTitle(R.string.timeline);


        logout = view.findViewById(R.id.buttonLogout);
        friends = view.findViewById(R.id.buttonFriends);
        add = view.findViewById(R.id.buttonAdd);
        recycler = view.findViewById(R.id.recyclerUserPhotos);
        recycler.setLayoutManager(linearLayoutManager);


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                getPictureFromCameraRoll();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timelineListener.userLoggedOut();
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: got back to activity");
        Log.d(TAG, "onActivityResult: " + requestCode);
        Log.d(TAG, "onActivityResult: " + resultCode);
        Log.d(TAG, "onActivityResult: " + data.toString());
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");
            if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data.getData() != null) {
                Log.d(TAG, "onActivityResult: we got data");
                Uri selectedImage = data.getData();
                uploadImage(selectedImage);
            }
        }
    }


    void uploadImage(Uri image) {
        final String randomID = UUID.randomUUID().toString();
        String uploadPath = FirebaseAuth.getInstance().getUid().toString() + "/" + randomID;
        Log.d(TAG, "uploadImage: " + uploadPath);
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child(uploadPath);


        riversRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: ");
                Snackbar.make(getActivity().findViewById(android.R.id.content), "image uploaded", Snackbar.LENGTH_LONG).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
                Snackbar.make(getActivity().findViewById(android.R.id.content), "image failed to upload", Snackbar.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        if (context instanceof TimelineListener) {
            timelineListener = (TimelineListener)context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }


    void getPictureFromCameraRoll() {
        Log.d(TAG, "getPictureFromCameraRoll: ");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);


//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
//        }
    }


    public interface TimelineListener {
        void userLoggedOut();
    }
}