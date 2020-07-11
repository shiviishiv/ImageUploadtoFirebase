package com.example.picsgallery;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hendrawd.storageutil.library.StorageUtil;

public class MainActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
 //   final StorageReference ref = storage.getReference().child("drivers/" + ".jpg");
    List<String> selectedImage = new ArrayList<>();
    private static final String TAG = "MainActivity";
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            doWork();
            for (String t : list) {
                Log.d(TAG, "onCreate: " + t);
            }
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerView.setAdapter(new MyAdapter(list));

            findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (String temp : selectedImage) {
                        Bitmap bitmap = BitmapFactory.decodeFile(temp);
                        Log.d(TAG, "onClick: " + temp);
                        uploadImage(temp);
                    }
                }
            });
            findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Toast.makeText(getApplicationContext(),"I will add this feature soon",Toast.LENGTH_SHORT).show();

                }});

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }

// do whatever you want with the externalStoragePaths

    }



    private void doWork() {
        String[] externalStoragePaths = StorageUtil.getStorageDirectories(this);
// do whatever you want with the externalStoragePaths
        for (String temp : externalStoragePaths) {
            getResourcePath(new File(temp));
        }

    }

    private void getResourcePath(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            for (String str : list) {
                getResourcePath(new File(file, str));
            }

        }
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith("jpg") || absolutePath.endsWith("png")) {
            list.add(absolutePath);
        }


    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<String> mylist;

        public MyAdapter(List<String> mylist) {
            this.mylist = mylist;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.card_view, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            Bitmap bitmap1 = BitmapFactory.decodeFile(mylist.get(position));
            holder.imageView.setImageBitmap(bitmap1);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.imageView.setSelected(true);
                    if (holder.imageView.isSelected()) {
                        selectedImage.add(mylist.get(position));
                        holder.imageView.setBackgroundColor(Color.BLUE);
                    } else {
                        selectedImage.remove(mylist.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mylist.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
    private void uploadImage(String temp) {

          Toast.makeText(getApplicationContext(),"token saved",Toast.LENGTH_SHORT).show();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(temp);

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/"+temp);

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        // Get the data from an ImageView as bytes

        Bitmap bitmap = BitmapFactory.decodeFile(temp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                  Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                  Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                // ...
            }
        });
    }}