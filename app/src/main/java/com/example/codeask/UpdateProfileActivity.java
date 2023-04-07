package com.example.codeask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
//import com.example.codeask.Activity.Model.User;
import com.example.codeask.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import com.agomi.codeasks.R;
public class UpdateProfileActivity extends AppCompatActivity {
   FirebaseUser firebaseUser;
    DatabaseReference reference;
    CircleImageView image;
    EditText name,email,mobile,dob,organization,profession,username,bio,gender;
    Button update;
//    ImageView upload;
    ArrayList<String>link=new ArrayList<>();
    TextView resumelink;
    Button button;
    Uri imageuri = null;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
       // firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getName());
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                mobile.setText(user.getMobile());
                dob.setText(user.getDob());
                gender.setText(user.getGender());
                organization.setText(user.getOrganization());
                profession.setText(user.getProfession());
                bio.setText(user.getBio());
                if(user.getImageURL().equals("default"))
                    image.setImageResource(R.drawable.krishna);
                else
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        image=findViewById(R.id.profileimage);
        name=findViewById(R.id.profilename);
        email=findViewById(R.id.profileemail);
        mobile=findViewById(R.id.profilephone);
        dob=findViewById(R.id.profiledob);
        resumelink=findViewById(R.id.resumelink);
        gender=findViewById(R.id.profilegender);
        organization=findViewById(R.id.profileorg);
        profession=findViewById(R.id.profileprofession);
        update=findViewById(R.id.updateprofile);
        username=findViewById(R.id.profileusername);
        bio=findViewById(R.id.profilebio);
        button=findViewById(R.id.uploadresume);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
         firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                link.clear();
                User user = snapshot.getValue(User.class);
                assert user != null;
                if(!user.getResume().equals("None")) {
                    resumelink.setText(user.getResume());
                    link.add(user.getResume());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        button.setOnClickListener(v -> uploadresume());
        update.setOnClickListener(v -> updateProfile(name.getText().toString(),dob.getText().toString(),organization.getText().toString(),profession.getText().toString(),bio.getText().toString(),gender.getText().toString()));
    }

    private void updateProfile(String name, String dob, String org, String profe, String bio,String gender) {
        String uid=firebaseUser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(uid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("bio",bio);
        hashMap.put("dob",dob);
        hashMap.put("gender",gender);
        hashMap.put("profession",profe);
        hashMap.put("organization",org);
        reference.updateChildren(hashMap);
        Toast.makeText(UpdateProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void uploadresume(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // We will be redirected to choose pdf
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }
    ProgressDialog dialog;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            assert data != null;
            imageuri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Resumes");
            //final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + "pdf");
            Toast.makeText(UpdateProfileActivity.this, imageuri.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(timestamp + "." + "pdf");
            Toast.makeText(UpdateProfileActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Uri uri = task.getResult();
                    String myurl;
                    myurl = uri.toString();
                    resumelink.setText(myurl);
                    String uid=firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("resume", myurl);
                    reference.updateChildren(map);
                    Toast.makeText(UpdateProfileActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UpdateProfileActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void finishthis(View view) {
        finish();
    }
    public void openresume(View view) {

        String url = link.get(0);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        }
}