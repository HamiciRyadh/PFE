package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Category;

@Dao
public interface CategoryDao {

    @Query("SELECT categoryName FROM Category ORDER BY categoryId ASC")
    String[] getAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);
}
