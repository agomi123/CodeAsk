package com.example.codeask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import com.agomi.codeasks.R;
public class RegistrationActivity extends AppCompatActivity {
   EditText username,email,password,nameedt,mobiledt;
   Button registerbutton;
    CheckBox checkBox;
   FirebaseAuth auth;
   DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        Toolbar toolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Register");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          username=findViewById(R.id.username);
          email=findViewById(R.id.email);
          password=findViewById(R.id.password);
          registerbutton=findViewById(R.id.register);
          nameedt=findViewById(R.id.name);
          mobiledt=findViewById(R.id.mobile);
        checkBox = findViewById(R.id.checkBox);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
          auth=FirebaseAuth.getInstance();

          registerbutton.setOnClickListener(view -> {
              String txtusername=username.getText().toString();
              String txtemail=email.getText().toString();
              String txtpassword=password.getText().toString();
              String txtname=nameedt.getText().toString();
              String txtmobile=mobiledt.getText().toString();
              boolean ischecked= checkBox.isChecked();
              if(TextUtils.isEmpty(txtusername) || TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword) ||TextUtils.isEmpty(txtname) ||TextUtils.isEmpty(txtmobile)){
                  Toast.makeText(RegistrationActivity.this,"All Field Required",Toast.LENGTH_SHORT).show();
              }
              else if(txtpassword.length()<6){
                  Toast.makeText(RegistrationActivity.this,"password will be atleast 6 character",Toast.LENGTH_SHORT).show();
              }
              else if(!ischecked){
                  Toast.makeText(RegistrationActivity.this,"Kindly Agree with the Privacy Policy then Register",Toast.LENGTH_SHORT).show();
              }
              else{
                  register(txtusername,txtemail,txtpassword,txtname,txtmobile);
              }
          });
    }
    private void register(String username,String email,String password,String name,String mobile){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait !");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid=firebaseUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String,String>hashMap=new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("name",name);
                            hashMap.put("email",email);
                            hashMap.put("mobile",mobile);
                            hashMap.put("bio","null");
                            hashMap.put("gender","null");
                            hashMap.put("password",password);
                            hashMap.put("dob","null");
                            hashMap.put("profession","null");
                            hashMap.put("organization","null");
                            hashMap.put("resume","null");
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());
                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    progressDialog.dismiss();
                                    sendVerificationEmail();
                                    Intent it=new Intent(RegistrationActivity.this,MainActivity.class);
                                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(it);
                                    finish();
                                }
                            });
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this,"You cannot regiter with this email id",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this,"Please Verify Your Email ID",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // email not sent, so display message and restart the activity or do whatever you wish to do

                        //restart this activity
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());

                    }
                });
    }
    public void tandc(View view) {
        Uri uri = Uri.parse("https://www.freeprivacypolicy.com/live/964bb3d9-213e-4242-82c0-e21e56b1a639"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}