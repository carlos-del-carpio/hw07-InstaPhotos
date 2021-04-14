package com.example.instaphotos;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.Date;


public class FriendTimeline extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Friend friend;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;
    FriendTimelineAdapter adapter;
    ArrayList<Post> posts = new ArrayList<>();

    public FriendTimeline(Friend friend) {
        this.friend = friend;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_timeline, container, false);
        getActivity().setTitle(friend.name + getString(R.string.friendTimeline));


        recycler = view.findViewById(R.id.friendsRecycler);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new FriendTimelineAdapter(posts, this, friend);


        getProfileImages();


        return view;
    }


    void getProfileImages(){
        database.collection("users")
                .document(friend.getUserID())
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
}