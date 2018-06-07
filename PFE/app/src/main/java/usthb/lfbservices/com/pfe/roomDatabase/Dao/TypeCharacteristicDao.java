package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.TypeCharacteristic;

@Dao
public interface TypeCharacteristicDao {

    /**
     * Inserts a {@link List} of {@link TypeCharacteristic} in the database, if the {@link TypeCharacteristic#typeCharacteristicId}
     * already exists, it will replace the row.
     * @param typeCharacteristics A {@link List} of {@link TypeCharacteristic}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TypeCharacteristic> typeCharacteristics);

    /**
     * Inserts a {@link TypeCharacteristic} in the database, if the {@link TypeCharacteristic#typeCharacteristicId}
     * already exists, it will replace the row.
     * @param typeCharacteristics A {@link TypeCharacteristic}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TypeCharacteristic... typeCharacteristics);

    /**
     * Extracts from the database the {@link TypeCharacteristic#typeCharacteristicName} associated with
     * the given {@link TypeCharacteristic#typeCharacteristicName}.
     * @param typeCharacteristicId A {@link TypeCharacteristic#typeCharacteristicName}.
     * @return The associated {@link TypeCharacteristic#typeCharacteristicName}.
     */
    @Query("SELECT typeCharacteristicName FROM TypeCharacteristic WHERE  typeCharacteristicId = :typeCharacteristicId ")
    String getTypeCharacteristicName(int typeCharacteristicId);

    /**
     * Extracts from the database all the {@link TypeCharacteristic}.
     * @return A {@link List} of {@link TypeCharacteristic}.
     */
    @Query("SELECT * FROM TypeCharacteristic")
    List<TypeCharacteristic> getAll();
}
