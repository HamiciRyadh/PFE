package usthb.lfbservices.com.pfe.RoomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Product;

@Dao
public interface ProductDao {

   @Query("SELECT * FROM Product")
   List<Product> getAll();

   @Query("SELECT * FROM Product WHERE productBarcode = :productBarcode")
   Product getById(String productBarcode);

   @Query("DELETE FROM Product WHERE productBarcode = :productBarcode")
   void deleteById(String productBarcode);

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insertAll(Product... products);
}
