package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.ProductCaracteristic;

@Dao
public interface ProductCaracteristicDao {

    @Query("SELECT * FROM ProductCaracteristic")
    List<ProductCaracteristic> getAll();

    @Query("SELECT typeCaracteristicId, productCaracteristicValue " +
            "FROM ProductCaracteristic "+
            " WHERE productBarcode = :productBarcode")
    List<KeyValue> getCaracteristics(String productBarcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductCaracteristic> productCaracteristics);
}