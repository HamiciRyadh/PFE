package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.City;

@Dao
public interface CityDao {

    /**
     * Extracts all the {@link City#cityName} associated with the given {@link usthb.lfbservices.com.pfe.models.Wilaya#wilayaId}
     * from the database and return them in a {@link List}.
     * @param wilayaId A {@link usthb.lfbservices.com.pfe.models.Wilaya#wilayaId}.
     * @return A {@link List} of {@link City#cityName}.
     */
    @Query("SELECT cityName FROM City WHERE wilayaId = :wilayaId ORDER BY cityId ASC")
    List<String> getAll(int wilayaId);

    /**
     * Extracts from the database the {@link City#cityId} associated with the given {@link City#cityName}.
     * @param cityName A {@link City#cityName}.
     * @return A {@link City#cityId}.
     */
    @Query("SELECT cityId FROM City WHERE cityName = :cityName")
    int getCityIdByName(String cityName);

    /**
     * Inserts a {@link List} of {@link City} in the database, if the {@link City#cityId} already
     * exists, it will replace the row.
     * @param cities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}