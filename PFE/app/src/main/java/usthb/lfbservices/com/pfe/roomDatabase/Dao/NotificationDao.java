package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Notification;


@Dao
public interface NotificationDao {

    /**
     * Extracts from the database all the {@link Notification} and return them in a {@link List}.
     * @return A {@link List} of {@link Notification}.
     */
    @Query("SELECT * FROM Notification ORDER BY NotificationDate DESC ")
    List<Notification> getAll();

    /**
     * Deletes from the database the {@link Notification} corresponding to the given {@link Notification#notificationId}.
     * @param notificationId A {@link Notification#notificationId}.
     */
    @Query("DELETE FROM Notification WHERE notificationId = :notificationId")
    void deleteById( int notificationId);

    /**
     * Inserts in the database a {@link Notification}, if its {@link Notification#notificationId}
     * already exists, it will replace the row.
     * @param notifications
     */
    @Insert
    void insertAll(Notification... notifications);
}
