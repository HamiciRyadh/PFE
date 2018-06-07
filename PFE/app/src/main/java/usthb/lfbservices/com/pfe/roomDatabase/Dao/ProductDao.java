package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Product;

@Dao
public interface ProductDao {

   /**
    * Extracts from the database all the {@link Product} and return them in a {@link List}.
    * @return A {@link List} of {@link Product}.
    */
   @Query("SELECT * FROM Product")
   List<Product> getAll();

   /**
    * Deletes from the database the {@link Product} corresponding with the given {@link Product#productBarcode}.
    * @param productBarcode A {@link Product#productBarcode}.
    */
   @Query("DELETE FROM Product WHERE productBarcode = :productBarcode")
   void deleteById(String productBarcode);

   /**
    * Inserts a {@link List} of {@link Product} in the database, if the {@link Product#productBarcode}
    * already exists, it will replace the row.
    * @param products A {@link List} of {@link Product}.
    */
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insertAll(List<Product> products);

   /**
    * Inserts a {@link Product} in the database, if the {@link Product#productBarcode}
    * already exists, it will replace the row.
    * @param products A {@link Product}.
    */
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insert(Product... products);

   /**
    * Checks whether or not a {@link Product} is associated with the given {@link Product#productBarcode}.
    * @param productBarcode A {@link Product#productBarcode}.
    * @return true if the {@link Product} exists, false otherwise.
    */
   @Query("SELECT EXISTS (SELECT 1 FROM Product WHERE productBarcode = :productBarcode)")
   boolean productExists(String productBarcode);
}
