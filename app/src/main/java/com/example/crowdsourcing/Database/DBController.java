package com.example.crowdsourcing.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.crowdsourcing.DataCollector.GetLocation;
import com.example.crowdsourcing.Database.Model.Location;
import com.example.crowdsourcing.Database.Model.User;
import com.example.crowdsourcing.Database.Table.LocationDatabase;
import com.example.crowdsourcing.Database.Table.UserDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBController {

    public FirebaseFirestore db = null;
    public UserDatabase dbUser;
    public LocationDatabase dbLocation;
    public Context context;

    // --------- Initiate

    public DBController(Context context){
        this.context = context;

        if(db == null) db = FirebaseFirestore.getInstance();
        dbUser = Room.databaseBuilder(context, UserDatabase.class, "user").build();
        dbLocation = Room.databaseBuilder(context, LocationDatabase.class, "location").build();
    }


    // Check User First

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
        user.put("time", GetLocation.getTimeStamp());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                User user = new User(documentReference.getId(), GetLocation.getTimeStamp());
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

    // -------- Insert Location Local

    public void saveDataRoom(final Double lat, final Double lng){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Location location = new Location(lat.toString(), lng.toString(), getTimeStamp());
                dbLocation.locationDao().insert(location);
            }
        });
    }

    public void showLocation(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Location> location = dbLocation.locationDao().get();
                if(location.size() > 0){
                    for (int i = 0; i < location.size(); i++)
                    {
                        Location item = location.get(i);
                        if(i == location.size() - 1) Log.d("lokasiDB", "Lat: " + item.getLat() + " Lng: " + item.getLng() + " Time: " + item.getTime());
                    }
                }
            }
        });
    }


    public static String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
