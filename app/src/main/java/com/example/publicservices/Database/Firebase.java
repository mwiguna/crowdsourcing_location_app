package com.example.publicservices.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.publicservices.Database.Model.User;
import com.example.publicservices.Database.Table.UserDatabase;
import com.example.publicservices.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firebase {

    public FirebaseFirestore db = null;
    public UserDatabase dbUser;
    public Context context;

    // --------- Firebase

    public Firebase(Context context){
        this.context = context;

        if(db == null) db = FirebaseFirestore.getInstance();
        dbUser = Room.databaseBuilder(context, UserDatabase.class, "user").build();
    }


    // Check User

    public void saveDataLocation(final Map<String, Object> data){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<User> user = dbUser.userDao().get();
                if(user.size() > 0){
                    String id = user.get(0).getId();
                    saveLocationOnly(id, data);
                } else createUser(data);
            }
        });

    }

    // Create id user

    public void createUser(final Map<String, Object> data){
        Map<String, Object> user = new HashMap<>();
        user.put("time", MainActivity.getTimeStamp());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                User user = new User(documentReference.getId(), MainActivity.getTimeStamp());
                                dbUser.userDao().insert(user);

                                saveLocationOnly(documentReference.getId(), data);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("user_db_fail", "Error adding document", e);
                    }
                });
    }

    // After Check user

    public void saveLocationOnly(String id, Map<String, Object> data){
        data.put("user_id", id);

        db.collection("locations")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("lokasi_db", "Success Add ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("lokasi_db", "Error adding document", e);
                    }
                });
    }
}
