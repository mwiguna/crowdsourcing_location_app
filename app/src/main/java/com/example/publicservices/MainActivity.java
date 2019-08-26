package com.example.publicservices;

import androidx.appcompat.app.AppCompatActivity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.publicservices.DataCollector.GetLocation;
import com.example.publicservices.Database.Firebase;
import com.example.publicservices.Database.Model.User;
import com.example.publicservices.Database.Table.UserDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    GetLocation getLocation;
    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        init();
        getLocation();
    }

    public void init(){
       firebase = new Firebase(this);
       getLocation = new GetLocation(this, this);
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }


    // --------- Tool

    public static String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


    // --------- Location


    public void getLocation(){
        if(getLocation.getPermission()){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Map<String, Object> address = getLocation.getAddress(location.getLatitude(), location.getLongitude());
                                firebase.saveDataLocation(address);
                                Log.d("lokasi", address.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Can't find your location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}