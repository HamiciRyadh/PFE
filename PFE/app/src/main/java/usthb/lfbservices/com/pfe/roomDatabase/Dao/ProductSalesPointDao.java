package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import usthb.lfbservices.com.pfe.models.ProductSalesPoint;

@Dao
public interface ProductSalesPointDao {

    /**
     * Extracts from the database a {@link List} of {@link ProductSalesPoint} corresponding to the
     * given {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @param productBarcode A {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @return A {@link List} of {@link ProductSalesPoint}.
     */
    @Query("SELECT * FROM ProductSalesPoint WHERE productBarcode = :productBarcode")
    List<ProductSalesPoint> getAll(String productBarcode);

    /**
     * Deletes from the database the {@link ProductSalesPoint} corresponding to the given {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}
     * and {@link usthb.lfbservices.com.pfe.models.SalesPoint#salesPointId}.
     * @param productBarcode A {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @param salesPointId A {@link usthb.lfbservices.com.pfe.models.SalesPoint#salesPointId}.
     */
    @Query("DELETE FROM ProductSalesPoint WHERE productBarcode = :productBarcode AND salesPointId = :salesPointId")
    void deleteById(String productBarcode, String salesPointId);

    /**
     * Checks whether or not a {@link ProductSalesPoint} exists in the database.
     * @param productBarcode A {@link usthb.lfbservices.com.pfe.models.Product#productBarcode}.
     * @param salesPointId A {@link usthb.lfbservices.com.pfe.models.SalesPoint#salesPointId}.
     * @return true if the {@link ProductSalesPoint} exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM ProductSalesPoint WHERE productBarcode = :productBarcode AND salesPointId = :salesPointId)")
    boolean exists(String productBarcode, String salesPointId);

    /**
     * Updates a {@link ProductSalesPoint}.
     * @param productSalesPoint The new values of the {@link ProductSalesPoint}.
     */
    @Update
    void update(ProductSalesPoint productSalesPoint);

    /**
     * Inserts a {@link ProductSalesPoint} in the database, if the {@link usthb.lfbservices.com.pfe.models.SalesPoint#salesPointId}
     * already exists, it will replace the row.
     * @param productSalesPoints The {@link ProductSalesPoint} to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ProductSalesPoint... productSalesPoints);
}


