package com.example.codeask;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import com.agomi.codeasks.R;
public class PostActivity extends AppCompatActivity {
    Uri imageuri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    ImageView image;
    EditText description, location;
    Button post;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        image = findViewById(R.id.upimage);
        description = findViewById(R.id.description);
        post = findViewById(R.id.post);
        location = findViewById(R.id.location);
        relativeLayout=findViewById(R.id.l1);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            NotificationChannel channel=new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager=getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        CropImage.activity().setAspectRatio(1, 1).start(PostActivity.this);

    }

    private void uploadImage() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();
        if (imageuri != null) {
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
            uploadTask = filereference.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDateTime now = LocalDateTime.now();
                        String date= dtf.format(now);
                        Uri downloaduri = task.getResult();
                        myUri = downloaduri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();
                        addnotifi(uid,postid);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postdate", date);
                        hashMap.put("postimage", myUri);
                        hashMap.put("location", location.getText().toString());
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", uid);
                        assert postid != null;
                        reference.child(postid).setValue(hashMap);
                        progressDialog.dismiss();
                        makesnack("Post created successfully",relativeLayout);
                      // notifyall();
                     //   FirebaseMessaging.getInstance().subscribeToTopic("notification");
                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultcode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            imageuri = result.getUri();
            image.setImageURI(imageuri);
        } else {
            Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    private void addnotifi(String userid,String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String ,Object>hashMap=new HashMap<>();
        hashMap.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text","New post added");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }
    public void finishthis(View view) {
        finish();
    }

    private void makesnack(String s, RelativeLayout layout){
        Snackbar snackbar = Snackbar.make(layout, "Message is deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {                            }
                        });
        snackbar.show();
    }

}