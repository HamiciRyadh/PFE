package usthb.lfbservices.com.pfe.roomDatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import usthb.lfbservices.com.pfe.roomDatabase.Dao.CategoryDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.CityDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.NotificationDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.ProductCaracteristicDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.ProductDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.ProductSalesPointDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.SalesPointDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.TypeCaracteristicDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.TypeDao;
import usthb.lfbservices.com.pfe.roomDatabase.Dao.WilayaDao;
import usthb.lfbservices.com.pfe.models.Category;
import usthb.lfbservices.com.pfe.models.City;
import usthb.lfbservices.com.pfe.models.Notification;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.ProductCaracteristic;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Type;
import usthb.lfbservices.com.pfe.models.TypeCaracteristic;
import usthb.lfbservices.com.pfe.models.Wilaya;

/**
 * An implementation of the {@link RoomDatabase}, creates the database and initializes the tables.
 */
@Database(entities = {ProductSalesPoint.class, Product.class, SalesPoint.class,
                        Category.class,Type.class, Wilaya.class, City.class,
                        TypeCaracteristic.class, ProductCaracteristic.class,
                         Notification.class
                        }, version = 20)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract ProductSalesPointDao productSalesPointDao();
    public abstract SalesPointDao salesPointDao();
    public abstract ProductDao productDao();
    public abstract CategoryDao categorytDao();
    public abstract TypeDao typeDao();
    public abstract WilayaDao wilayaDao();
    public abstract CityDao cityDao();
    public abstract TypeCaracteristicDao typeCaracteristicDao();
    public abstract ProductCaracteristicDao productCaracteristicDao();
    public abstract NotificationDao notificationDao();

    private static AppRoomDatabase INSTANCE;

    /**
     * Returns an instance of the class.
     * @param context The Context of the current Activity.
     * @return An instance of this class.
     */
    public static AppRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Initializes the database's tables.
     * @param context The Context of the current Activity.
     * @return An instance of the database with the tables created and initialized.
     */
    private static AppRoomDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, AppRoomDatabase.class, "PFE.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance(context).wilayaDao().insertAll(Wilaya.Data());
                                getInstance(context).cityDao().insertAll(City.Data());
                                getInstance(context).categorytDao().insertAll(Category.Data());
                                getInstance(context).typeDao().insertAll(Type.Data());
                                getInstance(context).typeCaracteristicDao().insertAll(TypeCaracteristic.Data());
                            }
                        });
                    }
                })
                .build();
    }
}

