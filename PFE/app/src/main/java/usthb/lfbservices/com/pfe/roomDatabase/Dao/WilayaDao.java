package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Wilaya;

@Dao
public interface WilayaDao {

    @Query("SELECT wilayaName FROM Wilaya ORDER BY wilayaId ASC")
    String[] getAll();

    @Query("SELECT wilayaName FROM Wilaya WHERE wilayaId = (SELECT wilayaId FROM City WHERE cityId = :cityId)")
    String getWilayaNameByCity(int cityId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Wilaya> wilayas);
}

