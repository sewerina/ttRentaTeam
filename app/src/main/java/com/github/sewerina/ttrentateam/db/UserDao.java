package com.github.sewerina.ttrentateam.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    Single<List<UserEntity>> getAllUsers();

    @Query("DELETE FROM user")
    Completable clearTableUser();

    @Insert
    Completable insertAllUsers(List<UserEntity> userEntities);



}
