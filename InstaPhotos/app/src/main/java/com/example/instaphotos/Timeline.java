package com.example.instaphotos;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class Timeline extends Fragment {
    TimelineListener timelineListener;
    ImageView logout;
    Button friends;
    Button add;
    RecyclerView recycler;


    public Timeline() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        getActivity().setTitle(R.string.timeline);


        logout = view.findViewById(R.id.buttonLogout);
        friends = view.findViewById(R.id.buttonFriends);
        add = view.findViewById(R.id.buttonAdd);
        recycler = view.findViewById(R.id.recyclerUserPhotos);


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        if (context instanceof TimelineListener) {
            timelineListener = (TimelineListener)context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }


    public interface TimelineListener {
        void userLoggedOut();
    }
}