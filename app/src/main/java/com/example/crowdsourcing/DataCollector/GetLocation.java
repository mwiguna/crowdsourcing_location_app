package com.example.crowdsourcing.DataCollector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.crowdsourcing.Database.DBController;
import com.example.crowdsourcing.MainActivity;
import com.example.crowdsourcing.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

public class GetLocation {

    //    CLASS INI TIDAK DIGUNAKAN SECARA LANGSUNG. HANYA DOKUMENTASI / TESTING

    public Context context;
    public Activity activity;
    DBController dbController;
    FusedLocationProviderClient fusedLocationClient;

    public GetLocation(Context context, Activity activity){
        this.context = context;
        this.activity = activity;

        dbController = new DBController(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }


    // --------- Get Location


    public void getLocation(){
        if(getPermission() && isLocationEnabled()){
            requestNewLocationData();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Map<String, Object> address = getAddress(location.getLatitude(), location.getLongitude());
                                //dbController.saveDataLocation(address);

                                dbController.saveDataRoom(location.getLatitude(), location.getLongitude());
                                dbController.showLocation();

                                Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
                            } else {
                                requestNewLocationData();
                                Toast.makeText(context, "Tidak dapat menemukan lokasi, harap hidupkan GPS dan koneksi internet, coba periksa lokasi lewat google maps.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else Toast.makeText(context, "Tidak dapat menemukan lokasi, harap hidupkan GPS dan koneksi internet.", Toast.LENGTH_LONG).show();
    }


    //------------- Request Location


//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData(){
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(5000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setNumUpdates(1);
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationClient.requestLocationUpdates(
//                mLocationRequest, mLocationCallback,
//                Looper.myLooper()
//        );
//    }
//
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location location = locationResult.getLastLocation();
//            Log.d("lokasi-old", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
//        }
//    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        locationManager.requestLocationUpdates(1000, (float) 0.1, criteria, locationListener, Looper.myLooper());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("lokasi-new", "Lat:" + location.getLatitude() + " Long:" + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("lokasi-old", "status");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("lokasi-old", "providerenable");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("lokasi-old", "providerdisable");
        }
    };


    //     ---------- Checking only


    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean getPermission(){

        // Permission

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(context)
                        .setTitle("Izin Lokasi")
                        .setMessage("Aplikasi ini membutuhkan data lokasi agar dapat digunakan.")
                        .setPositiveButton("SAYA MENGERTI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .setNegativeButton("BELUM BISA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } return false;
        } else return true;
    }


    // ----- Get Address From Lat Lang


    public Map<String, Object> getAddress(double latitude, double longitude) {
        Map<String, Object> result = new HashMap<>();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.put("lat", latitude);
                result.put("long", longitude);
                result.put("feature", address.getFeatureName());
                result.put("thoroughfare", address.getThoroughfare());
                result.put("locality", address.getLocality());
                result.put("city", address.getSubAdminArea());
                result.put("province", address.getAdminArea());
                result.put("country", address.getCountryName());
                result.put("fulladdress", address.getAddressLine(0));
                result.put("time", getTimeStamp());
            }
        } catch (IOException e) {
            Log.e("address_error", e.getMessage());
        }

        return result;
    }


    // --------- Tool


    public static String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}