package com.github.sewerina.ttrentateam;

import android.app.Application;

import androidx.room.Room;

import com.github.sewerina.ttrentateam.db.AppDatabase;
import com.github.sewerina.ttrentateam.db.UserDao;

public class TaskApp extends Application {
    public static UserDao sUserDao;

    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase db = Room.databaseBuilder(
                this,
                AppDatabase.class, "user.db").build();
        sUserDao = db.userDao();
    }
}
