package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.ProductCharacteristic;

@Dao
public interface ProductCharacteristicDao {

    @Query("SELECT * FROM ProductCharacteristic")
    List<ProductCharacteristic> getAll();

    @Query("SELECT typeCharacteristicId, productCharacteristicValue " +
            "FROM ProductCharacteristic "+
            " WHERE productBarcode = :productBarcode")
    List<KeyValue> getCharacteristics(String productBarcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductCharacteristic> productCharacteristics);
}