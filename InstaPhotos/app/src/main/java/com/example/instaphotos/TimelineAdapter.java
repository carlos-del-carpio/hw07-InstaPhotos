package com.example.instaphotos;


import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;


public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    final String TAG = "Carlos";
    TimelineActionListener timelineActionListener;
    ArrayList<Post> posts;


    public TimelineAdapter (ArrayList<Post> posts, Fragment fragment) {
        this.posts = posts;
        this.timelineActionListener = (TimelineActionListener)fragment;
    }


    @NonNull
    @Override
    public TimelineAdapter.TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_row, parent, false);
        TimelineViewHolder timelineViewHolder = new TimelineViewHolder(view, timelineActionListener);


        return timelineViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.TimelineViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.dateCreated.setText(post.getDateCreated().toString());
        holder.downloadPicture(post.getPostID());
    }


    @Override
    public int getItemCount() {
        return this.posts.size();
    }


    public class TimelineViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView dateCreated;
        ImageView delete;

        public TimelineViewHolder(@NonNull View itemView, TimelineActionListener timelineActionListener) {
            super(itemView);


            image = itemView.findViewById(R.id.imagePost);
            dateCreated = itemView.findViewById(R.id.outputDateCreated);
            delete = itemView.findViewById(R.id.buttonDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timelineActionListener.deleteCurrentPost();
                }
            });
        }


        void downloadPicture(String postID) {
            Log.d(TAG, "downloadPicture: " + postID);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference()
                                                 .child(FirebaseAuth.getInstance().getUid() + "/" + postID );


            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(itemView).load(uri).into(image);
                }
            });
        }
    }


    public interface TimelineActionListener {
        void deleteCurrentPost();
    }
}