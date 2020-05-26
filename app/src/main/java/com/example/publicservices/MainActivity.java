package com.example.publicservices;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.publicservices.DataCollector.GetLocation;
import com.example.publicservices.Database.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
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
    }

    public void init(){
       firebase = new Firebase(this);
       getLocation = new GetLocation(this, this);
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

       Button btnGet = findViewById(R.id.btnGet);
       btnGet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getLocation();
           }
       });
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


    // --------- Location


    public void getLocation(){
        if(getLocation.getPermission() || isLocationEnabled()){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Map<String, Object> address = getLocation.getAddress(location.getLatitude(), location.getLongitude());
                                //firebase.saveDataLocation(address);
                                Log.d("lokasi-old", address.toString());
                                Toast.makeText(MainActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                            } else {
                                requestNewLocationData();
                                Toast.makeText(MainActivity.this, "Tidak dapat menemukan lokasi, harap hidupkan GPS dan koneksi internet, coba periksa lokasi lewat google maps.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else Log.d("lokasi-old", "ada yang tidak aktif");
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("lokasi-after-fail", mLastLocation.getLatitude() + "");
        }
    };

    //     ---------- Checking only

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) Log.d("lokasi-inet", "connected");
        else Log.d("lokasi-inet", "tidak tekoneksi");
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}