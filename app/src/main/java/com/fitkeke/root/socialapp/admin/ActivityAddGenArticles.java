package com.fitkeke.root.socialapp.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fitkeke.root.socialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddGenArticles extends AppCompatActivity {


    private Button btnPost;
    private Button btnAddImg;
    private EditText txtTitle;
    private EditText txtDesc;
    private ImageView imgPost;
    private StorageReference storageReference;
    private int GALLERY_REQUEST = 100;
    private Uri imgUri = null;
    private DatabaseReference reference;
    private Spinner spinner;
    private ImageButton btnClosePost;
    private Uri tempURI = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gen_articles);
        // init views
        initViews();

        // firebase
        storageReference = FirebaseStorage.getInstance().getReference().child("gen_articles").child("posts_imgs");;
        reference = FirebaseDatabase.getInstance().getReference().child("articles");

        // get intent

        Intent intent = getIntent();
        final String type = intent.getStringExtra("type");

        String key = "";
        if (type.equals("edit")){
            key = intent.getStringExtra("key");
            getPostData(key);
            btnAddImg.setText("تغيير الصوره  ");
        }else {

        }

        //button event
        final String finalKey = key;
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (type.equals("add")){
                    addPost("add", finalKey);
                }else if (type.equals("edit")){
                    addPost("edit", finalKey);
                }

            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i , GALLERY_REQUEST);

            }
        });

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i , GALLERY_REQUEST);
            }
        });

        btnClosePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAddGenArticles.this, ActivityAddHealth.class));
                finish();
            }
        });


    }

    private void addPost(final String Addtype, final String post_key) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.show();

        final String title = txtTitle.getText().toString();
        final String desc = txtDesc.getText().toString();
        final String type = spinner.getSelectedItem().toString();
        final String date = GetDate();
        final String postImg = String.valueOf(imgUri);
        final String userImg = "default";
        final String name = "default";

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "اضف عنوان", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(getApplicationContext(), "اضف وصف للمقال", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        if (imgUri == null || imgUri.equals("default") || imgUri.equals("")){
            imgUri = Uri.parse("default");
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", name);
            map.put("userImg", userImg);
            map.put("title", title);
            map.put("desc", desc);
            map.put("date", date);
            map.put("postImg", "default");
            map.put("type", type);
            map.put("video", "default");
            String key = reference.push().getKey();


            if (Addtype.equals("add")){
                map.put("key", key);
                reference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                            Toast.makeText(ActivityAddGenArticles.this, "تم النشر بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else if (Addtype.equals("edit")){
                map.put("key", post_key);
                reference.child(post_key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                            Toast.makeText(ActivityAddGenArticles.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }else {
            // handle img
            if (tempURI == imgUri){
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", name);
                map.put("userImg", userImg);
                map.put("title", title);
                map.put("desc", desc);
                map.put("date", date);
                map.put("postImg", imgUri.toString());
                map.put("type", type);
                map.put("video", "default");
                String key = reference.push().getKey();


                if (Addtype.equals("add")){
                    map.put("key", key);
                    reference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                                Toast.makeText(ActivityAddGenArticles.this, "تم النشر بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if (Addtype.equals("edit")){
                    map.put("key", post_key);
                    reference.child(post_key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                                Toast.makeText(ActivityAddGenArticles.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else {
                StorageReference file_path = storageReference.child(imgUri.getLastPathSegment());
                file_path.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("userImg", userImg);
                        map.put("title", title);
                        map.put("desc", desc);
                        map.put("date", date);
                        map.put("postImg", downloadUri);
                        map.put("type", type);
                        map.put("video", "default");
                        String key = reference.push().getKey();


                        if (Addtype.equals("add")){
                            map.put("key", key);
                            reference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                                        Toast.makeText(ActivityAddGenArticles.this, "تم النشر بنجاح", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else if (Addtype.equals("edit")){
                            map.put("key", post_key);
                            reference.child(post_key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        startActivity(new Intent(ActivityAddGenArticles.this , Dashboard.class));
                                        Toast.makeText(ActivityAddGenArticles.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(ActivityAddGenArticles.this, "Failure !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }

        }



    }


    private void initViews() {

        btnPost = findViewById(R.id.btn_new_post);
        txtTitle = findViewById(R.id.txt_new_post_title);
        txtDesc = findViewById(R.id.txt_new_post);
        imgPost = findViewById(R.id.post_img);
        btnAddImg = findViewById(R.id.btn_add_img);
        spinner = findViewById(R.id.spinner);
        btnClosePost = (ImageButton) findViewById(R.id.close_post);

    }


    private String GetDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String full_date = day + "/" + month + "/" + year + "   at " + hour + ":" + minutes;
        return full_date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST){
            imgUri = data.getData();
            imgPost.setImageURI(imgUri);
        }
    }

    private void deletePost(final String postKey) {
        reference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                reference.child(postKey).removeValue();
                Toast.makeText(ActivityAddGenArticles.this, "تم حذف المقال", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getPostData(final String postKey) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtDesc.setText(dataSnapshot.child(postKey).child("desc").getValue().toString());
                txtTitle.setText(dataSnapshot.child(postKey).child("title").getValue().toString());
                String imgEditUrl = dataSnapshot.child(postKey).child("postImg").getValue().toString();
                imgUri = Uri.parse(imgEditUrl);
                Picasso.with(ActivityAddGenArticles.this).load(imgEditUrl).into(imgPost);
                tempURI = imgUri;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}