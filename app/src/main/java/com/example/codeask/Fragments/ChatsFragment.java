package com.example.codeask.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.codeask.Adapter.UserAdapter;
import com.example.codeask.Model.Chat;
import com.example.codeask.Model.Chatlist;
import com.example.codeask.Model.User;
import com.example.codeask.Notifications.Data;
import com.example.codeask.Notifications.Token;
//import com.example.codeask.R/;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import com.agomi.codeasks.R;
public class ChatsFragment extends Fragment {
   private RecyclerView recyclerView;
   private UserAdapter userAdapter;
   private List<User>mUsers;
   FirebaseUser fuser;
   DatabaseReference reference;
   private List<Chatlist>userlist;
  private View viewer;
    private RecyclerView recyclerView2;
    private UserAdapter userAdapter2;
    private List<User>mUsers2;
    EditText searchuser;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_chats, container, false);
         recyclerView=view.findViewById(R.id.message_recyler);
         recyclerView.setHasFixedSize(true);
        LinearLayoutManager ll=new LinearLayoutManager(getContext());
        LinearLayoutManager ll2=new LinearLayoutManager(getContext());
         recyclerView.setLayoutManager(ll);
         fuser= FirebaseAuth.getInstance().getCurrentUser();
         userlist=new ArrayList<>();
         viewer=view.findViewById(R.id.separater);

        recyclerView2=view.findViewById(R.id.recyleruser);
        searchuser=view.findViewById(R.id.search_user);
        recyclerView2.setHasFixedSize(true);
       // LinearLayoutManager ll=new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(ll2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ll.getOrientation());
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView2.getContext(),
                ll2.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_divider));
        dividerItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView2.addItemDecoration(dividerItemDecoration2);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mUsers2=new ArrayList<>();
        searchuser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readUsers();

         reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 userlist.clear();
                 for(DataSnapshot sn:snapshot.getChildren()){
                     Chatlist chatlist=sn.getValue(Chatlist.class);
                     userlist.add(chatlist);
                 }
                 chatList();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
//         reference= FirebaseDatabase.getInstance().getReference("Chats");
//         reference.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 userlist.clear();
//                 for(DataSnapshot sm:snapshot.getChildren()){
//                     Chat chat=sm.getValue(Chat.class);
//                     if(chat.getSender().equals(fuser.getUid()))
//                         userlist.add(chat.getReceiver());
//                     if(chat.getReceiver().equals(fuser.getUid()))
//                         userlist.add(chat.getSender());
//                 }
//                 readChats();
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError error) {
//
//             }
//         });
         updateToken(FirebaseInstanceId.getInstance().getToken());
         return view;
    }
    private void chatList(){
        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();;
                for(DataSnapshot sn:snapshot.getChildren()){
                    User user=sn.getValue(User.class);
                    for(Chatlist chatlist:userlist){
                        assert user != null;
                        if(user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
                if(mUsers.size()==0){
                    viewer.setVisibility(View.GONE);
                }
                else{
                    viewer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
//    private void readChats(){
//        mUsers=new ArrayList<>();
//        reference=FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();;
//                for(DataSnapshot sm:snapshot.getChildren()){
//                    User user=sm.getValue(User.class);
//                    for(Chatlist id:userlist){
//                        if(user.getId().equals(id.getId())){
//                            if(mUsers.size()!=0){
//                                for(User user1:mUsers){
//                                    if(!user.getId().equals(user1.getId()))
//                                        mUsers.add(user);
//                                }
//                            }
//                            else
//                                mUsers.add(user);
//                        }
//                    }
//                }
//                userAdapter=new UserAdapter(getContext(),mUsers,true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void searchUsers(String s) {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s+"u\f8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers2.clear();
                for(DataSnapshot sm:snapshot.getChildren()){
                    User user=sm.getValue(User.class);
                    assert user!=null;
                    assert firebaseUser!=null;
                    if(!user.getId().equals(firebaseUser.getUid()))
                        mUsers2.add(user);
                }
                userAdapter2=new UserAdapter(getContext(),mUsers2,false);
                recyclerView.setAdapter(userAdapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers(){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (searchuser.getText().toString().equals("")) {
                    mUsers2.clear();
                    for (DataSnapshot sm : snapshot.getChildren()) {
                        User user = sm.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers2.add(user);
                        }
                    }
                    userAdapter2 = new UserAdapter(getContext(), mUsers2, false);
                    recyclerView2.setAdapter(userAdapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}