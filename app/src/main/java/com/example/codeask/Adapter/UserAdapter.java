package com.example.codeask.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.codeask.MessageActivity;
import com.example.codeask.Model.Chat;
import com.example.codeask.Model.User;
//import com.example.codeask.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.agomi.codeasks.R;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context context;
    private final List<User> mUsers;
    private final boolean isChat;
    String thelastmessage;
    public UserAdapter(Context context,List<User>mUsers,boolean isChat){
        this.context=context;
        this.mUsers=mUsers;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User user= mUsers.get(position);
      holder.username.setText(user.getUsername());
      if(user.getImageURL().equals("default")){
          holder.profileimg.setImageResource(R.mipmap.ic_launcher);
      }
      else{
          Glide.with(context).load(user.getImageURL()).into(holder.profileimg);
      }
      if(isChat){
          lastMessage(user.getId(),holder.last_msg);
      }
      else{
          holder.last_msg.setVisibility(View.GONE);
      }
      if(isChat){
          if(user.getStatus().equals("online")){
              holder.img_on.setVisibility(View.VISIBLE);
              holder.img_off.setVisibility(View.GONE);
          }
          else{
              holder.img_on.setVisibility(View.GONE);
              holder.img_off.setVisibility(View.VISIBLE);
          }
      }
      else{
          holder.img_on.setVisibility(View.GONE);
          holder.img_off.setVisibility(View.GONE);
      }
      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent it =new Intent(context, MessageActivity.class);
              it.putExtra("userid",user.getId());
              context.startActivity(it);
          }
      });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public CircleImageView profileimg;
        private ImageView img_on,img_off;
        private TextView last_msg;
        public ViewHolder(View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.name_user);
            profileimg=itemView.findViewById(R.id.profile_user);
            img_off=itemView.findViewById(R.id.img_off);
            img_on=itemView.findViewById(R.id.img_on);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }
    private void lastMessage(final String userid,final TextView last_msg){
        thelastmessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sm:snapshot.getChildren()){
                    Chat chat=sm.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                      chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                        thelastmessage=chat.getMessage();
                }
             switch (thelastmessage){
                 case "default":
                     last_msg.setText("No message");
                     break;
                 default:
                     last_msg.setText(thelastmessage);
                     break;
             }
                thelastmessage="default";
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
