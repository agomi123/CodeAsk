package com.example.codeask.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.codeask.Adapter.PostAdapter;
import com.example.codeask.Adapter.UserAdapter;
import com.example.codeask.Model.Post;
import com.example.codeask.Model.User;
import com.example.codeask.PostActivity;
//import com.example.codeask.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import com.agomi.codeasks.R;
public class UserFragment extends Fragment {
//   private RecyclerView recyclerView;
//   private UserAdapter userAdapter;
//   private List<User>mUsers;
//   EditText searchuser;
ImageView floatingActionButton,notification;
    private PostAdapter postAdapter;
    private List<Post>postList;
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_user,container,false);
        floatingActionButton=view.findViewById(R.id.addpost);
        RecyclerView recyclerView = view.findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layout= new LinearLayoutManager(getContext());
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);

        recyclerView.setLayoutManager(layout);
        //Toast.makeText(getContext(),getkey(),Toast.LENGTH_LONG).show();
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout1);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                readPosts();
            }
        });
        postList=new ArrayList<>();
        notification=view.findViewById(R.id.notification);
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), NotificationActivity.class));
//            }
//        });
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PostActivity.class));
            }
        });
        readPosts();
//       recyclerView=view.findViewById(R.id.recyleruser);
//       searchuser=view.findViewById(R.id.search_user);
//       searchuser.addTextChangedListener(new TextWatcher() {
//           @Override
//           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//           }
//
//           @Override
//           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                searchUsers(charSequence.toString().toLowerCase());
//           }
//
//           @Override
//           public void afterTextChanged(Editable editable) {
//
//           }
//       });
//       recyclerView.setHasFixedSize(true);
//       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//       mUsers=new ArrayList<>();
//       readUsers();
       return view;
    }
    private void readPosts(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);
                    postList.add(post);

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//
//    private void searchUsers(String s) {
//        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s+"u\f8ff");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//                for(DataSnapshot sm:snapshot.getChildren()){
//                    User user=sm.getValue(User.class);
//                    assert user!=null;
//                    assert firebaseUser!=null;
//                    if(!user.getId().equals(firebaseUser.getUid()))
//                        mUsers.add(user);
//                }
//                userAdapter=new UserAdapter(getContext(),mUsers,false);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void readUsers(){
//        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (searchuser.getText().toString().equals("")) {
//                    mUsers.clear();
//                    for (DataSnapshot sm : snapshot.getChildren()) {
//                        User user = sm.getValue(User.class);
//                        assert user != null;
//                        assert firebaseUser != null;
//                        if (!user.getId().equals(firebaseUser.getUid())) {
//                            mUsers.add(user);
//                        }
//                    }
//                    userAdapter = new UserAdapter(getContext(), mUsers, false);
//                    recyclerView.setAdapter(userAdapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//

}