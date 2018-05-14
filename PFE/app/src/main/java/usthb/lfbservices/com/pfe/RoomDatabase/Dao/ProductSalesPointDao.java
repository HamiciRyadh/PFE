package usthb.lfbservices.com.pfe.RoomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.ProductSalesPoint;

@Dao
public interface ProductSalesPointDao {


    @Query("SELECT * FROM ProductSalesPoint WHERE productBarcode = :productBarcode")
    List<ProductSalesPoint> getAll(String productBarcode);


    @Query("DELETE FROM ProductSalesPoint WHERE productBarcode = :productBarcode AND salesPointId = :salespointId")
    void deleteById(String productBarcode, String salespointId);

    @Query("DELETE FROM ProductSalesPoint WHERE productBarcode = :productBarcode ")
    void deleteByproductBarcode(String productBarcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ProductSalesPoint... productSalesPoints);
}


