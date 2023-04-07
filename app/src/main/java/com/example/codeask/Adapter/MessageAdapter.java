package com.example.codeask.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agomi.codeasks.R;
import com.bumptech.glide.Glide;
import com.example.codeask.MessageActivity;
import com.example.codeask.Model.Chat;
import com.example.codeask.Model.User;
//import com.example.codeask.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context context;
    private final List<Chat> mChats;
    private final String imgurl;
    FirebaseUser firebaseUser;
    public static final int MSG_TYPE_LEFT=0,MSG_TYPE_RIGHT=1;
    public MessageAdapter(Context context,List<Chat>mUsers,String imgurl){
        this.context=context;
        this.mChats=mUsers;
        this.imgurl=imgurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat user= mChats.get(position);
        holder.show_message.setText(user.getMessage());
        if(imgurl.equals("default")){
         holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(context).load(imgurl).into(holder.profile_img);
        }
       if(position==mChats.size()-1){
           if(user.isIsseen())
               holder.txt_seen.setText("seen");
           else
               holder.txt_seen.setText("Delivered");
       }
       else
           holder.txt_seen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public CircleImageView profile_img;
        public TextView txt_seen;
        public ViewHolder(View itemView){
            super(itemView);
            profile_img=itemView.findViewById(R.id.profile_img);
           show_message=itemView.findViewById(R.id.show_message);
           txt_seen=itemView.findViewById(R.id.seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }

        return MSG_TYPE_LEFT;
    }
}
