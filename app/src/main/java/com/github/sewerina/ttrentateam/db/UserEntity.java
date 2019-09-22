package com.github.sewerina.ttrentateam.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "user")
public class UserEntity implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "firstName")
    @SerializedName("first_name")
    public String firstName;

    @ColumnInfo(name = "lastName")
    @SerializedName("last_name")
    public String lastName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "photo")
    @SerializedName("avatar")
    public String photo;
}
