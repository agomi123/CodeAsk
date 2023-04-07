package com.example.codeask.Fragments;

//import static android.app.appsearch.AppSearchResult.RESULT_OK;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
//import android.net.Uri;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codeask.Adapter.QueryAdapter;
import com.example.codeask.Model.Query;
import com.example.codeask.Model.User;
//import com.example.codeask.R;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.agomi.codeasks.R;

public class ProfileFragment extends Fragment {
//    CircleImageView circleImageView;
//    TextView username;
//    DatabaseReference reference;
//    FirebaseUser fuser;
//    StorageReference storageReference;
//    private static final int IMAGE_REQUEST=1;
//    private Uri imageuri;
//    private StorageTask uploadtask;
ImageView floatingActionButton;
    private QueryAdapter postAdapter;
    private List<Query> postList;
    EditText askquery;
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        floatingActionButton = view.findViewById(R.id.postquerybtn);
        askquery = view.findViewById(R.id.postqueryedt);
        RecyclerView recyclerView = view.findViewById(R.id.recycler4);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                readQuery();
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView.getContext(),
                layout.getOrientation());
        dividerItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(dividerItemDecoration2);
        recyclerView.setLayoutManager(layout);
        postList = new ArrayList<>();
        postAdapter = new QueryAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //add new query
                if(askquery.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Empty Queries cannot be post",Toast.LENGTH_SHORT).show();
                    return;
                }
                addQuery();
            }
        });
        readQuery();
//        circleImageView=view.findViewById(R.id.profile_imgs);
//        username=view.findViewById(R.id.usernames);
//        storageReference= FirebaseStorage.getInstance().getReference("uploads");
//        fuser= FirebaseAuth.getInstance().getCurrentUser();
//        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user=snapshot.getValue(User.class);
//                username.setText(user.getUsername());
//                if(user.getImageURL().equals("default")){
//                    circleImageView.setImageResource(R.mipmap.ic_launcher);
//                }
//                else{
//                    Glide.with(getContext()).load(user.getImageURL()).into(circleImageView);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        circleImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openImage();
//            }
//        });
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addQuery() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid=firebaseUser.getUid();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date= dtf.format(now);
        String val =askquery.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Query");
        String postid = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("queryid", postid);
        hashMap.put("publisher", uid);
        hashMap.put("querydate", date);
        hashMap.put("query", val);
        assert postid != null;
        reference.child(postid).setValue(hashMap);
        Toast.makeText(getContext(), "Query Added", Toast.LENGTH_SHORT).show();
        readQuery();
        askquery.setText("");
    }

    private void readQuery() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Query");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Query post = dataSnapshot.getValue(Query.class);
                    postList.add(post);

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void openImage(){
//        Intent it=new Intent();
//        it.setType("image/*");
//        it.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(it,IMAGE_REQUEST);
//
//    }
//    private String getFileExtension(Uri uri){
//        ContentResolver contentResolver=getContext().getContentResolver();
//        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
//    private void uploadImage(){
//        final ProgressDialog pd=new ProgressDialog(getContext());
//        pd.setMessage("Uploading");
//        pd.show();
//        if(imageuri!=null){
//            final StorageReference filerefernce=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
//            uploadtask=filerefernce.putFile(imageuri);
//            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if(!task.isSuccessful())
//                        throw task.getException();
//
//                    return filerefernce.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUri = task.getResult();
//                        String mUri=downloadUri.toString();
//                        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                        HashMap<String,Object>mp=new HashMap<>();
//                        mp.put("imageURL",mUri);
//                        reference.updateChildren(mp);
//                        pd.dismiss();
//                    }
//                    else{
//                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
//                        pd.dismiss();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                }
//            });
//        }
//        else{
//            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
////            pd.dismiss();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK  && data !=null && data.getData()!=null){
//            imageuri=data.getData();
//            if(uploadtask!=null && uploadtask.isInProgress()){
//                Toast.makeText(getContext(),"Upload in Progress",Toast.LENGTH_SHORT).show();
////                pd.dismiss();
//            }
//            else{
//                uploadImage();
//            }
//        }
//    }
}