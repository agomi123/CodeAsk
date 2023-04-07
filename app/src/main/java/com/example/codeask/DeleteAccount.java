package com.example.codeask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.agomi.codeasks.R;
public class DeleteAccount extends AppCompatActivity {
    EditText email, password, reason;
    MaterialButton button;
    ProgressDialog progressDialog;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        email = findViewById(R.id.emailm);
        password = findViewById(R.id.passw);
        reason = findViewById(R.id.reason);
        button = findViewById(R.id.delrt);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        //uid = sharedPreferences.getString("userid", "null");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    email.setError("Field Required");
                    return;
                }
                if (password.getText().toString().equals("")) {
                    password.setError("Field Required");
                    return;
                }
                progressDialog = new ProgressDialog(DeleteAccount.this);
                progressDialog.setMessage("Deleting Account");
                progressDialog.show();
                deleteuser(email.getText().toString(), password.getText().toString(), reason.getText().toString());
            }
        });
    }

    private void deleteuser(String email, String password, String reason) {
        //Toast.makeText(DeleteAccount.this, "kkkkkk", Toast.LENGTH_LONG).show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        // Prompt the user to re-provide their sign-in credentials
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        if (user != null) {
            // Toast.makeText(DeleteAccount.this, "kkk", Toast.LENGTH_LONG).show();
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (delete(uid)) {
                                            progressDialog.cancel();
                                            SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("userid", "null");
                                            editor.apply();
                                            startActivity(new Intent(DeleteAccount.this, StartActivity.class));
                                            finish();
                                        }
                                    } else {
                                        //progressDialog.cancel();
                                        Toast.makeText(DeleteAccount.this, "Cannot Delete Try Later,", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
        } else {
            Toast.makeText(DeleteAccount.this, "Error", Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }
    }

    private boolean delete(String uid) {
        final boolean[] flag = {false};
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = dbref.child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(DeleteAccount.this, "Deleted User Successfully,", Toast.LENGTH_LONG).show();
                flag[0] = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteAccount.this, "Cannot Delete Try Later,", Toast.LENGTH_LONG).show();

            }
        });
        return flag[0];
    }


    public void openreport(View view) {
        dialog = new Dialog(DeleteAccount.this);
        dialog.setContentView(R.layout.repost_problem);
        ImageView close = dialog.findViewById(R.id.close);
        EditText editText = dialog.findViewById(R.id.problem);
        MaterialButton button = dialog.findViewById(R.id.btnpto);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"himanshugupta3385@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting A Problem ");
                intent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString().trim());
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
        dialog.show();
    }
}