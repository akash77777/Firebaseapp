package com.example.myapplication4;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    ArrayList<String> videos=new ArrayList<>();
    VideoView views;
    Upload uplo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         uplo =new Upload();
        views = (VideoView) findViewById(R.id.videoView2);
        Bundle bundelobj = getIntent().getExtras();
        videos = (ArrayList<String>) bundelobj.getSerializable("list");
        //text2.setText(videos.get(videos.size()-1));

        final DatabaseReference dReference= FirebaseDatabase.getInstance().getReference().child("uploads");

        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {

                    uplo=ds.getValue(Upload.class);
                    videos.add(uplo.getImageUrl());
                    //Toast.makeText(MainActivity.this,"download",Toast.LENGTH_SHORT).show();
                    //text1.setText(videos.get(videos.size()-1));
                    int k=videos.size();
                    while(k>=0) {
                        Uri uri = Uri.parse(videos.get(videos.size()-1));
                        views.setVideoURI(uri);
                        views.requestFocus();
                        views.start();
                        k=k-1;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
