package com.example.unityapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoginDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLoginData(LoginData loginData);

    @Query("SELECT * FROM user_table WHERE userUid = :userUid")
    LoginData getLoginDataByUid(String userUid);
}
