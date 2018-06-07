package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Wilaya;

@Dao
public interface WilayaDao {

    /**
     * Extracts the name of the wilaya associated with the given {@link Wilaya#wilayaId}.
     * @return A {@link String} representing the name of the wilaya.
     */
    @Query("SELECT wilayaName FROM Wilaya ORDER BY wilayaId ASC")
    String[] getAll();

    /**
     * Extracts the name of the wilaya correspondig to a defined city.
     * @param cityId The id of the {@link usthb.lfbservices.com.pfe.models.City}.
     * @return A {@link String} representing the associated wilaya name.
     */
    @Query("SELECT wilayaName FROM Wilaya WHERE wilayaId = (SELECT wilayaId FROM City WHERE cityId = :cityId)")
    String getWilayaNameByCity(int cityId);

    /**
     * Inserts a {@link List} of {@link Wilaya} to the database, if the {@link Wilaya#wilayaId} already
     * exists, that row will replace it.
     * @param wilayas A {@link List} of {@link Wilaya} to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Wilaya> wilayas);
}

