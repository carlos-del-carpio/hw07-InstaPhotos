package com.example.instaphotos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String TAG = "Carlos";
    ArrayList<Comment> comments;


    public CommentAdapter (ArrayList<Comment> comments) {
        this.comments = comments;
    }


    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        CommentAdapter.CommentViewHolder commentViewHolder = new CommentAdapter.CommentViewHolder(view);


        return commentViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentText.setText(comment.getComment());
        holder.authorID = comment.getAuthorID();
        holder.postID = comment.getPostID();
        holder.commentID = comment.getCommentID();
        holder.getAuthorName();
        holder.commentDetails.setText(holder.commentDetails + " on " + comment.getDateCreated());


        if (!comment.getAuthorID().equals(FirebaseAuth.getInstance().getUid())) {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        TextView commentDetails;
        ImageView deleteButton;
        String authorID;
        String postID;
        String commentID;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentText = itemView.findViewById(R.id.commentText);
            commentDetails = itemView.findViewById(R.id.commentDetails);
            deleteButton = itemView.findViewById(R.id.deleteComment);


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteComment();
                }
            });
        }


        void getAuthorName() {
            database.collection("users")
                    .document(authorID)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            commentDetails.setText(value.get("name").toString());
                        }
                    });
        }


        void deleteComment() {
            database.collection("users")
                    .document(authorID)
                    .collection("posts")
                    .document(postID)
                    .collection("comments")
                    .document(commentID)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
    }
}
