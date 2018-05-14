package usthb.lfbservices.com.pfe.RoomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.TypeCaracteristic;

@Dao
public interface TypeCaracteristicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TypeCaracteristic> typeCaracteristics);

    @Query("SELECT typeCaracteristicName FROM TypeCaracteristic WHERE  typeCaracteristicId = :typeCaracteristicId ")
    String getTypeCaracteristicName(int typeCaracteristicId);

    @Query("SELECT * FROM TypeCaracteristic")
    List<TypeCaracteristic> getAll();

    @Query("SELECT * FROM TypeCaracteristic WHERE typeCaracteristicName = :typeCaracteristicName")
    TypeCaracteristic get(String typeCaracteristicName);
}
