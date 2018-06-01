package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.TypeCharacteristic;

@Dao
public interface TypeCharacteristicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TypeCharacteristic> typeCharacteristics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TypeCharacteristic... typeCharacteristics);

    @Query("SELECT typeCharacteristicName FROM TypeCharacteristic WHERE  typeCharacteristicId = :typeCharacteristicId ")
    String getTypeCharacteristicName(int typeCharacteristicId);

    @Query("SELECT * FROM TypeCharacteristic")
    List<TypeCharacteristic> getAll();

    @Query("SELECT * FROM TypeCharacteristic WHERE typeCharacteristicName = :typeCharacteristicName")
    TypeCharacteristic get(String typeCharacteristicName);
}
