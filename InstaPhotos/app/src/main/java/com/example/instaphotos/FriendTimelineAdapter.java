/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;


public class FriendTimelineAdapter extends RecyclerView.Adapter<FriendTimelineAdapter.FriendTimelineViewHolder>{
    FriendTimelineAdapterListener friendTimelineAdapterListener;
    Friend friend;
    ArrayList<Post> posts;
    ArrayList<String> likes;
    int commentCount;


    public FriendTimelineAdapter (ArrayList<Post> posts, Fragment fragment, Friend friend) {
        this.posts = posts;
        this.friend = friend;
        this.likes = new ArrayList<>();
        this.friendTimelineAdapterListener = (FriendTimelineAdapterListener) fragment;
    }


    @NonNull
    @Override
    public FriendTimelineAdapter.FriendTimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_row, parent, false);
        FriendTimelineAdapter.FriendTimelineViewHolder friendTimelineViewHolder = new FriendTimelineAdapter.FriendTimelineViewHolder(view, friendTimelineAdapterListener);


        return friendTimelineViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FriendTimelineAdapter.FriendTimelineViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.postID = post.getPostID();
        holder.dateCreated.setText(post.getDateCreated());
        holder.downloadPicture(post.getPostID());
        holder.getCommentCount();
        holder.getLikesCount();
    }


    @Override
    public int getItemCount() {
        return this.posts.size();
    }


    public class FriendTimelineViewHolder extends RecyclerView.ViewHolder {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        ImageView postImage;
        ImageView likeImage;
        TextView dateCreated;
        TextView likesCount;
        TextView commentsCount;
        String postID;
        int likeStatus;


        public FriendTimelineViewHolder(@NonNull View itemView, FriendTimelineAdapterListener friendTimelineAdapterListener) {
            super(itemView);


            postImage = itemView.findViewById(R.id.imagePost);
            likeImage = itemView.findViewById(R.id.buttonLike);
            dateCreated = itemView.findViewById(R.id.outputDateCreated);
            likesCount = itemView.findViewById(R.id.outputNumberOfLikes);
            commentsCount = itemView.findViewById(R.id.outputNumbeOfComments);


            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLike(likeStatus);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendTimelineAdapterListener.friendPostSelected(friend.getUserID(), postID);
                }
            });
        }


        void downloadPicture(String postID) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference()
                                                 .child(friend.getUserID() + "/" + postID);


            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(itemView).load(uri).into(postImage);
                }
            });
        }


        void getCommentCount() {
            database.collection("users")
                    .document(friend.getUserID())
                    .collection("posts")
                    .document(postID)
                    .collection("comments")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            commentCount = 0;


                            for (DocumentSnapshot document : value) {
                                commentCount += 1;
                            }

                            commentsCount.setText("Comment(s) " + commentCount);
                        }
                    });
        }


        void getLikesCount() {
            database.collection("users")
                    .document(friend.getUserID())
                    .collection("posts")
                    .document(postID)
                    .collection("likes")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            likes.clear();


                            for (DocumentSnapshot document : value) {
                                likes.add(document.get("userID").toString());
                            }


                            likesCount.setText("Like(s) " + likes.size());


                            if (likes.contains(FirebaseAuth.getInstance().getUid())) {
                                likeImage.setImageResource(R.drawable.like_favorite);
                                likeStatus = 1;
                            } else {
                                likeImage.setImageResource(R.drawable.like_not_favorite);
                                likeStatus = 0;
                            }
                        }
                    });
        }


        void toggleLike(int likeStatus) {
            if (likeStatus == 0) {
                addLike(postID);
                this.likeStatus = 1;
            } else if (likeStatus == 1) {
                removeLike(postID);
                this.likeStatus = 0;
            }
        }


        void addLike(String postID) {
            HashMap<String, Object> userLiked = new HashMap<>();


            userLiked.put("userID", FirebaseAuth.getInstance().getUid());


            database.collection("users")
                    .document(friend.getUserID())
                    .collection("posts")
                    .document(postID)
                    .collection("likes")
                    .document(FirebaseAuth.getInstance().getUid())
                    .set(userLiked)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            likeImage.setImageResource(R.drawable.like_favorite);
                        }
                    });
        }


        void removeLike(String postID) {
            database.collection("users")
                    .document(friend.getUserID())
                    .collection("posts")
                    .document(postID)
                    .collection("likes")
                    .document(FirebaseAuth.getInstance().getUid())
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            likeImage.setImageResource(R.drawable.like_not_favorite);
                        }
                    });
        }
    }


    public interface FriendTimelineAdapterListener {
        void friendPostSelected(String authorID, String postID);
    }
}