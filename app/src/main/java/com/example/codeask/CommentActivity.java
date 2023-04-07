package com.example.codeask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.codeask.Adapter.CommentAdapter;
import com.example.codeask.Model.Comment;
import com.example.codeask.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.agomi.codeasks.R;

public class CommentActivity extends AppCompatActivity {
    private CommentAdapter postAdapter;
    private List<Comment> postList;
    SwipeRefreshLayout swipeRefreshLayout;
    String postid,publisherid;
    EditText commentedt;
    ImageView commentbtn;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        RecyclerView recyclerView = findViewById(R.id.recycler3);
        commentbtn=findViewById(R.id.postcommentbtn);
        commentedt=findViewById(R.id.postcommentedt);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
               readComment();
            }
        });
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager layout= new LinearLayoutManager(this);
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        postList=new ArrayList<>();
        postAdapter=new CommentAdapter(this,postList);
        recyclerView.setAdapter(postAdapter);
        Intent it=getIntent();
        postid=it.getStringExtra("postid");
        publisherid=it.getStringExtra("publishid");
        getImage();
        readComment();
        commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentedt.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this,"You cannot send empty comment",Toast.LENGTH_SHORT).show();
                }
                else{
                    addComment();
                }
            }
        });
    }

    private void readComment(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment=dataSnapshot.getValue(Comment.class);
                    postList.add(comment);
                }
                postAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addComment() {
    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("comment",commentedt.getText().toString());
        hashMap.put("publisher",uid);
        reference.push().setValue(hashMap);
        commentedt.setText("");
    }

    private void getImage(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        ImageView profile =findViewById(R.id.prfile);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void finishthis(View view) {
        finish();
    }
}