package com.example.codeask.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.codeask.Model.Query;
import com.example.codeask.Model.User;
import com.example.codeask.QueryAnswer;
//import com.example.codeask.R;
import com.agomi.codeasks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {
    public Context context;
    public List<Query> postList;

    public QueryAdapter(Context context, List<Query> postList) {
        this.context = context;
        this.postList = postList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.query_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        Query post = postList.get(position);
        holder.dateq.setText(post.getQuerydate());
        holder.query.setText(post.getQuery());
        holder.upvotecount.setText(post.getUpvote());
        holder.downvotecount.setText(post.getDownvote());
        isupvote(post.getQueryid(), holder.upvoteimg);
        nrlikes(holder.upvotecount, post.getQueryid());
        isdownvote(post.getQueryid(), holder.downvote);
        nrdownvote(holder.downvotecount, post.getQueryid());
        getComments(post.getQueryid(),holder.totalans);
        publisherInfo(holder.circleImageView, holder.publisher, post.getPublisher());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(uid.equals(post.getPublisher())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Sure to delete query?");
                    builder.setTitle("CodeAsk Alert !");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletepost(post.getQueryid());
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                return true;
            }
        });
        holder.upvoteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //like =upvote
                //Toast.makeText(context, "ffff", Toast.LENGTH_SHORT).show();
                if(holder.upvoteimg.getTag().equals("upvote")){
                    FirebaseDatabase.getInstance().getReference().child("upvote").child(post.getQueryid()).child(uid).setValue(true);
                    if(holder.upvoteimg.getTag().equals("downvoted")){
                        FirebaseDatabase.getInstance().getReference().child("downvote").child(post.getQueryid()).child(uid).removeValue();
                    }
                }else{
                    FirebaseDatabase.getInstance().getReference().child("upvote").child(post.getQueryid()).child(uid).removeValue();
                    if(holder.upvoteimg.getTag().equals("downvote")){
                        FirebaseDatabase.getInstance().getReference().child("downvote").child(post.getQueryid()).child(uid).setValue(true);
                    }
                }
            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unlike =downvote
                //Toast.makeText(context, "ffff", Toast.LENGTH_SHORT).show();
                if(holder.downvote.getTag().equals("downvote")){
                    FirebaseDatabase.getInstance().getReference().child("downvote").child(post.getQueryid()).child(uid).setValue(true);
                    if(holder.upvoteimg.getTag().equals("upvoted")){
                        FirebaseDatabase.getInstance().getReference().child("upvote").child(post.getQueryid()).child(uid).removeValue();
                    }
                }else{
                    FirebaseDatabase.getInstance().getReference().child("downvote").child(post.getQueryid()).child(uid).removeValue();
                    if(holder.upvoteimg.getTag().equals("upvote")){
                        FirebaseDatabase.getInstance().getReference().child("upvote").child(post.getQueryid()).child(uid).setValue(true);
                    }
                }
            }
        });
        holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //comment
                Intent it=new Intent(context, QueryAnswer.class);
                it.putExtra("queryid",post.getQueryid());
                it.putExtra("publishid",post.getPublisher());
                context.startActivity(it);
            }
        });
        holder.totalans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show all comments
                Intent it=new Intent(context, QueryAnswer.class);
                it.putExtra("queryid",post.getQueryid());
                it.putExtra("publishid",post.getPublisher());
                context.startActivity(it);
            }
        });
    }

    private void deletepost(String postid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Query");
        // Query applesQuery = ref.orderByChild("postid").equalTo(postid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Query postd = snapshot1.getValue(Query.class);
                    assert postd != null;
                    String po = postd.getQueryid();
                    if(po.equals(postid)) {
                        snapshot1.getRef().removeValue();

                        Toast.makeText(context,"Post Deleted !",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
          CircleImageView circleImageView;
          TextView query,publisher,dateq,upvotecount,downvotecount,totalans;
          ImageView upvoteimg,downvote,answer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.queryimg);
            query=itemView.findViewById(R.id.query);
            publisher=itemView.findViewById(R.id.queryusername);
            dateq=itemView.findViewById(R.id.querydate);
            upvotecount=itemView.findViewById(R.id.upvotecount);
            downvotecount=itemView.findViewById(R.id.downvotecount);
            upvoteimg=itemView.findViewById(R.id.upvoteimg);
            downvote=itemView.findViewById(R.id.downvoteimg);
            answer=itemView.findViewById(R.id.answerquery);
            totalans=itemView.findViewById(R.id.totalanswers);

        }
    }
    private void publisherInfo(final ImageView profile, final TextView username, final String userid) {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default"))
                    profile.setImageResource(R.drawable.krishna);
                else
                    Glide.with(context).load(user.getImageURL()).into(profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isupvote(String postid,final  ImageView imageView){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("upvote").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    imageView.setImageResource(R.drawable.upvote);
                    imageView.setTag("upvoted");
                }
                else{
                    imageView.setImageResource(R.drawable.karoupvote);
                    imageView.setTag("upvote");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nrlikes(final TextView likes, String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("upvote").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isdownvote(String postid,final  ImageView imageView){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("downvote").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    imageView.setImageResource(R.drawable.downvote);
                    imageView.setTag("downvoted");
                }
                else{
                    imageView.setImageResource(R.drawable.karodownvote);
                    imageView.setTag("downvote");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nrdownvote(final TextView likes, String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("downvote").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getComments(String postid,final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Answers").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("view All "+snapshot.getChildrenCount()+" answers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}