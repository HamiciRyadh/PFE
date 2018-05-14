package usthb.lfbservices.com.pfe.RoomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import usthb.lfbservices.com.pfe.models.SalesPoint;

@Dao
public interface SalesPointDao {

    @Query("SELECT * FROM SalesPoint WHERE salesPointId = :salesPointId")
    SalesPoint getById(String salesPointId);

    @Query("SELECT salesPointName FROM SalesPoint WHERE salesPointId = :salesPointId")
    String getSalesPointNameById(String salesPointId);

    @Query("UPDATE SalesPoint SET image = :image")
    void update(byte[] image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SalesPoint... SalesPoints);
}
