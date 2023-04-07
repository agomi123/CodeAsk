package com.example.codeask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.agomi.codeasks.R;
public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginbtn;
    FirebaseAuth auth;
    TextView forgetpass;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        username=findViewById(R.id.username);
        email=findViewById(R.id.lgemail);
        password=findViewById(R.id.lgpassword);
        loginbtn=findViewById(R.id.loginbtn);
        forgetpass=findViewById(R.id.forget_pass);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });
        auth= FirebaseAuth.getInstance();
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String txtusername=username.getText().toString();
                String txtemail=email.getText().toString();
                String txtpassword=password.getText().toString();
                if(TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword)){
                    Toast.makeText(LoginActivity.this,"All Field Required",Toast.LENGTH_SHORT).show();
                }
                else if(txtpassword.length()<6){
                    Toast.makeText(LoginActivity.this,"password will be atleast 6 character",Toast.LENGTH_SHORT).show();
                }
                else{
                    ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Welcome Back!");
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(txtemail,txtpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent it=new Intent(LoginActivity.this,MainActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(it);
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}