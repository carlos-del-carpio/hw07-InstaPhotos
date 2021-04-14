/**Assignment: HW07
 *File name: HW07
 *Student: Carlos Del Carpio
 */


package com.example.instaphotos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class PostWithComments extends Fragment {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String postID;
    String creatorID;
    ImageView imagePost;
    ImageView likeButton;
    Button postButton;
    TextView date;
    TextView likesCounter;
    TextView commentsCounter;
    EditText commentInput;
    ArrayList<Comment> comments;
    ArrayList<String> likes;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;
    CommentAdapter adapter;
    int likeStatus;


    public PostWithComments(String creatorID, String postID) {
        this.creatorID = creatorID;
        this.postID = postID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_with_comments, container, false);
        getActivity().setTitle(R.string.post);


        imagePost = view.findViewById(R.id.postImage);
        likeButton = view.findViewById(R.id.postLike);
        postButton = view.findViewById(R.id.postButton);
        date = view.findViewById(R.id.postCreatedDate);
        likesCounter = view.findViewById(R.id.postLikeCount);
        commentsCounter = view.findViewById(R.id.postCommentCount);
        commentInput = view.findViewById(R.id.commentInput);
        comments = new ArrayList<>();
        likes = new ArrayList<>();
        recycler = view.findViewById(R.id.postRecycler);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new CommentAdapter(comments);


        getAllData();


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(likeStatus);
            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputValidation()) {
                    postComment(commentInput.getText().toString());
                    commentInput.setText("");
                }
            }
        });


        return view;
    }


    void getAllData() {
        downloadPicture();
        getDateCreated();
        getCommentCount();
        getLikesCount();
    }


    void downloadPicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference()
                                             .child(creatorID + "/" + postID);


        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).into(imagePost);
            }
        });
    }


    void getDateCreated() {
        database.collection("users")
                .document(creatorID)
                .collection("posts")
                .document(postID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable  DocumentSnapshot value, FirebaseFirestoreException error) {
                        date.setText(value.getDate("dateCreated").toString());
                    }
                });
    }


    void getCommentCount() {
        database.collection("users")
                .document(creatorID)
                .collection("posts")
                .document(postID)
                .collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        comments.clear();


                        for (DocumentSnapshot document : value) {
                            comments.add(createNewComment(document));
                        }

                        adapter.notifyDataSetChanged();
                        recycler.setAdapter(adapter);
                        commentsCounter.setText(comments.size() + " Comment(s)");
                    }
                });
    }


    void getLikesCount() {
        database.collection("users")
                .document(creatorID)
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

                        likesCounter.setText(likes.size() + " Like(s)");


                        if (likes.contains(FirebaseAuth.getInstance().getUid())) {
                            likeButton.setImageResource(R.drawable.like_favorite);
                            likeStatus = 1;
                        } else {
                            likeButton.setImageResource(R.drawable.like_not_favorite);
                            likeStatus = 0;
                        }
                    }
                });
    }


    Comment createNewComment(DocumentSnapshot document) {
        String commentID = document.getId();
        String comment = document.get("comment").toString();
        String authorID = document.get("authorID").toString();
        String postID = document.get("postID").toString();
        Date date = document.getDate("commentDate");


        Comment newComment = new Comment(commentID, comment, authorID, postID, date);


        return newComment;
    }


    void toggleLike(int likeStatus) {
        if (likeStatus == 0) {
            addLike();
            this.likeStatus = 1;
        } else if (likeStatus == 1) {
            removeLike();
            this.likeStatus = 0;
        }
    }


    void addLike() {
        HashMap<String, Object> userLiked = new HashMap<>();

        userLiked.put("userID", FirebaseAuth.getInstance().getUid());


        database.collection("users")
                .document(creatorID)
                .collection("posts")
                .document(postID)
                .collection("likes")
                .document(FirebaseAuth.getInstance().getUid())
                .set(userLiked)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        likeButton.setImageResource(R.drawable.like_favorite);
                    }
                });
    }


    void removeLike() {
        database.collection("users")
                .document(creatorID)
                .collection("posts")
                .document(postID)
                .collection("likes")
                .document(FirebaseAuth.getInstance().getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        likeButton.setImageResource(R.drawable.like_not_favorite);
                    }
                });
    }


    void postComment(String newComment) {
        HashMap<String, Object> comment = new HashMap<>();

        comment.put("comment", newComment);
        comment.put("authorID", FirebaseAuth.getInstance().getUid());
        comment.put("postID", postID);
        comment.put("commentDate", Timestamp.now());


        database.collection("users")
                .document(creatorID)
                .collection("posts")
                .document(postID)
                .collection("comments")
                .add(comment)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });
    }


    Boolean inputValidation() {
        String alertMessage = "";

        if (commentInput.getText().toString().isEmpty()) {
            alertMessage += getString(R.string.empty_comment) + "\n";
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