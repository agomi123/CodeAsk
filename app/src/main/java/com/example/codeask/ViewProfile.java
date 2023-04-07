package com.example.codeask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.agomi.codeasks.R;
import com.bumptech.glide.Glide;
//import com.example.codeask.Activity.Model.User;
import com.example.codeask.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {
CircleImageView circleImageView;
Intent it;
TextView username,name,email,organization;
static String resumelink;
Button resumes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        circleImageView=findViewById(R.id.imgp);
        username=findViewById(R.id.usp);
        name=findViewById(R.id.namep);
        email=findViewById(R.id.emailp);
        resumes=findViewById(R.id.resumee);
        organization=findViewById(R.id.orgp);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        it=getIntent();
        String userid=it.getStringExtra("userid");
       DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                name.setText(user.getName());
                organization.setText(user.getOrganization());
                resumelink=user.getResume();
                email.setText(user.getEmail());
                if (user.getImageURL().equals("default")) {
                    circleImageView.setImageResource(R.drawable.krishna);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(circleImageView);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     resumes.setOnClickListener(v -> {
         String url = resumelink;
         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
         startActivity(intent);
     });
    }

    public void backplease(View view) {
        finish();
    }

}