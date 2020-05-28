package com.example.crowdsourcing.Database.Table;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.crowdsourcing.Database.Model.Location;
import com.example.crowdsourcing.Database.Query.LocationDao;

@Database(entities = {Location.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
}
