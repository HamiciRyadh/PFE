package usthb.lfbservices.com.pfe.RoomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Product;

@Dao
public interface ProductDao {

   @Query("SELECT * FROM Product")
   List<Product> getAll();

   @Query("SELECT * FROM Product WHERE productBarcode = :productBarcode")
   Product getByBarcode(String productBarcode);

   @Query("DELETE FROM Product WHERE productBarcode = :productBarcode")
   void deleteById(String productBarcode);

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insertAll(List<Product> products);

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insert(Product... products);

   @Query("SELECT EXISTS (SELECT 1 FROM Product WHERE productBarcode = :productBarcode)")
   boolean productExists(String productBarcode);

   @Query("SELECT productName FROM Product WHERE productBarcode = :productBarcode")
   String getProductNameById(String productBarcode);
}
