package com.example.publicservices.Database.Table;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.publicservices.Database.Model.User;
import com.example.publicservices.Database.Query.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}