package com.example.crowdsourcing.Database.Query;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.crowdsourcing.Database.Model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> get();

    @Insert
    void insert(User user);

    @Query("DELETE FROM user")
    void reset();
}
