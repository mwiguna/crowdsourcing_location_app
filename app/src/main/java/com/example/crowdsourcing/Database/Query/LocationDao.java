package com.example.crowdsourcing.Database.Query;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.crowdsourcing.Database.Model.Location;
import com.example.crowdsourcing.Database.Model.User;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<Location> get();

    @Insert
    void insert(Location location);

    @Query("DELETE FROM location")
    void reset();
}
