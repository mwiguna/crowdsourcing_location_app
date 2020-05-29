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
import com.example.crowdsourcing.ForegroundService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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


    // ----------- Check User

    public void firstInitiate(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<User> user = dbUser.userDao().get();
                if(user.size() == 0) createUser();
            }
        });

    }


    // ----------- Create id user


    private void createUser(){
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


    // -------- Insert Location Local


    public void saveDataRoom(final Double lat, final Double lng){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Location location = new Location(lat.toString(), lng.toString(), ForegroundService.getTimeStamp());
                dbLocation.locationDao().insert(location);
            }
        });
    }

    public void saveMultipleLocation() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<User> user = dbUser.userDao().get();
                List<Location> location = dbLocation.locationDao().get();

                // --------- Measure Time

                final long start;
                final long[] stop = new long[1];
                final long[] calculate = new long[1];
                final int size = location.size();
                start = System.currentTimeMillis();

                for (int i = 0; i < size; i++) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("user_id", user.get(0).getId());
                    data.put("lat", location.get(i).getLat());
                    data.put("lng", location.get(i).getLng());
                    data.put("time", location.get(i).getTime());


                    final int finalI = i;
                    db.collection("locations")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    // --- If Last data, measure time & delete

                                    if (finalI == size - 1) {
                                        stop[0] = System.currentTimeMillis();
                                        calculate[0] = stop[0] - start;
                                        Log.d("Perhitungan", "Total: " + size + " Start: " + start + " | stop: " + stop[0] + " | selisih: " + calculate[0]);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("location_db", "Error adding location", e);
                                }
                            });
                }

            }
        });

    }

    // ------------ Tool Only ----------- //


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


}
