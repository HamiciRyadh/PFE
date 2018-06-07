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

    /**
     * Extracts from the database a {@link List} of {@link KeyValue} containing the characteristics
     * values of a {@link usthb.lfbservices.com.pfe.models.Product} corresponding to the given
     * {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @param productBarcode A {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @return A {@link List} of {@link KeyValue}.
     */
    @Query("SELECT typeCharacteristicId, productCharacteristicValue " +
            "FROM ProductCharacteristic "+
            " WHERE productBarcode = :productBarcode")
    List<KeyValue> getCharacteristics(String productBarcode);

    /**
     * Inserts a {@link List} of {@link ProductCharacteristic} in the database, if the {@link ProductCharacteristic#typeCharacteristicId}
     * and {@link ProductCharacteristic#productBarcode} couple already exists, it will replace the row.
     * @param productCharacteristics A {@link List} of {@link ProductCharacteristic}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductCharacteristic> productCharacteristics);
}