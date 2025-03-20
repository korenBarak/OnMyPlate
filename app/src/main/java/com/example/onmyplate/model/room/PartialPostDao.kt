package com.example.onmyplate.model.room;

import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query;

@Dao
public interface PartialPostDao {
    @Query("SELECT * FROM PartialPost ORDER BY id DESC LIMIT 1")
    fun getLastPartialPost(): PartialPost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(vararg partialPost: PartialPost)

    @Delete
    fun delete(partialPost: PartialPost)

    @Query("DELETE FROM PartialPost WHERE id = :partialPostId")
    fun deleteById(partialPostId: String)
}
