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

    @Query("SELECT * FROM location ORDER BY id ASC LIMIT :limit")
    List<Location> limit(int limit);

    @Insert
    void insert(Location location);

    @Query("DELETE FROM location WHERE id < :id OR id = :id")
    void deleteSuccesful(int id);

    @Query("DELETE FROM location")
    void reset();
}
