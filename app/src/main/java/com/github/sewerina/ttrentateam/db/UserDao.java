package com.github.sewerina.ttrentateam.db;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<UserEntity> getAllUsers();
}
