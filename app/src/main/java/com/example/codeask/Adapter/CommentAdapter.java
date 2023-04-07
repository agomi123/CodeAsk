package com.example.codeask.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agomi.codeasks.R;
import com.bumptech.glide.Glide;
import com.example.codeask.Model.Comment;
import com.example.codeask.Model.User;
//import com.example.codeask.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final Context context;
    private final List<Comment> commentList;


    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.circleImageView, holder.username, comment.getPublisher());
//        holder.comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(context, MainActivity.class);
//                it.putExtra("publisherid", comment.getPublisher());
//                context.startActivity(it);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernamecomm);
            comment = itemView.findViewById(R.id.usercomment);
            circleImageView = itemView.findViewById(R.id.commimg);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if(user.getImageURL().equals("default"))
                    imageView.setImageResource(R.drawable.krishna);
                else
                Glide.with(context).load(user.getImageURL()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
