package usthb.lfbservices.com.pfe.roomDatabase.Dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import usthb.lfbservices.com.pfe.models.SalesPoint;

@Dao
public interface SalesPointDao {

    /**
     * Extracts from the database a {@link SalesPoint} corresponding to the given {@link SalesPoint#salesPointId}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     * @return The associated {@link SalesPoint}.
     */
    @Query("SELECT * FROM SalesPoint WHERE salesPointId = :salesPointId")
    SalesPoint getById(String salesPointId);

    /**
     * Extracts from the database the {@link SalesPoint#salesPointName} associated with the given
     * {@link SalesPoint#salesPointId}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     * @return The associated {@link SalesPoint#salesPointName}.
     */
    @Query("SELECT salesPointName FROM SalesPoint WHERE salesPointId = :salesPointId")
    String getSalesPointNameById(String salesPointId);

    /**
     * Updates the {@link SalesPoint#image} corresponding to the given {@link SalesPoint#salesPointId}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     * @param image A byte array representing an image.
     */
    @Query("UPDATE SalesPoint SET image = :image WHERE salesPointId = :salesPointId")
    void updatePhoto(String salesPointId, byte[] image);

    /**
     * Inserts a {@link SalesPoint} in the database, if the {@link SalesPoint#salesPointId} already
     * exists, it will replace the row.
     * @param SalesPoint A {@link SalesPoint}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SalesPoint... SalesPoint);

    /**
     * Inserts a {@link List} of {@link SalesPoint} in the database, if the {@link SalesPoint#salesPointId}
     * already exists, it will replace the row.
     * @param SalesPoints A {@link List} of {@link SalesPoint}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SalesPoint> SalesPoints);

    /**
     * Updates a sales point.
     * @param salesPoint The new values of the sales point row identified by the {@link SalesPoint#salesPointId}.
     */
    @Update
    void update(SalesPoint salesPoint);

    /**
     * Checks whether or not a {@link SalesPoint} identified by its {@link SalesPoint#salesPointId}
     * exists in the database.
     * @param salesPointId A {@link SalesPoint}.
     * @return true if a {@link SalesPoint} identified by the id passed in parameter is present in
     * the database, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM SalesPoint WHERE salesPointId = :salesPointId)")
    boolean salesPointExists(String salesPointId);
}
