package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Type;

@Dao
public interface TypeDao {

    /**
     * Extracts from the database an array of {@link String} containing all the {@link Type#typeName}.
     * @return An array of {@link String} containing all the {@link Type#typeName}.
     */
    @Query("SELECT typeName FROM Type ORDER BY typeId ASC")
    String[] getAll();

    /**
     * Extracts from the database the {@link Type#typeName} associated with the given {@link Type#typeId}.
     * @param typeId A {@link Type#typeId}.
     * @return A {@link String} representing the {@link Type#typeName}.
     */
    @Query("SELECT typeName FROM Type WHERE typeId = :typeId")
    String getName(int typeId);

    /**
     * Extracts from the database the {@link usthb.lfbservices.com.pfe.models.Category#categoryName}
     * corresponding to the given type identified by its {@link Type#typeId}.
     * @param typeId A {@link Type#typeId}.
     * @return A {@link String} representing the {@link usthb.lfbservices.com.pfe.models.Category#categoryName}.
     */
    @Query("SELECT categoryName FROM Category WHERE categoryId = (SELECT categoryId FROM Type WHERE typeId = :typeId)")
    String getCategoryName(int typeId);

    /**
     * Inserts a {@link List} of {@link Type} in the database, if the {@link Type#typeId} already
     * exists, that row will be replaced.
     * @param types A {@link List} of {@link Type}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Type> types);
}
