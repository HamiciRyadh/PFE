package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Category;

@Dao
public interface CategoryDao {

    /**
     * Extracts from the database all the {@link Category#categoryName} ordered ascending by their {@link Category#categoryId}.
     * @return An array of {@link Category#categoryName}.
     */
    @Query("SELECT categoryName FROM Category ORDER BY categoryId ASC")
    String[] getAll();

    /**
     * Inserts a {@link List} of {@link Category} in the database, if the {@link Category#categoryId}
     * already exists, it will replace the row.
     * @param categories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);
}
