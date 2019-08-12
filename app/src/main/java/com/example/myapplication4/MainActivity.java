package com.example.myapplication4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button upload, get,download ,play;
    ImageView img;
    TextView text1;
    EditText editText;
    DatabaseReference databaseReference;
    private Uri imguri;
    Upload ups;
    ArrayList<String>List=new ArrayList<>();
    private StorageReference mStorageRef;
    //ArrayAdapter<String> adapter;
    StorageTask upl;
    long g=0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imguri=data.getData();
        img.setImageURI(imguri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ups=new Upload();
        setContentView(R.layout.activity_main);
        get = (Button) findViewById(R.id.button);
        img = (ImageView) findViewById(R.id.imageView);
        text1=(TextView)findViewById(R.id.textView3);
        upload = (Button) findViewById(R.id.button2);
        download=(Button) findViewById(R.id.button3);
        play=(Button)findViewById(R.id.button4);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("images");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("uploads");
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdata();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upl != null && upl.isInProgress()) {
                    Toast.makeText(MainActivity.this, "inprogress", Toast.LENGTH_SHORT).show();
                } else {
                    uploaddata();

                }


            }
        });
        download.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 download();
             }
         });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,Main2Activity.class);
                Bundle bundel=new Bundle();
                bundel.putSerializable("list",List);
                intent.putExtras(bundel);
                startActivity(intent);
            }
        });
    }



    private void download() {

        final DatabaseReference dReference= FirebaseDatabase.getInstance().getReference().child("uploads");

        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              for(DataSnapshot ds:dataSnapshot.getChildren())
                {

                 ups=ds.getValue(Upload.class);
                    List.add(ups.getImageUrl());
                    Toast.makeText(MainActivity.this,"download",Toast.LENGTH_SHORT).show();
                    text1.setText(List.get(List.size()-1));


                }
                downloaddata(List.get(List.size()-1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void downloaddata(String url)
    {
        DownloadManager downloadManager =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request =new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadManager.enqueue(request);
    }



    private void uploaddata() {
        final StorageReference imagename = mStorageRef.child(System.currentTimeMillis()+"."+getext(imguri));
        upl=imagename.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
                imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Upload Upload =new Upload(String.valueOf(uri));
                        String uploadid=databaseReference.push().getKey();
                        databaseReference.child(uploadid).setValue(Upload);

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_LONG).show();

            }
        });


    }

    private void getdata() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getext(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return ((MimeTypeMap) mimeTypeMap).getExtensionFromMimeType(cr.getType(uri));
    }
}