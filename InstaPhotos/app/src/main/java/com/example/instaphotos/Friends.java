/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class Friends extends Fragment {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FriendListener friendListener;
    ListView friends;
    ArrayAdapter adapter;
    ArrayList<Friend> friendsList = new ArrayList<>();


    public Friends() {}


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        getActivity().setTitle(R.string.friends);


        friends = view.findViewById(R.id.friendsListView);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, friendsList);


        getFriends();


        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendListener.friendSelected(friendsList.get(position));
            }
        });


        return view;
    }

    void getFriends() {
        database.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        friendsList.clear();


                        for (DocumentSnapshot document : value) {
                            if (value.size() > 0 && !document.getId().equals(mAuth.getUid())) {
                                friendsList.add(new Friend(document.get("name").toString(), document.getId()));
                            } else {
                                //TODO: Make get some friends message
                            }
                        }


                        adapter.notifyDataSetChanged();
                        friends.setAdapter(adapter);
                    }
                });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        if (context instanceof FriendListener) {
            friendListener = (FriendListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }


    public interface FriendListener {
        void friendSelected(Friend friend);
    }
}