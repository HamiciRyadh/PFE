package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.City;

@Dao
public interface CityDao {

    @Query("SELECT cityName FROM City WHERE wilayaId = :wilayaId ORDER BY cityId ASC")
    List<String> getAll(int wilayaId);

    @Query("SELECT cityId FROM City WHERE cityName = :cityName")
    int getCityIdByName(String cityName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}