package com.example.codeask.Adapter;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.Glide;
//import com.agomi.codeask.BuildConfig;
import com.example.codeask.CommentActivity;
import com.example.codeask.Model.Post;
import com.example.codeask.Model.User;
//import com.example.codeask.R;
import com.example.codeask.ViewProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import com.agomi.codeasks.R;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context context;
    public List<Post> postList;

    boolean flag=false;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;

    }
 public PostAdapter(Context context, List<Post> postList, boolean fl) {
        this.context = context;
        this.postList = postList;

        this.flag=fl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        Post post = postList.get(position);
        holder.moreoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.savepost:
                                savepost(holder, post);
                                return true;
                            case R.id.sharepost:
                                sharepost(holder.postimg, post.getDescription());
                                return true;
                            case R.id.reportuser:
                                reportpost(post);
                                return true;
                            case R.id.blockuser:
                                bloackpost(post);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.postitem_menu);
//                popupMenu.setForceShowIcon(true);
                popupMenu.show();
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharepost(holder.postimg,post.getDescription());
            }
        });
        holder.imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ViewProfile.class);
                it.putExtra("userid", post.getPublisher());
                context.startActivity(it);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (uid.equals(post.getPublisher())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Sure to Delete Post?");
                    builder.setTitle("CodeAsk Alert !");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(flag){
                                deletepost2(post.getPostid());
                            }
                            else {
                                deletepost(post.getPostid());
                                notifyDataSetChanged();
                            }
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
                return false;
            }
        });
        Glide.with(context).load(post.getPostimage()).into(holder.postimg);
        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        if (post.getLocation().equals("")) {
            holder.location.setVisibility(View.GONE);
        } else {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(post.getLocation());
        }

        publisherInfo(holder.imgprofile, holder.username, post.getPublisher());
        isLikes(post.getPostid(), holder.like);
        issaved(post.getPostid(), holder.savepost2);
        nrlikes(holder.likes, post.getPostid());
         getComments(post.getPostid(),holder.commentcount);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        holder.postdate.setText(post.getPostdate());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(uid).setValue(true);
                  //  addnotifi(uid, post.getPostid());
                    //likednoti();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(uid).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, CommentActivity.class);
                it.putExtra("postid", post.getPostid());
                it.putExtra("publishid", post.getPublisher());
                context.startActivity(it);
            }
        });

    }

    private void sharepost(ImageView postimg, String caption) {
        Drawable drawable=postimg.getDrawable();
        Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
        try{
            File file=new File(context.getApplicationContext().getExternalCacheDir(),File.separator+"postimage.jpg");
            FileOutputStream fout=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);
            fout.flush();
            fout.close();
            file.setReadable(true,false);
            final Intent it=new Intent(Intent.ACTION_SEND);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photouri= FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",file);
            it.putExtra(Intent.EXTRA_STREAM,photouri);
           // it.putExtra(Intent.EXTRA_SUBJECT,"Hey See this Post from CodeAsk-Come Together App.Download it Now From Google Play Store-\n https://play.google.com/store/apps/details?id=com.agomi.codeASK");
            it.putExtra(Intent.EXTRA_TEXT,caption+"\n\nHey See this Post from CodeAsk-Come Together App.Download it Now From Google Play Store-\\n https://play.google.com/store/apps/details?id=com.agomi.codeASK");
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            it.setType("image/jpg");
            context.startActivity(Intent.createChooser(it,"Share Post via"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reportpost(Post post) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        if(post.getPublisher().equals(uid)){
            Toast.makeText(context,"Can Not Report yourself",Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report").child(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", post.getPublisher());
        reference.push().setValue(hashMap);
        Toast.makeText(context,"User Reported",Toast.LENGTH_SHORT).show();
    }

    private void bloackpost(Post post) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        if(post.getPublisher().equals(uid)){
            Toast.makeText(context,"Can Not Block yourself",Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Block").child(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", post.getPublisher());
        reference.push().setValue(hashMap);
        Toast.makeText(context,"User Blocked",Toast.LENGTH_SHORT).show();
    }

    private void savepost(ViewHolder holder, Post post) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        if (holder.savepost2.getTag().equals("save")) {
            FirebaseDatabase.getInstance().getReference().child("Favourite").child(uid).child(post.getPostid()).setValue(true);
            // addnotifi(firebaseUser.getUid(),post.getPostid());
            //addNotification();
            holder.savepost2.setTag("saved");
        } else {
            FirebaseDatabase.getInstance().getReference().child("Favourite").child(uid).child(post.getPostid()).removeValue();
            holder.savepost2.setTag("save");
        }
        addtofavourite(post.getPostid());
        Toast.makeText(context,"Post Saved to Favourite",Toast.LENGTH_SHORT).show();
    }

    private void addtofavourite(String postid) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Favourite");
        String pos = reference.push().getKey();
        addnotifi(uid, postid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", postid);
        reference.child(postid).setValue(hashMap);
    }

    private void getComments(String postid, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgprofile, postimg, like, comment, moreoption, savepost2,share;
        public TextView username, likes, location, description, postdate,commentcount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgprofile = itemView.findViewById(R.id.publimage);
            postimg = itemView.findViewById(R.id.postimage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            savepost2 = itemView.findViewById(R.id.savepost12);
            username = itemView.findViewById(R.id.pubpublisher);
            likes = itemView.findViewById(R.id.likecount);
            description = itemView.findViewById(R.id.postdescr);
            location = itemView.findViewById(R.id.publocation);
            postdate = itemView.findViewById(R.id.postdate);
             share = itemView.findViewById(R.id.share);
             commentcount=itemView.findViewById(R.id.commentcount);
            moreoption = itemView.findViewById(R.id.moreoption);


        }
    }

    private void addnotifi(String userid, String postid) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", uid);
        hashMap.put("text", "like the post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
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

    private void isLikes(String postid, final ImageView imageView) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()) {
                    imageView.setImageResource(R.drawable.heart);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deletepost(String postid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Post postd = snapshot1.getValue(Post.class);
                    assert postd != null;
                    String po = postd.getPostid();
                    if (po.equals(postid)) {
                        snapshot1.getRef().removeValue();

                        Toast.makeText(context, "Post Deleted !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void deletepost2(String postid) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourite").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    String postd = snapshot1.getKey();
                    assert postd != null;
                    if (postd.equals(postid)) {
                        snapshot1.getRef().removeValue();
                        Toast.makeText(context, "Post Removed From Favourite !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void issaved(String postid, final ImageView imageView) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favourite").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.save);
                    imageView.setTag("save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void nrlikes(final TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void notifyall(){
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
//        FcmNotificationsSender fcmNotificationsSender =new FcmNotificationsSender("/topic/all","Post Sent Successfully",context,(Activity)context)
//    }
}