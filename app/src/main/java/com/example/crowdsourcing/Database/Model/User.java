package com.example.crowdsourcing.Database.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

    @Entity
    public class User {
        @PrimaryKey()
        @NonNull()
        public String id;

        @ColumnInfo(name = "time")
        public String time;

        public User(String id, String time) {
            this.id = id;
            this.time = time;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

    }