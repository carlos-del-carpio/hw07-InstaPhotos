/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;


public class Timeline extends Fragment implements TimelineAdapter.TimelineActionListener {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    final String TAG = "Carlos";
    static final int REQUEST_IMAGE_GET = 714;
    TimelineListener timelineListener;
    ImageView logout;
    Button friends;
    Button add;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;
    TimelineAdapter adapter;
    ArrayList<Post> posts = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public Timeline() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        getActivity().setTitle(R.string.timeline);


        logout = view.findViewById(R.id.buttonLogout);
        friends = view.findViewById(R.id.buttonFriends);
        add = view.findViewById(R.id.buttonAdd);
        recycler = view.findViewById(R.id.recyclerUserPhotos);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new TimelineAdapter(posts, this);


        getProfileImages();


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timelineListener.userClickedFriends();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromCameraRoll();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                timelineListener.userLoggedOut();
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data.getData() != null) {
            Uri selectedImage = data.getData();
            uploadImage(selectedImage);
        }
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


    void uploadImage(Uri image) {
        final String randomID = UUID.randomUUID().toString();
        String uploadPath = FirebaseAuth.getInstance().getUid().toString() + "/" + randomID;
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child(uploadPath);


        riversRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                addNewPost(randomID);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "image uploaded", Snackbar.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "image failed to upload", Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    void getPictureFromCameraRoll() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }


    void getProfileImages(){
        database.collection("users")
                .document(mAuth.getUid())
                .collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        posts.clear();


                        for (DocumentSnapshot document : value) {
                            posts.add(createNewPost(document));
                        }


                        adapter.notifyDataSetChanged();
                        recycler.setAdapter(adapter);
                    }
                });
    }


    Post createNewPost(DocumentSnapshot document) {
        String userID = document.get("userID").toString();
        String postID = document.get("postID").toString();
        Date date = document.getDate("dateCreated");
        
        
        Post post = new Post(userID, postID, date);


        return post;
    }


    void addNewPost(String postID) {
        HashMap<String, Object> post = new HashMap<>();


        post.put("userID", mAuth.getUid());
        post.put("postID", postID);
        post.put("dateCreated", Timestamp.now());


        database.collection("users")
                .document(mAuth.getUid())
                .collection("posts")
                .document(postID)
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: post added");
                    }
                });
    }


    void deletePost(String postID) {
        database.collection("users")
                .document(mAuth.getUid())
                .collection("posts")
                .document(postID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: post deleted too");
                    }
                });
    }


    @Override
    public void deleteCurrentPost(String postID) {
        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child(FirebaseAuth.getInstance().getUid() + "/" + postID);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deletePost(postID);
            }
        });
    }


    @Override
    public void postSelected(String creatorID, String postID) {
        timelineListener.userSelectedPost(creatorID, postID);
    }


    public interface TimelineListener {
        void userLoggedOut();
        void userClickedFriends();
        void userSelectedPost(String creatorID, String postID);
    }
}