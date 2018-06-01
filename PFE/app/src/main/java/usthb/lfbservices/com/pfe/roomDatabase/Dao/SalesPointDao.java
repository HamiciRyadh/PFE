package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import usthb.lfbservices.com.pfe.models.SalesPoint;

@Dao
public interface SalesPointDao {

    @Query("SELECT * FROM SalesPoint WHERE salesPointId = :salesPointId")
    SalesPoint getById(String salesPointId);

    @Query("SELECT salesPointName FROM SalesPoint WHERE salesPointId = :salesPointId")
    String getSalesPointNameById(String salesPointId);

    @Query("UPDATE SalesPoint SET image = :image WHERE salesPointId = :salesPointId")
    void updatePhoto(String salesPointId, byte[] image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SalesPoint... SalesPoints);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SalesPoint> SalesPoints);

    @Update
    void update(SalesPoint salesPoint);

    @Query("SELECT EXISTS (SELECT 1 FROM SalesPoint WHERE salesPointId = :salesPointId)")
    boolean salesPointExists(String salesPointId);
}
