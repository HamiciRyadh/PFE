package usthb.lfbservices.com.pfe.RoomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Type;

@Dao
public interface TypeDao {


    @Query("SELECT typeName FROM Type ORDER BY typeId ASC")
    String[] getAll();

    @Query("SELECT typeName FROM Type WHERE typeId = :typeId")
    String getName(int typeId);

    @Query("SELECT categoryName FROM Category WHERE categoryId = :categoryId")
    String getCategoryName(int categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Type> types);
}
