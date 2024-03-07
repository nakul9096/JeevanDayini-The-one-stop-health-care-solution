package com.example.unityapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class LoginData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userUid")
    private String userUid;

    @ColumnInfo(name = "userType")
    private String userType;

    public LoginData() {
    }

    @Ignore
    public LoginData(@NonNull String userUid, String userType) {
        this.userUid = userUid;
        this.userType = userType;
    }

    // Getters and setters
    @NonNull
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(@NonNull String userUid) {
        this.userUid = userUid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
