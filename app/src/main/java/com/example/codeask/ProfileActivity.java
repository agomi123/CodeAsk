package com.example.codeask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codeask.Model.Post;
import com.example.codeask.Model.Query;
import com.example.codeask.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import com.agomi.codeasks.R;
public class ProfileActivity extends AppCompatActivity {
    LinearLayout  openprofile, openasked, openfav,q1,q2,q3;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    TextView adminname, adminusername, noofopost, noofquery, nooffollow;
    ImageView adminimage;
    private Uri imageuri;
    private StorageTask uploadtask;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onResume() {
        super.onResume();
        readPosts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView openmenu = findViewById(R.id.openmenu);
        adminimage = findViewById(R.id.adminimg);
        adminname = findViewById(R.id.adminname);
//        openfriend = findViewById(R.id.openfriend);
        openasked = findViewById(R.id.openfriendsd);
        openfav = findViewById(R.id.openfav);;
        nooffollow = findViewById(R.id.nooffollowing);
        q1=findViewById(R.id.q1);
        q2=findViewById(R.id.q2);
        q3=findViewById(R.id.q3);
        noofopost = findViewById(R.id.noofpost);
        noofquery = findViewById(R.id.noofaskquet);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,AskedQuestionActivity.class));
            }
        });
        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyPostActivity.class));
            }
        });
//        q3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProfileActivity.this,ShowUsersActivity.class));
//            }
//        });
        readPosts();

//        openfriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProfileActivity.this, ShowUsersActivity.class));
//            }
//        });
        openfav.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FavouriteActivity.class)));
        openasked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AskedQuestionActivity.class));
            }
        });
        openprofile = findViewById(R.id.openedtprofile);
        openprofile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class)));
        adminusername = findViewById(R.id.adminusername);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(getContext(),firebaseUser.getUid(),Toast.LENGTH_LONG).show();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                adminname.setText(user.getName());
                adminusername.setText("username: " + user.getUsername());
                if (user.getImageURL().equals("default"))
                    adminimage.setImageResource(R.drawable.krishna);
                else
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(adminimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        openmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.upload:
                                openImage();
                                return true;
                            case R.id.changepass:
                                startActivity(new Intent(ProfileActivity.this, ForgetPasswordActivity.class));
                                return true;
                            case R.id.deleteacc:
                                startActivity(new Intent(ProfileActivity.this, DeleteAccount.class));
                                return true;
                            case R.id.invite:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Share The App And Invite Your friends to join with you \n https://play.google.com/store/apps/details?id=com.agomi.codeASK");
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                return true;
                            case R.id.logout:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setMessage("Do you want to Logout ?");
                                builder.setTitle("Alert !");
                                builder.setCancelable(true);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userid", "null");
                                        editor.apply();
                                        startActivity(new Intent(ProfileActivity.this, RegistrationActivity.class));
                                        getFragmentManager().popBackStack();
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
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
            }
        });
    }

    private void readPosts() {
        final int[] c = {0};
        final int[] b = {0};
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        DatabaseReference rreference = FirebaseDatabase.getInstance().getReference("Follow").child(uid).child("Followers");
        DatabaseReference rrreference = FirebaseDatabase.getInstance().getReference("Query");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // noofopost.setText( snapshot.getChildrenCount());
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(uid)) c[0]++;
                }
                noofopost.setText(String.valueOf(c[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        rreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nooffollow.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        rrreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Query query = snapshot1.getValue(Query.class);
                    // Log.d("tgn", snapshot1.getKey());
                    assert query != null;
                    if (query.getPublisher().equals(uid)) {
                        b[0]++;
                    }
                }
                noofquery.setText(String.valueOf(b[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void upoadImage() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        if (imageuri != null) {
            ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setMessage("Uploading");
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference("uploads");
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
            uploadtask = filereference.putFile(imageuri);
            uploadtask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return filereference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloaduri = (Uri) task.getResult();
                    String muri = downloaduri.toString();
                    Glide.with(getApplicationContext()).load(muri).into(adminimage);
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", muri);
                    reference.updateChildren(map);
                    progressDialog.cancel();
                    // pd.dismiss();
                } else {
                    Toast.makeText(ProfileActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    // pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //pd.dismiss();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "no image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageuri = data.getData();
            if (uploadtask != null && uploadtask.isInProgress()) {
                Toast.makeText(ProfileActivity.this, "upload in progress", Toast.LENGTH_SHORT).show();

            } else {
                upoadImage();
            }
        }

    }

}