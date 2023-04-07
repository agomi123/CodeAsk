package com.example.codeask;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codeask.Adapter.QueryAdapter;
import com.example.codeask.Model.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.agomi.codeasks.R;
public class AskedQuestionActivity extends AppCompatActivity {
    ImageView fsearch;
    private QueryAdapter postAdapter;
    private List<Query> postList;
    EditText searchquery;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asked_question);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        fsearch = findViewById(R.id.oppoe);
        searchquery = findViewById(R.id.searchq);
        RecyclerView recyclerView = findViewById(R.id.recycler298);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
               readQuery();
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        postList = new ArrayList<>();
        postAdapter = new QueryAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);
       // firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        readQuery();
        searchquery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());
                if(s.length()==0)
                    readQuery();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fsearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //search query
                searchUser(searchquery.getText().toString());
            }
        });

    }
    private void searchUser(String s){
        com.google.firebase.database.Query query= FirebaseDatabase.getInstance().getReference("Query").orderByChild("query").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Query user=dataSnapshot.getValue(Query.class);
                    postList.add(user);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readQuery() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Query");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Query post = dataSnapshot.getValue(Query.class);
                    assert post != null;
                    if(post.getPublisher().equals(uid))
                      postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}