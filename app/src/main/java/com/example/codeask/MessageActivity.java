package com.example.codeask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codeask.Adapter.MessageAdapter;
import com.example.codeask.Fragments.APIService;
import com.example.codeask.Model.Chat;
import com.example.codeask.Model.User;
import com.example.codeask.Notifications.Client;
import com.example.codeask.Notifications.Data;
import com.example.codeask.Notifications.MyResponse;
import com.example.codeask.Notifications.Sender;
import com.example.codeask.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.agomi.codeasks.R;
public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_img;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent it;
    ImageButton btn_send;
    EditText txt_send;
MessageAdapter messageAdapter;
APIService apiService;
RecyclerView recyclerView;
List<Chat>mChats;
boolean notify=false;
ValueEventListener seenListener;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar=findViewById(R.id.toolbar);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        apiService= Client.getClient("https://fcm.googleapis.com").create(APIService.class);
        recyclerView=findViewById(R.id.message_recyler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_img=findViewById(R.id.profile_img);
        username=findViewById(R.id.username);
        txt_send=findViewById(R.id.txt_send);
        btn_send=findViewById(R.id.btn_send);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        it=getIntent();
        String userid=it.getStringExtra("userid");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
                }
                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userid);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                String msg=txt_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendmessage(firebaseUser.getUid(),userid,msg);
                }
                txt_send.setText("");
            }
        });
    }
    private void seenMessage(String userid){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sm:snapshot.getChildren()){
                    Chat chat=sm.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object>mp=new HashMap<>();
                        mp.put("isseen",true);
                        sm.getRef().updateChildren(mp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void sendmessage(String sender ,String reciever,String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();;
        HashMap<String,Object>mp=new HashMap<>();
        mp.put("sender",sender);
        mp.put("receiver",reciever);
        mp.put("message",message);
        mp.put("isseen",false);
        reference.child("Chats").push().setValue(mp);
        String userid=it.getStringExtra("userid");
        final DatabaseReference chatref=FirebaseDatabase.getInstance().
                getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        chatref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    chatref.child("id").setValue(userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(notify){
                    assert user != null;
                    sendNotification(reciever, user.getUsername(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void sendNotification(String reciver,final String username,final String message){
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(reciver);
        String userid=it.getStringExtra("userid");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sm:dataSnapshot.getChildren()){
                    Token token=sm.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);
                    assert token != null;
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        assert response.body() != null;
                                        if(response.body().success!=1){
                                          //  Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readMessage(final String myid,final String userid,final  String imgurl){
        mChats=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();;
                for(DataSnapshot sm:snapshot.getChildren()){
                    Chat chat=sm.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                      chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChats.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this,mChats,imgurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object>mp=new HashMap<>();
        mp.put("status",status);
        reference.updateChildren(mp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);

        status("offline");
    }
}