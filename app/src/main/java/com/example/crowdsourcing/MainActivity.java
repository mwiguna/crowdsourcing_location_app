package com.example.crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.crowdsourcing.DataCollector.GetLocation;
import com.example.crowdsourcing.Database.DBController;


public class MainActivity extends AppCompatActivity {
    GetLocation getLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        init();
    }

    public void init(){
       if(!isMyServiceRunning(ForegroundService.class)) startService();
       getLocation = new GetLocation(MainActivity.this, MainActivity.this);
       getLocation.getLocation();

        DBController dbController = new DBController(this);
        dbController.firstInitiate();

       Button btnGet = findViewById(R.id.btnGet);
       btnGet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getLocation.getLocation();
           }
       });
    }


    // --------- Foreground Service


    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        } return false;
    }


    // --------- Menu Interface Setting (Abaikan)


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


    // ------- ALUR : Main activity > Start foreground > foreground mengakses DB dan akses Lokasi > DB Controller menyimpan & hapus data
}