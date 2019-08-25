package com.example.publicservices;

import androidx.appcompat.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.publicservices.DataCollector.GetLocation;
import com.example.publicservices.Database.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public FusedLocationProviderClient fusedLocationClient;

    GetLocation getLocation = new GetLocation(this, this);
    Firebase db = new Firebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
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
                getLocation();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
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
                                db.saveDataLocation(address);
                                Log.d("lokasi", address.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Can't find your location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}