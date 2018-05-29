package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import usthb.lfbservices.com.pfe.models.Notification;


@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notification ORDER BY NotificationDate DESC ")
    List<Notification> getAll();


    @Query("DELETE FROM Notification WHERE notificationId = :notificationId")
    void deleteById( int notificationId);

    @Insert
    void insertAll(Notification... notifications);
}
