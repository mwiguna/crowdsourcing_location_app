package com.example.crowdsourcing.Database.Table;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.crowdsourcing.Database.Model.User;
import com.example.crowdsourcing.Database.Query.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}